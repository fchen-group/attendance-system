package com.uzpeng.sign.web.dto;

import java.util.List;

/**
 */
public class CourseDTO {
    private String courseId;
    private Integer teacherId;             //后端在map中获取添加
    private String courseName;             //课程名
    private String courseNum;              //课程号
    private String semesterId;                         //后端添加
    private Integer startWeek;             //第几周开始    ---已废弃
    private Integer endWeek;               //第几周结束    ---已废弃
    private List<CourseTimeDetailDTO> time;

    private List<StudentDTO> students;       //学生数组     ---新添加

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
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

    public List<CourseTimeDetailDTO> getTime() {
        return time;
    }

    public void setTime(List<CourseTimeDetailDTO> time) {
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
        return semesterId;
    }

    public void setSemester(String semester) {
        this.semesterId = semester;
    }

    @Override
    public String toString() {
        return "CourseDTO{" +
                "courseId='" + courseId + '\'' +
                ", teacherId=" + teacherId +
                ", courseName='" + courseName + '\'' +
                ", courseNum='" + courseNum + '\'' +
                ", semesterId='" + semesterId + '\'' +
                ", startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", time=" + time +
                ", students=" + students +
                '}';
    }
}
