package com.uzpeng.sign.domain;

import java.time.LocalDateTime;
import java.util.*;

/**
 *  表示微信端 每个学生签到记录
 */
public class SignRecordDO {




    private Integer id;

    private Integer course_sign_id;
    private Integer student_id;
    private Double longitude;   //经度  113
    private Double latitude;    //维度   22
    private Integer state;       //RECORD_FAILED SUCCESS 0 1 2 3

    private Double accuracy;
    private String device_no;
    private LocalDateTime sign_time;


    //使用有序集合对对象内容进行封装
    public LinkedList<String> getLinkList(){
        LinkedList<String> ll = new LinkedList<String>();
        ll.add(id+"");
        ll.add(course_sign_id+"");
        ll.add(student_id+"");
        ll.add(String.valueOf(longitude));
        ll.add(latitude+"");
        ll.add(state+"");
        ll.add(accuracy+"");
        ll.add(device_no);
        ll.add(sign_time+"");
        return ll;

    }

    public Integer getSignId() {
        return course_sign_id;
    }

    public void setSignId(Integer sign_id) {
        this.course_sign_id = sign_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }


    public String getDeviceNo() {
        return device_no;
    }

    public void setDeviceNo(String device_no) {
        this.device_no = device_no;
    }

    public LocalDateTime getSignTime() {
        return sign_time;
    }

    public void setSignTime(LocalDateTime time) {
        this.sign_time = time;
    }


    public Integer getStudentId() {
        return student_id;
    }

    public void setStudentId(Integer student_id) {
        this.student_id = student_id;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getCourse_sign_id() {
        return course_sign_id;
    }

    public void setCourse_sign_id(Integer course_sign_id) {
        this.course_sign_id = course_sign_id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public String getDevice_no() {
        return device_no;
    }

    public void setDevice_no(String device_no) {
        this.device_no = device_no;
    }

    public String getSignRecordDO() {
        return "SignRecordDO{" +
                "id=" + id +
                ", course_sign_id=" + course_sign_id +
                ", student_id=" + student_id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", state=" + state +
                '}';
    }

    @Override
    public String toString() {
        return "SignRecordDO{" +
                "id=" + id +
                ", course_sign_id=" + course_sign_id +
                ", student_id=" + student_id +
                ", state=" + state +
                '}';
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignRecordDO that = (SignRecordDO) o;
        return Objects.equals(longitude, that.longitude) &&
                Objects.equals(latitude, that.latitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }*/
}
