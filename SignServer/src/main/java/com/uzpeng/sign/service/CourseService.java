package com.uzpeng.sign.service;

import com.uzpeng.sign.bo.CourseBO;
import com.uzpeng.sign.bo.CourseListBO;
import com.uzpeng.sign.bo.CourseTimeBO;
import com.uzpeng.sign.bo.CourseTimeListBO;
import com.uzpeng.sign.dao.CourseDAO;
import com.uzpeng.sign.dao.CourseTimeDAO;
import com.uzpeng.sign.dao.SignDAO;
import com.uzpeng.sign.domain.CourseTimeDO;
import com.uzpeng.sign.domain.SemesterDO;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.util.SemesterIdUtil;
import com.uzpeng.sign.web.dto.CourseDTO;
import com.uzpeng.sign.web.dto.SemesterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private CourseTimeDAO courseTimeDAO;
    @Autowired
    private StudentService studentService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private SignDAO signDAO;



    public void addCourse(CourseDTO courseDTO){
       int courseId = courseDAO.addCourse(ObjectTranslateUtil.courseDTOToCourseDO(courseDTO));

        //将上课时间添加到数据库中
       courseTimeDAO.addCourseTimeList(ObjectTranslateUtil.courseDTOToCourseTimeDO(courseDTO, courseId));

        /*//将老师信息添加到 学期管理中  不需要
        SemesterDO semesterDO = new SemesterDO();
        semesterDO.setName(SemesterIdUtil.getSemesterName());
        semesterDO.setTeacherId(courseDTO.getTeacherId());
        semesterService.addSemester(semesterDO);*/


        //将学生数组添加到 数据库中
        studentService.addStudentList(courseDTO.getStudents(),courseId);
        //studentService.addStudentList(courseDTO.getStudents(),2);
    }

    public CourseListBO getCourse(Integer teacherId){
        return courseDAO.getCourseList(teacherId);
    }

    public CourseBO getCourseById(Integer courseId){
        return courseDAO.getCourseById(courseId);
    }

   /* public CourseListBO getCourseByName(String name){
        return courseDAO.getCourseByName(name);
    }*/

    public void deleteCourseById(Integer id){
         courseDAO.deleteCourseById(id);
    }


    public void updateCourse(CourseDTO courseDTO){
        Integer courseId = Integer.parseInt(courseDTO.getCourseId());

        List<CourseTimeDO> courseTimeDOs = ObjectTranslateUtil.courseDTOToCourseTimeDO(courseDTO, courseId);
        boolean isExistSignRecord = signDAO.checkIsExistRecordByCourseId(courseId);    //因为发起的签到存在  课程 上课地点，上课时间 星期一1-2节，这些信息可能会改
        if(isExistSignRecord){            //若存在该门课的签到记录，则设置  数据库中 courseTime的flag为1，可能是因为存在外键 ，不好删除
            courseTimeDAO.updateCourseTimeList(courseId);
        } else {
            courseTimeDAO.deleteCourseTime(courseId);  //否则则直接删除 该课程存在的 上课时间courseTime
        }
        courseTimeDAO.addCourseTimeList(courseTimeDOs);   //经过上面废弃 之前该课程存在的上课时间，这时则直接添加新进来的上课时间即可
        courseDAO.updateCourse(courseDTO);                //还要更新原本 课程的 课程名或者课程号
    }


    public CourseTimeListBO getCourTimeById(Integer courseId){
        CourseBO courseBO = courseDAO.getCourseById(courseId);
        List<CourseTimeDO> courseTimeDOs = courseTimeDAO.getCourseTimeByCourseId(courseId);

        List<CourseTimeBO> courseTimeBOs = new ArrayList<>();
        for (CourseTimeDO courseTimeDO :
                courseTimeDOs) {
            courseTimeBOs.add(ObjectTranslateUtil.courseTimeDOToCourseTimeBO(courseTimeDO));
        }

        CourseTimeListBO courseTimeListBO = new CourseTimeListBO();
        courseTimeListBO.setList(courseTimeBOs);
        courseTimeListBO.setStartWeek(courseBO.getStartWeek());
        courseTimeListBO.setEndWeek(courseBO.getEndWeek());

        return courseTimeListBO;
    }
}
