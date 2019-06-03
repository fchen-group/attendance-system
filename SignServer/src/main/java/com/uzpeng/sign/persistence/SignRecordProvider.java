package com.uzpeng.sign.persistence;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;


public class SignRecordProvider {

    public String insertAll(Map map){
        //todo 类型检测
        List list = (List)map.get("list");

        String statement =  "INSERT INTO course_sign_record(course_sign_id, student_id, state, sign_time) VALUES";
        MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].course_sign_id}, #'{'list[{0}].student_id}," +
                " #'{'list[{0}].state}, #'{'list[{0}].sign_time})");

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


    public String getBySignIds(Map map){
        //todo 类型检测
        List list = (List)map.get("list");

        String statement =  "SELECT * FROM course_sign_record WHERE course_sign_id IN ";
        MessageFormat messageFormat = new MessageFormat("#'{'list[{0}]}");

        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append(statement);
        statementBuilder.append("(");
        for (int i = 0; i < list.size(); i++) {
            statementBuilder.append(messageFormat.format(new Object[]{i}));
            if(i < list.size() -1){
                statementBuilder.append(",");
            }
        }
        statementBuilder.append(")");
        return statementBuilder.toString();
    }

    public String deleteBySignIds(Map map){
        //todo 类型检测
        List list = (List)map.get("list");

        String statement =  "DELETE FROM course_sign_record WHERE course_sign_id IN ";
        MessageFormat messageFormat = new MessageFormat("#'{'list[{0}]}");

        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append(statement);
        statementBuilder.append("(");
        for (int i = 0; i < list.size(); i++) {
            statementBuilder.append(messageFormat.format(new Object[]{i}));
            if(i < list.size() -1){
                statementBuilder.append(",");
            }
        }
        statementBuilder.append(")");
        return statementBuilder.toString();
    }

    public String deleteBySignIdsAndStudentId(Map map){
        //todo 类型检测
        List list = (List)map.get("list");
        Integer studentId = (Integer) map.get("studentId");

        String statement =  "DELETE FROM course_sign_record WHERE student_id="+studentId+" AND course_sign_id IN ";
        MessageFormat messageFormat = new MessageFormat("#'{'list[{0}]}");

        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append(statement);
        statementBuilder.append("(");
        for (int i = 0; i < list.size(); i++) {
            statementBuilder.append(messageFormat.format(new Object[]{i}));
            if(i < list.size() -1){
                statementBuilder.append(",");
            }
        }
        statementBuilder.append(")");
        return statementBuilder.toString();
    }

    public String updateAll(Map map){
        List list = (List)map.get("list");

        MessageFormat messageFormat = new MessageFormat("UPDATE course_sign_record SET state=#'{'list[{0}].state}" +
                ",sign_time=#'{'list[{0}].sign_time} WHERE id=#'{'list[{0}].id}");

        StringBuilder statementBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            statementBuilder.append(messageFormat.format(new Object[]{i}));
            if(i < list.size() -1){
                statementBuilder.append(";");
            }
        }
        return statementBuilder.toString();
    }


    //添加到evaluate_sign_record表
    public String insertAllEvaluate(Map map){
        //todo 类型检测
        List list = (List)map.get("list");

       /* String statement =  "INSERT INTO evaluate_sign_record(course_sign_id, student_id,state,sign_time) VALUES";
        MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].course_sign_id}, #'{'list[{0}].student_id}," +
                " #'{'list[{0}].state}, #'{'list[{0}].sign_time})");*/

       String statement =  "INSERT INTO evaluate_sign_record(course_sign_id, student_id,longitude,latitude, state,accuracy,device_no,sign_time) VALUES";
        MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].course_sign_id}, #'{'list[{0}].student_id}," +
                " #'{'list[{0}].longitude}, #'{'list[{0}].latitude} ,#'{'list[{0}].state}, #'{'list[{0}].accuracy},"+
                " #'{'list[{0}].device_no}, #'{'list[{0}].sign_time})");

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

    public String updateEvaluateAll(Map map){
        List list = (List)map.get("list");

        MessageFormat messageFormat = new MessageFormat("UPDATE evaluate_sign_record SET state=#'{'list[{0}].state}" +
                ",sign_time=#'{'list[{0}].sign_time} WHERE id=#'{'list[{0}].id}");

        StringBuilder statementBuilder = new StringBuilder();
        int temp = 0;
        for (int i = 0; i < list.size(); i++) {
            statementBuilder.append(messageFormat.format(new Object[]{i}));
            if(i < list.size() -1){
                statementBuilder.append(";");
            }
        }
        return statementBuilder.toString();
    }


}
