package com.uzpeng.sign.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentSignRecordBO implements Comparable<StudentSignRecordBO>{
                                     //学生信息
    private String studentName;
    private String  studentNum;
    private String  classInfo;    //班级信息

    private Integer courseId;      //课程信息
    private String  courseNum;

    private String course;

    private String teacher;
    private String loc;

    private Integer signId;

    private String time;           //第6周星期三
    private Integer state;
    //新添加

    private String createtime;         //发起时间
    private String endtime;            //结束时间
    public Integer getSignId() {
        return signId;
    }

    public void setSignId(Integer signId) {
        this.signId = signId;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public static void main(String [] args){
        String str1 = "2018-11-21 21:34:01";
        String str2 = "2018-11-25 12:36:28";
        String str3 = "2018-11-24 12:36:28";
        String str4 = "2018-11-25 12:39:28";
        StudentSignRecordBO c1= new StudentSignRecordBO();
        c1.setCreatetime(str1);
        StudentSignRecordBO c2= new StudentSignRecordBO();
        c2.setCreatetime(str2);
        StudentSignRecordBO c3= new StudentSignRecordBO();
        c3.setCreatetime(str3);
        StudentSignRecordBO c4= new StudentSignRecordBO();
        c4.setCreatetime(str4);

        List<StudentSignRecordBO> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        Collections.sort(list);
        for(StudentSignRecordBO s:list){
            System.out.println(s);
        }
    }

    @Override
    public int compareTo(StudentSignRecordBO c) {
        if(this.getCreatetime().compareTo(c.getCreatetime()) > 0){
            return -1;
        }
        if(this.getCreatetime().compareTo(c.getCreatetime()) < 0){
            return 1;
        }
        if(this.getCreatetime().compareTo(c.getCreatetime()) == 0){
            return 0;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "StudentSignRecordBO{" +
                "studentName='" + studentName + '\'' +
                ", studentNum='" + studentNum + '\'' +
                ", classInfo='" + classInfo + '\'' +
                ", courseId=" + courseId +
                ", courseNum='" + courseNum + '\'' +
                ", course='" + course + '\'' +
                ", teacher='" + teacher + '\'' +
                ", loc='" + loc + '\'' +
                ", signId=" + signId +
                ", time='" + time + '\'' +
                ", state=" + state +
                ", createtime='" + createtime + '\'' +
                ", endtime='" + endtime + '\'' +
                '}';
    }
}
