package com.uzpeng.sign.bo;

public class StudentBO {
    private Integer id;
    private String studentNum;     //学号
    private String name;
    private String classInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
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

    @Override
    public String toString() {
        return "StudentBO{" +
                "id=" + id +
                ", studentNum='" + studentNum + '\'' +
                ", name='" + name + '\'' +
                ", classInfo='" + classInfo + '\'' +
                '}';
    }
}
