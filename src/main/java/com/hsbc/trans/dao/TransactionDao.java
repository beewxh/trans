package com.hsbc.trans.dao;

import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.bean.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Transaction Data Access Interface
 * Defines data access operations for transactions, including create, query, update, and delete
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public interface TransactionDao {

    /**
     * Create a new transaction record
     *
     * @param transaction Transaction record to create
     * @return Created transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录已存在
     */
    Transaction add(Transaction transaction);

    /**
     * Query transaction record by ID
     *
     * @param id Transaction record ID
     * @return Transaction record, null if not found
     */
    Optional<Transaction> queryById(Long id);

    /**
     * Query transaction record by business transaction ID
     *
     * @param transId Business transaction ID
     * @return Transaction record, null if not found
     */
    Optional<Transaction> queryByTransId(String transId);

    /**
     * Query all transaction records
     *
     * @return List of transaction records
     */
    List<Transaction> queryList();

    /**
     * Query transaction records with pagination
     *
     * @param pageRequest Pagination request parameters
     * @return Paginated result
     */
    PageResult<Transaction> queryPage(PageRequest pageRequest);

    /**
     * Update transaction record
     *
     * @param transaction Transaction record to update
     * @return Updated transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在或更新时发生并发冲突
     */
    Transaction updateById(Transaction transaction);

    /**
     * Delete transaction record
     *
     * @param id Transaction record ID
     * @return Deleted transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在或删除时发生并发冲突
     */
    Transaction deleteById(Long id);

}