package com.qfqg_es.config;


import com.qfqg_es.param.Param;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haoyuan
 * 配置ElasticSearch 高级Client
 * 2020/7/10 7:58
 */
@Configuration
public class ESClientConfig {

    @Bean
    public RestHighLevelClient highLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(Param.ES_HOSTNAME, Param.ES_PORT, Param.ES_SCHEME)));
        return client;
    }
}
