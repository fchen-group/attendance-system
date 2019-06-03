package com.uzpeng.sign.persistence;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class CourseTimeProvider {

    public String insertCourseTimeList(Map map){
        List list = (List)map.get("list");

        String statement =  "INSERT INTO course_time(course_id, course_weekday, course_section_start," +
                "course_section_end, loc,flag) VALUES";
        MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].courseId}, #'{'list[{0}].course_weekday}," +
                " #'{'list[{0}].course_section_start}, #'{'list[{0}].course_section_end}, #'{'list[{0}].loc},0)");

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

    public String updateCourseTimeList(Map map){
        List list = (List) map.get("list");

        String statement =  "UPDATE course_time SET";
        MessageFormat messageFormat = new MessageFormat("course_weekday=#'{'list[{0}].course_weekday}," +
                "course_section_start=#'{'list[{0}].course_section_start}," +
                " course_section_end=#'{'list[{0}].course_section_end}, loc=#'{'list[{0}].loc})" +
                "WHERE id=list[{0}].id");

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
