package com.uzpeng.sign.web.dto;

/**
 */
public class CreateSignRecordDTO {
    private Integer courseTimeId;     //课程时间ID
    private Integer week;              //第几周

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getCourseTimeId() {
        return courseTimeId;
    }

    public void setCourseTimeId(Integer courseTimeId) {
        this.courseTimeId = courseTimeId;
    }
}
