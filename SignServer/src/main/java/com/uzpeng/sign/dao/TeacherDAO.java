package com.uzpeng.sign.dao;

import com.uzpeng.sign.bo.TeacherBO;
import com.uzpeng.sign.bo.TeacherBOList;
import com.uzpeng.sign.domain.StudentDO;
import com.uzpeng.sign.domain.TeacherDO;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.persistence.TeacherMapper;
import com.uzpeng.sign.util.CryptoUtil;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.util.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public class TeacherDAO {

    private static final Logger logger = LoggerFactory.getLogger(TeacherDAO.class);
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private UserDAO userDAO;

    public void addTeacher(TeacherDO teacherDO,String password){

        teacherMapper.addTeacher(teacherDO);

        logger.info("添加老师后，返回的教师ID为:"+teacherDO.getId());

        UserDO userDO = new UserDO();
        userDO.setName(String.valueOf(teacherDO.getTeacher_num()));
        userDO.setPassword(CryptoUtil.encodePassword(password));
        userDO.setRole(Role.TEACHER);
        userDO.setRoleId(teacherDO.getId());
        userDO.setRegisterTime(LocalDateTime.now());

        userDAO.insertUser(userDO);
        logger.info("添加教师，以及注册用户成功...");
    }

    public void deleteTeacher(Integer teacherId){

        //先删除user表中用户 根据techerId查出userID
        Integer userId = userDAO.getIdByRoleId(teacherId,"TEACHER");

        logger.info("删除老师用户前，查出来的userID:"+userId);
        userDAO.deleteUserById(userId);
        //再删除教师表中教师
        teacherMapper.deleteTeacher(teacherId);
        logger.info("删除老师以及用户成功...");

    }


    public TeacherDO getTeacherId(Integer teacherId){
        return teacherMapper.getTeacher(teacherId);
    }

    public TeacherBOList getTeacherList(){

        List<TeacherBO> teacherBOs = new ArrayList<TeacherBO>();

        List<TeacherDO> teacherDOs = teacherMapper.getTeacherList();

        for(TeacherDO teacherDO : teacherDOs){
            teacherBOs.add(ObjectTranslateUtil.teacherDOToTeacherBO(teacherDO));
        }

        TeacherBOList teacherBOList = new TeacherBOList();
        teacherBOList.setTeacherList(teacherBOs);
        return teacherBOList;

    }

    public TeacherDO getTeacherByTeacherNum(Integer teacherNum){
        return teacherMapper.getTeacherByTeacherNum(teacherNum);
    }


    public void updateTeacher(TeacherDO teacherDO){
        teacherMapper.updateTeacher(teacherDO);
    }


}
