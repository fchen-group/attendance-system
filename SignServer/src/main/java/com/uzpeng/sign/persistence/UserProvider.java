package com.uzpeng.sign.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class UserProvider {
    private static final Logger logger = LoggerFactory.getLogger(UserProvider.class);

    public String insertUserList(Map map){
        // todo 添加类型检测

        List users = (List)map.get("list");
        String statement = "INSERT INTO user(name, password, register_time, role_id, role) VALUES";
        MessageFormat message = new MessageFormat("(#'{'list[{0}].name}, #'{'list[{0}].password}," +
                " #'{'list[{0}].registerTime}, #'{'list[{0}].roleId}, #'{'list[{0}].role})");

        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append(statement);

        logger.info("user list's size is "+users.size());

        for (int i = 0; i < users.size(); i++) {
            statementBuilder.append(message.format(new Object[]{i}));
            if(i < users.size() - 1){
                statementBuilder.append(",");
            }
        }
        return statementBuilder.toString();
    }
}
