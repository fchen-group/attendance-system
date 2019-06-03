package com.uzpeng.sign.bo;

public class SignRecordTimeBO {
    //private Integer courseId;

    private Integer signId;          //签到ID          新

    private Integer week;              //所在周数

    //private Integer courseTimeId;     //上课时间 ID    已废弃
    private String courseTime;        //上课时间        新

    //private String weekday;           //星期几         已废弃

    private String  attendanceRate;   //出勤率         新

    //private Integer amount;           //总人数         已废弃
    private Integer signedAmount;     //已签到人数
    private  Integer notSignAmount;   //未签到人数     新添加

    private String createtime;        //发起时间
    private String endtime;            //结束时间

    private Integer state;              // -1;正在创建状态 1;签到开始状态  0签到结束状态


    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getSignedAmount() {
        return signedAmount;
    }

    public void setSignedAmount(Integer signedAmount) {
        this.signedAmount = signedAmount;
    }

    public Integer getSignId() {
        return signId;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public String getAttendanceRate() {
        return attendanceRate;
    }

    public Integer getNotSignAmount() {
        return notSignAmount;
    }

    public String getCreatetime() {
        return createtime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setSignId(Integer signId) {
        this.signId = signId;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public void setAttendanceRate(String attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public void setNotSignAmount(Integer notSignAmount) {
        this.notSignAmount = notSignAmount;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    @Override
    public String toString() {
        return "SignRecordTimeBO{" +
                ", signId=" + signId +
                ", week=" + week +
                ", courseTime='" + courseTime + '\'' +
                ", attendanceRate='" + attendanceRate + '\'' +
                ", signedAmount=" + signedAmount +
                ", notSignAmount=" + notSignAmount +
                ", createtime='" + createtime + '\'' +
                ", endtime='" + endtime + '\'' +
                '}';
    }
}
