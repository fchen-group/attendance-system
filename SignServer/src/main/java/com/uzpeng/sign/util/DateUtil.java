package com.uzpeng.sign.util;

import com.uzpeng.sign.domain.CourseTimeDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class DateUtil {
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static boolean isHistoryCourse(Integer semester){
        int year = semester / 10;
        int num = semester % 10;

        int month = num == 1 ? 3 : 8;
        LocalDate courseTime = LocalDate.of(year+1, month, 1);

        return  LocalDate.now().isAfter(courseTime);
    }

    public static String semesterNumToName(Integer semesterId){
        MessageFormat messageFormat = new MessageFormat("{0}-{1} 第{2}学期");

        int year = semesterId / 10;
        int num = semesterId % 10;

        String startYear= String.valueOf(year);
        String endYear= String.valueOf(year+1);
        String semesterNum = num == 1 ? "一" : "二";

        return messageFormat.format(new Object[]{startYear, endYear, semesterNum});
    }

    public static Integer semesterNameToNum(String name){
        String patternStr = "^[1-9]\\d{3}-[1-9]\\d{3}\\s第[一二]学期$";

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(name);
        if(matcher.matches()){
            String[] data = name.split("\\s");

            String yearStr = data[0].substring(0,4);
            Character semesterNumChar = data[1].charAt(1);

            int year = Integer.parseInt(yearStr);
            int semesterNum = semesterNumChar == '一' ? 1 : 2;

            return year * 10 + semesterNum;
        } else {
            return -1;
        }
    }

    public static Integer getWeekFrom(LocalDateTime time){
        LocalDateTime now = LocalDateTime.now();
        GregorianCalendar paramCalendar = new GregorianCalendar(time.getYear(), time.getMonthValue(), time.getDayOfMonth());
        GregorianCalendar nowCalendar = new GregorianCalendar(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        paramCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        nowCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        Integer paramWeek = paramCalendar.get(GregorianCalendar.WEEK_OF_YEAR);
        Integer currentWeek = nowCalendar.get(GregorianCalendar.WEEK_OF_YEAR);

        logger.info("Parameter time is "+time+",paramWeek:"+paramWeek+",currentWeek:"+currentWeek);

        /**
         * 深大校历从开始时间的下一周开始算第一周
         */
        return currentWeek - paramWeek;
    }

}
