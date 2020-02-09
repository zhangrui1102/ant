package com.etc.ant.common.elasticsearch.service.impl;

import com.etc.ant.common.core.model.IndexTypeModel;
import com.etc.ant.common.core.model.request.SyncByTableRequest;
import com.etc.ant.common.elasticsearch.dao.ElasticsearchDAO;
import com.etc.ant.common.elasticsearch.service.ElasticsearchService;
import com.etc.ant.common.elasticsearch.service.TransactionalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:44:00
 */
@Service
public class TransactionalServiceImpl implements TransactionalService {

    @Resource
    private ElasticsearchDAO elasticsearchDAO;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public void batchInsertElasticsearch(SyncByTableRequest request, String primaryKey, long from, long to, IndexTypeModel indexTypeModel) {
        List<Map<String, Object>> dataList = elasticsearchDAO.selectByPKIntervalLockInShareMode(primaryKey, from, to, request.getDatabase(), request.getTable());
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        dataList = convertDateType(dataList);
        Map<String, Map<String, Object>> dataMap = dataList.parallelStream().collect(Collectors.toMap(strObjMap -> String.valueOf(strObjMap.get(primaryKey)), map -> map));
        elasticsearchService.batchInsertById(indexTypeModel.getIndex(), indexTypeModel.getType(), dataMap);
    }

    private List<Map<String, Object>> convertDateType(List<Map<String, Object>> source) {
        source.parallelStream().forEach(map -> map.forEach((key, value) -> {
            if (value instanceof Timestamp) {
                map.put(key, LocalDateTime.ofInstant(((Timestamp) value).toInstant(), ZoneId.systemDefault()));
            }
        }));
        return source;
    }
}
