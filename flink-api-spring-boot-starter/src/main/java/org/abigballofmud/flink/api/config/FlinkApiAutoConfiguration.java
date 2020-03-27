package org.abigballofmud.flink.api.config;

import org.abigballofmud.flink.api.context.FlinkApiContext;
import org.abigballofmud.flink.api.execeptions.RestTemplateErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 21:28
 * @since 1.0
 */
@Configuration
@ComponentScan("org.abigballofmud.flink.api")
public class FlinkApiAutoConfiguration {

    @Bean
    public FlinkApiContext flinkApiContext(RestTemplate restTemplate) {
        return new FlinkApiContext(restTemplate);
    }

    @Bean("flinkRestTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory simpleClientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(15000);
        return factory;
    }
}
