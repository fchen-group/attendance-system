package com.uzpeng.sign.web;

import com.uzpeng.sign.bo.StudentBO;
import com.uzpeng.sign.bo.StudentSignRecordListBO;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.RoleDO;
import com.uzpeng.sign.service.StudentService;
import com.uzpeng.sign.support.SessionAttribute;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.service.UserService;
import com.uzpeng.sign.util.*;
import com.uzpeng.sign.web.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;

    @Autowired
    private Environment env;

    //网站使用者登陆
    @RequestMapping(value = "/v1/login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response,
                        HttpSession session) {
        try {
            String json = SerializeUtil.readStringFromReader(request.getReader());
            logger.info("++=++++++++++++++++登陆获取的json:" + json);

            //product生产环境
            LoginDTO loginDTO = SerializeUtil.fromJson(json, LoginDTO.class);

            //test 测试环境
            /*LoginDTO loginDTO = new LoginDTO();
            loginDTO.setPassword("123456");
            loginDTO.setUsername("abc");*/

            logger.info("start check password!...");
            Integer id = userService.loginCheck(loginDTO);
            //logger.info("finish check password, start set cookie...");

            if (id != null) {
                UserDO userDO = userService.getUserInfo(String.valueOf(id));
                if (userDO.getRole().equals(Role.TEACHER)) {
                    /*
                    String cookieValue = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
                    SessionAttribute authInfo = new SessionAttribute(cookieValue, LocalDateTime.MAX);
                    session.setAttribute(SessionStoreKey.KEY_AUTH, authInfo);
                    UserMap.putUser(cookieValue, userDO);
                    */
                    //保存当前sessionId  以便拦截器判断是否存在该会话session
                    //同时只需保留 用户信息中roleId而不是整个用户对象,且存在session当中

                    session.setAttribute(SessionStoreKey.KEY_SESSION_ID, session.getId());
                    session.setAttribute(session.getId(),userDO.getRoleId());

                    //如果存在领导情况， 我觉得领导权限应该更广。因此在教师端接口，并不需要做处理。
                    //而当请求的是领导才有权限操作的接口，可以跟据session保存的角色id再次进行判断即可。
                    //logger.info("cookieValue:" + cookieValue);
                    logger.info("sessionId:" + session.getId());

                    logger.info("Login successfully! user is is " + userDO.getRoleId());

                    return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                            env.getProperty("msg.login.success"), env.getProperty("link.login"));
                }
            } else {
                response.setStatus(404);
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        env.getProperty("msg.login.error"), env.getProperty("link.host"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setStatus(404);
        return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                env.getProperty("msg.login.error"), env.getProperty("link.host"));
    }

    //网站退出
    @RequestMapping(value = "/v1/logout", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String logout(final HttpServletResponse response, HttpSession session) {
        session.removeAttribute(SessionStoreKey.KEY_AUTH);

        /*Cookie cookie = new Cookie(SessionStoreKey.KEY_AUTH, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
*/
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("msg.logout.success"), env.getProperty("link.login"));
    }

    //更改平台使用者密码
    @RequestMapping(value = "/v1/user/password", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String updatePassword(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        try {
            String json = SerializeUtil.readStringFromReader(request.getReader());
            PasswordDTO passwordDTO = SerializeUtil.fromJson(json, PasswordDTO.class);
            logger.info("passwordDTO:" + passwordDTO);

            /*SessionAttribute auth = (SessionAttribute) session.getAttribute(SessionStoreKey.KEY_AUTH);
            UserDO role = UserMap.getUser((String) auth.getObj()); */   //获取登陆时 保存的当前管理平台的使用者UserDO
            Integer roleId = (Integer) session.getAttribute(session.getId());
            logger.info("role:" + roleId);

            //直接根据 roleId 以及密码判断是否存在该 使用者
            Integer id = userService.oldPasswordCheck(roleId,passwordDTO.getOldPassword());
            //Integer id = userService.loginCheck(ObjectTranslateUtil.passwordDTOToLoginDTO(passwordDTO));

            if (id != null) {
                userService.updatePassword(id, passwordDTO.getNewPassword());
                return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                        env.getProperty("status.success"), env.getProperty("link.host"));
            }else{
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "请输入正确的旧密码!", env.getProperty("link.host"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "PasswordDTO传入有问题", env.getProperty("link.host"));
        } catch (NullPointerException e) {
            logger.info("登陆AUTH为空指针");
            return CommonResponseHandler.handleNoAuthentication(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResponseHandler.handleException(response);
    }

    //-----------------------以下是学生使用者 user微信小程序的接口

    //小程序登录，并绑定openid
    @RequestMapping(value = "/v1/student/wxlogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxlogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        try {
            String json = SerializeUtil.readStringFromReader(request.getReader());

            wxLoginDTO wxloginDTO = SerializeUtil.fromJson(json, wxLoginDTO.class);
            //logger.info("++++++++++++++++++微信小程序登陆获取的wxloginDTO:" + wxloginDTO);
            if (wxloginDTO == null)
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "wxLoginDTO为 null", env.getProperty("link.host"));

            boolean flag = userService.wxloginCheck(wxloginDTO);

            logger.info("finish check wxpassword!..." + flag);
            if (flag) {
                return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                        env.getProperty("msg.login.success"), env.getProperty("link.login"));
            } else {
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "用户名或密码错误", env.getProperty("link.host"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "wxLoginDTO传入有IO问题", env.getProperty("link.host"));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }

    }

    //学生更改密码
    @RequestMapping(value = "/v1/student/password", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String updateStudentPassword(HttpServletRequest request, HttpServletResponse response) {
        try {
            String json = SerializeUtil.readStringFromReader(request.getReader());
            StudentChangePasswordDTO studentChangePasswordDTO = SerializeUtil.fromJson(
                    json, StudentChangePasswordDTO.class);
            logger.info("studentChangePasswordDTO:" + studentChangePasswordDTO);
            if (studentChangePasswordDTO == null)
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "studentChangePasswordDTO为 null!", env.getProperty("link.host"));

            UserDO userDO = userService.getUserByOpenId(studentChangePasswordDTO.getOpenId(),
                    studentChangePasswordDTO.getOldPassword());
            if (userDO != null) {
                userService.updatePassword(userDO.getId(), studentChangePasswordDTO.getNewPassword());
                return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                        env.getProperty("status.success"), env.getProperty("link.host"));
            } else

                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "userDO 为 null", env.getProperty("link.host"));


        } catch (IOException e) {
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "StudentChangePasswordDTO传入有IO问题", env.getProperty("link.host"));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //检查学生的openid是否存在
    @RequestMapping(value = "/v1/student/OpenIdExist/{openId}", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkStudentByOpenId(@PathVariable("openId") String openId, HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("start check openId....");
            if (openId != null && openId != "") {

                Integer exist = studentService.checkStudentByOpenId(openId);
                //logger.info(exist+"  ");
                if (exist != null) {
                    logger.info("openId存在....");
                    return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                            env.getProperty("status.success"), env.getProperty("link.host"));
                } else {
                    logger.info("openId不存在....");
                    return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                            "openId不存在", env.getProperty("link.host"));
                }
            } else
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "openId传入为空 或 null", env.getProperty("link.host"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }
    }

    //学生根据openId获取 个人信息  /v1/student? openId=123456789
    @RequestMapping(value = "/v1/student", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getStudentByOpenId(HttpServletRequest request, HttpServletResponse response) {
        try {
            //logger.info("getStudentByOpenId---");
            String openId = request.getParameter("openId");


            if (openId != null && openId != "") {
                Integer studentId = studentService.getStudentByOpenId(openId);
                StudentBO studentBO = studentService.getStudentById(studentId);
                return CommonResponseHandler.handleResponse(studentBO, StudentBO.class);
            } else {
                return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                        "openId传入为空 或 null", env.getProperty("link.host"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }

    }


    //wx通过openId获取学生的所有签到记录
    @RequestMapping(value = "/v1/student/sign", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getStudentRecordByOpenId(HttpServletRequest request, HttpServletResponse response) {
        try {
            String openId = request.getParameter("openId");
            String type = request.getParameter("type");
            //logger.info("openId:"+openId+"  记录type类型:"+type);

            if (openId != null && !openId.equals("") && type != null && !type.equals("")) {
                Integer studentId = studentService.getStudentByOpenId(openId);
                if (studentId == null) {
                    return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                            "该openId找不到相应的学生Id", env.getProperty("link.host"));
                }
                StudentSignRecordListBO studentSignRecordListBO = studentService.getStudentSignRecordList(
                        studentId, Integer.parseInt(type));
                return CommonResponseHandler.handleResponse(studentSignRecordListBO, StudentSignRecordListBO.class);
            }

            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "openId或type传入为空 (提示：请进入小程序扫码签到!）", env.getProperty("link.host"));

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseHandler.handleException(response);
        }

    }


    /*网站注册 验证*/
    @RequestMapping(value = "/v1/register/verify", method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String sendVerifyCode(@Valid EmailDTO emailDTO, HttpSession session, HttpServletRequest request,
                                 HttpServletResponse response) {

        //缺少处理邮箱格式错误情况 bindresult
        String email = emailDTO.getEmail();
        boolean isValid = userService.checkEmailAddress(email);

        if (isValid) {      //true则数据库不存在该邮箱，可以注册
            SessionAttribute storedSessionAttr = (SessionAttribute) session.getAttribute(SessionStoreKey.KEY_VERIFY_CODE);
            String verifyCode;

            if (storedSessionAttr == null || storedSessionAttr.getExpireDate().isBefore(LocalDateTime.now())) { //验证码过期
                verifyCode = VerifyCodeGenerator.generate();
                HashMap<String, String> verifyCodeMap = new HashMap<>();
                verifyCodeMap.put(email, verifyCode);

                SessionAttribute verifyCodeAttribute = new SessionAttribute(verifyCodeMap, LocalDateTime.now().plusMinutes(30));

                session.setAttribute(SessionStoreKey.KEY_VERIFY_CODE, verifyCodeAttribute);
            } else {
                verifyCode = (String) ((Map) storedSessionAttr.getObj()).get(email);
            }
            userService.sendVerifyCodeByEmail(email, verifyCode);

            response.setStatus(200);
            return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                    env.getProperty("msg.register.verified"), env.getProperty("link.register"));
        } else {
            response.setStatus(403);
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    env.getProperty("msg.email.invalid"), env.getProperty("link.host"));
        }
    }


    //以下方法没有用到---------------------------------------------------------------------------
    //邮箱注册
    @RequestMapping(value = "/v1/register", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String register(@Valid final RegisterDTO registerDTO, HttpSession session, HttpServletResponse response) {
        boolean isValid = userService.checkEmailAddress(registerDTO.getEmail());
        if (!isValid) {    //fasle则代表已存在该邮箱
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    env.getProperty("msg.email.invalid"), env.getProperty("link.host"));
        }

        SessionAttribute verifyCodeAttr = (SessionAttribute) session.getAttribute(SessionStoreKey.KEY_VERIFY_CODE);

        String errorMsg = CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                env.getProperty("msg.register.verify.error"), env.getProperty("link.host"));
        if (verifyCodeAttr == null) {
            return errorMsg;
        }
        Map verifyCodeMap = (Map) verifyCodeAttr.getObj();

        if (!registerDTO.getVerifyCode().equals(verifyCodeMap.get(registerDTO.getEmail()))) {
            return errorMsg;
        }

        userService.registerNewUser(registerDTO);

        String cookieValue = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        SessionAttribute authInfo = new SessionAttribute(cookieValue, LocalDateTime.MAX);
        session.setAttribute(SessionStoreKey.KEY_AUTH, authInfo);

        //存在cookie
        Cookie cookie = new Cookie(SessionStoreKey.KEY_AUTH, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        session.removeAttribute(SessionStoreKey.KEY_VERIFY_CODE);

        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("msg.register.success"), env.getProperty("link.login"));
    }

    @RequestMapping(value = "/v1/user/{id}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getUserInfo(@PathVariable(name = "id") String id, final HttpServletRequest request, HttpSession session) {
        System.out.println("id---------------------------" + id);
        UserDO userInfo = userService.getUserInfo(id);
        return CommonResponseHandler.handleResponse(userInfo, UserDO.class);
    }

}
