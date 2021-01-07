package com.qfqg_es.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
@Component
/**
 * session拦截器
 * 拦截所有未在SessionConfig中放行的请求
 * 放行static中的静态资源
 * 对其他请求：检查session中是否存有用户信息，判断是否为以登录用户的请求
 *           是则放行，否则返回401
 * */
public class SessionInterceptor implements HandlerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }
    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        //权限路径拦截
        String uri = req.getRequestURI();
        if(uri.contains("/script/")||uri.contains("/bootstrap/")||uri.contains("/syntaxhighlighter/")){
            return true;
        }
        Object object = req.getSession().getAttribute("username");
        logger.info("请求用户名: " + object);
        if (object == null) {
            //logger.info("session is null");
            logger.info(uri);
            res.setStatus(401);
            res.sendRedirect("/user/");
            return false;
        }
        return true;
    }
}
