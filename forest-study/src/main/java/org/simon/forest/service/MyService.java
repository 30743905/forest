package org.simon.forest.service;

import com.dtflys.forest.Forest;
import lombok.extern.slf4j.Slf4j;
import org.simon.forest.bo.User;
import org.simon.forest.client.MyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MyService {

    @Autowired
    private MyClient myClient;

    public void testClient() {
        User result = myClient.helloForest();
        log.info("client result:{}", result);
    }

    public void testClient2(){
        // 实例化Forest请求接口
        MyClient myClient = Forest.client(MyClient.class);
        // 调用Forest请求接口，并获取响应返回结果
        User result = myClient.helloForest();
        // 打印响应结果
        log.info("client2 result:{}", result);
    }

    public void testClient3(){
        User result = Forest.get("http://localhost:9090/user/queryById?id=1").execute(User.class);
        log.info("client3 result:{}", result);

        User ret = Forest.get("/user/queryById")
                .backend("okhttp3")        // 设置后端为 okhttp3
                .host("127.0.0.1")         // 设置地址的host为 127.0.0.1
                .port(9090)
                .contentTypeJson()         // 设置 Content-Type 头为 application/json
                .addQuery("id", "1")
                .addQuery("other", "hahah")
                //.addBody("a", 1)           // 添加 Body 项(键值对)： a, 1
                //.addBody("b", 2)           // 添加 Body 项(键值对：  b, 2
                .maxRetryCount(3)          // 设置请求最大重试次数为 3
                // 设置 onSuccess 回调函数
                .onSuccess((data, req, res) -> {
                    log.info("client3--> result:{}", res);
                })
                // 设置 onError 回调函数
                .onError((ex, req, res) -> {
                    log.info("error!");
                })
                // 设置请求成功判断条件回调函数
                .successWhen((req, res) -> res.noException() && res.statusOk())
                // 执行并返回Map数据类型对象
                .execute(User.class);




    }

}