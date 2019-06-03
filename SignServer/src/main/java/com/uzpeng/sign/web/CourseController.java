package com.uzpeng.sign.web;

import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.bo.CourseBO;
import com.uzpeng.sign.bo.CourseListBO;
import com.uzpeng.sign.bo.CourseTimeListBO;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.interceptor.AuthenticatedInterceptor;
import com.uzpeng.sign.service.CourseService;
import com.uzpeng.sign.service.SemesterService;
import com.uzpeng.sign.support.SessionAttribute;
import com.uzpeng.sign.util.*;
import com.uzpeng.sign.web.dto.CourseDTO;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;

/**
 */
@Controller
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private Environment env;

    //添加课程+包括学生
    @RequestMapping(value = "/v1/course", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addCourse(HttpServletRequest request, HttpSession session, HttpServletResponse response) {

        try {
            Integer roleId = (Integer) session.getAttribute(session.getId());
            logger.info("role:" + roleId);


            /*SessionAttribute auth = (SessionAttribute) session.getAttribute(SessionStoreKey.KEY_AUTH);
            UserDO role = UserMap.getUser((String) auth.getObj());*/
            //logger.info("role:" + role);

            BufferedReader reader = request.getReader();
            String json = SerializeUtil.readStringFromReader(reader);
            CourseDTO courseDTO = SerializeUtil.fromJson(json, CourseDTO.class);

            logger.info(courseDTO + "");
            courseDTO.setTeacherId(roleId);         //后端添加 TeacherId  semesterId;
            //计算当前学期ID ,若不存在，则自主创建
            courseDTO.setSemester(semesterService.getSemesterIdByName(SemesterIdUtil.getSemesterName(), roleId));

            courseService.addCourse(courseDTO);
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

    //获取展示课程
    @RequestMapping(value = "/v1/course", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getCourse(HttpServletRequest request, HttpSession session, HttpServletResponse response) {


        try {
            /*SessionAttribute auth = (SessionAttribute) session.getAttribute(SessionStoreKey.KEY_AUTH);
            UserDO role = UserMap.getUser((String) auth.getObj());
            logger.info("role:" + role);*/

            Integer roleId = (Integer) session.getAttribute(session.getId());
            logger.info("role:" + roleId);

            //logger.info("--------------getCourses:" + role.getRoleId());
            //CourseListBO courseListBO = courseService.getCourse(role.getRoleId());
            CourseListBO courseListBO = courseService.getCourse(roleId);
            return CommonResponseHandler.handleResponse(courseListBO, CourseListBO.class);

        } catch (NullPointerException e) {
            logger.info("登陆AUTH为空指针");
            return CommonResponseHandler.handleNoAuthentication(response);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //根据课程id获取课程
    @RequestMapping(value = "/v1/course/{id}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getCourseById(@PathVariable("id") String id, HttpServletRequest request, HttpSession session,
                                HttpServletResponse response) {

        try {
            Integer courseId = Integer.parseInt(id);
            CourseBO courseBO = courseService.getCourseById(courseId);
            return CommonResponseHandler.handleResponse(courseBO, CourseBO.class);
        } catch (Exception e) {   //预防id转换失败
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }


    }

    @RequestMapping(value = "/v1/course/{id}", method = RequestMethod.DELETE, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteCourseById(@PathVariable("id") String id, HttpServletRequest request, HttpSession session,
                                   HttpServletResponse response) {
        try {

            courseService.deleteCourseById(Integer.parseInt(id));
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.login"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //根据课程id修改课程
    @RequestMapping(value = "/v1/course/{id}", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String updateCourseById(@PathVariable("id") String id, HttpServletRequest request, HttpSession session,
                                   HttpServletResponse response) {
        try {
           /* SessionAttribute auth = (SessionAttribute) session.getAttribute(SessionStoreKey.KEY_AUTH);
            UserDO role = UserMap.getUser((String) auth.getObj());*/
            Integer roleId = (Integer) session.getAttribute(session.getId());
            logger.info("role:" + roleId);

            BufferedReader reader = request.getReader();
            String json = SerializeUtil.readStringFromReader(reader);
            CourseDTO courseDTO = SerializeUtil.fromJson(json, CourseDTO.class);

            //Bug漏了这个
            //String semesterId = semesterService.getSemesterIdByName(SemesterIdUtil.getSemesterName(), role.getId());
            String semesterId = semesterService.getSemesterIdByName(SemesterIdUtil.getSemesterName(),  roleId);
            courseDTO.setSemester(semesterId);

            courseDTO.setCourseId(id);
            courseDTO.setTeacherId(roleId);
            courseService.updateCourse(courseDTO);
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (NullPointerException e) {
            logger.info("登陆AUTH为空指针");
            return CommonResponseHandler.handleNoAuthentication(response);
        }
        catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }


    @RequestMapping(value = "/v1/course/{id}/time", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getCourseTimeById(@PathVariable("id") String id, HttpServletRequest request, HttpSession session,
                                    HttpServletResponse response) {
        try {
            Integer courseId = Integer.parseInt(id);
            CourseTimeListBO courseTimeListBO = courseService.getCourTimeById(courseId);
            return CommonResponseHandler.handleResponse(courseTimeListBO, CourseTimeListBO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }
}
