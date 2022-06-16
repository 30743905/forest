package org.simon.forest.server.resource;

import lombok.extern.slf4j.Slf4j;
import org.simon.forest.server.bo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserResource {

    private Map<String, User> userMap = new HashMap<>();

    @PostConstruct
    public void init(){
        User user = new User()
                .setId("1")
                .setName("zhangsan")
                .setAge(10)
                .setAddress("四川");

        userMap.put(user.getId(), user);
    }


    @GetMapping("/queryById")
    public User getUser(@RequestParam("id") String id){
        log.info("receive queryById:{}", id);
        return userMap.get(id);
    }

}
