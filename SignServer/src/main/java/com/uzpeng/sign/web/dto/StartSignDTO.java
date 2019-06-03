package com.uzpeng.sign.web.dto;

/**
 */
public class StartSignDTO {


    private Integer state;    // -1正在创建状态 1 签到开始状态  0签到结束状态


    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }

}
