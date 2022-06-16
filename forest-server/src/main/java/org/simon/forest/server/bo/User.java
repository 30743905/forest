package org.simon.forest.server.bo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
    private int age;
    private String address;
}
