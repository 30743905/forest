package org.simon.forest.bo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
    private int age;
    private String address;
    private Date birthday;

}
