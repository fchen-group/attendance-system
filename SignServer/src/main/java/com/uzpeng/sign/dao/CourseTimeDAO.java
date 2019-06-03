package com.uzpeng.sign.dao;

import com.uzpeng.sign.domain.CourseTimeDO;
import com.uzpeng.sign.persistence.CourseTimeMapper;
import com.uzpeng.sign.web.dto.CourseTimeDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseTimeDAO {
    @Autowired
    private CourseTimeMapper mapper;

    public void addCourseTimeList(List<CourseTimeDO> courseTimeDOList){
        mapper.addCourseTimeList(courseTimeDOList);
    }

    public void updateCourseTimeList(Integer courseId){
        mapper.updateCourseTimeList(courseId);
    }


    public  List<CourseTimeDO> getCourseTimeByCourseId(Integer courseId){

        List<CourseTimeDO> list =  mapper.getCourseTimeByCourseId(courseId);
        List<CourseTimeDO> list1 = new ArrayList<CourseTimeDO>();
        for (CourseTimeDO courseTimeDO:list) {

            int weekday = courseTimeDO.getCourseWeekday();
            courseTimeDO.setCourseWeekday(weekday == 7 ? 0 : weekday);
            list1.add(courseTimeDO);
        }
        return list1;
    }

    public  CourseTimeDO getCourseTimeById(Integer courseTimeId){
        return mapper.getCourseTimeById(courseTimeId);
    }

    public void deleteCourseTime(Integer courseId){
        mapper.deleteCourseTime(courseId);
    }

}
