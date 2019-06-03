package com.uzpeng.sign.service;

import com.uzpeng.sign.bo.SemesterDOList;
import com.uzpeng.sign.dao.SemesterDAO;
import com.uzpeng.sign.domain.SemesterDO;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.web.dto.SemesterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SemesterService {
    private static final Logger logger = LoggerFactory.getLogger(SemesterService.class);

    @Autowired
    private SemesterDAO semesterDAO;

    public String getSemesterIdByName(String name,Integer teacherId){
        Integer id  = semesterDAO.getSemesterIdByName(name,teacherId);
        logger.info("查询后的ID+"+id);
        if(id != null) {
            logger.info("数据库DAO获取的学期id"+id.intValue());
            return Integer.toString(id);
        }
        else{
            return Integer.toString(addSemesterByName(name, teacherId));
        }
    }

    //根据 学期名称以及教师ID添加学期
    public Integer addSemesterByName(String name,Integer teacherId){
        return semesterDAO.addSemesterByName(name,teacherId);
    }

    //根据 教师ID获取学期信息
    public SemesterDOList getSemester(Integer teacherId){
        return semesterDAO.getSemester(teacherId);
    }


    public void addSemester(SemesterDO semesterDO){
        logger.info("Add semester, Parameter id:"+semesterDO.getName()+"TeacherId: "+semesterDO.getTeacherId());

        semesterDAO.addSemester(semesterDO);
    }

    /*public SemesterBO getSemesterById(Integer semesterId, Integer teacherId){
        return semesterDAO.getSemesterById(semesterId, teacherId);
    }*/



    /*public void updateSemester(SemesterDTO semesterDTO, Integer teacherId){
        semesterDAO.updateSemester(ObjectTranslateUtil.semesterDTOToSemesterDO(semesterDTO, teacherId));
    }*/

    public void deleteSemester(Integer semesterId){
        semesterDAO.deleteSemester(semesterId);
    }
}
