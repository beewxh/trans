package com.hsbc.trans.dao;

import com.hsbc.common.errorhandler.exception.BusinessException;
import com.hsbc.common.util.CopyBeanUtils;
import com.hsbc.common.validation.ValidationUtils;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.ErrorCode;
import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Repository
public class TransactionDaoMemoryImpl implements TransactionDao {

    private final TransactionStore store = new TransactionStore();

    private final Map<String, Long> transIdIndexMap = new ConcurrentHashMap<>();

    @Autowired
    private ValidationUtils validationUtils;



    @Override
    public Transaction add(Transaction transaction) {
        validationUtils.validate(transaction);
        if (existsByTransId(transaction.getTransId())) {
            throw new BusinessException("Transaction already exists " + transaction.getTransId()).code(ErrorCode.TRANSACTION_DUPLICATE.getCode());
        }
        transIdIndexMap.put(transaction.getTransId(), transaction.getId());
        store.put(transaction.getId(), transaction);
        return transaction;
    }

    private boolean existsByTransId(String transId) {
        return transIdIndexMap.containsKey(transId) && store.exists(transIdIndexMap.get(transId));
    }

    @Override
    public Optional<Transaction> queryById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Transaction> queryByTransId(String transId) {
        return Optional.ofNullable(existsByTransId(transId) ? store.get(transIdIndexMap.get(transId)) : null);
    }

    @Override
    public List<Transaction> queryList() {
        return store.values();
    }

    @Override
    public PageResult<Transaction> queryPage(PageRequest pageRequest) {
        List<Transaction> values = store.values(pageRequest.getOffset(), pageRequest.getPageSize());
        return new PageResult<>(values, store.size(), pageRequest);
    }


    @Override
    public Transaction updateById(Transaction transaction) {
        if (!store.exists(transaction.getId())) {
            throw new BusinessException("Transaction not found with id: " + transaction.getId()).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode());
        }
        synchronized (store.getLockKey(transaction.getId())) {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            if (store.exists(transaction.getId())) { // 锁记录后重新检查记录存在
                Transaction origin = store.get(transaction.getId());
                if (assign(origin, transaction)) {
                    store.put(transaction.getId(), origin);
                } else {
                    throw new BusinessException("transaction not changed while updating, " + transaction.getId()).code(ErrorCode.TRANSACTION_NOT_CHANGED.getCode());
                }
            } else {
                throw new BusinessException("while concurrent operating, transaction not found with id: " + transaction.getId()).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode());
            }
        }
        return transaction;
    }

    private boolean assign(Transaction origin, Transaction modified) {

        boolean changed = false;
        if (!origin.getStatus().equals(modified.getStatus())) {
            origin.setStatus(modified.getStatus());
            changed = true;
        }
        if (modified.getDescription() != null && !modified.getDescription().equals(origin.getDescription())) {
            origin.setDescription(modified.getDescription());
            changed = true;
        }
        if (changed) {
            origin.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        }
        return changed;
    }


    @Override
    public Transaction deleteById(Long id) {

        if (!store.exists(id)) {
            throw new BusinessException("Transaction not found with id: " + id).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode());
        }
        synchronized (store.getLockKey(id)) {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            if (store.exists(id)) { // 锁记录后重新检查记录存在
                Transaction transaction = store.delete(id);
                transIdIndexMap.remove(transaction.getTransId());
                return transaction;
            } else {
                throw new BusinessException("while concurrent operating, transaction not found with id: " + id).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode());
            }
        }
    }



    private static class TransactionStore {
        private final Map<Long, Transaction> transactionStore = new ConcurrentSkipListMap<>();

        private boolean exists(Long id) {
            return transactionStore.containsKey(id);
        }

        private Transaction copy(Transaction from) {
            if (from == null) {
                return null;
            }
            Transaction to = new Transaction();
            CopyBeanUtils.copyProperties(from, to);
            return to;
        }

        private Transaction get(Long id) {
            return copy(transactionStore.get(id));
        }

        private Transaction delete(Long id) {
            return copy(transactionStore.remove(id));
        }

        private void put(Long id, Transaction transaction) {
            transactionStore.put(id, copy(transaction));
        }

        private List<Transaction> values() {
            return transactionStore.values().stream()
                .map(this::copy)
                .collect(Collectors.toList());
        }

        private List<Transaction> values(long from, int limit) {
            return transactionStore.entrySet().stream()
                .skip(from)
                .limit(limit)
                .map(Map.Entry::getValue)
                .map(this::copy)
                .collect(Collectors.toList());
        }

        private long size() {
            return transactionStore.size();
        }

        private Long getLockKey(Long id) {
            if (transactionStore.containsKey(id)) {
                return transactionStore.get(id).getId();
            }
            throw new BusinessException("Transaction not found with id: " + id).code(ErrorCode.SYSTEM_ERROR.getCode());
        }

    }
}