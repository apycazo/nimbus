package com.github.apycazo.nimbus.central;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class NimbusCentralApp
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(NimbusCentralApp.class).run(args);
    }
}
