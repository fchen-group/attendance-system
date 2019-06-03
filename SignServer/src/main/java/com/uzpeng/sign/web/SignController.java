package com.uzpeng.sign.web;

import com.mysql.jdbc.PacketTooBigException;
import com.uzpeng.sign.bo.*;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.exception.DuplicateDataException;
import com.uzpeng.sign.exception.NoAuthenticatedException;
import com.uzpeng.sign.service.CourseService;
import com.uzpeng.sign.service.SignService;
import com.uzpeng.sign.service.StudentService;
import com.uzpeng.sign.util.*;
import com.uzpeng.sign.web.dto.CreateSignRecordDTO;
import com.uzpeng.sign.web.dto.SignRecordDTO;
import com.uzpeng.sign.web.dto.StartSignDTO;
import com.uzpeng.sign.web.dto.UpdateSignRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Controller
public class SignController {
    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private SignService signService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private Environment env;

    //生成二维码
    @RequestMapping(value = "/v1/course/{courseId}/sign", method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String generateCode(@PathVariable("courseId") String courseId, HttpServletResponse response,
                               HttpServletRequest request) {

        try {

            String json = SerializeUtil.readStringFromReader(request.getReader());
            CreateSignRecordDTO createSignRecordDTO = SerializeUtil.fromJson(json, CreateSignRecordDTO.class);

            logger.info("courseTimeId：" + createSignRecordDTO.getCourseTimeId());

            Integer signID = signService.createSign(createSignRecordDTO, Integer.parseInt(courseId));

            SignWebSocketLinkBO signWebSocketLinkBO = new SignWebSocketLinkBO();
            signWebSocketLinkBO.setLink(env.getProperty("ws.sign.link"));
            signWebSocketLinkBO.setSignID(signID);
            return CommonResponseHandler.handleResponse(signWebSocketLinkBO, SignWebSocketLinkBO.class);

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }


    //结束签到接口 其实courseId 同样不需要
    @RequestMapping(value = "/v1/course/{courseId}/sign/{signId}/signState", method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String signStateControl(@PathVariable("signId") String signIdStr, HttpServletResponse response,
                                   HttpServletRequest request) {
        try {

            logger.info("signStateControl...signIdStr：" + signIdStr);
            int signId = Integer.parseInt(signIdStr);
            String json = SerializeUtil.readStringFromReader(request.getReader());

            StartSignDTO startSignDTO = SerializeUtil.fromJson(json, StartSignDTO.class);
            int parameterState = startSignDTO.getState();
            int storedState = signService.getSignState(signId);

            if (storedState == StatusConfig.SIGN_START_FLAG || parameterState == StatusConfig.SIGN_CREATE_FLAG) {
                signService.updateSignState(signId, StatusConfig.SIGN_FINISH_FLAG); //以及结束时间

                //signService.evaluateSignResult(signId);
                return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                        env.getProperty("status.success"), env.getProperty("link.doc"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResponseHandler.handleException(response);
    }

    //生成二维码 开启签到任务

    //此次签到任务  course_sign（数据库）状态控制   //根据前端好像是  二维码链接接口：

   /*@RequestMapping(value = "/v1/course/{courseId}/sign/{signId}/link", method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String signStateControl_1(@PathVariable("signId")String signIdStr, HttpServletResponse response,
                                   HttpServletRequest request) {
        try {
            logger.info("signStateControl...");
            int signId = Integer.parseInt(signIdStr);
            String json = SerializeUtil.readStringFromReader(request.getReader());

            StartSignDTO startSignDTO = SerializeUtil.fromJson(json, StartSignDTO.class);
            int parameterState = startSignDTO.getStart();
            int storedState = signService.getSignState(signId);

            //使签到任务变 '正在签到'状态   ：相当于 返回一个WebSocketLink
            if((storedState == StatusConfig.SIGN_CREATE_FLAG || storedState == StatusConfig.SIGN_START_FLAG) && parameterState ==
                    StatusConfig.SIGN_START_FLAG) {
                signService.updateSignState(signId, StatusConfig.SIGN_START_FLAG);

                SignWebSocketLinkBO signWebSocketLinkBO = new SignWebSocketLinkBO();
                signWebSocketLinkBO.setLink(env.getProperty("ws.sign.link"));       //？？？

                return CommonResponseHandler.handleResponse(signWebSocketLinkBO, SignWebSocketLinkBO.class);

                //使签到任务变 '完成'状态
            } else if (storedState == StatusConfig.SIGN_START_FLAG && parameterState == StatusConfig.SIGN_FINISH_FLAG){
                signService.updateSignState(signId, StatusConfig.SIGN_FINISH_FLAG);
                signService.evaluateSignResult(signId);          //evaluate  评判
                return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                        env.getProperty("status.success"),  env.getProperty("link.doc"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CommonResponseHandler.handleException(response);
    }
*/

    //删除签到
    @RequestMapping(value = "/v1/course/deleteSign/{signId}", method = RequestMethod.DELETE,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteSign(@PathVariable("signId") String signId, HttpServletResponse response) {

        try {

            signService.deleteSign(Integer.parseInt(signId));
            logger.info("delete sign success!");
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //  这是前面 websocket 处理后，返回给学生这个api的地址包括 token
    //  学生端（SignRecordDTO包括了经纬度）  得到这个 token后，带着token 访问这个接口
    //  学生微信端 签到接口
    @RequestMapping(value = "/v1/student/sign", method = RequestMethod.PUT, params = {"token"},
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String startSign(HttpServletResponse response, HttpServletRequest request) {

        logger.info(".*.*.*.*. student start sign...");
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

                String storedToken = UserMap.getToken(Integer.parseInt(signRecordDTO.getSignId()));

                logger.info("按理是true跟false---  " + token.equals(storedToken) + "    " + decodedToken.equals(storedToken));

                //测试  后期如果没有出现这个相等的bug,可以直接删除
                if (decodedToken.equals(storedToken)) {
                    logger.info("竟然相等！！！！！！！");
                    logger.info("前端传过来的:" + token);
                    logger.info("decodedToken以及storedToken：" + storedToken);
                }
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

    //获得该课程中 创建的所有签到记录list
    @RequestMapping(value = "/v1/course/{courseId}/sign", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSignRecordWeek(@PathVariable("courseId") String courseId, HttpServletResponse response,
                                    HttpServletRequest request) {
        try {
            logger.info("courseId:  " + courseId);
            SignRecordTimeListBO signRecordTimeListBO = signService.getSignWeek(Integer.parseInt(courseId));
            return CommonResponseHandler.handleResponse(signRecordTimeListBO, SignRecordTimeListBO.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResponseHandler.handleException(response);
    }


    //更改具体签到详情列表上 course_sign_record（数据库） 签到记录id 学生的state状态
    @RequestMapping(value = "/v1/course/signRecord/{signRecordID}/update", method = RequestMethod.PUT,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String updateSignRecord(@PathVariable("signRecordID") String signRecordID, HttpServletResponse response,
                                   HttpServletRequest request) {
        try {
            String json = SerializeUtil.readStringFromReader(request.getReader());
            UpdateSignRecordDTO updateSignRecordDTO = SerializeUtil.fromJson(json, UpdateSignRecordDTO.class);

            updateSignRecordDTO.setId(Integer.parseInt(signRecordID));

            logger.info("sign record id is " + updateSignRecordDTO.getId() + ", state is " + updateSignRecordDTO.getState());

            signService.updateSignState(updateSignRecordDTO);
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("status.success"), env.getProperty("link.doc"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);

        }
    }

    //重写下面接口 获取当前签到中学生的所有记录

    @RequestMapping(value = "/v1/course/{signId}/signRecord", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSignRecordBySignId(@PathVariable("signId") String signId,
                                        HttpServletResponse response, HttpServletRequest request) {
        try {

            SignRecordListBO signRecordListBO = signService.getSignRecordBySignId(Integer.valueOf(signId));
            return CommonResponseHandler.handleResponse(signRecordListBO, SignRecordListBO.class);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return CommonResponseHandler.handleException(response);
    }

    // 导出签到记录
    @RequestMapping(value = "/v1/course/{courseId}/sign/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response, @PathVariable
            ("courseId") String courseId) throws Exception {
        try {
            logger.info("courseId:" + courseId);
            HttpHeaders headers = new HttpHeaders();
            CourseBO courseBO = courseService.getCourseById(Integer.parseInt(courseId));
            headers.setContentDispositionFormData("attachment", java.net.URLEncoder.encode(
                    courseBO.getCourseName() + "签到记录.xlsx", "UTF-8"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            byte[] bytes = signService.downloadSignAllRecord(Integer.parseInt(courseId));

            return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("表格下载出现异常");
        }

    }

    //创建签到记录
   /*@RequestMapping(value = "/v1/course/{courseId}/sign", method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String createSign(@PathVariable("courseId")String courseId, HttpServletResponse response,
                                          HttpServletRequest request) throws DuplicateDataException, IOException{
        String json = SerializeUtil.readStringFromReader(request.getReader());

        CreateSignRecordDTO createSignRecordDTO = SerializeUtil.fromJson(json, CreateSignRecordDTO.class);

        logger.info("courseTimeId"+createSignRecordDTO.getCourseTimeId());

        signService.createSign(createSignRecordDTO);
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"), env.getProperty("link.doc"));
    }*/

    //在签到管理上 根据学号 搜索学生签到情况，并随机输出 amount条记录
    /*@RequestMapping(value = "/v1/course/{courseId}/sign", method = RequestMethod.GET, params = {"time","week"},
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSignRecordByWeek(@PathVariable("courseId")String courseId,
                                      HttpServletResponse response, HttpServletRequest request) {
        try {
            String timeId = request.getParameter("time");
            String week = request.getParameter("week");
            String num = request.getParameter("num");   //学号
            String amount = request.getParameter("amount");  //查询每页总数

            //模糊搜索后的 学生记录
            SignRecordListBO signRecordListBO = signService.getSignRecordByParam(
                    Integer.parseInt(courseId), Integer.parseInt(timeId), Integer.parseInt(week), num);
            if(amount != null){
                List<SignRecordBO> signRecordList = signRecordListBO.getList();
                List<SignRecordBO> result =  RandomUtil.pickAmountRandomly(signRecordList, Integer.parseInt(amount));

                signRecordListBO.setList(result);
            }
            return CommonResponseHandler.handleResponse(signRecordListBO, SignRecordListBO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CommonResponseHandler.handleException(response);
    }
*/


}
