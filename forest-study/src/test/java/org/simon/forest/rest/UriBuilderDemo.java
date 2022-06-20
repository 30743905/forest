package org.simon.forest.rest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UriBuilderDemo {


    @Test
    public void defaultUriBuilderFactoryTest(){
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        Map<String,Object> defaultUriVariables = new HashMap<>();
        defaultUriVariables.put("schema", "https");
        defaultUriVariables.put("host", "127.0.0.1");
        uriFactory.setDefaultUriVariables(defaultUriVariables);

        String uriTemplate = "{schema}://{host}:8080/abc/{id}/s2/{name}/s3?r=编,码";
        Map<String,String> args = new HashMap();
        args.put("id", "id111");
        args.put("name", "nnn123");
        args.put("user", "zhangsan");
        args.put("promql", "up{level=\"1\"}");

        UriBuilder uriBuilder = uriFactory.uriString(uriTemplate);
        uriBuilder.queryParam("login", "{user}");
        uriBuilder.queryParam("password", "pwd123");
        uriBuilder.queryParam("promql", "{promql}");

        URI ret = uriBuilder.build(args);
        log.info("ret:{}", ret);

    }

}
