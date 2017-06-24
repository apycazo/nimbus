package com.github.apycazo.nimbus.demo.api;

import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "${spring.application.name}/api", url = "http://127.0.0.1:${server.port:8080}")
public interface MathServiceClient extends MathService
{
}
