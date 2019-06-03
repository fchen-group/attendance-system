package com.uzpeng.sign.validation;

import com.uzpeng.sign.util.SessionStoreKey;
import com.uzpeng.sign.web.UserController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthenticatedRequestValidator implements ConstraintValidator<AuthenticatedRequest, HttpServletRequest> {
    @Override
    public boolean isValid(HttpServletRequest request, ConstraintValidatorContext context) {
        String sessionId = (String)request.getSession().getAttribute(SessionStoreKey.KEY_AUTH);

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies){
            if(cookie.getName().equals(SessionStoreKey.KEY_AUTH) && cookie.getValue().equals(sessionId)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(AuthenticatedRequest constraintAnnotation) {

    }
}
