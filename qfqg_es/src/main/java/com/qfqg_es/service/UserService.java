package com.qfqg_es.service;


public interface UserService {
    /**
     * 用户注册
     * */
    String register(String username, String password);
    /**
     * 用户登录
     * */
    String login(String username, String password);

}
