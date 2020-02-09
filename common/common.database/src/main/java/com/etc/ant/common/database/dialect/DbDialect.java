package com.etc.ant.common.database.dialect;

import org.apache.ddlutils.model.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
public interface DbDialect {

    LobHandler getLobHandler();

    JdbcTemplate getJdbcTemplate();

    TransactionTemplate getTransactionTemplate();

    Table findTable(String schema, String table);

    Table findTable(String schema, String table, boolean useCache);

}
