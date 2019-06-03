package com.uzpeng.sign.bo;

public class StudentDetailBO {

    private Integer id;
    private String studentNum;     //学号
    private String name;
    private String classInfo;
    private String signRate;
    private Integer signedNum;
    private Integer notSignedNum;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    public void setSignRate(String signRate) {
        this.signRate = signRate;
    }

    public void setSignedNum(Integer signedNum) {
        this.signedNum = signedNum;
    }

    public void setNotSignedNum(Integer notSignedNum) {
        this.notSignedNum = notSignedNum;
    }

    public Integer getId() {
        return id;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public String getName() {
        return name;
    }

    public String getClassInfo() {
        return classInfo;
    }

    public String getSignRate() {
        return signRate;
    }

    public Integer getSignedNum() {
        return signedNum;
    }

    public Integer getNotSignedNum() {
        return notSignedNum;
    }
}
