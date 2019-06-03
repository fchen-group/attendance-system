package com.uzpeng.sign.web.dto;

/**
 */
public class wxLoginDTO {
    private String username;
    private String password;
    private String openId;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String toString() {
        return "wxLoginDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", openId='" + openId + '\'' +
                '}';
    }
}
