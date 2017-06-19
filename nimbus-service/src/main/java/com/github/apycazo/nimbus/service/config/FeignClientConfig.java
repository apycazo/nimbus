package com.github.apycazo.nimbus.service.config;

import feign.Logger;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class FeignClientConfig
{
    /**
     * Configures every feign logger level.
     * An alternative would be to include a property like: com.github.apycazo.codex.spring.rest.feign.FeignedServiceClient=DEBUG
     * (Note that is is the fully qualified name for the client, and that only responds to 'DEBUG' level).
     * @return The configured level
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
