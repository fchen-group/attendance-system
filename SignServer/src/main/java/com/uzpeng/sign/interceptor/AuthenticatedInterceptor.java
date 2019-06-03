package com.uzpeng.sign.interceptor;

import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.support.SessionAttribute;
import com.uzpeng.sign.util.Role;
import com.uzpeng.sign.util.SessionStoreKey;
import com.uzpeng.sign.util.UserMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticatedInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatedInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //微信学生端，不需要进行验证。。在basic-servlet.xml拦截器直接放行




        if(handler instanceof HandlerMethod) {
            HandlerMethod h = (HandlerMethod)handler;
            logger.info("Method: "+h.getMethod().getName()+"  ---- start checking authentication!");
            //return true;     //部署记得删除 该bool值
        }

        //logger.info("拦截收到的id为:"+request.getSession().getId());
        if(request.getSession().getId() == request.getSession().getAttribute(SessionStoreKey.KEY_SESSION_ID)) {
            //logger.info("是同一个会话session");
            logger.info("authenticate successfully!");
            return true;
        }

        /*SessionAttribute authInfo = (SessionAttribute)request.getSession().getAttribute(SessionStoreKey.KEY_AUTH);
        if(authInfo == null){
            logger.info("no authentication information!");
            response.setStatus(403);
            return false;      //product生产环境
            //return true;     //test 测试环境
        }

        UserDO role = UserMap.getUser((String)authInfo.getObj());
        if(role != null && role.getRole().equals(Role.TEACHER)) {
            logger.info("authenticate successfully!");
            return true;
        }*/



       /* 不需要判断cookies ,因为用户信息是 存在session里面
       Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(SessionStoreKey.KEY_AUTH) && cookie.getValue().equals(authId)) {
                    logger.info("---**---- cookie authenticate successfully!");
                    UserDO role = UserMap.getUser(authId);
                    if(role != null && role.getRole().equals(Role.TEACHER)) {
                        logger.info("---**---- role authenticate successfully!");
                        return true;
                    }
                }
            }
        }*/

        logger.info("no authentication information!");
        response.setStatus(403);
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
