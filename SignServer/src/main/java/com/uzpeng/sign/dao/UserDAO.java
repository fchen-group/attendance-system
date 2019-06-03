package com.uzpeng.sign.dao;

import com.uzpeng.sign.domain.RoleDO;
import com.uzpeng.sign.domain.UserDO;
import com.uzpeng.sign.domain.wxLoginDO;
import com.uzpeng.sign.persistence.UserMapper;
import com.uzpeng.sign.util.CryptoUtil;
import com.uzpeng.sign.web.dto.PasswordDTO;
import com.uzpeng.sign.web.dto.wxLoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 */
@Repository
public class UserDAO {
    @Autowired
    private UserMapper userMapper;

    public UserDO getUserInfo(int id){
        return  userMapper.getUserInfo(id);
    }

    public boolean checkEmailValid(String email){
        return userMapper.checkEmailValid(email) == null;   //如果等于null,则代表数据库不存在
    }

    public void insertUser(UserDO userDO){
         userMapper.addUser(userDO);
    }

    public void updateUserName(String username, int id){
        userMapper.updateUserName(username, id);
    }

    public Integer checkUserAndPassword(String name, String password){
        String storedPassword = userMapper.checkUserAndPassword(name,"TEACHER");

        boolean isCorrect = CryptoUtil.match(password, storedPassword);
        if(isCorrect) {
            return userMapper.getIdByName(name);
        }else {
            return null;
        }
    }
    public Integer oldPasswordCheck(Integer roleId, String password){
        String storedPassword = userMapper.checkOldPasswordByRoleId(roleId,"TEACHER");

        boolean isCorrect = CryptoUtil.match(password, storedPassword);
        if(isCorrect) {
            return userMapper.getIdByRoleId(roleId,"TEACHER");
        }else {
            return null;
        }
    }

    //建议把业务代码都移到 service层
    public boolean wxCheckUserAndPassword(wxLoginDO loginDO){

        String storedPassword = userMapper.checkUserAndPassword(loginDO.getUsername(),"STUDENT");

        boolean isCorrect = CryptoUtil.match(loginDO.getPassword(), storedPassword);
        if(isCorrect) {
            //写入openId
            userMapper.updateOpenIdByName(loginDO.getOpenId(),loginDO.getUsername());
            return true;
        }else {
            return false;
        }
    }



    public Integer getIdByRoleId(int role_id,String role){

        return userMapper.getIdByRoleId(role_id,role);
    }

    public void insertUserList(List<UserDO> userDOList){
        userMapper.insertUserList(userDOList);
    }

    public RoleDO getRole(int id){
        return userMapper.getRole(id);
    }

    public Integer getRoleIdByOpenId(String openId){
        return userMapper.getRoleIdByOpenId(openId);
    }
    public Integer getIdByOpenId(String openId){
        return userMapper.getIdByOpenId(openId);
    }

    public Integer getIdByName(String name){
        return userMapper.getIdByName(name);
    }




    public void updatePassword(Integer id, String newPassword){
        userMapper.updatePassword(id, CryptoUtil.encodePassword(newPassword));
    }

    public void deleteOpenId(Integer id){
        userMapper.deleteOpenId(id);
    }
    public void deleteUserById(Integer id){
        userMapper.deleteUserById(id);
    }

    public UserDO getUserByOpenId(String openId, String oldPassword){
        UserDO userDO = userMapper.getUserByOpenId(openId);
        if(CryptoUtil.match(oldPassword, userDO.getPassword())){
            return userDO;
        } else {
            return null;
        }
    }
}
