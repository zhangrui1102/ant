package com.etc.ant.common.elasticsearch.service;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * @author
 * @version 1.0
 * @create 2018-11-09 13:23
 * @since JDK1.8
 **/
public abstract class AbstractElasticsearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    protected Client getClient(){
        return elasticsearchTemplate.getClient();
    }

}
