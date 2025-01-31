package com.kklive.entity.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

import javax.annotation.Resource;

/**
 * es初始化
 */
@Configuration
public class EsConfiguration extends AbstractElasticsearchConfiguration implements DisposableBean {

    @Resource
    private AppConfig appConfig;

    private RestHighLevelClient client;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(appConfig.getEsHostPort()).build();
        client = RestClients.create(clientConfiguration).rest();
        return client;
    }

    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.close();
        }
    }
}
