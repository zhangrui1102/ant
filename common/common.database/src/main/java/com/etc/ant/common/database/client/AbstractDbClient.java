package com.etc.ant.common.database.client;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
public abstract class AbstractDbClient implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDbClient.class);

    protected static final String SEP = SystemUtils.LINE_SEPARATOR;
    protected static String contextFormat;
    protected static String rowFormat;
    protected static String transactionFormat;
    protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected abstract void insert(CanalEntry.Header header, List<CanalEntry.Column> afterColumns);

    protected abstract void update(CanalEntry.Header header, List<CanalEntry.Column> afterColumns);

    protected abstract void delete(CanalEntry.Header header, List<CanalEntry.Column> beforeColumns);

    protected abstract void create(String sql);

    protected abstract void dindex(String sql);

    protected abstract void alert(String sql);

    protected abstract void cindex(String sql);

    protected abstract void truncate(String sql);

    protected abstract void rename(String sql);

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(SEP)
                .append("-------------Batch-------------")
                .append(SEP)
                .append("* Batch Id: [{}] ,count : [{}] , Mem size : [{}] , Time : {}")
                .append(SEP)
                .append("* Start : [{}] ")
                .append(SEP)
                .append("* End : [{}] ")
                .append(SEP)
                .append("-------------------------------")
                .append(SEP);
        contextFormat = sb.toString();

        sb = new StringBuilder();
        sb.append(SEP)
                .append("+++++++++++++Row+++++++++++++>>>")
                .append("binlog[{}:{}] , name[{},{}] , eventType : {} , executeTime : {} , delay : {}ms")
                .append(SEP);
        rowFormat = sb.toString();

        sb = new StringBuilder();
        sb.append(SEP)
                .append("===========Transaction {} : {}=======>>>")
                .append("binlog[{}:{}] , executeTime : {} , delay : {}ms")
                .append(SEP);
        transactionFormat = sb.toString();
    }

    public void rowData(CanalEntry.Entry entry) throws Exception {
        CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        CanalEntry.EventType eventType = rowChange.getEventType();
        CanalEntry.Header header = entry.getHeader();
        long executeTime = header.getExecuteTime();
        long delayTime = System.currentTimeMillis() - executeTime;
        String sql = rowChange.getSql();

        try {
            if (!isDML(eventType) || rowChange.getIsDdl()) {
                processDDL(header, eventType, sql);
                return;
            }
            //处理DML数据
            processDML(header, eventType, rowChange, sql);
        } catch (Exception e) {
            logger.error("process event error ,", e);
            logger.error(rowFormat,
                    new Object[]{header.getLogfileName(), String.valueOf(header.getLogfileOffset()),
                            header.getSchemaName(), header.getTableName(), eventType,
                            String.valueOf(executeTime), String.valueOf(delayTime)});
            throw e;//重新抛出
        }
    }

    /**
     * 判断事件类型为DML 数据
     *
     * @param eventType
     * @return
     */
    private boolean isDML(CanalEntry.EventType eventType) {
        switch (eventType) {
            case INSERT:
            case UPDATE:
            case DELETE:
                return true;
            default:
                return false;
        }
    }

    /**
     * 处理 DDL数据
     *
     * @param header
     * @param eventType
     * @param sql
     */

    private void processDDL(CanalEntry.Header header, CanalEntry.EventType eventType, String sql) {
        String table = header.getSchemaName() + "." + header.getTableName();
        //对于DDL，直接执行，因为没有行变更数据
        switch (eventType) {
            case CREATE:
                create(sql);
                logger.warn("parse create table event, table: {}, sql: {}", table, sql);
                return;
            case ALTER:
                alert(sql);
                logger.warn("parse alter table event, table: {}, sql: {}", table, sql);
                return;
            case TRUNCATE:
                truncate(sql);
                logger.warn("parse truncate table event, table: {}, sql: {}", table, sql);
                return;
            case ERASE:
            case QUERY:
                logger.warn("parse event : {}, sql: {} . ignored!", eventType.name(), sql);
                return;
            case RENAME:
                rename(sql);
                logger.warn("parse rename table event, table: {}, sql: {}", table, sql);
                return;
            case CINDEX:
                cindex(sql);
                logger.warn("parse create index event, table: {}, sql: {}", table, sql);
                return;
            case DINDEX:
                dindex(sql);
                logger.warn("parse delete index event, table: {}, sql: {}", table, sql);
                return;
            default:
                logger.warn("parse unknown event: {}, table: {}, sql: {}", new String[]{eventType.name(), table, sql});
                break;
        }
    }

    /**
     * 强烈建议捕获异常，非上述已列出的其他操作，非核心
     * 除了“insert”、“update”、“delete”操作之外的，其他类型的操作.
     * 默认实现为“无操作”
     *
     * @param header 可以从header中获得schema、table的名称
     * @param sql
     */
    private void whenOthers(CanalEntry.Header header, String sql) {
        String schema = header.getSchemaName();
        String table = header.getTableName();
        logger.error("ignore event,schema: {},table: {},SQL: {}", new String[]{schema, table, sql});
    }

    /**
     * 处理 dml 数据
     *
     * @param header
     * @param eventType
     * @param rowChange
     * @param sql
     */
    private void processDML(CanalEntry.Header header, CanalEntry.EventType eventType, CanalEntry.RowChange rowChange, String sql) {
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            switch (eventType) {
                case DELETE:
                    delete(header, rowData.getBeforeColumnsList());
                    break;
                case INSERT:
                    insert(header, rowData.getAfterColumnsList());
                    break;
                case UPDATE:
                    update(header, rowData.getAfterColumnsList());
                    break;
                default:
                    whenOthers(header, sql);
            }
        }
    }
}




