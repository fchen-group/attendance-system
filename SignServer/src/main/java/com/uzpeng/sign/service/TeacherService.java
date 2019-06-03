package com.uzpeng.sign.service;

import com.uzpeng.sign.bo.TeacherBOList;
import com.uzpeng.sign.dao.StudentDAO;
import com.uzpeng.sign.dao.TeacherDAO;
import com.uzpeng.sign.dao.UserDAO;
import com.uzpeng.sign.domain.StudentDO;
import com.uzpeng.sign.domain.TeacherDO;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.util.CryptoUtil;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.util.Role;
import com.uzpeng.sign.web.StudentController;
import com.uzpeng.sign.web.dto.TeacherDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService {

    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

    @Autowired
    private TeacherDAO teacherDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserService userService;  //重置user密码需要用到

    public void addTeacher(TeacherDTO teacherDTO){

        TeacherDO teacherDO = ObjectTranslateUtil.teacherDTOToTeacherDO(teacherDTO);
        String password = teacherDTO.getPassword();
        if(password == "" || password == null) {
            password = teacherDTO.getTeacherNum();
        }

        //添加老师到老师表中
        teacherDAO.addTeacher(teacherDO,password);

    }

    public void deleteTeacher(Integer teacherId){

        //添加老师到老师表中
        teacherDAO.deleteTeacher(teacherId);

    }



    public void updateTeacher(TeacherDTO teacherDTO,Integer teacherId){
        TeacherDO teacherDO = ObjectTranslateUtil.teacherDTOToTeacherDO(teacherDTO);
        teacherDO.setId(teacherId);
        teacherDAO.updateTeacher(teacherDO);
    }

    public TeacherBOList getTeacherList(){
        return teacherDAO.getTeacherList();
    }

    public boolean getTeacherByTeacherNum(String teacherNum){
        TeacherDO teacherDo = teacherDAO.getTeacherByTeacherNum(Integer.valueOf(teacherNum));
        logger.info("检测查询到的老师为："+teacherDo);
        return teacherDo==null?false:true;
    }

    //重置学生密码，先在学生表中查出学号，再在user表中设置
    public void resetTeacherPassword(Integer teacherId){

        TeacherDO teacherDO = teacherDAO.getTeacherId(teacherId);
        logger.info("teacher's 学号为:"+teacherDO.getTeacher_num());
        //需要根据学号查出userID
        Integer id = userService.getIdByRoleId(teacherId,"TEACHER");
        userService.updatePassword(id,teacherDO.getTeacher_num());  //重置也就是更新user密码为 职工号
    }
}
