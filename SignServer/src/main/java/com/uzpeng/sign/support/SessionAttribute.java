package com.uzpeng.sign.support;

import java.time.LocalDateTime;

public class SessionAttribute {
    private Object obj;
    private LocalDateTime expireDate;

    public SessionAttribute(Object obj, LocalDateTime expireDate) {
        this.obj = obj;
        this.expireDate = expireDate;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }
}
