package org.simon.forest.server.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
    private int age;
    private String address;
    private String birthday;
}
