package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.SemesterDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SemesterMapper {
    @Insert("INSERT INTO semester(teacher_id,name,start_time,end_time) VALUES (#{semester.teacher_id}, #{semester.name}, #{semester.start_time}," +
            " #{semester.end_time})")
    void addSemester(@Param("semester") SemesterDO semesterDO);

    @Insert("INSERT INTO semester(teacher_id,name) VALUES (#{semesterDO.teacher_id}, #{semesterDO.name})")
    @Options(useGeneratedKeys = true, keyProperty = "semesterDO.id")
    void addSemesterByName(@Param("semesterDO") SemesterDO semesterDO);

    @Select("SELECT * FROM semester WHERE teacher_id=#{teacher_id}")
    List<SemesterDO> getSemester(@Param("teacher_id") Integer teacherId);

    @Select("SELECT * FROM semester WHERE id=#{id} AND teacher_id=#{teacher_id}")
    SemesterDO getSemesterById(@Param("id") Integer id, @Param("teacher_id") Integer teacherId);

    @Select("SELECT id FROM semester WHERE name LIKE #{name} AND teacher_id=#{teacher_id}")
    Integer  getSemesterIdByName(@Param("name") String name,@Param("teacher_id") Integer teacherId);



    @Update("Update semester SET name=#{semester.name}, start_time=#{semester.start_time}, end_time=#{semester.end_time} " +
            "WHERE id=#{semester.id} AND teacher_id=#{semester.teacher_id}")
    void updateSemester(@Param("semester") SemesterDO semesterDO);

    @Delete("DELETE FROM semester WHERE id=#{id}")
    void deleteSemester(@Param("id") Integer semesterId);
}
