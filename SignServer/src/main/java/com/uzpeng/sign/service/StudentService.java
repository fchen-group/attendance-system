package com.uzpeng.sign.service;

import com.uzpeng.sign.bo.StudentBO;
import com.uzpeng.sign.bo.StudentBOList;
import com.uzpeng.sign.bo.StudentDetailBOList;
import com.uzpeng.sign.bo.StudentSignRecordListBO;
import com.uzpeng.sign.dao.SignDAO;
import com.uzpeng.sign.dao.StudentDAO;
import com.uzpeng.sign.dao.UserDAO;
import com.uzpeng.sign.domain.CourseTimeDO;
import com.uzpeng.sign.domain.StudentDO;
import com.uzpeng.sign.exception.IllegalParameterException;
import com.uzpeng.sign.util.ObjectTranslateUtil;
import com.uzpeng.sign.web.dto.StudentDTO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Service
public class StudentService {
    private static final String XLS_FORMAT = "xls";
    private static final String XLSX_FORMAT = "xlsx";
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private SignDAO signDAO;
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserService userService;  //重置user密码需要用到

    public void addStudentList(List<StudentDTO> students,Integer courseId){
        if(students !=null && students.size() > 0) {   //可能传空集合

            List<StudentDO> studentDOList = new ArrayList<StudentDO>();
            for(StudentDTO studentDTO:students)
                studentDOList.add(ObjectTranslateUtil.studentDTOToStudentDO(studentDTO));

            studentDAO.insertStudents(studentDOList, courseId);
        } else {
            logger.warn("student list is empty!");
        }
    }

    public void insertStudentsByFile(InputStream excelFileStream, String fileName, Integer courseId)
            throws IllegalParameterException{

        List<StudentDO> studentDOList = parseExcelFile(excelFileStream, fileName);

        if(studentDOList != null) {
            studentDAO.insertStudents(studentDOList, courseId);
        } else {
            logger.warn("student list is empty!");
        }
    }

    public void insertStudent(StudentDTO studentDTO, Integer courseId){
        studentDAO.insertStudent(ObjectTranslateUtil.studentDTOToStudentDO(studentDTO),
                courseId);
    }

    public void updateStudent(StudentDTO studentDTO, Integer studentId){
        StudentDO studentDO =  ObjectTranslateUtil.studentDTOToStudentDO(studentDTO);
        studentDO.setId(studentId);
        studentDAO.updateStudent(studentDO);
    }

    //重置学生密码，先在学生表中查出学号，再在user表中设置
    public void resetStudentPassword(Integer studentId){

        StudentDO student = studentDAO.getStudentById(studentId);
        logger.info("student's 学号为:"+student.getNum());
        //需要根据学号查出userID
        Integer id = userService.getIdByRoleId(studentId,"STUDENT");
        userService.updatePassword(id,student.getNum());  //重置也就是更新user密码
    }

    //重置学生密码，先在学生表中查出学号，再在user表中设置
    public void deleteStudentOpenId(Integer studentId){

        //需要根据学号查出userID
        Integer id = userService.getIdByRoleId(studentId,"STUDENT");
        userService.deleteOpenId(id);  //重置也就是更新user密码
    }


    public StudentBOList getStudentByCourseId(Integer courseId){
        return studentDAO.getStudent(courseId);
    }

    public StudentDetailBOList getStudentDetailList(Integer courseId){
        return studentDAO.getStudentDetailList(courseId);
    }

    public void removeStudent(Integer courseId, Integer studentId){
        studentDAO.removeStudent(courseId, studentId);
    }

    public StudentSignRecordListBO getStudentSignRecordList(int studentId, int type){
        return signDAO.getSignRecordByStudentId(studentId, type);
    }

    public StudentBO getStudentById(Integer studentId){
        return ObjectTranslateUtil.studentDOToStudentBO(studentDAO.getStudentById(studentId));
    }

    public Integer checkStudentByOpenId(String openId){

        return userDAO.getIdByOpenId(openId);


    }

    public Integer getStudentByOpenId(String openId){
        return userDAO.getRoleIdByOpenId(openId);
    }

    private List<StudentDO> parseExcelFile(InputStream excelFileStream, String filename) throws IllegalParameterException{
        logger.info("filename is "+filename);
        Workbook workbook;
        try{
            if(filename.endsWith(XLS_FORMAT)){
                workbook = new HSSFWorkbook(excelFileStream);
            } else if (filename.endsWith(XLSX_FORMAT)){
                workbook = new XSSFWorkbook(excelFileStream);
            } else {
                throw new IllegalParameterException();
            }
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            List<StudentDO> studentDOList = new ArrayList<>();
            for (int i = 1; i <= lastRowNum; i++) {
                //todo 需要添加单元格判断
                Row currentRow = sheet.getRow(i);
                if(currentRow == null){
                    break;
                }
                Cell studentNumCell = currentRow.getCell(0);
                Cell studentNameCell = currentRow.getCell(1);
                Cell studentClassCell = currentRow.getCell(2);

                if (studentNumCell != null && studentNameCell != null){
                    String studentNum = String.valueOf((int)studentNumCell.getNumericCellValue());
                    String studentName = studentNameCell.getStringCellValue();
                    String studentClass = studentClassCell.getStringCellValue();
                    StudentDO tmpStudentDO = new StudentDO();
                    tmpStudentDO.setName(studentName);
                    tmpStudentDO.setNum(studentNum);
                    tmpStudentDO.setClassInfo(studentClass);
                    studentDOList.add(tmpStudentDO);
                } else {
                    break;
                }
            }
            return studentDOList;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public StudentBOList searchStudentByNum(String num){
        return studentDAO.searchStudentByNum(num);
    }

    public StudentBOList pickStudent(Integer courseId, Integer amount){
        return studentDAO.pickStudent(courseId, amount);
    }

}
