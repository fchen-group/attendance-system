package com.uzpeng.sign.util;

import com.uzpeng.sign.bo.*;
import com.uzpeng.sign.domain.*;
import com.uzpeng.sign.web.dto.*;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class ObjectTranslateUtil {

    public static UserDO registerDTOToUserDO(RegisterDTO registerDTO){
        UserDO userDO = new UserDO();

        userDO.setName(registerDTO.getUsername());
        userDO.setPassword(CryptoUtil.encodePassword(registerDTO.getPassword()));
        userDO.setEmail(registerDTO.getEmail());
        userDO.setRegisterTime(LocalDateTime.now());

        return userDO;
    }
    public static wxLoginDO wxloginDTOTowxloginDO(wxLoginDTO wxloginDto){
        wxLoginDO loginDO = new wxLoginDO();
        loginDO.setUsername(wxloginDto.getUsername());
        loginDO.setPassword(wxloginDto.getPassword());
        loginDO.setOpenId(wxloginDto.getOpenId());
        return loginDO;
    }

    public static TeacherDO teacherDTOToTeacherDO(TeacherDTO teacherDTO){
        TeacherDO teacherDO = new TeacherDO();

        teacherDO.setName(teacherDTO.getName());
        teacherDO.setTeacher_num(teacherDTO.getTeacherNum());
        teacherDO.setCollege(teacherDTO.getCollege());
        return teacherDO;
    }

    public static TeacherBO teacherDOToTeacherBO(TeacherDO teacherDO){
        TeacherBO teacherBO = new TeacherBO();

        teacherBO.setId(teacherDO.getId());
        teacherBO.setName(teacherDO.getName());
        teacherBO.setTeacher_num(teacherDO.getTeacher_num());
        teacherBO.setCollege(teacherDO.getCollege());
        return teacherBO;
    }



    /*public static SemesterDO semesterDTOToSemesterDO(SemesterDTO semesterDTO, int teacherId){
        SemesterDO semesterDO = new SemesterDO();

        if(semesterDTO.getId() !=null) {
            semesterDO.setId(semesterDTO.getId());
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

        Integer semesterNum = Integer.parseInt(semesterDTO.getStartYear()) * 10 + Integer.parseInt(semesterDTO.getSemester());
        semesterDO.setName(DateUtil.semesterNumToName(semesterNum));
        semesterDO.setTeacherId(teacherId);
        semesterDO.setStartTime(LocalDateTime.parse(semesterDTO.getDate().get(0), dateTimeFormatter));
        semesterDO.setEndTime(LocalDateTime.parse(semesterDTO.getDate().get(1), dateTimeFormatter));

        return semesterDO;
    }
*/
    public static CourseDO courseDTOToCourseDO(CourseDTO courseDTO){
        CourseDO courseDO = new CourseDO();

        if(courseDTO.getCourseId() != null) {
            courseDO.setId(Integer.parseInt(courseDTO.getCourseId()));
        }
        courseDO.setTeacherId(courseDTO.getTeacherId());
        courseDO.setName(courseDTO.getCourseName());
        courseDO.setCourseNum(courseDTO.getCourseNum());
        courseDO.setSemester(Integer.parseInt(courseDTO.getSemester()));
        //courseDO.setStartWeek(courseDTO.getStartWeek());
       // courseDO.setEndWeek(courseDTO.getEndWeek());

        return courseDO;
    }

    public static List<CourseTimeDO> courseDTOToCourseTimeDO(CourseDTO courseDTO, int courseId){
        List<CourseTimeDO> courseTimeList = new ArrayList<>();

        List<CourseTimeDetailDTO> courseTimeDetails =courseDTO.getTime();
        for (CourseTimeDetailDTO courseTimeDetailDTO:
                courseTimeDetails) {
            System.out.println(courseTimeDetailDTO);
        }
        for (CourseTimeDetailDTO time:
             courseTimeDetails) {

            CourseTimeDO courseTimeDO = new CourseTimeDO();
            courseTimeDO.setLoc(time.getLoc());
            courseTimeDO.setCourseSectionStart(time.getStart());
            courseTimeDO.setCourseSectionEnd(time.getEnd());
            int weekday = time.getWeekday();
            courseTimeDO.setCourseWeekday(weekday == 0 ? 7 : weekday);
            courseTimeDO.setCourseId(courseId);

            courseTimeList.add(courseTimeDO);
        }

        return courseTimeList;
    }

    public static StudentDO studentDTOToStudentDO(StudentDTO studentDTO){
        StudentDO studentDO = new StudentDO();

        studentDO.setName(studentDTO.getName());
        studentDO.setClassInfo(studentDTO.getClassInfo());
        studentDO.setNum(studentDTO.getStudentNum());

        return studentDO;
    }

   public static LoginDTO passwordDTOToLoginDTO(PasswordDTO passwordDTO){
       LoginDTO loginDTO = new LoginDTO();

       loginDTO.setUsername(passwordDTO.getUserName());
       loginDTO.setPassword(passwordDTO.getOldPassword());

       return loginDTO;
   }

   /*public static SemesterBO semesterDOToSemesterBO(SemesterDO semesterDO){
       SemesterBO semesterBO = new SemesterBO();
       semesterBO.setSemesterId(semesterDO.getId());

       Integer semesterInt = DateUtil.semesterNameToNum(semesterDO.getName());
       semesterBO.setNum(semesterInt % 10);
       semesterBO.setStartYear(semesterInt / 10);
       semesterBO.setEndYear(semesterInt / 10 + 1);
       semesterBO.setSemesterName(String.valueOf(semesterDO.getName()));
       DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
       semesterBO.setStartTime(semesterDO.getStartTime().format(dateTimeFormatter));
       semesterBO.setEndTime(semesterDO.getEndTime().format(dateTimeFormatter));

       return semesterBO;
   }*/

   public static StudentBO studentDOToStudentBO(StudentDO studentDO){
        StudentBO studentBO = new StudentBO();

        studentBO.setId(studentDO.getId());
        studentBO.setName(studentDO.getName());
        studentBO.setStudentNum(studentDO.getNum());
        studentBO.setClassInfo(studentDO.getClassInfo());

        return studentBO;
   }

   public static CourseBO courseDOToCourseBO(CourseDO courseDO, List<CourseTimeDO> courseTimeDOList
                                             ){
       CourseBO courseBO = new CourseBO();
       courseBO.setCourseId(courseDO.getId());
       courseBO.setCourseName(courseDO.getName());
       courseBO.setCourseNum(courseDO.getCourseNum());
       courseBO.setStartWeek(courseDO.getStartWeek());
       courseBO.setEndWeek(courseDO.getEndWeek());
       courseBO.setTime(new ArrayList<>());
       courseBO.setTeacherId(courseDO.getTeacherId());

       for (CourseTimeDO courseTimeDO :
               courseTimeDOList) {
           CourseTimeDetailBO timeDetail = new CourseTimeDetailBO();

           timeDetail.setCourseTimeId(courseTimeDO.getId());
           timeDetail.setStart(courseTimeDO.getCourseSectionStart());
           timeDetail.setEnd(courseTimeDO.getCourseSectionEnd());
           timeDetail.setWeekday(courseTimeDO.getCourseWeekday());
           timeDetail.setLoc(courseTimeDO.getLoc());

           courseBO.getTime().add(timeDetail);
       }
       return courseBO;
   }

   public static String courseTimeDoToString(CourseTimeDO courseTimeDO){
        int weekday = courseTimeDO.getCourseWeekday();
        int start = courseTimeDO.getCourseSectionStart();
        int end = courseTimeDO.getCourseSectionEnd();

        String[] weekdayStr={"一","二","三","四","五","六","日"};
        String formatStr = "星期{0} 第{1}节-第{2}节";

       MessageFormat messageFormat = new MessageFormat(formatStr);
       return messageFormat.format(new Object[]{weekdayStr[weekday-1], start, end});
   }

   public static CourseTimeBO courseTimeDOToCourseTimeBO(CourseTimeDO courseTimeDO){
        CourseTimeBO courseTimeBO = new CourseTimeBO();

        courseTimeBO.setCourseId(courseTimeDO.getCourseId());
        courseTimeBO.setCourseSectionStart(courseTimeDO.getCourseSectionStart());
        courseTimeBO.setCourseSectionEnd(courseTimeDO.getCourseSectionEnd());
        courseTimeBO.setCourseWeekday(courseTimeDO.getCourseWeekday());
        courseTimeBO.setLoc(courseTimeDO.getLoc());
        courseTimeBO.setId(courseTimeDO.getId());

        return courseTimeBO;
   }
}
