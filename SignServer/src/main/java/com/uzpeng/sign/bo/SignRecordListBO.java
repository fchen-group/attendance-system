package com.uzpeng.sign.bo;

import java.util.List;

/**
 */
public class SignRecordListBO {
    private Integer signId;
    private List<SignRecordBO> list;

    public Integer getSignId() {
        return signId;
    }

    public void setSignId(Integer signId) {
        this.signId = signId;
    }

    public List<SignRecordBO> getList() {
        return list;
    }

    public void setList(List<SignRecordBO> list) {
        this.list = list;
    }
}
