package com.uzpeng.sign.domain;

public class CourseTimeDO {
    private Integer id;                         //上课时间ID
    private Integer course_id;                 //课程ID
    private Integer course_weekday;           //周几
    private Integer course_section_start;
    private Integer course_section_end;
    private String  loc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return course_id;
    }

    public void setCourseId(Integer courseId) {
        this.course_id = courseId;
    }

    public Integer getCourseWeekday() {
        return course_weekday;
    }

    public void setCourseWeekday(Integer courseWeekday) {
        this.course_weekday = courseWeekday;
    }

    public Integer getCourseSectionStart() {
        return course_section_start;
    }

    public void setCourseSectionStart(Integer courseSectionStart) {
        this.course_section_start = courseSectionStart;
    }

    public Integer getCourseSectionEnd() {
        return course_section_end;
    }

    public void setCourseSectionEnd(Integer courseSectionEnd) {
        this.course_section_end = courseSectionEnd;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }


    @Override
    public String toString() {
        return "CourseTimeDO{" +
                "id=" + id +
                ", course_id=" + course_id +
                ", course_weekday=" + course_weekday +
                ", course_section_start=" + course_section_start +
                ", course_section_end=" + course_section_end +
                ", loc='" + loc + '\'' +
                '}';
    }
}
