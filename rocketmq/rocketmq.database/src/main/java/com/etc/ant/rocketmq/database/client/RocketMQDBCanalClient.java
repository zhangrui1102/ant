package com.etc.ant.rocketmq.database.client;

import com.etc.ant.common.core.client.AbstractCanalClient;
import com.etc.ant.common.core.client.rocketmq.RocketMQSimpleCanalClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @version 1.0
 * @create 2018-11-09 13:38
 * @since JDK1.8
 **/
@Configuration
public class RocketMQDBCanalClient {

    @Value("${rocketmq.db.nameServers}")
    private String nameServers;
    @Value("${rocketmq.db.topic}")
    private String topic;
    @Value("${rocketmq.db.groupName}")
    private String groupName;

    @Bean
    public AbstractCanalClient canalClient(){
        return new RocketMQSimpleCanalClient(nameServers,topic,groupName);
    }

}
