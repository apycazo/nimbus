package com.github.apycazo.nimbus.central;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableAdminServer
@EnableConfigServer
@EnableEurekaServer
@EnableDiscoveryClient
@SpringBootApplication
public class NimbusCentralApp
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(NimbusCentralApp.class).run(args);
    }
}
