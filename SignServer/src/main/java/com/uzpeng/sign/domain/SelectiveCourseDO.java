package com.uzpeng.sign.domain;

public class SelectiveCourseDO {
    private Integer course_id;
    private Integer student_id;
    private String type;           //未清楚 type什么意思

    public Integer getCourseId() {
        return course_id;
    }

    public void setCourseId(Integer courseId) {
        this.course_id = courseId;
    }

    public Integer getStudentId() {
        return student_id;
    }

    public void setStudentId(Integer studentId) {
        this.student_id = studentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
