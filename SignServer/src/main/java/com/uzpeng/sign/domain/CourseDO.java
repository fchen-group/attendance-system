package com.uzpeng.sign.domain;

public class CourseDO {
    private Integer id;
    private String course_num;
    private String name;
    private Integer semester;      //学期ID
    private Integer teacher_id;
    private Integer credit;
    private Integer amount;
    private Integer start_week;    //已废弃
    private Integer end_week;       //已废弃

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseNum() {
        return course_num;
    }

    public void setCourseNum(String courseNum) {
        this.course_num = courseNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getTeacherId() {
        return teacher_id;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacher_id = teacherId;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getStartWeek() {
        return start_week;
    }

    public void setStartWeek(Integer startWeek) {
        this.start_week = startWeek;
    }

    public Integer getEndWeek() {
        return end_week;
    }

    public void setEndWeek(Integer endWeek) {
        this.end_week = endWeek;
    }

    @Override
    public String toString() {
        return "CourseDO{" +
                "id=" + id +
                ", course_num='" + course_num + '\'' +
                ", name='" + name + '\'' +
                ", semester=" + semester +
                ", teacher_id=" + teacher_id +
                ", credit=" + credit +
                ", amount=" + amount +
                ", start_week=" + start_week +
                ", end_week=" + end_week +
                '}';
    }
}
