package com.github.apycazo.nimbus.demo.api;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public interface MathService
{
    String MAPPING = "api/math";

    @RequestMapping(value = MAPPING + "/{value}")
    Integer inc(@PathVariable(value = "value") Integer value);
}
