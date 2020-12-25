package com.qfqg_es.service;



import com.qfqg_es.model.User;
import com.qfqg_es.repository.UserRepository;
import com.qfqg_es.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserRepository repo;

    @Override
    //注册
    public String register(String username, String password) {
        if(username.isEmpty()||password.isEmpty()){
            // TODO
            return "username and password cannot be null" ;
        }
        //TODO validation

        User user =new User(username,DigestUtils.md5DigestAsHex(password.getBytes()));
        if(repo.findByUsername(username).isPresent()){
            return "username already exist, please rename";
        }
        repo.save(user);
        return "successfully registered";
    }

    @Override
    //登陆
    public String login(String username, String password) {
        if(username.isEmpty()||password.isEmpty()) {
            // TODO
            return "username and password cannot be null";
        }

        if(!repo.findByUsername(username).isPresent()){
            return "username not exists";
        }
        User user =repo.findByUsername(username).get();
        if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
            return "wrong password, please retype";
        }
        return "successfully login";
    }
    //登出
//    @Override
//    public String logout(String username, String password) {
//
//    }
}
