package com.uzpeng.sign.config;

public interface StatusConfig {
    String SUCCESS = "success";
    String FAILED = "failed";

    //教师创建的 签到任务 state
    Integer SIGN_CREATE_FLAG = -1;    //正在创建状态
    Integer SIGN_START_FLAG = 1;      //签到开始状态
    Integer SIGN_FINISH_FLAG = 0;     //签到结束状态

    //每次学生签到的记录state
    Integer RECORD_CREATED = 0;    //签到刚创建  也代表未签到
    //Integer RECORD_SIGNED = 1;     //未签到
    Integer RECORD_FAILED = 2;     //签到失败
    Integer RECORD_SUCCESS = 3;    //签到成功

    Integer HISTORY = 0;
    Integer TODAY = 1;
    Integer ALL = 2;
}
