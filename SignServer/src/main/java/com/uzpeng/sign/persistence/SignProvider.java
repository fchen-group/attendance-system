package com.uzpeng.sign.persistence;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class SignProvider {

    public String insertAll(Map map){
            //todo 类型检测
            List list = (List)map.get("list");

            String statement =  "INSERT INTO course_sign(course_id, create_time, course_time_id, week, state) VALUES";
            MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].course_id}, #'{'list[{0}].create_time}," +
                    " #'{'list[{0}].course_time_id}, #'{'list[{0}].week}, #'{'list[{0}].state})");

            StringBuilder statementBuilder = new StringBuilder();
            statementBuilder.append(statement);
            for (int i = 0; i < list.size(); i++) {
                statementBuilder.append(messageFormat.format(new Object[]{i}));
                if(i < list.size() -1){
                    statementBuilder.append(",");
                }
            }

            return statementBuilder.toString();
    }
}
