package com.github.apycazo.nimbus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan
public class NimbusServiceConfig
{
    /**
     * Sleuth sampler
     * @return Always create a sample
     */
//    @Lazy
//    @Bean
//    public AlwaysSampler defaultSampler() {
//        return new AlwaysSampler();
//    }
}
