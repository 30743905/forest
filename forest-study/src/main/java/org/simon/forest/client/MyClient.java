package org.simon.forest.client;

import com.dtflys.forest.annotation.Request;
import org.simon.forest.bo.User;

public interface MyClient {

    @Request("http://localhost:9090/user/queryById?id=1")
    User helloForest();

}
