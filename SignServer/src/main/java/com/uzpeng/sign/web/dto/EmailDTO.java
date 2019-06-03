package com.uzpeng.sign.web.dto;

import com.uzpeng.sign.validation.ValidEmail;

import javax.validation.constraints.NotNull;

/**
 */
@ValidEmail
public class EmailDTO {
    @ValidEmail
    @NotNull
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
