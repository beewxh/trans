package com.hsbc.trans.dao;

import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.bean.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionDao {

    Transaction add(Transaction transaction);

    Optional<Transaction> queryById(Long id);

    Optional<Transaction> queryByTransId(String transId);

    List<Transaction> queryList();

    PageResult<Transaction> queryPage(PageRequest pageRequest);

    Transaction updateById(Transaction transaction);

    void deleteById(Long id);

    boolean existsByTransId(String transId);

}