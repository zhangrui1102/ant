package com.etc.ant.common.elasticsearch.service;


import com.etc.ant.common.core.model.IndexTypeModel;
import com.etc.ant.common.core.model.request.SyncByTableRequest;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:44:00
 */
public interface TransactionalService {

    /**
     * 开启事务的读取mysql并插入到mysql中（读锁）
     */
    void batchInsertElasticsearch(SyncByTableRequest request, String primaryKey, long from, long to, IndexTypeModel indexTypeModel);
}
