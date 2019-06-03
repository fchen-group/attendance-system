package com.uzpeng.sign.dao;

import com.uzpeng.sign.domain.SelectiveCourseDO;
import com.uzpeng.sign.persistence.SelectiveCourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SelectiveCourseDAO {
    @Autowired
    private SelectiveCourseMapper mapper;

    public void addSelectiveCourseList(List<SelectiveCourseDO> selectiveCourseDOs){
        mapper.addSelectiveCourseList(selectiveCourseDOs);
    }

    public List<Integer> getStudentIdByCourseId(Integer courseId){
        return mapper.getStudentIdByCourseId(courseId);
    }

    public void removeStudent(Integer courseId, Integer studentId){
        mapper.removeStudent(courseId, studentId);
    }

    public void removeCourse(Integer courseId){
        mapper.deleteByCourseId(courseId);
    }

    public Integer getStudentCount(Integer courseId){
        return mapper.getStudentIdCountByCourseId(courseId);
    }
}
