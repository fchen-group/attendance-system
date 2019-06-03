package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.TeacherDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author liguibiao
 */
public interface TeacherMapper {
    @Insert("INSERT INTO teacher(name,teacher_num,college) VALUES (#{teacher.name},#{teacher.teacher_num},#{teacher.college})")
    @Options(useGeneratedKeys = true, keyProperty = "teacher.id")
    void addTeacher(@Param("teacher")TeacherDO teacherDO);

    @Update("UPDATE teacher set name=#{teacher.name},teacher_num=#{teacher.teacher_num} ,"
            + " college = #{teacher.college} WHERE id =#{teacher.id}")
    void updateTeacher(@Param("teacher") TeacherDO teacherDO);

    @Select("SELECT * FROM teacher WHERE id=#{id}")
    TeacherDO getTeacher(@Param("id") int id);

    @Select("SELECT * FROM teacher WHERE teacher_num=#{teacher_num}")
    TeacherDO  getTeacherByTeacherNum(@Param("teacher_num") int teacher_num);


    @Select("SELECT * FROM teacher")
    List<TeacherDO> getTeacherList();

    @Delete("DELETE FROM teacher where id=#{id}")
    void deleteTeacher(@Param("id") int id);


}
