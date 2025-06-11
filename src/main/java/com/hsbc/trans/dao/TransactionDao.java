package com.hsbc.trans.dao;

import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.bean.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * 交易数据访问接口
 * 定义了交易记录的基本CRUD操作和分页查询功能
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public interface TransactionDao {

    /**
     * 添加新的交易记录
     *
     * @param transaction 待添加的交易记录
     * @return 添加成功后的交易记录（包含生成的ID）
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录已存在
     */
    Transaction add(Transaction transaction);

    /**
     * 根据ID查询交易记录
     *
     * @param id 交易记录ID
     * @return 包含交易记录的Optional对象，如果记录不存在则返回空Optional
     */
    Optional<Transaction> queryById(Long id);

    /**
     * 根据业务交易ID查询交易记录
     *
     * @param transId 业务交易ID
     * @return 包含交易记录的Optional对象，如果记录不存在则返回空Optional
     */
    Optional<Transaction> queryByTransId(String transId);

    /**
     * 查询所有交易记录
     *
     * @return 交易记录列表
     */
    List<Transaction> queryList();

    /**
     * 分页查询交易记录
     *
     * @param pageRequest 分页请求参数
     * @return 分页结果，包含当前页数据和总记录数
     */
    PageResult<Transaction> queryPage(PageRequest pageRequest);

    /**
     * 更新交易记录
     *
     * @param transaction 待更新的交易记录
     * @return 更新后的交易记录
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在或更新时发生并发冲突
     */
    Transaction updateById(Transaction transaction);

    /**
     * 删除交易记录
     *
     * @param id 待删除的交易记录ID
     * @return 被删除的交易记录
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在或删除时发生并发冲突
     */
    Transaction deleteById(Long id);

}