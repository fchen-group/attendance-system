package com.uzpeng.sign.service;

import com.uzpeng.sign.config.EmailConfig;
import com.uzpeng.sign.dao.UserDAO;
import com.uzpeng.sign.domain.RoleDO;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.util.ThreadPool;
import com.uzpeng.sign.web.dto.LoginDTO;
import com.uzpeng.sign.web.dto.PasswordDTO;
import com.uzpeng.sign.web.dto.RegisterDTO;
import com.uzpeng.sign.web.dto.wxLoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EmailConfig emailConfig;

    public void registerNewUser(RegisterDTO registerDTO){
        userDAO.insertUser(ObjectTranslateUtil.registerDTOToUserDO(registerDTO));
    }

    public Integer loginCheck(LoginDTO loginDTO){
        return userDAO.checkUserAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
    }

    public Integer oldPasswordCheck(Integer roleId,String oldPassword){
        return userDAO.oldPasswordCheck(roleId,oldPassword);

    }

    public boolean wxloginCheck(wxLoginDTO wxloginDto){
       return userDAO.wxCheckUserAndPassword(ObjectTranslateUtil.wxloginDTOTowxloginDO(wxloginDto));
    }

    public boolean checkEmailAddress(String email){
        return userDAO.checkEmailValid(email);
    }

    public void sendVerifyCodeByEmail(String email, String code){
        System.out.println("email:"+email+",verify code:"+code);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText(code);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(emailConfig.getFrom());

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setPort(Integer.parseInt(emailConfig.getPort()));
        mailSender.setHost(emailConfig.getHost());
        mailSender.setUsername(emailConfig.getUserName());
        mailSender.setPassword(emailConfig.getPassword());

        //todo
        ThreadPool.run(()-> mailSender.send(simpleMailMessage));
    }

    public RoleDO getRole(int id){
        return userDAO.getRole(id);
    }

    public UserDO getUserInfo(String id){
        return userDAO.getUserInfo(Integer.parseInt(id));
    }

    public void updatePassword(Integer id, String newPassword){
        logger.info("更新学生密码service层   userID:"+id+"学生密码重置为： "+newPassword);
        userDAO.updatePassword(id , newPassword);
    }


    public void deleteOpenId(Integer id){
        logger.info("deleteStudentOpenId  ...userID:"+id);
        userDAO.deleteOpenId(id);
    }

    public Integer getIdByRoleId(int role_id,String role){
        return userDAO.getIdByRoleId(role_id,role);
    }


    public UserDO getUserByOpenId(String openId, String oldPassword){
        return userDAO.getUserByOpenId(openId, oldPassword);
    }

}
