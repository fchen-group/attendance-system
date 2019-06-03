package com.uzpeng.sign.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class NoAuthenticatedException extends BaseException {
    @Autowired
    private Environment env;

    @Override
    public int getStatus() {
        return HttpStatus.FORBIDDEN.value();
    }

    @Override
    public String getMsg() {
        return env.getProperty("exception.noAuthentication");
    }
}
