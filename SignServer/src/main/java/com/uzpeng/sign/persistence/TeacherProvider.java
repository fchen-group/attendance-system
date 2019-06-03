package com.uzpeng.sign.persistence;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class TeacherProvider {

    public String batchInsertTeacher(Map map){
        //todo 类型检测
        List list = (List)map.get("list");

        String statement =  "INSERT INTO teacher(name, card_num) VALUES";
        MessageFormat messageFormat = new MessageFormat("#'{'list[{0}].name}, #'{'list[{0}].cardNum}");

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
