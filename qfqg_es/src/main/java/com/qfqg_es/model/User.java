package com.qfqg_es.model;

import com.qfqg_es.param.Param;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name= Param.USER_TABLE)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;
    private String username;
    private String password;
    //private int phonenumber;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        //this.phonenumber=phonenumber;

    }

}

