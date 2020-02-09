package com.etc.ant.common.elasticsearch.service.impl;


import com.etc.ant.common.core.util.JSONUtils;
import com.etc.ant.common.elasticsearch.service.AbstractElasticsearchService;
import com.etc.ant.common.elasticsearch.service.ElasticsearchService;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:44:00
 */
@Service
public class ElasticsearchServiceImpl extends AbstractElasticsearchService implements ElasticsearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    @Override
    public void insertById(String index, String type, String id, Map<String, Object> dataMap) {
        getClient().prepareIndex(index, type, id).setSource(dataMap).get();
    }

    @Override
    public void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap) {
        BulkRequestBuilder bulkRequestBuilder = getClient().prepareBulk();

        idDataMap.forEach((id, dataMap) -> bulkRequestBuilder.add(getClient().prepareIndex(index, type, id).setSource(dataMap)));
        try {
            BulkResponse bulkResponse = bulkRequestBuilder.execute().get();
            if (bulkResponse.hasFailures()) {
                logger.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", data=" + JSONUtils.convertObjectToJson(idDataMap) + ", cause:" + bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            logger.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", data=" + JSONUtils.convertObjectToJson(idDataMap), e);
        }

    }

    @Override
    public void update(String index, String type, String id, Map<String, Object> dataMap) {
        this.insertById(index, type, id, dataMap);
    }

    @Override
    public void deleteById(String index, String type, String id) {
        getClient().prepareDelete(index, type, id).get();
    }
}
