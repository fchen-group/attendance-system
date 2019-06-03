package com.uzpeng.sign.domain;

public class StudentDO {
    private Integer id;
    private String student_num;
    private String name;
    private String class_info;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNum() {
        return student_num;
    }

    public void setNum(String num) {
        this.student_num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassInfo() {
        return class_info;
    }

    public void setClassInfo(String classInfo) {
        this.class_info = classInfo;
    }
}
