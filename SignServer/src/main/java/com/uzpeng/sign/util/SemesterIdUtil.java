package com.uzpeng.sign.util;

import java.util.Calendar;

public class SemesterIdUtil {

    public static String getSemesterName(){
        Calendar now = Calendar.getInstance();
        //now.set(2019,0,2);
        int mon = now.get(Calendar.MONTH)+1;
        String str = "";
        int Year = now.get(Calendar.YEAR);
        if((mon >= 9 && mon <= 12)) {             //默认秋季课程在9月份到2月份
            str += "秋季课程";
        }
        else if(mon >= 1 && mon <= 2){
            str +="秋季课程";
            Year -= 1;                           //第二年需要修改下
        }
        else if((mon >= 3 && mon <= 7))                                 //默认秋季课程在3月份到7月份
            str+="春季课程";
        else
            str+="非春秋季课程";
        return Year+str;
    }


    public static void main(String [] args){
        System.out.println(getSemesterName());
    }





}
