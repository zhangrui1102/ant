package com.etc.ant.database;

import com.etc.ant.common.database.client.mysql.MysqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
@SpringBootApplication
public class DataBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataBaseApplication.class, args);
    }

    @Autowired
    private DataSource dataSource;

    @Bean
    public MysqlClient mysqlClient(){
        return new MysqlClient(dataSource);
    }
}