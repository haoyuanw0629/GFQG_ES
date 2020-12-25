package com.qfqg_es.service;


public interface UserService {
    // 注册
    public String register(String username, String password);
    // 登陆
    public String login(String username, String password);
    //登出
//    public String logout(String username, String password);

}
