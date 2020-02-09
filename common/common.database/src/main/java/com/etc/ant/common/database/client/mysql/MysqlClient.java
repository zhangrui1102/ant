package com.etc.ant.common.database.client.mysql;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
@Component
public class MysqlClient extends AbstractMysqlClient {

    public MysqlClient(DataSource dataSource){
        setDataSource(dataSource);
    }

    @Override
    protected void insert(CanalEntry.Header header, List<CanalEntry.Column> afterColumns) {
        execute(header, afterColumns);
    }

    @Override
    protected void update(CanalEntry.Header header, List<CanalEntry.Column> afterColumns) {
        execute(header, afterColumns);
    }

    @Override
    protected void delete(CanalEntry.Header header, List<CanalEntry.Column> beforeColumns) {
        execute(header, beforeColumns);
    }

    @Override
    protected void create(String sql) {
        executeDDL(sql);
    }

    @Override
    protected void dindex(String sql) {
        executeDDL(sql);
    }

    @Override
    protected void alert(String sql) {
        executeDDL(sql);
    }

    @Override
    protected void cindex(String sql) {
        executeDDL(sql);
    }

    @Override
    protected void truncate(String sql) {
        executeDDL(sql);
    }

    @Override
    protected void rename(String sql) {
        executeDDL(sql);
    }
}
