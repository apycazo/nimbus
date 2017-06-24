package com.github.apycazo.nimbus.demo.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class MathRestController implements MathService
{
    public static final String MAPPING = "math";

    @Override
    public Integer inc(@PathVariable Integer value)
    {
        return value++;
    }
}
