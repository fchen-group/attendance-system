package com.uzpeng.sign.persistence;

import com.uzpeng.sign.domain.RoleDO;
import com.uzpeng.sign.domain.UserDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 */
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    UserDO getUserInfo(@Param("id") int id);

    @Select("SELECT id FROM user WHERE name=#{name}")
    Integer getIdByName(@Param("name")String name);

    @Select("SELECT id FROM user WHERE role_id =#{role_id} AND role = #{role}")
    Integer getIdByRoleId(@Param("role_id") int role_id,@Param("role") String role);

    @Update("UPDATE user SET username = #{username} WHERE id=#{id}")
    void updateUserName(@Param("username") String username, @Param("id")int id);

    @Select("SELECT * FROM user WHERE email=#{email}")
    UserDO checkEmailValid(@Param("email") String email);

    @Select("SELECT * FROM user WHERE open_id=#{openId}")
    UserDO getUserByOpenId(@Param("openId") String openId);

    @Insert("INSERT INTO user(name, password, register_time, role_id,role) VALUES(#{user.name}, #{user.password}," +
            " #{user.register_time,jdbcType=TIMESTAMP}, #{user.role_id}, #{user.role})")
    void addUser(@Param("user")UserDO userDO);

    @Select("SELECT password FROM user WHERE name=#{name} AND role = #{role} ")
    String checkUserAndPassword(@Param("name")String name,@Param("role") String role);


    @Select("SELECT password FROM user WHERE role_Id=#{role_id} AND role = #{role} ")
    String checkOldPasswordByRoleId(@Param("role_id")int role_id,@Param("role") String role);

    @InsertProvider(type = UserProvider.class, method = "insertUserList")
    void insertUserList(@Param("list")List<UserDO> userDOList);

    @Select("SELECT role, role_id FROM user WHERE id=#{id}")
    RoleDO getRole(@Param("id") int id);

    @Update("UPDATE user SET password=#{password} WHERE id=#{id}")
    void updatePassword(@Param("id")Integer id, @Param("password") String password);

    @Update("UPDATE user SET open_id=#{open_id} WHERE name=#{name}")
    void updateOpenIdByName(@Param("open_id")String open_id, @Param("name") String name);

    //deleteOpenId 即设置为空
    @Update("UPDATE user SET open_id = ''  WHERE id=#{id}")
    void deleteOpenId(@Param("id")Integer id);

    @Delete("DELETE FROM user WHERE id= #{id}")
    void deleteUserById(@Param("id")Integer id);

    @Select("SELECT role_id FROM user WHERE open_id=#{openId}")
    Integer getRoleIdByOpenId(@Param("openId")String openId);

    @Select("SELECT id FROM user WHERE open_id=#{openId}")
    Integer getIdByOpenId(@Param("openId")String openId);
}
