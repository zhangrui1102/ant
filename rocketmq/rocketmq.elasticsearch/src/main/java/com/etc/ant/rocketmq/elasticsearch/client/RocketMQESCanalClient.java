package com.etc.ant.rocketmq.elasticsearch.client;

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
public class RocketMQESCanalClient {

    @Value("${rocketmq.es.nameServers}")
    private String nameServers;
    @Value("${rocketmq.es.topic}")
    private String topic;
    @Value("${rocketmq.es.groupName}")
    private String groupName;

    @Bean
    public AbstractCanalClient canalClient(){
        return new RocketMQSimpleCanalClient(nameServers,topic,groupName);
    }

}
