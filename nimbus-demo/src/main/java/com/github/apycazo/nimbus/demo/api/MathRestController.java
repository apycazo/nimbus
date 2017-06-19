package com.github.apycazo.nimbus.demo.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MathRestController
{
    public static final String MAPPING = "math";

    @RequestMapping(value = MAPPING)
    public Integer inc(@PathVariable Integer value)
    {
        return value++;
    }
}