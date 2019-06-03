package com.uzpeng.sign.dao;

import com.uzpeng.sign.bo.CourseBO;
import com.uzpeng.sign.bo.CourseListBO;
import com.uzpeng.sign.bo.CourseSemesterBO;
import com.uzpeng.sign.bo.SemesterDOList;
import com.uzpeng.sign.domain.CourseDO;
import com.uzpeng.sign.domain.CourseTimeDO;
import com.uzpeng.sign.domain.SemesterDO;
import com.uzpeng.sign.persistence.CourseMapper;
import com.uzpeng.sign.service.SemesterService;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.util.SemesterIdUtil;
import com.uzpeng.sign.web.dto.CourseDTO;
import com.uzpeng.sign.web.dto.CourseTimeDetailDTO;
import javafx.scene.effect.SepiaTone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class CourseDAO {
    private static final Logger logger = LoggerFactory.getLogger(CourseDAO.class);

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseTimeDAO courseTimeDAO;
    @Autowired
    private SemesterDAO semesterDAO;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private SelectiveCourseDAO selectiveCourseDAO;
    @Autowired
    private SignDAO signDAO;
    @Autowired
    private StudentDAO studentDAO;

    public int addCourse(CourseDO courseDO){
        courseMapper.insertCourse(courseDO);

        return courseDO.getId();
    }

    public static void main(String [] args){
        HashMap<Integer,List<String>> hm = new HashMap<>();
        List<String> lt = new ArrayList<String>();
       // lt.add("ahha");
        for (int i = 0;i < 5;i++)
            hm.put(i,lt);
         for(int i = 0;i < 5;i++)
         {
             List<String> l = hm.get(i);
             l.add("hehh");
         }

        for(int i = 0;i < 5;i++)
            System.out.println(hm.get(i));
    }

    public  List<Integer> getCourseIdListByTeacherId(Integer teacherId,Integer semesterId){
        return courseMapper.getCourseIdListByTeacherId(teacherId,semesterId);
    }

    public CourseListBO getCourseList(Integer teacherId){

        List<CourseSemesterBO> currentCourseList = new ArrayList<>();
        List<CourseSemesterBO> historyCourseList = new ArrayList<>();

        //获取该老师所有学期信息
        SemesterDOList semesterDOList = semesterService.getSemester(teacherId);
       /* for (SemesterDO semesterDO:
                semesterDOList.getSemesterList()) {
            System.out.println(semesterDO);
        }*/

        //总是出现集合复用情况，需要重新new,而不可以添加之前已经new过的，否则两个list是一样的
        HashMap<Integer,List<CourseBO>> hm = new HashMap<>();
        for(SemesterDO semesterDO:semesterDOList.getSemesterList()){
            List<CourseBO> cl = new ArrayList<CourseBO>();
            hm.put(semesterDO.getId(),cl);
            //System.out.println(hm.get(semesterDO.getId())+"   "+semesterDO.getId());
        }


        //将同一学期的课程归类
        List<CourseDO> courseDOList = courseMapper.getCourseByTeacherId(teacherId);
       for (CourseDO courseDO:
                courseDOList ) {
            System.out.println(courseDO);
        }
        for (CourseDO courseDO :
                courseDOList) {
            int studentCount = selectiveCourseDAO.getStudentCount(courseDO.getId());
            List<CourseTimeDO> courseTimeDOList = courseTimeDAO.getCourseTimeByCourseId(courseDO.getId());
            //System.out.println("课程时间个数："+courseTimeDOList.size());

            CourseBO courseBO = ObjectTranslateUtil.courseDOToCourseBO(courseDO, courseTimeDOList);
            courseBO.setStudentAmount(studentCount);
           // System.out.println(courseDO.getSemester()+" ===99"+hm.get(courseDO.getSemester()));
            hm.get(courseDO.getSemester()).add(courseBO);
        }

        //获取当前学期名
        String currentCourseSemesterName = SemesterIdUtil.getSemesterName();

        //进行组合
        for(SemesterDO semesterDO:semesterDOList.getSemesterList()){
            String semesterName = semesterDO.getName();

            CourseSemesterBO courseSemesterBO = new CourseSemesterBO();
            courseSemesterBO.setSemesterId(semesterDO.getId());       //设置学期ID
            courseSemesterBO.setSemester(semesterDO.getName());

            courseSemesterBO.setCourseList(hm.get(semesterDO.getId()));

            if(semesterName.equals(currentCourseSemesterName)){      //如果是当前学期
                currentCourseList.add(courseSemesterBO);
            }
            else{
                historyCourseList.add(courseSemesterBO);
            }
        }
        /*2017秋季 - 2017春季*/

        CourseListBO courseVO = new CourseListBO();
        courseVO.setCurrentCourseList(currentCourseList);
        Collections.sort(historyCourseList);
        courseVO.setHistoryCourseList(historyCourseList);
        return courseVO;
    }

    public CourseBO getCourseById(Integer id){
        CourseDO  courseDO = courseMapper.getCourseById(id);
        int studentCount = selectiveCourseDAO.getStudentCount(id);
        List<CourseTimeDO> courseTimeDOList = courseTimeDAO.getCourseTimeByCourseId(courseDO.getId());

        //SemesterBO semesterBO = semesterDAO.getSemesterById(courseDO.getSemester(), courseDO.getTeacherId());

        CourseBO courseBO = ObjectTranslateUtil.courseDOToCourseBO(courseDO, courseTimeDOList);
        courseBO.setStudentAmount(studentCount);
        return courseBO;
    }

   /* public CourseListBO getCourseByName(String name){
        String builder = "%" +
                name +
                "%";
        List<CourseDO> courseDOList = courseMapper.getCourseByName(builder);

        List<CourseBO> currentCourseList = new ArrayList<>();
        List<CourseBO> historyCourseList = new ArrayList<>();

        for (CourseDO courseDO :
                courseDOList) {
            List<CourseTimeDO> courseTimeDOList = courseTimeDAO.getCourseTimeByCourseId(courseDO.getId());
            //SemesterBO semesterBO = semesterDAO.getSemesterById(courseDO.getSemester(), courseDO.getTeacherId());

            int studentCount = selectiveCourseDAO.getStudentCount(courseDO.getId());
            CourseBO courseBO = ObjectTranslateUtil.courseDOToCourseBO(courseDO, courseTimeDOList);
            courseBO.setStudentAmount(studentCount);

            LocalDateTime endTime = LocalDateTime.parse(semesterBO.getEndTime());
            if(LocalDateTime.now().isAfter(endTime)) {
                historyCourseList.add(courseBO);
            } else {
                currentCourseList.add(courseBO);
            }
        }

        CourseListBO courseVO = new CourseListBO();
        courseVO.setCurrentCourseList(currentCourseList);
        courseVO.setHistoryCourseList(historyCourseList);
        return courseVO;
    }*/

    public void deleteCourseById(Integer id){
        selectiveCourseDAO.removeCourse(id);
        signDAO.deleteSignRecord(id);
        courseTimeDAO.deleteCourseTime(id);
        courseMapper.deleteCourse(id);

    }

    public void updateCourse(CourseDTO courseDTO){
        logger.info("courseDTO.getSemester():"+courseDTO.getSemester());       //可能存在 bug ,因为界面不会修改 该学期ID
        courseMapper.updateCourse(ObjectTranslateUtil.courseDTOToCourseDO(courseDTO));
    }

    public void deleteCourseBySemester(Integer semesterId){
        List<CourseDO> courseDOs = courseMapper.getCourseBySemesterId(semesterId);
        for (CourseDO courseDO :
                courseDOs) {
         deleteCourseById(courseDO.getId());
        }
    }
}
