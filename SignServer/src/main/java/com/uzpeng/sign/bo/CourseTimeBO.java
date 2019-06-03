package com.uzpeng.sign.bo;

/**
 */
public class CourseTimeBO {
    private Integer id;
    private Integer courseId;
    private Integer courseWeekday;
    private Integer courseSectionStart;
    private Integer courseSectionEnd;
    private String  loc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getCourseWeekday() {
        return courseWeekday;
    }

    public void setCourseWeekday(Integer courseWeekday) {
        this.courseWeekday = courseWeekday;
    }

    public Integer getCourseSectionStart() {
        return courseSectionStart;
    }

    public void setCourseSectionStart(Integer courseSectionStart) {
        this.courseSectionStart = courseSectionStart;
    }

    public Integer getCourseSectionEnd() {
        return courseSectionEnd;
    }

    public void setCourseSectionEnd(Integer courseSectionEnd) {
        this.courseSectionEnd = courseSectionEnd;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
