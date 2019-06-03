package com.uzpeng.sign.dao;


import com.uzpeng.sign.bo.SemesterDOList;
import com.uzpeng.sign.domain.SemesterDO;
import com.uzpeng.sign.persistence.SemesterMapper;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import org.omg.PortableInterceptor.INACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SemesterDAO {
    private static final Logger logger = LoggerFactory.getLogger(SemesterDAO.class);

    @Autowired
    private SemesterMapper semesterMapper;
    @Autowired
    private CourseDAO courseDAO;

    public Integer addSemesterByName(String name,Integer teacherId){
        SemesterDO semesterDO = new SemesterDO();
        semesterDO.setTeacherId(teacherId);
        semesterDO.setName(name);
        semesterMapper.addSemesterByName(semesterDO);
        return semesterDO.getId();
    }
    public void addSemester(SemesterDO semesterDO){
        semesterMapper.addSemester(semesterDO);
    }

    public SemesterDOList getSemester(Integer teacherId){
        List<SemesterDO> semesterDOs = semesterMapper.getSemester(teacherId);
        //List<SemesterBO> semesterBOs = new ArrayList<>();
        SemesterDOList semesterDOList   = new SemesterDOList();
        semesterDOList.setSemesterList(semesterDOs);
        return semesterDOList;
    }

    public SemesterDO getSemesterById(Integer id, Integer teacherId){
        logger.info("get semester,semesterId:"+id+", teacherId:"+teacherId);

        SemesterDO semesterDO = semesterMapper.getSemesterById(id, teacherId);
        return semesterDO;
        //不需要进行转换
        //return ObjectTranslateUtil.semesterDOToSemesterBO(semesterDO);
    }

    public Integer  getSemesterIdByName(String name,Integer teacherId){
        logger.info("get semesterName:"+name+"  teacherId :"+teacherId);

        return  semesterMapper.getSemesterIdByName(name,teacherId);
    }

    public void updateSemester(SemesterDO semesterDO){
        semesterMapper.updateSemester(semesterDO);
    }

    public void deleteSemester(Integer id){
        courseDAO.deleteCourseBySemester(id);

        semesterMapper.deleteSemester(id);
    }
}
