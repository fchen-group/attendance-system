package com.uzpeng.sign.bo;

import com.uzpeng.sign.domain.SemesterDO;

import java.util.List;

public class SemesterDOList {
    private List<SemesterDO> semesterList;

    public void setSemesterList(List<SemesterDO> semesterList) {
        this.semesterList = semesterList;
    }

    public List<SemesterDO> getSemesterList() {
        return semesterList;
    }

    @Override
    public String toString() {
        return "SemesterDOList{" +
                "semesterList=" + semesterList +
                '}';
    }
}
