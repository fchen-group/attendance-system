package com.uzpeng.sign.interceptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CrossSiteInterceptor implements HandlerInterceptor{
    private static final Logger logger = LoggerFactory.getLogger(CrossSiteInterceptor.class);
    @Autowired
    private Environment environment;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("Method is "+request.getMethod()+" add cross site header!");

        String origin1 = environment.getProperty("cross-site.origin1");
        String origin2 = environment.getProperty("cross-site.origin2");
        String myOrigin = request.getHeader("origin");
        if(myOrigin != null && myOrigin.equals(origin1))
            response.setHeader("Access-Control-Allow-Origin",origin1);
        else if(myOrigin != null && myOrigin.equals(origin2))
            response.setHeader("Access-Control-Allow-Origin",origin2);

        //response.setHeader("Access-Control-Allow-Origin", environment.getProperty("cross-site.origin"));
        response.setHeader("Access-Control-Allow-Methods","POST, PUT, GET, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, authorization," +
                "withCredentials, Content-Type, Accept");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials","true");
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(200);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
