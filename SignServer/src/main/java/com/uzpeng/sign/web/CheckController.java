package com.uzpeng.sign.web;

import com.uzpeng.sign.bo.*;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.service.*;
import com.uzpeng.sign.support.SessionAttribute;
import com.uzpeng.sign.util.*;
import com.uzpeng.sign.web.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;



//此类为本地测试类，不需要看
@Controller
public class CheckController {


    private static final Logger logger = LoggerFactory.getLogger(CheckController.class);
    @Autowired
    private CourseService courseService;

    @Autowired
    private SemesterService semesterService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private Environment env;
    @Autowired
    private SignService signService;


    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String check() {
        logger.info("服务器启动啦！");
        return "helloWorld!";
    }

    @RequestMapping(value = "/check/test/{signId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkTest(@PathVariable("signId") String signId,HttpServletResponse response) {
        try {

            signService.deleteSign(Integer.parseInt(signId));

        }catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
        return "helloWorld!";
    }

    //entoken=%2BeNe6FbJeJ%2FxM9Ne4ex7VivPmogBFm0bQ2ux61JlT0iq5eMb%2BMhHUwehqcuK6X%2FRovRf7AZtsJ%2F%2BvbeHlclCkA%3D%3D
    //需要注意的是，在接收的时候，已经是解码的状态。奇怪1
    @RequestMapping(value = "/check/token", method = RequestMethod.GET, params = {"entoken"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkToken(HttpServletRequest request) {
        try {
            //服务器
            String token = "+eNe6FbJeJ/xM9Ne4ex7VivPmogBFm0bQ2ux61JlT0iq5eMb+MhHUwehqcuK6X/RovRf7AZtsJ/+vbeHlclCkA==";
            String encodedToken = URLEncoder.encode(token,"utf8");

            //浏览器
            String entoken = request.getParameter("entoken");
            String decodedToken = URLDecoder.decode(token, "utf8");     //解码

            logger.info("-----该学生传进来的entoken为:" + entoken  + "  "+token.equals(entoken));  //按理不一样

            //奇怪2。编码后，传至浏览器，进行解码，但跟原来的token已不相同
            logger.info("-----decodedToken:" + decodedToken+"  "+token.equals(decodedToken));
            logger.info("-----encodedToken:" + encodedToken + "  "+encodedToken.equals(entoken));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "token解码失败!", env.getProperty("link.doc"));

        }

        return "hehe";
    }




    /*//重置学生密码 （管理台）
    @RequestMapping(value = "/v1/course/student/resetPass1/{studentId}", method = RequestMethod.PUT,            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String resetStudentPasswordByStudentId(@PathVariable("studentId") String studentId, HttpSession session, HttpServletResponse response,
                                                  HttpServletRequest request) {

       TeacherDTO  teacherDTO = new TeacherDTO();
        teacherDTO.setName("test1888");
        teacherDTO.setCollege("计算机学院");
        teacherDTO.setTeacherNum("8888");
        teacherDTO.setPassword("9999");
        if(teacherService.getTeacherByTeacherNum(teacherDTO.getTeacherNum()))
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "职工号已存在",  "");

        teacherService.addTeacher(teacherDTO);
        logger.info("添加老师列表成功...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));
    }*/

    @RequestMapping(value = "/check/{courseId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String check(@PathVariable String courseId) {
        StudentDetailBOList studentDetailBOList = studentService.getStudentDetailList(Integer.parseInt(courseId));
        return CommonResponseHandler.handleResponse(studentDetailBOList, StudentDetailBOList.class);
    }

    //****************************测试并发数接口
    //获取展示课程
    @RequestMapping(value = "/check/v1/course/{roleId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getCourse(@PathVariable String roleId, HttpServletResponse response) {

        try {
            CourseListBO courseListBO = courseService.getCourse(Integer.parseInt(roleId));
            return CommonResponseHandler.handleResponse(courseListBO, CourseListBO.class);

        } catch (NullPointerException e) {
            logger.info("登陆AUTH为空指针");
            return CommonResponseHandler.handleNoAuthentication(response);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //  学生微信端 签到接口
    @RequestMapping(value = "/check/v1/student/sign", method = RequestMethod.PUT, params = {"token"},
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String startSign(HttpServletResponse response, HttpServletRequest request) {

        try {

            String json = SerializeUtil.readStringFromReader(request.getReader());
            SignRecordDTO signRecordDTO = SerializeUtil.fromJson(json, SignRecordDTO.class);
            if (signRecordDTO == null) {
                logger.info("signRecordDTO为 null...");
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "signRecordDTO为 null", env.getProperty("link.host"));
            }

            String openId = signRecordDTO.getOpenId();
            logger.info("students' openId：" + openId);
            if (openId != null && openId != "") {

                Integer studentId = studentService.getStudentByOpenId(openId);    //获取该学生ID
                if (studentId == null) {
                    logger.info(".*.*.*.*.根据openId查不到该名学生!" + openId);
                    return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                            "根据openId查不到该名学生!", env.getProperty("link.doc"));
                }
                String token = request.getParameter("token");

                String decodedToken = null;
                if (token != null && !token.equals("")) {
                    try {
                        decodedToken = URLDecoder.decode(token, "utf8");     //解码

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                                "token解码失败!", env.getProperty("link.doc"));
                    }
                } else {
                    logger.info("..... token为null或空!");
                    return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                            "token为null或空!", env.getProperty("link.doc"));
                }

                //String storedToken = UserMap.getToken(Integer.parseInt(signRecordDTO.getSignId()));
                String storedToken = "Ad/kXDCAr1wQDPyVGKVlWXgG9r7qBfaDXhdsIxe9Kg3WyShpiTKjX4E3TFqHIraeDJIx54wT7LsIizD6dON1IQ==";

                logger.info("按理是true跟false---  " + token.equals(storedToken) + "    " + decodedToken.equals(storedToken));

                //如果这个存在，即可证明那个bug正是该问题

                if (!token.equals(storedToken) && decodedToken.equals(storedToken)) {
                    logger.info("这个一旦成立被记录，即可解释 为什么出现一些学生签到失败的情况！");
                }

                if (token.equals(storedToken) || decodedToken.equals(storedToken)) {

                    //logger.info("studentId"+studentId+"signRecordDTO"+signRecordDTO);
                    signService.doingSign(signRecordDTO, studentId);       //将签到记录保存到数据库中
                    logger.info(".*.*.*.* student sign success...");

                    return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                            env.getProperty("status.success"), env.getProperty("link.doc"));
                } else {
                    logger.info("..... token跟原存储的token不一致，或加密后token为null! 需要重新扫码！");
                    return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                            "token跟原存储的token不一致，或加密后token为null!", env.getProperty("link.doc"));
                }
            } else {
                logger.info("openId为null或空!");
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "openId为空! (提示：请进入小程序扫码签到！)", env.getProperty("link.doc"));
            }
        } catch (IOException e) {

            e.printStackTrace();
            logger.info("request数据流为空，出现IO错误！");
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "request数据流为空，出现IO错误！", env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }


}
