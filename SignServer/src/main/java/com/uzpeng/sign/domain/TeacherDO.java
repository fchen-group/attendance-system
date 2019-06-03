package com.uzpeng.sign.domain;

public class TeacherDO {
    private Integer id;
    private String name;

    private String teacher_num;
    private String college;

    /*  以后根据前端页面选择添加
    private String tel_number;
    private String office_hour;
    private String office_loc;
    private String note;
    */

    public String getTeacher_num() {
        return teacher_num;
    }

    public void setTeacher_num(String teacher_num) {
        this.teacher_num = teacher_num;
    }


    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TeacherDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacher_num='" + teacher_num + '\'' +
                ", college='" + college + '\'' +
                '}';
    }
}
