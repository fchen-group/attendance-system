package com.uzpeng.sign.web.dto;

import com.uzpeng.sign.validation.ValidEmail;

import javax.validation.constraints.NotNull;

/**
 */
public class RegisterDTO {
    @NotNull
    private String username;

    @NotNull
    private String password;

    @ValidEmail
    private String email;

    @NotNull
    private String verifyCode;

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
