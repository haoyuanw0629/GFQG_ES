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

    @GetMapping("/")
    public ModelAndView toLogin(){

        return new ModelAndView("loginPage");
    }
    @GetMapping("/signup")
    public ModelAndView toRegister(){

        return new ModelAndView("registerPage");
    }

    //TODO session
    @PostMapping("/login")
    @ResponseBody
    public LoginResponse login(@RequestBody User user, HttpSession session){
        LoginResponse response = new LoginResponse();
        String str = service.login(user.getUsername(), user.getPassword());
        if(str.equals("successfully login")){
            session.setAttribute("username",user.getUsername());
            session.setMaxInactiveInterval(360);
            logger.info("登陆过期时间： "+session.getMaxInactiveInterval());
            response.setResponseCode(ResponseCode.SUCCESS);
            response.setResponseDesc(str);
            response.setLoginName(user.getUsername());
            return response;
        }
        response.setResponseCode(ResponseCode.PARAMETER_ERROR);
        response.setResponseDesc(str);
        return response;
    }
    @PostMapping("/register")
    @ResponseBody
    public BaseResponse register(@RequestBody User user){
        BaseResponse response = new BaseResponse();
        String str = service.register(user.getUsername(), user.getPassword());
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseDesc(str);
        return response;
    }

}
