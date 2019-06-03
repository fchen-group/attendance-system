package com.uzpeng.sign.web;

import com.uzpeng.sign.bo.*;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.service.StudentService;
import com.uzpeng.sign.support.SessionAttribute;
import com.uzpeng.sign.util.*;
import com.uzpeng.sign.web.dto.CourseDTO;
import com.uzpeng.sign.web.dto.StudentDTO;
import com.uzpeng.sign.web.dto.StudentDeleteListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private Environment env;

    @Autowired
    private StudentService studentService;

    //上传文件
    @CrossOrigin(allowedHeaders = "withCredentials")
    @RequestMapping(value = "/v1/course/{courseId}/student", method = RequestMethod.POST, consumes = "multipart/form-data",
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String insertStudentList(@RequestParam("file") MultipartFile file, @PathVariable("courseId") String id,
                                    HttpSession session, HttpServletResponse response) {
        logger.info("start upload file " + file.getOriginalFilename());

        try {
            Integer courseId = Integer.parseInt(id);
            InputStream studentList = file.getInputStream();
            studentService.insertStudentsByFile(studentList, file.getOriginalFilename(), courseId);
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (IOException e) {
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "文件传入有问题!", env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //逐个添加 学生
    @RequestMapping(value = "/v1/course/{courseId}/student", method = RequestMethod.POST
            , produces = "application/json;charset=utf-8")
    @ResponseBody
    public String insertStudent(HttpServletRequest request, HttpSession session, HttpServletResponse response,
                                @PathVariable("courseId") String id) {

        try {
            String json = SerializeUtil.readStringFromReader(request.getReader());
            StudentDTO studentDTO = SerializeUtil.fromJson(json, StudentDTO.class);

            logger.info("start add student " + json);
            studentService.insertStudent(studentDTO, Integer.parseInt(id));

            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (IOException e) {
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "StudentDTO传入有问题!", env.getProperty("link.doc"));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }


    @RequestMapping(value = "/v1/course/{courseId}/student", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getStudentByCourseId(@PathVariable("courseId") String courseId, HttpServletResponse response) {

        try {

            StudentBOList studentBOS = studentService.getStudentByCourseId(Integer.parseInt(courseId));
            return CommonResponseHandler.handleResponse(studentBOS, StudentBOList.class);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }

        /*  可能进行分页查询吧
        String num = request.getParameter("num");
        String amountStr = request.getParameter("amount");

        if (amountStr != null) {
            int amount = Integer.parseInt(amountStr);
            StudentBOList studentBOList = studentService.pickStudent(Integer.parseInt(courseId), amount);

            return CommonResponseHandler.handleResponse(studentBOList, StudentBOList.class);
        } else if (num != null) {
            StudentBOList studentBOList = studentService.searchStudentByNum(num);
            return CommonResponseHandler.handleResponse(studentBOList, StudentBOList.class);
        } else {

            StudentBOList studentBOS = studentService.getStudentByCourseId(courseId);
            return CommonResponseHandler.handleResponse(studentBOS, StudentBOList.class);
        }*/

    }


    @RequestMapping(value = "/v1/course/{courseId}/student/{studentId}", method = RequestMethod.DELETE,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteStudentByCourseId(@PathVariable("courseId") String courseId, HttpServletResponse response,
                                          HttpServletRequest request, @PathVariable("studentId") String studentId) {
        try {

            studentService.removeStudent(Integer.parseInt(courseId), Integer.parseInt(studentId));
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //批量删除学生   跟上面接口 类似 单独删除学生 类似
    @RequestMapping(value = "/v1/course/{courseId}/studentList", method = RequestMethod.DELETE,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteStudentListByCourseId(@PathVariable("courseId") String courseId, HttpSession session, HttpServletResponse response,
                                              HttpServletRequest request) {
        try {

            BufferedReader reader = request.getReader();
            String json = SerializeUtil.readStringFromReader(reader);
            StudentDeleteListDTO studentDeleteListDTO = SerializeUtil.fromJson(json, StudentDeleteListDTO.class);


            List<Integer> students = studentDeleteListDTO.getStudents();
            for (Integer studentId : students) {
                studentService.removeStudent(Integer.parseInt(courseId), studentId);
            }

            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.host"));

        } catch (IOException e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //修改学生接口
    @RequestMapping(value = "/v1/course/student/{studentId}", method = RequestMethod.PUT,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String updateStudentByCourseId(@PathVariable("studentId") String studentId, HttpSession session, HttpServletResponse response,
                                          HttpServletRequest request) {

        try {

            String json = SerializeUtil.readStringFromReader(request.getReader());
            StudentDTO studentDTO = SerializeUtil.fromJson(json, StudentDTO.class);

            logger.info("start update student " + json);
            studentService.updateStudent(studentDTO, Integer.parseInt(studentId));

            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));


        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //重置学生密码 （管理台）

    @RequestMapping(value = "/v1/course/student/resetPassword/{studentId}", method = RequestMethod.PUT,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String resetStudentPasswordByStudentId(@PathVariable("studentId") String studentId, HttpSession session, HttpServletResponse response,
                                                  HttpServletRequest request) {
        try {

            logger.info("start reset student's password");
            studentService.resetStudentPassword(Integer.parseInt(studentId));

            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //学生解绑微信号接口（即删除openid）

    @RequestMapping(value = "/v1/course/student/deleteOpenId/{studentId}", method = RequestMethod.PUT,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteStudentOpenIdByStudentId(@PathVariable("studentId") String studentId, HttpSession session, HttpServletResponse response,
                                                 HttpServletRequest request) {
        try {
            logger.info("deleteStudentOpenId....");
            studentService.deleteStudentOpenId(Integer.parseInt(studentId));

            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));


        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //获取课程 学生详情 列表     根据课程ID
    @RequestMapping(value = "/v1/course/studentDetail/{courseId}", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getStudentDetailByCourseId(@PathVariable("courseId") String courseId,  HttpServletResponse response) {
        try {

            StudentDetailBOList studentDetailBOList = studentService.getStudentDetailList(Integer.parseInt(courseId));
            return CommonResponseHandler.handleResponse(studentDetailBOList, StudentDetailBOList.class);

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }


}
