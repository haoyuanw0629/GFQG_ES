package com.qfqg_es.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
@Component
//就是拦截器
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
        logger.info("==========拦截请求==========");
        //权限路径拦截
        String uri = req.getRequestURI();
        if(uri.contains("/script/")||uri.contains("/bootstrap/")||uri.contains("/syntaxhighlighter/")){
            logger.info("==========放行静态资源==========");
            return true;
        }
        Object object = req.getSession().getAttribute("username");
        logger.info("Session 用户名: " + object);
        if (object == null) {
            //logger.info("session is null");
            logger.info(uri);
            logger.info("==========跳转==========");
            res.setStatus(401);
            res.sendRedirect("/user/");
            return false;
        }
        return true;
    }
}
