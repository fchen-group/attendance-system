package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.web.dto.SignRecordDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SignRecordMapper {

    @InsertProvider(type = SignRecordProvider.class, method = "insertAll")
    void insertSignRecordList(@Param("list")List<SignRecordDO> signRecordDOS);

    @UpdateProvider(type = SignRecordProvider.class, method = "updateAll")
    void updateSignRecord(@Param("list")List<SignRecordDO> signRecordDO);

    @UpdateProvider(type = SignRecordProvider.class, method = "updateEvaluateAll")
    void updateEvaluateSignRecord(@Param("list")List<SignRecordDO> signRecordDO);

    @Update("UPDATE course_sign_record SET longitude=#{sign.longitude}, latitude=#{sign.latitude}," +
            "accuracy=#{sign.accuracy}, state=#{state} , device_no=#{sign.device_no}"+
            "WHERE course_sign_id=#{sign.signId} AND student_id=#{studentId}")
    void sign(@Param("sign") SignRecordDTO signRecordDTO, @Param("studentId")Integer studentId,
              @Param("state") Integer state);
    //好像时间是自动更新的

    @Select("SELECT * FROM course_sign_record WHERE course_sign_id=#{id}")
    List<SignRecordDO> getSignRecord(@Param("id") Integer signId);

    @Select("SELECT * FROM course_sign_record WHERE student_id=#{id}")
    List<SignRecordDO> getSignRecordByStudentId(@Param("id") Integer studentId);

    @SelectProvider(type = SignRecordProvider.class, method = "getBySignIds")
    List<SignRecordDO> getSignRecordBySignId(@Param("list") List<Integer> signIds);

    @Select("SELECT week FROM course_sign_record WHERE course_id=#{courseId} GROUP BY week")
    List<Integer> getWeeks(@Param("courseId") Integer courseId);

    @Select("SELECT COUNT(*) FROM course_sign_record WHERE course_sign_id=#{signId} AND state=#{state}")
    Integer getSignCountBySignId(@Param("signId") Integer signId, @Param("state")Integer state);

    //获取当次 该学生 签到记录
    //SELECT COUNT(*) FROM course_sign_record WHERE course_sign_id=3 AND state=4 AND student_id = 12
    @Select("SELECT COUNT(*) FROM course_sign_record WHERE course_sign_id=#{signId} AND student_id = #{student_id} AND state=#{state}")
    Integer getSignCount(@Param("signId") Integer signId, @Param("state")Integer state,@Param("student_id")Integer student_id);

    @DeleteProvider(type = SignRecordProvider.class, method = "deleteBySignIds")
    void deleteBySignIdList(@Param("list") List<Integer> signIds);

    @DeleteProvider(type = SignRecordProvider.class, method = "deleteBySignIdsAndStudentId")
    void deleteBySignIdListAndStudentId(@Param("list") List<Integer> signIds, @Param("studentId") Integer studentId);

    @Delete("delete From course_sign_record WHERE course_sign_id = #{signId}")
    void deleteSignRecordBySignId(@Param("signId") Integer signId);

    //----------------------分析异常数据所用sql语句-------
    //@Select("SELECT * FROM course_sign_record WHERE course_sign_id=#{id} AND latitude is not null AND longitude is not null")
    //List<SignRecordDO> getSignedRecord(@Param("id") Integer id);
    //用于判别分析此次 签到数据
    @Select("SELECT * FROM course_sign_record WHERE course_sign_id=#{course_sign_id} AND state = 3 AND latitude is not null AND longitude is not null")
    List<SignRecordDO> getSignedRecord(@Param("course_sign_id") Integer course_sign_id);


    //Evaluate_Sign_Record额外异常分析表
    @InsertProvider(type = SignRecordProvider.class, method = "insertAllEvaluate")
    void insertEvaluateSignRecordList(@Param("list")List<SignRecordDO> signRecordDOS);

    @Select("SELECT * FROM evaluate_sign_record WHERE course_sign_id=#{id}")
    List<SignRecordDO> getEvaluateSignRecord(@Param("id") Integer signId);

}
