package com.etc.ant.elasticsearch.client;

import com.etc.ant.common.core.client.AbstractCanalClient;
import com.etc.ant.common.core.client.direct.AbstractDirectCanalClient;
import com.etc.ant.common.core.client.direct.DirectClusterCanalClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @version 1.0
 * @create 2018-10-05 17:35
 * @since JDK1.8
 **/
@Configuration
public class ElasticsearchCanalClient {

    @Value("${canal.zkServers}")
    private String zkServers;
    @Value("${canal.destination}")
    private String destination;
    @Bean
    public AbstractCanalClient canalClient(){
        return new DirectClusterCanalClient(zkServers,destination);
    }
}
