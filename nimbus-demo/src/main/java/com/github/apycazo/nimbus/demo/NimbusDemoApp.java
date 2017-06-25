package com.github.apycazo.nimbus.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@EnableHystrix
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class NimbusDemoApp
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(NimbusDemoApp.class)
                .properties("sun.misc.URLClassPath.disableJarChecking=true")
                .run(args);
    }
}
