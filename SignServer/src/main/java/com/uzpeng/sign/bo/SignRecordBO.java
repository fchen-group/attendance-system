package com.uzpeng.sign.bo;

/**
 */
public class SignRecordBO {
    private Integer id;
    private Integer course_id;
    private String student_num;
    private String name;
    private String class_info;
    private Integer state;      //0，1 签到刚创建  2 签到失败  3 签到成功
    private Integer week;
    private String weekday;
    private String sign_time;

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return course_id;
    }

    public void setCourseId(Integer course_id) {
        this.course_id = course_id;
    }

    public String getStudentNum() {
        return student_num;
    }

    public void setStudentNum(String student_num) {
        this.student_num = student_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClass_info() {
        return class_info;
    }

    public void setClass_info(String class_info) {
        this.class_info = class_info;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getSignTime() {
        return sign_time;
    }

    public void setSignTime(String time) {
        this.sign_time = time;
    }
}
