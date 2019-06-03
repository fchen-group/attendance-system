package com.uzpeng.sign.net.dto;

/**
 */
public class SignDTO {
    private Integer start;
    private Integer time;
    private Integer week;
    private Integer courseId;
    private Integer signId;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getSignId() {
        return signId;
    }

    public void setSignId(Integer signId) {
        this.signId = signId;
    }

    @Override
    public String toString() {
        return "SignDTO{" +
                "start=" + start +
                ", time=" + time +
                ", week=" + week +
                ", courseId=" + courseId +
                ", signId=" + signId +
                '}';
    }
}
