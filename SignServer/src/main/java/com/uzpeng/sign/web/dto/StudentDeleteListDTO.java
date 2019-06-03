package com.uzpeng.sign.web.dto;

import java.util.List;

public class StudentDeleteListDTO {

    public List<Integer> getStudents() {
        return students;
    }

    public void setStudents(List<Integer> students) {
        this.students = students;
    }

    private List<Integer> students;

}
