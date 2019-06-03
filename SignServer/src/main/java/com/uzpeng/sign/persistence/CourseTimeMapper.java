package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.CourseTimeDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CourseTimeMapper {
    @InsertProvider(type = CourseTimeProvider.class, method = "insertCourseTimeList")
    @Options(useGeneratedKeys = true)
    void addCourseTimeList(@Param("list")List<CourseTimeDO> c);

    @Update("UPDATE course_time SET flag=1 WHERE course_id=#{id}")
    void updateCourseTimeList(@Param("id") Integer id);

    @Select("SELECT * FROM course_time WHERE course_id=#{id} AND flag=0")
    List<CourseTimeDO> getCourseTimeByCourseId(@Param("id") int courseId);

    @Select("SELECT * FROM course_time WHERE id=#{id}")
    CourseTimeDO getCourseTimeById(@Param("id") Integer courseTimeId);

    @Delete("DELETE FROM course_time WHERE course_id=#{id} ")
    void deleteCourseTime(@Param("id") Integer id);
}
