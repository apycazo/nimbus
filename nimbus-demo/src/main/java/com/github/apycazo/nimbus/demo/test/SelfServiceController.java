package com.github.apycazo.nimbus.demo.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class SelfServiceController
{
    AtomicInteger counter = new AtomicInteger(1);

    @Value("${info.instanceId:default}")
    private String instanceId = "";

    @GetMapping(value = "self")
    public Map<String, Object> selfRequest()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("instance", instanceId);
        map.put("value", counter.getAndIncrement());
        return map;
    }
}
