package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.SelectiveCourseDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SelectiveCourseMapper {
    @InsertProvider(type =SelectiveCourseProvider.class, method = "addSelectiveCourseList")
    void addSelectiveCourseList(@Param("list")List<SelectiveCourseDO> list);

    @Select("SELECT student_id FROM  selective_course WHERE course_id=#{course_id}")
    List<Integer> getStudentIdByCourseId(@Param("course_id") Integer courseId);

    @Delete("DELETE FROM selective_course WHERE course_id=#{course_id}")
    void deleteByCourseId(@Param("course_id") Integer courseId);

    @Delete("DELETE FROM selective_course WHERE student_id=#{student_id} AND course_id=#{course_id}")
    void removeStudent(@Param("course_id") Integer courseId, @Param("student_id") Integer studentId);

    @Select("SELECT COUNT(*) FROM  selective_course WHERE course_id=#{course_id}")
    Integer getStudentIdCountByCourseId(@Param("course_id") Integer courseId);
}
