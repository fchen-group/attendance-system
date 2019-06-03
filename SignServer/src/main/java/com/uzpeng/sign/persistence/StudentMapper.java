package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.CourseDO;
import com.uzpeng.sign.domain.StudentDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface StudentMapper {
    @InsertProvider(type = StudentProvider.class, method = "insertAll")
    @Options(useGeneratedKeys=true)
    void insertStudentList(@Param("list")List<StudentDO> students);

    @Insert("INSERT INTO student(name, student_num) VALUES(#{student.name}, #{student.num})")
    void insertStudent(@Param("student") StudentDO studentDO);

    @Select("SELECT * FROM  student WHERE id=#{id}")
    StudentDO getStudent(@Param("id") int id);

    @SelectProvider(type = StudentProvider.class, method = "getIdByNum")
    List<Integer> getStudentIdByNum(@Param("list") List<String> num);

    @SelectProvider(type = StudentProvider.class, method = "getStudentsByNum")
    List<StudentDO> getStudentsByNum(@Param("list") List<String> num);

    @Select("SELECT student_num FROM student")
    List<String> getStudentNum();

    @SelectProvider(type = StudentProvider.class, method = "getStudentListById")
    List<StudentDO> getStudentListByStudentId(@Param("list") List<Integer> studentId);

    @Select("SELECT * FROM student WHERE student_num like #{num}")
    List<StudentDO> getStudentListByStudentNum(@Param("num") String studentNum);

   //修改学生信息
    @Update("UPDATE student SET student_num=#{student.student_num}, name=#{student.name}," +
            "class_info=#{student.class_info} WHERE id" +
            "=#{student.id}")
    void updateStudent(@Param("student") StudentDO studentDO);



}
