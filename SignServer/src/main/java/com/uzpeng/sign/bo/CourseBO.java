package com.uzpeng.sign.bo;

import java.util.List;

/**
 */
public class CourseBO {
    private Integer courseId;
    private Integer teacherId;
    private String courseName;
    private Integer semesterId;     //已废弃，在外面显示
    private String courseNum;
    private String semester;        //已废弃，在外面显示
    private Integer startWeek;      //已废弃
    private Integer endWeek;         //已废弃
    private Integer studentAmount;
    private List<CourseTimeDetailBO> time;

    public Integer getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Integer semesterId) {
        this.semesterId = semesterId;
    }

    public Integer getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }

    public Integer getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(Integer endWeek) {
        this.endWeek = endWeek;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public List<CourseTimeDetailBO> getTime() {
        return time;
    }

    public void setTime(List<CourseTimeDetailBO> time) {
        this.time = time;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getStudentAmount() {
        return studentAmount;
    }

    public void setStudentAmount(Integer studentAmount) {
        this.studentAmount = studentAmount;
    }

    @Override
    public String toString() {
        return "CourseBO{" +
                "courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", courseName='" + courseName + '\'' +
                ", semesterId=" + semesterId +
                ", courseNum='" + courseNum + '\'' +
                ", semester='" + semester + '\'' +
                ", startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", studentAmount=" + studentAmount +
                ", time=" + time +
                '}';
    }
}
