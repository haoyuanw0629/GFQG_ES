package com.qfqg_es.controller;


import com.qfqg_es.helper.ResponseCode;
import com.qfqg_es.model.User;
import com.qfqg_es.response.BaseResponse;
import com.qfqg_es.response.LoginResponse;
import com.qfqg_es.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
@CrossOrigin(allowedHeaders = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl service;

    //返回登录页面
    @GetMapping("/")
    public ModelAndView toLogin(){
        return new ModelAndView("loginPage");
    }
    //返回注册页面
    @GetMapping("/signup")
    public ModelAndView toRegister(){
        return new ModelAndView("registerPage");
    }

    /**
    * 用户登录
    * @param： user：user对象
    * @param： session：当前session
    * @return：UserResponse对象
    * */
    @PostMapping("/login")
    @ResponseBody
    public LoginResponse login(@RequestBody User user, HttpSession session){
        LoginResponse response = new LoginResponse();
        //调用用户服务验证用户名以及密码，以字符串的形式返回登录结果
        String str = service.login(user.getUsername(), user.getPassword());
        //如果登录成功，设置HttpSession
        if(str.equals("登录成功")){
            //为session新建一个名为"username"的属性，其值为用户名
            session.setAttribute("username",user.getUsername());
            //设置session过期时间
            session.setMaxInactiveInterval(360);
            logger.info("登陆过期时间： "+session.getMaxInactiveInterval());
            //封装response对象并返回
            response.setResponseCode(ResponseCode.SUCCESS);
            response.setResponseDesc(str);
            response.setLoginName(user.getUsername());
            return response;
        }
        //以下为登录失败的操作，返回失败信息让前端进行后续跳转操作
        response.setResponseCode(ResponseCode.PARAMETER_ERROR);
        response.setResponseDesc(str);
        return response;
    }
    //新用户注册
    @PostMapping("/register")
    @ResponseBody
    public BaseResponse register(@RequestBody User user){
        BaseResponse response = new BaseResponse();
        String str = service.register(user.getUsername(), user.getPassword());
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseDesc(str);
        return response;
    }
    //获取用户ID
    @GetMapping("/newid")
    @ResponseBody
    public LoginResponse getNewID(){
        LoginResponse res = new LoginResponse();
        res.setResponseCode(ResponseCode.SUCCESS);
        res.setUserId(service.getNewID());
        return res;
    }
    //用户退出，删除session中的用户信息
    @GetMapping("/logout")
    @ResponseBody
    public BaseResponse logout(HttpSession session){
        session.removeAttribute("username");
        BaseResponse res = new BaseResponse();
        res.setResponseCode(ResponseCode.SUCCESS);
        return res;
    }

}
