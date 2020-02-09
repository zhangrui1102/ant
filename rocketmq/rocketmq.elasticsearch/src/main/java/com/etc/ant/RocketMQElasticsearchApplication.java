package com.etc.ant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author
 * @version 1.0
 * @create 2018-11-09 13:31
 * @since JDK1.8
 **/
@SpringBootApplication
@MapperScan("com.etc.ant.common.elasticsearch.dao")
public class RocketMQElasticsearchApplication {

    public static void main(String args[]){
        SpringApplication.run(RocketMQElasticsearchApplication.class, args);
    }
}
