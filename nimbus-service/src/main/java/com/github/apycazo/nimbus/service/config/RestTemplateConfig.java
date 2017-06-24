package com.github.apycazo.nimbus.service.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Data
@Lazy
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "rest.template")
public class RestTemplateConfig
{
    // bean names
    public static class BeanNames {
        public static final String REST_TEMPLATE = "nimbus::rest-template";
        public static final String REQUEST_FACTORY = "nimbus::request-factory";
    }
    // connection Pool
    private int maxTotal = 200;
    private int defaultMaxPerRoute = 100;
    // timeouts
    private int connectionRequestTimeout = 5000;
    private int connectTimeout = 5000;
    private int socketTimeout = 5000;

    @Bean(BeanNames.REST_TEMPLATE)
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
    {
        return restTemplateBuilder.requestFactory(clientHttpRequestFactory()).build();
    }

    @Bean(BeanNames.REQUEST_FACTORY)
    public ClientHttpRequestFactory clientHttpRequestFactory()
    {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        RequestConfig config = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        HttpClientBuilder builder = HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config);

        return new HttpComponentsClientHttpRequestFactory(builder.build());
    }
}
