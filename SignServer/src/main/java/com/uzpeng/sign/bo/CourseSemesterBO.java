package com.uzpeng.sign.bo;

import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CourseSemesterBO implements Comparable<CourseSemesterBO>{

    private Integer semesterId;
    private String semester;
    private List<CourseBO> CourseList;

    //排序测试
    public static void main(String [] args){
        String str1 = "2017春季课程";
        String str2 = "2017秋季课程";
        String str3 = "2016春季课程";
        String str4 = "2016秋季课程";
        CourseSemesterBO c1= new CourseSemesterBO();
        c1.setSemester(str1);
        CourseSemesterBO c2= new CourseSemesterBO();
        c2.setSemester(str2);
        CourseSemesterBO c3= new CourseSemesterBO();
        c3.setSemester(str3);
        CourseSemesterBO c4= new CourseSemesterBO();
        c4.setSemester(str4);

        List<CourseSemesterBO> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        Collections.sort(list);
        for(CourseSemesterBO s:list){
            System.out.println(s);
        }
    }

    /*
       根据semester进行倒叙排序  即2017秋季 2017春季 2016秋季
     */
    @Override
    public int compareTo(CourseSemesterBO c) {
        if(this.getSemester().compareTo(c.getSemester()) > 0){
            return -1;
        }
        if(this.getSemester().compareTo(c.getSemester()) < 0){
            return 1;
        }
        if(this.getSemester().compareTo(c.getSemester()) == 0){
            return 0;
        }
        return 0;
    }


    public Integer getSemesterId() {
        return semesterId;
    }

    public String getSemester() {
        return semester;
    }

    public List<CourseBO> getCourseList() {
        return CourseList;
    }

    public void setSemesterId(Integer semesterId) {
        this.semesterId = semesterId;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setCourseList(List<CourseBO> courseList) {
        CourseList = courseList;
    }

    @Override
    public String toString() {
        return "CourseSemesterBO{" +
                "semesterId=" + semesterId +
                ", semester='" + semester + '\'' +
                ", CourseList=" + CourseList +
                '}';
    }
}
