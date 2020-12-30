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
