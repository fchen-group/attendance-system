package com.uzpeng.sign.exception;

import org.springframework.stereotype.Component;
@Component
public class BaseException extends Exception {
    private int  status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
