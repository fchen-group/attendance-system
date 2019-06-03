package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.CourseDO;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface CourseMapper {

    @Insert("INSERT INTO course(course_num, name, semester, teacher_id, start_week, end_week)" +
            "VALUES(#{course.course_num}, #{course.name},#{course.semester},#{course.teacher_id}," +
            "#{course.start_week}, #{course.end_week})")
    @Options(useGeneratedKeys = true, keyProperty = "course.id")
    void insertCourse(@Param("course")CourseDO courseDO);

    @Select("SELECT * FROM course WHERE teacher_id=#{id}")
    List<CourseDO> getCourseByTeacherId(@Param("id") int teacherId);

    @Select("SELECT id FROM course WHERE teacher_id=#{id} AND semester = #{semesterId} ")
    List<Integer> getCourseIdListByTeacherId(@Param("id") int teacherId, @Param("semesterId") int   semesterId);
    /*
    因为新版本并不会传入 semesterID,这里直接不用修改  start_week end_week也直接删除
       其实 teacher_id也可以删除
    @Update("UPDATE course SET course_num=#{course.course_num}, name=#{course.name}, semester=#{course.semester}," +
            "teacher_id=#{course.teacher_id},start_week=#{course.start_week},end_week=#{course.end_week} WHERE id" +
            "=#{course.id}")*/
    @Update("UPDATE course SET course_num=#{course.course_num}, name=#{course.name}," +
            "teacher_id=#{course.teacher_id} WHERE id" +
            "=#{course.id}")
    void updateCourse(@Param("course")CourseDO courseDO);

    @Delete("DELETE FROM course WHERE id = #{id}")
    void deleteCourse(@Param("id")int id);

    @Select("SELECT * FROM course WHERE semester =#{semester}")
    List<CourseDO> getCourseBySemesterId(@Param("semester")int id);

    @Select("SELECT * FROM course WHERE name LIKE #{name}")
    List<CourseDO> getCourseByName(@Param("name") String courseName);

    @Select("SELECT * FROM course WHERE id=#{id}")
    CourseDO getCourseById(Integer id);
}
