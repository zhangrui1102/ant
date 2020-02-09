package com.etc.ant.common.elasticsearch.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.etc.ant.common.core.event.CanalEvent;
import com.etc.ant.common.core.listener.CanalListener;
import com.etc.ant.common.core.model.DatabaseTableModel;
import com.etc.ant.common.core.model.IndexTypeModel;
import com.etc.ant.common.core.util.JSONUtils;
import com.etc.ant.common.elasticsearch.service.ElasticsearchService;
import com.etc.ant.common.elasticsearch.service.MappingService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.event.DocumentEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:44:00
 */
@Component
public class ElasticsearchCanalListener implements CanalListener {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchCanalListener.class);

    @Resource
    private MappingService mappingService;
    @Resource
    private ElasticsearchService elasticsearchService;

    @Override
    public void onApplicationEvent(CanalEvent event) {
        Entry entry = event.getEntry();
        CanalEntry.EventType eventType = entry.getHeader().getEventType();
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        IndexTypeModel indexTypeModel = mappingService.getIndexType(new DatabaseTableModel(database, table));
        if (indexTypeModel == null) {
            return;
        }
        String index = indexTypeModel.getIndex();
        String type = indexTypeModel.getType();
        RowChange change;
        try {
            change = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            logger.error("canalEntry_parser_error,根据CanalEntry获取RowChange失败！", e);
            return;
        }
        change.getRowDatasList().forEach(rowData -> doSync(database, table, index, type, rowData,eventType));
    }

    private Map<String, Object> parseColumnsToMap(List<Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if (column == null) {
                return;
            }
            jsonMap.put(column.getName(), column.getIsNull() ? null : mappingService.getElasticsearchTypeObject(column.getMysqlType(), column.getValue()));
        });
        return jsonMap;
    }


    private void doSync(String database, String table, String index, String type, RowData rowData,CanalEntry.EventType eventType){
        switch (eventType){
            case INSERT:
                insert(database,table,index,type,rowData);
                break;
            case DELETE:
                delete(database,table,index,type,rowData);
                break;
            case UPDATE:
                update(database,table,index,type,rowData);
                break;
            default:
                break;
        }
    }

    private void delete(String database, String table, String index, String type, RowData rowData) {
        List<Column> columns = rowData.getBeforeColumnsList();
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(database + "." + table)).orElse("id");
        Column idColumn = columns.stream().filter(column -> column.getIsKey() && primaryKey.equals(column.getName())).findFirst().orElse(null);
        if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
            logger.info("delete_column_find_null_warn delete从column中找不到主键,database=" + database + ",table=" + table);
            return;
        }
        logger.info("delete_column_id_info delete主键id,database=" + database + ",table=" + table + ",id=" + idColumn.getValue());
        elasticsearchService.deleteById(index, type, idColumn.getValue());
        logger.info("delete_es_info 同步es删除操作成功！database=" + database + ",table=" + table + ",id=" + idColumn.getValue());
    }

    private void insert(String database, String table, String index, String type, RowData rowData) {
        List<Column> columns = rowData.getAfterColumnsList();
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(database + "." + table)).orElse("id");
        Column idColumn = columns.stream().filter(column -> column.getIsKey() && primaryKey.equals(column.getName())).findFirst().orElse(null);
        if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
            logger.info("insert_column_find_null_warn insert从column中找不到主键,database=" + database + ",table=" + table);
            return;
        }
        logger.info("insert_column_id_info insert主键id,database=" + database + ",table=" + table + ",id=" + idColumn.getValue());
        Map<String, Object> dataMap = parseColumnsToMap(columns);
        elasticsearchService.insertById(index, type, idColumn.getValue(), dataMap);
        logger.info("insert_es_info 同步es插入操作成功！database=" + database + ",table=" + table + ",data=" + JSONUtils.convertObjectToJson(dataMap));
    }

    private void update(String database, String table, String index, String type, RowData rowData) {
        List<Column> columns = rowData.getAfterColumnsList();
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(database + "." + table)).orElse("id");
        Column idColumn = columns.stream().filter(column -> column.getIsKey() && primaryKey.equals(column.getName())).findFirst().orElse(null);
        if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
            logger.info("update_column_find_null_warn update从column中找不到主键,database=" + database + ",table=" + table);
            return;
        }
        logger.info("update_column_id_info update主键id,database=" + database + ",table=" + table + ",id=" + idColumn.getValue());
        Map<String, Object> dataMap = parseColumnsToMap(columns);
        elasticsearchService.update(index, type, idColumn.getValue(), dataMap);
        logger.info("update_es_info 同步es插入操作成功！database=" + database + ",table=" + table + ",data=" + dataMap);
    }

}
