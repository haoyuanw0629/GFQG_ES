package com.qfqg_es.service;



import com.qfqg_es.model.User;
import com.qfqg_es.repository.UserRepository;
import com.qfqg_es.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserRepository repo;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 用户注册
     * */
    @Override
    public String register(String username, String password) {
        if(username.isEmpty()||password.isEmpty()){
            return "用户名或密码不能为空" ;
        }
        if(password.length()<6){
            return "密码长度不能少于6位";
        }
        User user =new User(username,DigestUtils.md5DigestAsHex(password.getBytes()));
        if(repo.findByUsername(username).isPresent()){
            return "用户名已存在";
        }
        repo.save(user);
        return "注册成功";
    }

    @Override
    /**
     * 用户登录
     * */
    public String login(String username, String password) {
        if(username.isEmpty()||password.isEmpty()) {
            return "用户名或密码不能为空";
        }

        if(!repo.findByUsername(username).isPresent()){
            return "用户名不存在";
        }
        User user =repo.findByUsername(username).get();
        if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
            return "密码错误";
        }
        return "登录成功";
    }
    //获取用户总数量转为账号
    public String getNewID(){
        long id = repo.count()+1;
        String idStr = "";
        if(id<10){
            idStr = "00"+id;
        } else if(id>=10 && id<100){
            idStr = "0"+id;
        } else {
            idStr = String.valueOf(id);
        }
        logger.info(idStr);
        return idStr;
    }
    //登出
//    @Override
//    public String logout(String username, String password) {
//
//    }
}
