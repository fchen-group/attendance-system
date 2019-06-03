package com.uzpeng.sign.persistence;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class SelectiveCourseProvider {

    public String addSelectiveCourseList(Map map){
        //todo 类型检测
        List list = (List)map.get("list");

        String statement =  "INSERT INTO selective_course(course_id, student_id) VALUES";
        MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].course_id}, #'{'list[{0}].student_id})");

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
