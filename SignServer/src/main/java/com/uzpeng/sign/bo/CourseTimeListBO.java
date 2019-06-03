package com.uzpeng.sign.bo;

import java.util.List;

/**
 */
public class CourseTimeListBO {
    private Integer startWeek;
    private Integer endWeek;
    private List<CourseTimeBO> list;

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

    public List<CourseTimeBO> getList() {
        return list;
    }

    public void setList(List<CourseTimeBO> list) {
        this.list = list;
    }
}
