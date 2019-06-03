package com.uzpeng.sign.web.dto;

/**
 */
public class SignRecordDTO {

    private String openId;  //openid同一用户同一应用唯一，unionid同一用户不同应用唯一
    private String signId;  //该次签到的ID
    private Double longitude;
    private Double latitude;
    private Double accuracy;

    private String device_no;


    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
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

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public String getDevice_no() {
        return device_no;
    }

    public void setDevice_no(String device_no) {
        this.device_no = device_no;
    }

    @Override
    public String toString() {
        return "SignRecordDTO{" +
                "openId='" + openId + '\'' +
                ", signId='" + signId + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", accuracy=" + accuracy +
                ", device_no='" + device_no + '\'' +
                '}';
    }
}
