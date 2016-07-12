package com.example.crawler;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebCrawlerConfiguration {

    @Value("${com.example.crawler.connectionRequestTimeout:5000}")
    private int connectionRequestTimeout;
    @Value("${com.example.crawler.socketTimeout:3000}")
    private int socketTimeout;
    @Value("${com.example.crawler.connectionTimeout:3000}")
    private int connectionTimeout;

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        RequestConfig config = RequestConfig.custom()
                .setRedirectsEnabled(false)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectionTimeout)
                .build();

        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(config);
        return builder.build();
    }
}