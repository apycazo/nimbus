package com.github.apycazo.nimbus.demo.cfg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="nimbus.demo")
public class NimbusDemoSettings
{
    private Integer randomId = 0;
    private String cloudId = "local";
}
