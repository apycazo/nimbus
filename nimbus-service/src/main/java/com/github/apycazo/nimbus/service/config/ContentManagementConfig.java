package com.github.apycazo.nimbus.service.config;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configures how to negotiate content with the client.
 * In this case we will give priority to path extensions, not ignore the headers received, and provide only
 * json and xml representations as options when using extensions.
 * <br>
 * Final behaviour will be:
 * <ul>
 * <li>No header, No extension -> Return Json content </li>
 * <li>Header, No extension -> Return whatever is specified on headers</li>
 * <li>Header, Extension -> Return whatever is requested by the extension (json | xml)</li>
 * </ul>
 */
public class ContentManagementConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer config)
    {
        config
                // path extensions will always be used when present
                .favorPathExtension(true)
                // allow us to define media types manually
                .useJaf(false)
                // Note that a browser might send a default xml header, which would override this
                .defaultContentType(MediaType.APPLICATION_JSON)
                // Extensions we allow to use (note that using xml requires dependency on 'jackson-dataformat-xml')
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }
}
