package com.uzpeng.sign.dao;

import com.uzpeng.sign.bo.StudentBO;
import com.uzpeng.sign.bo.StudentBOList;
import com.uzpeng.sign.bo.StudentDetailBO;
import com.uzpeng.sign.bo.StudentDetailBOList;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.*;
import com.uzpeng.sign.persistence.SignRecordMapper;
import com.uzpeng.sign.persistence.StudentMapper;
import com.uzpeng.sign.util.CryptoUtil;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.util.RandomUtil;
import com.uzpeng.sign.util.Role;
import com.uzpeng.sign.web.dto.SignRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentDAO {
    private static final Logger logger = LoggerFactory.getLogger(StudentDAO.class);

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private SignRecordMapper signRecordMapper;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private SelectiveCourseDAO selectiveCourseDAO;
    @Autowired
    private SignDAO signDAO;

    //添加学生数组，并将课程与学生关联
    public void insertStudents(List<StudentDO> students, Integer courseId){
        logger.info("student list's size is " + students.size());

        List<String> existNumList = studentMapper.getStudentNum();  //存在的学生学号

        logger.info("existNumList is "+existNumList.size());

        List<String> repeatNumList = new ArrayList<>();             //已存在的学生
        List<StudentDO> noAccountStudents = new ArrayList<>();      //第一次出现的学生

        for (StudentDO student :
                students) {
            if (existNumList.size() == 0 || !existNumList.contains(student.getNum())){
                noAccountStudents.add(student);
            } else if(existNumList.size() != 0){
                repeatNumList.add(student.getNum());
            }
        }
        logger.info("noAccountStudents is "+noAccountStudents.size());

        if(noAccountStudents.size() > 0) {   //除了添加到 student ,还要添加到user中
            studentMapper.insertStudentList(noAccountStudents);

            List<UserDO> users = new ArrayList<>();
            for (StudentDO currentStudent : noAccountStudents) {
                UserDO tmpUser = new UserDO();

                tmpUser.setName(String.valueOf(currentStudent.getNum()));
                tmpUser.setPassword(CryptoUtil.encodePassword(String.valueOf(currentStudent.getNum())));
                tmpUser.setRole(Role.STUDENT);
                tmpUser.setRoleId(currentStudent.getId());
                tmpUser.setRegisterTime(LocalDateTime.now());
                users.add(tmpUser);       //不允许学生自主注册
            }
            userDAO.insertUserList(users);
        }

        insertSelectiveCourse(students, courseId);   //课程与学生关联

        List<SignDO> signDOList = signDAO.getAllSignByCourseId(courseId);   //获取当前课程开启的 签到任务
        List<StudentBO> studentBOS = new ArrayList<>();
        if(repeatNumList.size() > 0) {                                //是不是重复？直接根据原始students即可获得
            List<StudentDO> repeatStudents = studentMapper.getStudentsByNum(repeatNumList); //获取重复的学生信息
            for (StudentDO student :
                    repeatStudents) {
                studentBOS.add(ObjectTranslateUtil.studentDOToStudentBO(student));
            }
        }
        for (StudentDO student :
                noAccountStudents) {
            studentBOS.add(ObjectTranslateUtil.studentDOToStudentBO(student));
        }

        for (SignDO sign :
                signDOList) {   //把所有新加入的学生添加到 以前的签到记录中
            signDAO.addStudentToSignRecord(sign.getId(), studentBOS);
        }
    }

    public static void main(String[] args) {
        System.out.println(CryptoUtil.encodePassword("123456"));
        System.out.println(CryptoUtil.match("123456","$2a$04$gt9UzHOieol7uxYji6Y03u9k99BwY6kQZi6/o85b90ctXy2BII4R6"));
    }

    //单个添加学生时，亦是调用上面的添加 学生数组
    public void insertStudent(StudentDO studentDO, Integer courseId){
        List<StudentDO> student = new ArrayList<>();
        student.add(studentDO);
        insertStudents(student, courseId);
    }

    //修改学生信息
    public void updateStudent(StudentDO studentDO){
        studentMapper.updateStudent(studentDO);

    }

    public StudentDO getStudentById(Integer studentId){
        return studentMapper.getStudent(studentId);
    }


    public StudentDetailBOList getStudentDetailList(Integer courseId){
        List<StudentBO> studentBOs = getStudent(courseId).getStudentList();  //调用下面已有接口

        //根据courseID 获取 该课程创建的所有 签到记录ID
        List<Integer>  signIDs =  signDAO.getSignIDByCourseId(courseId);
        double signAmount = (double)signIDs.size();     //总签到次数

        List<StudentDetailBO> studentDetailBOList = new ArrayList<StudentDetailBO>();
        for (StudentBO studentBO: studentBOs ) {

            StudentDetailBO studentDetailBO = new StudentDetailBO();
            studentDetailBO.setId(studentBO.getId());
            studentDetailBO.setStudentNum(studentBO.getStudentNum());
            studentDetailBO.setName(studentBO.getName());
            studentDetailBO.setClassInfo(studentBO.getClassInfo());

            int signedAmount = 0;                 //该学生 已签到次数
            for (Integer signID:signIDs) {        //当前   SignId下，该学生ID的的签到状况
                int count = signRecordMapper.getSignCount(signID, StatusConfig.RECORD_SUCCESS,studentBO.getId()); //已签到人数
                if(count > 0){
                    signedAmount+=1;
                }
            }

            studentDetailBO.setSignedNum(signedAmount);
            studentDetailBO.setNotSignedNum((int)(signAmount - signedAmount));
            studentDetailBO.setSignRate((int)(signedAmount/signAmount*100)+"%");
            studentDetailBOList.add(studentDetailBO);
        }

        StudentDetailBOList studentDetailBO = new StudentDetailBOList();
        studentDetailBO.setStudentDetailBOList(studentDetailBOList);
        return studentDetailBO;

    }

    public StudentBOList getStudent(Integer courseId){
        List<StudentBO> studentBOs = new ArrayList<>();

        List<Integer> studentId = selectiveCourseDAO.getStudentIdByCourseId(courseId);

        if(studentId == null || studentId.size() == 0){
            StudentBOList studentBOList = new StudentBOList();
            studentBOList.setStudentList(studentBOs);
            return studentBOList;
        }
        List<StudentDO> studentDOs = studentMapper.getStudentListByStudentId(studentId);

        for (StudentDO studentDO :
                studentDOs) {
            studentBOs.add(ObjectTranslateUtil.studentDOToStudentBO(studentDO));
        }

        StudentBOList studentBOList = new StudentBOList();
        studentBOList.setStudentList(studentBOs);

        return studentBOList;
    }

    public StudentBOList searchStudentByNum(String num){
        String queryStr = "%" + num + "%";

        StudentBOList studentBOList = new StudentBOList();

        List<StudentBO> studentBOS = new ArrayList<>();
        List<StudentDO> studentDOS = studentMapper.getStudentListByStudentNum(queryStr);
        for (StudentDO studentDO :
                studentDOS) {
            StudentBO studentBO = ObjectTranslateUtil.studentDOToStudentBO(studentDO);
            studentBOS.add(studentBO);
        }

        studentBOList.setStudentList(studentBOS);

        return studentBOList;
    }





    public void removeStudent(Integer courseId, Integer studentId){
        //好像没有删除 数据库中学生信息 ，但是应该不用，只要删除 学生表与课程表的联系表即可
        selectiveCourseDAO.removeStudent(courseId, studentId);
        signDAO.deleteSignRecord(courseId, studentId);
    }


    //关联 学生和课程表
    private void insertSelectiveCourse(List<StudentDO> studentDOS, Integer courseId){
        logger.info("insert selective course info....");

        List<String> numList = new ArrayList<>();

        for (StudentDO student :
                studentDOS) {
            numList.add(student.getNum());  //所有学生学号
        }

        List<Integer> ids = studentMapper.getStudentIdByNum(numList);   //学生ID

        List<SelectiveCourseDO> selectiveCourseDOS = new ArrayList<>();
        for (Integer id : ids) {
            SelectiveCourseDO selectiveCourseDO = new SelectiveCourseDO();
            selectiveCourseDO.setCourseId(courseId);
            selectiveCourseDO.setStudentId(id);

            selectiveCourseDOS.add(selectiveCourseDO);
        }
        selectiveCourseDAO.addSelectiveCourseList(selectiveCourseDOS);
    }

    public StudentBOList pickStudent(Integer courseId, int amount){
        StudentBOList studentBOList = getStudent(courseId);

        List<StudentBO> studentBOS = studentBOList.getStudentList();

        List<StudentBO> result = RandomUtil.pickAmountRandomly(studentBOS, amount);
        studentBOList.setStudentList(result);

        return studentBOList;
    }

    public void sign(SignRecordDTO signRecordDTO){

    }
}
