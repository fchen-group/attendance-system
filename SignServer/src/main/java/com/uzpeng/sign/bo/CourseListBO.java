package com.uzpeng.sign.bo;

import java.util.List;

/**
 */
public class CourseListBO {
    private List<CourseSemesterBO> currentCourseList;
    private List<CourseSemesterBO> historyCourseList;

    public List<CourseSemesterBO> getCurrentCourseList() {
        return currentCourseList;
    }

    public List<CourseSemesterBO> getHistoryCourseList() {
        return historyCourseList;
    }

    public void setCurrentCourseList(List<CourseSemesterBO> currentCourseList) {
        this.currentCourseList = currentCourseList;
    }

    public void setHistoryCourseList(List<CourseSemesterBO> historyCourseList) {
        this.historyCourseList = historyCourseList;
    }
}
