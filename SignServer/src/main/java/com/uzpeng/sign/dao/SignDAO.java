package com.uzpeng.sign.dao;

import com.mysql.jdbc.PacketTooBigException;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.bo.*;
import com.uzpeng.sign.domain.*;
import com.uzpeng.sign.persistence.SignMapper;
import com.uzpeng.sign.persistence.SignRecordMapper;
import com.uzpeng.sign.util.CommonResponseHandler;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.web.dto.SignRecordDTO;
import com.uzpeng.sign.web.dto.UpdateSignRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SignDAO {
    private final Logger logger = LoggerFactory.getLogger(SignDAO.class);
    @Autowired
    private SignRecordMapper signRecordMapper;
    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private SemesterDAO semesterDAO;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private SelectiveCourseDAO selectiveCourseDAO;
    @Autowired
    private CourseTimeDAO courseTimeDAO;

    @Autowired
    private SignMapper signMapper;
    @Autowired
    private TeacherDAO teacherDAO;

    public static void main(String [] args){


        //测试日期转换
        /*LocalDateTime rightNow=LocalDateTime.now();
         *//*String date=DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(rightNow);
        System.out.println(date);*//*
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        System.out.println(formatter.format(rightNow));*/

        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        System.out.println(now.plusHours(2));
    }


    //创建签到记录
    public Integer createSign(Integer courseTimeId, Integer week,Integer courseId){

        //CourseTimeDO courseTimeDO = courseTimeDAO.getCourseTimeById(courseTimeId);
        //Integer courseId = courseTimeDO.getCourseId();
        //System.out.println("++++++courseId:"+courseId);


        /*CourseBO courseBO = courseDAO.getCourseById(courseId);

        String startTimeStr = semesterDAO.getSemesterById(courseBO.getSemesterId(),
                courseBO.getTeacherId()).getStartTime();

       LocalDateTime startTime = LocalDateTime.parse(startTimeStr);*/  //  startTime没有用到

        /*if(signMapper.checkExistSign(courseTimeId, week) > 0){
            throw SpringContextUtil.getBeanByClass(DuplicateDataException.class);
        }*/

        SignDO signDO = new SignDO();
        signDO.setCourseId(courseId);
        LocalDateTime now = LocalDateTime.now();
        signDO.setCreateTime(now);
        //同时设置签到  两小时后过期时间  //出现Bug，就是mapper忘记添加这个
        signDO.setEnd_time(now.plusHours(2));

        signDO.setWeek(week);
        signDO.setCourseTimeId(courseTimeId);
        signDO.setState(StatusConfig.SIGN_START_FLAG);   //合并两个接口
        //signDO.setState(StatusConfig.SIGN_CREATE_FLAG);
        /*List<SignDO> signDOs = new ArrayList<>();
        signDOs.add(signDO);*/
        //返回signID

        signMapper.insertSign(signDO);                                                   //第一步添加 sign
        //System.out.println("+++++++++++++++signID11："+signDO.getId());
        Integer signID = signDO.getId();
        StudentBOList studentList = studentDAO.getStudent(courseId);
        List<StudentBO> studentBOS = studentList.getStudentList();
        /*for (StudentBO studentBO :
                studentBOS) {
            System.out.println(studentBO);
        }*/
        //addStudentToSignRecord(signDO.getId(), studentBOS);                            //同时将学生都添加到签到记录中
        addStudentToSignRecord(signID, studentBOS);
        return signID;
    }

    public void deleteSign(Integer signId){

        //先根据signId删除 学生签到记录
        signRecordMapper.deleteSignRecordBySignId(signId);
        //再删除该次签到
        signMapper.deleteSignBySignId(signId);




    }
    public void insertEvaluateSignRecordList(List<SignRecordDO> signRecordDOS){
        signRecordMapper.insertEvaluateSignRecordList(signRecordDOS);
    }

    public void addStudentToSignRecord(Integer signId, List<StudentBO> studentBOS){
        List<SignRecordDO> signRecordDOS = new ArrayList<>();

        for (StudentBO studentBO :
                studentBOS) {
            SignRecordDO signRecordDO = new SignRecordDO();

            signRecordDO.setStudentId(studentBO.getId());
            signRecordDO.setSignId(signId);
            signRecordDO.setSignTime(LocalDateTime.of(1, 1,1,1,1));
            signRecordDO.setState(StatusConfig.RECORD_CREATED);
            signRecordDOS.add(signRecordDO);
        }
        signRecordMapper.insertSignRecordList(signRecordDOS);
    }


    //获取具体 学号学生的签到记录
    public SignRecordListBO getSignRecordByTime(Integer courseId, Integer courseTimeId, Integer week, String num){
        SignDO signDO = signMapper.getSignByTimeAndCourse(courseId, courseTimeId, week);

        List<SignRecordBO> signRecordBOs = new ArrayList<>();

        List<Integer> studentIds = new ArrayList<>();
        if(num != null ){
            StudentBOList studentBOList = studentDAO.searchStudentByNum(num);  //进行模糊查询
            List<StudentBO> studentBOs = studentBOList.getStudentList();

            for (StudentBO studentBO :
                    studentBOs) {
                studentIds.add(studentBO.getId());
            }
        }  //获得 需要查询的 学生ID

        //获得创建的 签到
        Integer signId = signDO.getId();
        List<SignRecordDO> records = signRecordMapper.getSignRecord(signId);//获得该签到任务的 每个学生 DO记录


        for (SignRecordDO recordDO :
                records) {
            Integer studentId = recordDO.getStudentId();
            if(num != null){
                if(!studentIds.contains(studentId)){
                    continue;
                }
            }   //当找到该学生有 签到记录，则进行以下操作

            StudentDO studentDO = studentDAO.getStudentById(studentId);

            SignRecordBO signRecordBO = new SignRecordBO();
            signRecordBO.setCourseId(courseId);
            signRecordBO.setStudentNum(studentDO.getNum());
            signRecordBO.setName(studentDO.getName());
            signRecordBO.setClass_info(studentDO.getClassInfo());
            signRecordBO.setId(recordDO.getId());
            signRecordBO.setWeek(signDO.getWeek());
            signRecordBO.setState(recordDO.getState());

            CourseTimeDO courseTimeDO = courseTimeDAO.getCourseTimeById(courseTimeId);

            signRecordBO.setWeekday(ObjectTranslateUtil.courseTimeDoToString(courseTimeDO));

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            signRecordBO.setSignTime(dateTimeFormatter.format(recordDO.getSignTime()));

            signRecordBOs.add(signRecordBO);
        }

        SignRecordListBO signRecordListBO = new SignRecordListBO();
        signRecordListBO.setList(signRecordBOs);
        signRecordListBO.setSignId(signId);
        return signRecordListBO;
    }

    public SignRecordListBO getSignRecordListBySignId(Integer signId){
        List<SignRecordDO> records = signRecordMapper.getSignRecord(signId);//获得该签到任务的 每个学生 DO记录
        List<SignRecordBO> signRecordBOs = new ArrayList<>();

        //将List<SignRecordDO> 变为 List<SignRecordBO>
        for (SignRecordDO recordDO :
                records) {

            SignRecordBO signRecordBO = new SignRecordBO();
            signRecordBO.setId(recordDO.getId());

            Integer studentId = recordDO.getStudentId();

            StudentDO studentDO = studentDAO.getStudentById(studentId);

            //当前端需要 courseId再给
            /*
               Integer SignId =  recordDO.getSignId();
               Integer courseId = getCourseIDBysignID(SignId); //代写
               signRecordBO.setCourseId(courseId);  //根据
             */

            signRecordBO.setStudentNum(studentDO.getNum());
            signRecordBO.setName(studentDO.getName());
            signRecordBO.setClass_info(studentDO.getClassInfo());
            signRecordBO.setState(recordDO.getState());   //签到状态

            /*
            SignDO signDO = signMapper.getSignById(signId);
            signRecordBO.setWeek(signDO.getWeek());      //周几
            CourseTimeDO courseTimeDO = courseTimeDAO.getCourseTimeById(courseTimeId);
            signRecordBO.setWeekday(ObjectTranslateUtil.courseTimeDoToString(courseTimeDO));
            */
            //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
            signRecordBO.setSignTime(dateTimeFormatter.format(recordDO.getSignTime())); //签到时间
            signRecordBOs.add(signRecordBO);
        }

        SignRecordListBO signRecordListBO = new SignRecordListBO();
        signRecordListBO.setList(signRecordBOs);
        signRecordListBO.setSignId(signId);
        return signRecordListBO;
    }

    public List<SignRecordDO> getSignRecordBySignId(Integer signId){
        return signRecordMapper.getSignedRecord(signId);
    }

    //获取EvaluateSignRecord表中id
    public List<SignRecordDO> getEvaluateSignRecord(Integer signId){
        return signRecordMapper.getEvaluateSignRecord(signId);
    }

    //该课程下有多少次签到id
    public  List<Integer> getSignIDByCourseId(Integer courseId){
        return signMapper.getSignIdByCourseId(courseId);
    }

    //获取所有签到记录
    public DownloadSignRecordBOList getAllSignRecord(Integer courseId){
        //根据课程courseId获取该班级所有学生
        StudentBOList studentBOList = studentDAO.getStudent(courseId);
        List<StudentBO> studentBOS = studentBOList.getStudentList();

        DownloadSignRecordBOList downloadSignRecordBOList = new DownloadSignRecordBOList();
        List<List<StudentSignRecordBO>> studentSignList = new ArrayList<>();

        //遍历该课程所有学生
        for(StudentBO studentBO :
                studentBOS) {
            int studentId = studentBO.getId();

            //获取 该学生所有记录 ALL
            StudentSignRecordListBO studentSignRecordListBO = getSignRecordByStudentId(studentId, StatusConfig.ALL);


            //如果当前学生签到记录中，课程ID并非该课程，则去掉
            //这里使用了 CopyOnWriteArrayList
            CopyOnWriteArrayList<StudentSignRecordBO> studentSignRecordBOList = new CopyOnWriteArrayList<>();
            studentSignRecordBOList.addAll(studentSignRecordListBO.getList());
            for (StudentSignRecordBO record :
                    studentSignRecordBOList) {
                if(!record.getCourseId().equals(courseId)){
                    studentSignRecordBOList.remove(record);
                }
            }
            studentSignList.add(studentSignRecordBOList);
        }

        downloadSignRecordBOList.setDownloadSignRecordLists(studentSignList);
        return downloadSignRecordBOList;
    }

    public void updateEvaluateSignRecordStatus(List<UpdateSignRecordDTO> updateSignRecordDTOs){
        List<SignRecordDO> records = new ArrayList<>();

        for (UpdateSignRecordDTO newData:
                updateSignRecordDTOs) {
            SignRecordDO signRecordDO = new SignRecordDO();

            signRecordDO.setState(newData.getState());
            signRecordDO.setId(newData.getId());
            signRecordDO.setSignTime(LocalDateTime.now());
            int i = 0; //消除注释
            records.add(signRecordDO);
        }

        signRecordMapper.updateEvaluateSignRecord(records);
    }

    public void updateSignRecordStatus(List<UpdateSignRecordDTO> updateSignRecordDTOs){
        List<SignRecordDO> records = new ArrayList<>();
        for (UpdateSignRecordDTO newData:
                updateSignRecordDTOs) {
            SignRecordDO signRecordDO = new SignRecordDO();

            signRecordDO.setState(newData.getState());
            signRecordDO.setId(newData.getId());
            signRecordDO.setSignTime(LocalDateTime.now());

            records.add(signRecordDO);
        }

        signRecordMapper.updateSignRecord(records);
    }

    public void sign(SignRecordDTO signRecordDTO, Integer studentId){
        //signRecordMapper.sign(signRecordDTO, studentId, StatusConfig.RECORD_SIGNED);
        signRecordMapper.sign(signRecordDTO, studentId, StatusConfig.RECORD_SUCCESS);
    }

   public SignRecordTimeListBO getRecordWeek(Integer courseId){
        double  count = (double)selectiveCourseDAO.getStudentCount(courseId);


        List<SignDO> signDOs = signMapper.getSign(courseId);
        List<SignRecordTimeBO> signRecordWeekBOs = new ArrayList<>();
        for (SignDO signDO :
               signDOs) {

            int signedCount = signRecordMapper.getSignCountBySignId(signDO.getId(), StatusConfig.RECORD_SUCCESS); //已签到人数

            //课程时间 星期二 第1节~第2节
            CourseTimeDO courseTimeDO = courseTimeDAO.getCourseTimeById(signDO.getCourseTimeId());

            SignRecordTimeBO signRecordTimeBO = new SignRecordTimeBO();

            signRecordTimeBO.setSignId(signDO.getId());
            signRecordTimeBO.setWeek(signDO.getWeek());  //2
            signRecordTimeBO.setCourseTime(ObjectTranslateUtil.courseTimeDoToString(courseTimeDO));

            signRecordTimeBO.setAttendanceRate((int)(signedCount/count*100)+"%");
            signRecordTimeBO.setSignedAmount(signedCount);
            signRecordTimeBO.setNotSignAmount((int) (count-signedCount));

            //存在时间转化失败情况 因为老师可能有时忘记点击 结束签到
            //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");

            //logger.info("signDO:-----------"+signDO);
            signRecordTimeBO.setCreatetime(dateTimeFormatter.format(signDO.getCreateTime()));

            signRecordTimeBO.setEndtime(dateTimeFormatter.format(signDO.getEnd_time()));

            //设置此次签到状态
            signRecordTimeBO.setState(signDO.getState());

            //logger.info(signRecordTimeBO+"");
            signRecordWeekBOs.add(signRecordTimeBO);
        }

       SignRecordTimeListBO signRecordTimeListBO = new SignRecordTimeListBO();
       signRecordTimeListBO.setList(signRecordWeekBOs);

       return signRecordTimeListBO;
   }



   public void deleteSignRecord(Integer courseId){
        List<Integer> signIds = signMapper.getSignIdByCourseId(courseId);
        if(signIds.size() > 0) {
            signRecordMapper.deleteBySignIdList(signIds);
            signMapper.deleteSignByCourseId(courseId);
        }
   }

    public void deleteSignRecord(Integer courseId, Integer studentId){
        List<Integer> signIds = signMapper.getSignIdByCourseId(courseId);
        if(signIds.size() > 0) {
            signRecordMapper.deleteBySignIdListAndStudentId(signIds, studentId);
        }
    }

   public Integer getSignState(Integer signId){
        return signMapper.getStateById(signId);
   }

    public void updateSignState(Integer signId, Integer state){
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = signMapper.getSignById(signId).getCreateTime();
        logger.info("endTime:"+endTime);
        logger.info("starTime:"+startTime);
        //不知道为什么会出现 更新结束时间时，会导致 开始时间也改变
        //因此这里也传入开始时间
        //可以考虑要不要同时将未签到的学生状态改为 失败
        signMapper.updateStateById(signId, state,endTime,startTime);
    }


    //网站以及微信端    通过该学生studentId获取   所有签到记录
    public StudentSignRecordListBO getSignRecordByStudentId(Integer studentId, Integer type){

        //获取该学生所有的签到记录
        List<SignRecordDO> signRecordDOs = signRecordMapper.getSignRecordByStudentId(studentId);

        StudentSignRecordListBO studentSignRecordListBO = new StudentSignRecordListBO();

        List<StudentSignRecordBO> studentSignRecordBOS = new ArrayList<>();
        List<StudentSignRecordBO> historyStudentSignRecordBOS = new ArrayList<>();
        List<StudentSignRecordBO> todayStudentSignRecordBOS = new ArrayList<>();

        for (SignRecordDO signRecordDO :
                signRecordDOs) {
            int signId = signRecordDO.getSignId();

            SignDO signDO = signMapper.getSignById(signId);
            CourseBO courseBO = courseDAO.getCourseById(signDO.getCourseId());
            //SemesterDO semesterDO = semesterDAO.getSemesterById(courseBO.getSemesterId(), courseBO.getTeacherId()); //其实不需要传 老师ID因为学期ID唯一
            TeacherDO teacherDO = teacherDAO.getTeacherId(courseBO.getTeacherId());
            CourseTimeDO courseTimeDO = courseTimeDAO.getCourseTimeById(signDO.getCourseTimeId());
            StudentDO studentDO = studentDAO.getStudentById(studentId);

            String weekday = ObjectTranslateUtil.courseTimeDoToString(courseTimeDO);
            String time = "第"+signDO.getWeek()+"周 "+weekday;

            StudentSignRecordBO studentSignRecordBO = new StudentSignRecordBO();

            //学生信息
            studentSignRecordBO.setStudentNum(studentDO.getNum());
            studentSignRecordBO.setStudentName(studentDO.getName());
            studentSignRecordBO.setClassInfo(studentDO.getClassInfo());

            //课程信息
            studentSignRecordBO.setCourseId(courseBO.getCourseId());
            studentSignRecordBO.setCourseNum(courseBO.getCourseNum());
            studentSignRecordBO.setCourse(courseBO.getCourseName());
            studentSignRecordBO.setTeacher(teacherDO.getName());
            studentSignRecordBO.setLoc(courseTimeDO.getLoc());

            //该次签到信息
            studentSignRecordBO.setSignId(signId);
            studentSignRecordBO.setTime(time);
            studentSignRecordBO.setState(signRecordDO.getState());

            //设置该签到开始结束时间
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
            //logger.info("signDO:-----------"+signDO);
            studentSignRecordBO.setCreatetime(dateTimeFormatter.format(signDO.getCreateTime()));
            studentSignRecordBO.setEndtime(dateTimeFormatter.format(signDO.getEnd_time()));

            if(signDO.getState() == 0)
                historyStudentSignRecordBOS.add(studentSignRecordBO);
            else
                todayStudentSignRecordBOS.add(studentSignRecordBO);
            studentSignRecordBOS.add(studentSignRecordBO);
        }

       if(type.equals(StatusConfig.HISTORY)){
           Collections.sort(historyStudentSignRecordBOS);
           studentSignRecordListBO.setList(historyStudentSignRecordBOS);
       } else if(type.equals(StatusConfig.TODAY)){
           Collections.sort(todayStudentSignRecordBOS);
           studentSignRecordListBO.setList(todayStudentSignRecordBOS);
       } else {
           Collections.sort(studentSignRecordBOS);
           studentSignRecordListBO.setList(studentSignRecordBOS);
       }
        //studentSignRecordListBO.setList(studentSignRecordBOS);
        return  studentSignRecordListBO;
    }

    public List<SignDO> getAllSignByCourseId(Integer courseId){
        return signMapper.getSign(courseId);
    }

    public boolean checkIsExistRecordByCourseId(Integer courseId){
        return signMapper.getSign(courseId).size() > 0;
    }
}
