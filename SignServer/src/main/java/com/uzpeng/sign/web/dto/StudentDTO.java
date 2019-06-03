package com.uzpeng.sign.web.dto;

/**
 */
public class StudentDTO {

    private String courseId;    //可能写错，但不会用到
    private String name;
    private String classInfo;
    private String studentNum;            //学号

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }


    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "courseId='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", classInfo='" + classInfo + '\'' +
                ", studentNum='" + studentNum + '\'' +
                '}';
    }
}
