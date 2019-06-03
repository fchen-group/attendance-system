package com.uzpeng.sign.web;

import com.uzpeng.sign.bo.StudentBOList;
import com.uzpeng.sign.bo.TeacherBOList;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.TeacherDO;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.service.TeacherService;
import com.uzpeng.sign.support.SessionAttribute;
import com.uzpeng.sign.util.*;
import com.uzpeng.sign.web.dto.CourseDTO;
import com.uzpeng.sign.web.dto.StudentDTO;
import com.uzpeng.sign.web.dto.TeacherDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Reader;

@Controller
public class TeacherController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private Environment env;

    @RequestMapping(value = "/v1/course/teacher/addTeacher", method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addTeacher(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        try {

            Reader reader = request.getReader();
            String json = SerializeUtil.readStringFromReader(reader);
            TeacherDTO teacherDTO = SerializeUtil.fromJson(json, TeacherDTO.class);

            //先判断下 该老师是否存在，即职工号是否唯一
            if (teacherService.getTeacherByTeacherNum(teacherDTO.getTeacherNum()))
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "职工号已存在", "");

            teacherService.addTeacher(teacherDTO);
            logger.info("添加老师成功...");

            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (IOException e) {
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "TeacherDTO传入有问题!", env.getProperty("link.doc"));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //删除教师逻辑
    @RequestMapping(value = "/v1/course/teacher/deleteTeacher/{teacherId}", method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteTeacher(@PathVariable("teacherId") String teacherId, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        try {

            teacherService.deleteTeacher(Integer.parseInt(teacherId));
            logger.info("删除老师成功...");

            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }


    }

    //修改老师接口
    @RequestMapping(value = "/v1/course/teacher/updateTeacher/{teacherId}", method = RequestMethod.PUT,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String updateStudentByCourseId(@PathVariable("teacherId") String teacherId, HttpSession session, HttpServletResponse response,
                                          HttpServletRequest request) {

        try {


            String json = SerializeUtil.readStringFromReader(request.getReader());
            TeacherDTO teacherDTO = SerializeUtil.fromJson(json, TeacherDTO.class);

            teacherService.updateTeacher(teacherDTO, Integer.parseInt(teacherId));
            logger.info("修改老师成功...");
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //获取老师接口
    @RequestMapping(value = "/v1/course/teacher/getTeacherList", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getTeacherList(HttpSession session, HttpServletResponse response,
                                 HttpServletRequest request) {

        try {

            TeacherBOList teacherBOS = teacherService.getTeacherList();
            logger.info("获取老师列表成功...");
            return CommonResponseHandler.handleResponse(teacherBOS, TeacherBOList.class);

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }

    }

    //重置老师密码

    @RequestMapping(value = "/v1/course/teacher/resetPassword/{teacherId}", method = RequestMethod.PUT,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String resetStudentPasswordByStudentId(@PathVariable("teacherId") String teacherId, HttpSession session, HttpServletResponse response,
                                                  HttpServletRequest request) {
        try {

            logger.info("start reset teacher's password");
            teacherService.resetTeacherPassword(Integer.parseInt(teacherId));

            logger.info("重置教师密码成功...");
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));


        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }


}
