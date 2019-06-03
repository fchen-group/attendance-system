package com.uzpeng.sign.domain;

import java.time.LocalDateTime;

public class SignDO {
    private Integer id;

    private Integer course_time_id;    //课程上课时间
    private Integer course_id;          //课程ID
    private Integer week;               //第几周
    private LocalDateTime create_time; //创建签到时间

    private LocalDateTime end_time;  //创建签到时间
    private Integer state;            //签到状态

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
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

    public void setCourseId(Integer courseId) {
        this.course_id = courseId;
    }

    public LocalDateTime getCreateTime() {
        return create_time;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.create_time = createTime;
    }

    public Integer getCourseTimeId() {
        return course_time_id;
    }

    public void setCourseTimeId(Integer courseTimeId) {
        this.course_time_id = courseTimeId;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SignDO{" +
                "id=" + id +
                ", course_time_id=" + course_time_id +
                ", course_id=" + course_id +
                ", week=" + week +
                ", create_time=" + create_time +
                ", end_time=" + end_time +
                ", state=" + state +
                '}';
    }
}
