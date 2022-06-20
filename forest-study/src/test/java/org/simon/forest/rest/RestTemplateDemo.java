package org.simon.forest.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.simon.forest.bo.User;
import org.simon.forest.bo.User2;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class RestTemplateDemo {

    public static void main2(String[] args) {
        /**
         * 20.06.2022 05:36:33
         * 06/20/2022 5:36:33 下午
         */
        System.out.println(new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(new Date()));
        System.out.println(new SimpleDateFormat("MM/dd/yyyy K:mm:ss a").format(new Date()));
    }

    public static void main(String[] args) {
        RestTemplate restT = new RestTemplate();

        ObjectMapper objectMapper1 = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                .configure(SerializationFeature.INDENT_OUTPUT, false)
                .setDateFormat(new SimpleDateFormat("dd.MM.yyyy hh:mm:ss"));

        ObjectMapper objectMapper2 = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                .configure(SerializationFeature.INDENT_OUTPUT, false)
                .setDateFormat(new SimpleDateFormat("MM/dd/yyyy K:mm:ss a"));

        restT.getMessageConverters().forEach(x -> {
            if(MappingJackson2HttpMessageConverter.class.isAssignableFrom(x.getClass())){
                ((MappingJackson2HttpMessageConverter) x).registerObjectMappersForType(User.class, m -> m.put(MediaType.APPLICATION_JSON, objectMapper1));
                ((MappingJackson2HttpMessageConverter) x).registerObjectMappersForType(User2.class, m -> m.put(MediaType.APPLICATION_XML, objectMapper2));
            }
        });


        restT.getClientHttpRequestInitializers().add(new ClientHttpRequestInitializer() {
            @Override
            public void initialize(ClientHttpRequest request) {
                log.info("--->header:{}", request.getHeaders());
            }
        });




        String uri1 = "http://localhost:9090/user/queryById?id=1";
        String uri2 = "http://localhost:9090/user/testzero?num=0";

        //通过Jackson JSON processing library直接将返回值绑定到对象
        User2 user = restT.getForObject(uri1, User2.class);
        log.info("user:{}", user);
        String userString = restT.getForObject(uri1, String.class);
        log.info("userString:{}", userString);


//        Integer ret = restT.getForObject(uri2, Integer.class);
//        log.info("ret:{}", ret);



    }

}
