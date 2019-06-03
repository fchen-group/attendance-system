package com.uzpeng.sign.web.dto;


public class CourseTimeDetailDTO {
    private Integer id;
    private Integer weekday;   //星期几
    private Integer start;    //第几节开始
    private Integer end;       //第几节结束
    private String loc;       //上课地点

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "CourseTimeDetailDTO{" +
                "id=" + id +
                ", weekday=" + weekday +
                ", start=" + start +
                ", end=" + end +
                ", loc='" + loc + '\'' +
                '}';
    }
}
