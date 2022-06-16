package org.simon.forest;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.simon.forest.bo.User;
import org.simon.forest.client.MyClient;
import org.simon.forest.service.MyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ForestScan(basePackages = "org.simon.forest.client")
public class MyApp {
    public static void main(String[] args) {
     ConfigurableApplicationContext context = SpringApplication.run(MyApp.class, args);
     MyService service = context.getBean(MyService.class);
     service.testClient3();
    }
}
