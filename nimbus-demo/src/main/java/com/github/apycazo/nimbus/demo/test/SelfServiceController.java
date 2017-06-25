package com.github.apycazo.nimbus.demo.test;

import com.github.apycazo.nimbus.demo.api.MathServiceClient;
import com.github.apycazo.nimbus.demo.cfg.NimbusDemoSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private NimbusDemoSettings settings;

    @Autowired
    private MathServiceClient mathServiceClient;

    @Value("${info.instanceId:default}")
    private String instanceId = "";

    @GetMapping(value = "self")
    public Map<String, Object> selfRequest()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("instance", instanceId);
        map.put("value", counter.getAndIncrement());
        map.put("math", mathServiceClient.inc(100));
        map.put("demo", settings.getCloudId());
        return map;
    }
}
