package com.etc.ant.common.database.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.etc.ant.common.core.event.CanalEvent;
import com.etc.ant.common.core.listener.CanalListener;
import com.etc.ant.common.database.client.mysql.MysqlClient;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
@Component
public class DataBaseCanalListener implements CanalListener {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseCanalListener.class);

    @Resource
    private MysqlClient mysqlClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onApplicationEvent(CanalEvent event) {
        Entry entry = event.getEntry();
        try {
            String database = entry.getHeader().getSchemaName();
            String table = entry.getHeader().getTableName();
            CanalEntry.RowChange change;
            try {
                change = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                logger.error("canalEntry_parser_error,根据CanalEntry获取RowChange失败！", e);
                return;
            }
            mysqlClient.rowData(entry);
            logger.info("同步slave操作成功！database=" + database + ",table=" + table+",data:{}",change);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
