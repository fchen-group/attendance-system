package com.uzpeng.sign.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DuplicateDataException extends BaseException {
    @Autowired
    private Environment environment;

    @Override
    public int getStatus() {
        return HttpStatus.CONFLICT.value();
    }

    @Override
    public String getMsg() {
        return environment.getProperty("exception.duplicateData");
    }

    @Override
    public String getMessage() {
        return getMsg();
    }
}
