package com.qfqg_es.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="gf_user")
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

