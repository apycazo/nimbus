package com.github.apycazo.nimbus.demo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
public class MathRestController implements MathService
{

    @Override
    public Integer inc(@PathVariable Integer value)
    {
        log.info("Received request to increment value '{}'", value);
        return (value + 1);
    }
}
