package com.uzpeng.sign.domain;

import java.time.LocalDateTime;

public class UserDO {
    private  int id;
    private String name;
    private String password;
    private String email;
    private LocalDateTime register_time;
    private int role_id;
    private String role;
    private String open_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getRegisterTime() {
        return register_time;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.register_time = registerTime;
    }

    public int getRoleId() {
        return role_id;
    }

    public void setRoleId(int roleId) {
        this.role_id = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getOpenId() {
        return open_id;
    }

    public void setOpenId(String open_id) {
        this.open_id = open_id;
    }
}
