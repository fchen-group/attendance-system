package com.uzpeng.sign.service;

import com.uzpeng.sign.bo.*;
import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.dao.CourseDAO;
import com.uzpeng.sign.dao.SignDAO;
import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.util.*;
import com.uzpeng.sign.web.dto.CreateSignRecordDTO;
import com.uzpeng.sign.web.dto.SignRecordDTO;
import com.uzpeng.sign.web.dto.UpdateSignRecordDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 */
@Service
public class SignService {
    private final Logger logger = LoggerFactory.getLogger(SignService.class);
    @Autowired
    private SignDAO signDAO;

    @Autowired
    private CourseDAO courseDAO;

    @Autowired
    private SemesterService semesterService;


    public Integer createSign(CreateSignRecordDTO signRecordDTO, Integer courseId) {
        return signDAO.createSign(signRecordDTO.getCourseTimeId(), signRecordDTO.getWeek(), courseId);
    }

    public void deleteSign(Integer signId) {
        signDAO.deleteSign(signId);
    }

    public SignRecordListBO getSignRecordBySignId(Integer signId) {
        return signDAO.getSignRecordListBySignId(signId);
    }

    public SignRecordListBO getSignRecordByParam(Integer courseId, Integer time, Integer week, String num) {
        return signDAO.getSignRecordByTime(courseId, time, week, num);
    }

    public SignRecordTimeListBO getSignWeek(Integer courseId) {
        return signDAO.getRecordWeek(courseId);
    }

    public void updateSignState(UpdateSignRecordDTO updateSignRecordDTO) {
        signDAO.updateSignRecordStatus(Collections.singletonList(updateSignRecordDTO));
    }

    public void doingSign(SignRecordDTO signRecordDTO, int studentId) {
        signDAO.sign(signRecordDTO, studentId);
    }

    public Integer getSignState(Integer signId) {
        return signDAO.getSignState(signId);
    }

    public void updateSignState(Integer signId, Integer state) {
        signDAO.updateSignState(signId, state);
    }

    /*
      通俗的理解是当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，复制出一个新的容器，
      然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。
      这样做的好处是我们可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。
     */

    //------------------异常分析相关接口-----------------------


    //分析此次签到结果，将异常签到学生 改变状态state  正式版本
    public void evaluateSignResult(Integer signId) throws IOException {

        List<SignRecordDO> signRecordDOList = signDAO.getEvaluateSignRecord(signId);
        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        signRecordDOs.addAll(signRecordDOList);
        if (signRecordDOs.size() == 0) {
            return;
        }

        logger.info("++++++++++++++++++++++++++分析本次签到结果++++++++++++++++++++++++++");
        StatisticsTool_GeoHash2.pickAbnormalPointFormal(signRecordDOs);   //选择异常点后，设置state状态，然后在signRecordDOs中设置状态

        //把一些异常的点 重新设置 签到状态
        List<UpdateSignRecordDTO> newDataList = new ArrayList<>();
        for (SignRecordDO oldData :
                signRecordDOs) {
            UpdateSignRecordDTO newData = new UpdateSignRecordDTO();
            //if(oldData.getState()== 2)
                //logger.info("   该签到id:"+oldData.getId()+"存在异常-----state: " + oldData.getState());
            newData.setState(oldData.getState());
            newData.setId(oldData.getId());
            newDataList.add(newData);
        }

        //真正修改数据库签到状态  感觉算法精度不够，以后可以对这方面进行修改再真正调用     ！！！打开注释须谨慎
        //signDAO.updateSignRecordStatus(newDataList);
    }

    //以下皆为各种算法分析方法----------------------------------------------------------
    //Gauss 分析此次签到结果
    public void evaluateSignResultGauss(Integer signId) throws IOException {

        List<SignRecordDO> signRecordDOList = signDAO.getEvaluateSignRecord(signId);
        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        signRecordDOs.addAll(signRecordDOList);

        if (signRecordDOs.size() == 0) {
            return;
        }
        StatisticsTool_Gauss.pickAbnormalPoint(signRecordDOs,signId);
    }

    //LOF 分析此次签到结果，将异常签到学生 改变状态state
    public void evaluateSignResultLOF(Integer signId) throws IOException {

        //去重
        List<SignRecordDO> signRecordDOList = signDAO.getEvaluateSignRecord(signId);
        Set<SignRecordDO> set = new HashSet<>(signRecordDOList);
        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        signRecordDOs.addAll(set);

        if (signRecordDOs.size() == 0) {
            return;
        }

        StatisticsTool_LOF.pickAbnormalPoint(signRecordDOs);      //选择异常点后，设置state状态，然后在signRecordDOs中设置状态
    }

    //GeoHash 分析此次签到结果，将异常签到学生 改变状态state
    public void evaluateSignResultGeoHash(Integer signId) throws IOException {

        List<SignRecordDO> signRecordDOList = signDAO.getEvaluateSignRecord(signId);
        Set<SignRecordDO> set = new HashSet<>(signRecordDOList);
        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        signRecordDOs.addAll(set);

        if (signRecordDOs.size() == 0) {
            return;
        }

        StatisticsTool_GeoHash.pickAbnormalPoint(signRecordDOs,signId);      //选择异常点后，设置state状态，然后在signRecordDOs中设置状态
    }

    //GeoHashGauss1 分析此次签到结果，将异常签到学生 改变状态state
    public void evaluateSignResultGeoHashGauss1(Integer signId) throws IOException {

        List<SignRecordDO> signRecordDOList = signDAO.getEvaluateSignRecord(signId);
        Set<SignRecordDO> set = new HashSet<>(signRecordDOList);
        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        signRecordDOs.addAll(set);

        if (signRecordDOs.size() == 0) {
            return;
        }
        StatisticsTool_GeoHash_Gauss1.pickAbnormalPoint(signRecordDOs);      //选择异常点后，设置state状态，然后在signRecordDOs中设置状态
    }
    //GeoHashGauss2 分析此次签到结果，将异常签到学生 改变状态state
    public void evaluateSignResultGeoHash2(Integer signId) throws IOException {

        List<SignRecordDO> signRecordDOList = signDAO.getEvaluateSignRecord(signId);
        Set<SignRecordDO> set = new HashSet<>(signRecordDOList);
        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        signRecordDOs.addAll(set);

        if (signRecordDOs.size() == 0) {
            return;
        }
        //StatisticsTool_GeoHash2.pickAbnormalPoint(signRecordDOs,signId);      //选择异常点后，设置state状态，然后在signRecordDOs中设置状态
        StatisticsTool_GeoHash2.pickAbnormalPoint2(signRecordDOs,signId);
    }


    public byte[] downloadSignAllRecord(Integer courseId) throws IOException {
        logger.info("开始获取");
        DownloadSignRecordBOList downloadSignRecordBOList = signDAO.getAllSignRecord(courseId);

        String str = CommonResponseHandler.handleResponse(downloadSignRecordBOList, DownloadSignRecordBOList.class);
        logger.info("------------------这是分割线----------");
        logger.info(str);

        return writeToExcel(downloadSignRecordBOList.getDownloadSignRecordLists());
    }

    //管理台-导出签到记录---- 写入 表格中
    //List<List<StudentSignRecordBO>> list
    // {
    //   {(小明,Num,classInfo,课程名，state,第6周星期三)，(小明,Num,classInfo,课程名，state,第6周星期三)}，
    //   {(小红,Num,classInfo,课程名，state,第6周星期三)，(小红,Num,classInfo,课程名，state,第6周星期三)}
    //  }
    private byte[] writeToExcel(List<List<StudentSignRecordBO>> list) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int[] columnWidth = new int[]{255 * 15, 255 * 20, 255 * 10, 255 * 15};   //column列 row行
        String[] studentInfoTitle = {"学号", "班级", "姓名", "课程"};
        sheet.setColumnWidth(0, columnWidth[0]);
        sheet.setColumnWidth(1, columnWidth[1]);
        sheet.setColumnWidth(2, columnWidth[2]);
        sheet.setColumnWidth(3, columnWidth[3]);
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < studentInfoTitle.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(studentInfoTitle[i]);
        }

        //包含所有 签到时间名称
        List<String> timeTitleList = new ArrayList<>();
        List<StudentSignRecordBO> tmpList = list.get(0);
        for (StudentSignRecordBO tmp :
                tmpList) {
            timeTitleList.add(tmp.getTime());
        }

        //设置标题中 每次签到时间
        for (int i = 0; i < tmpList.size(); i++) {
            titleRow.createCell(studentInfoTitle.length + i).setCellValue(timeTitleList.get(i));
            sheet.setColumnWidth(studentInfoTitle.length + i, 255 * 30);  //设置宽度
        }

        //遍历每个学生，并赋值 每一行
        for (int i = 0; i < list.size(); i++) {
            List<StudentSignRecordBO> recordBOs = list.get(i);
            Row currentRow = sheet.createRow(i + 1);

            currentRow.createCell(0).setCellValue(recordBOs.get(0).getStudentNum());
            currentRow.createCell(1).setCellValue(recordBOs.get(0).getClassInfo());
            currentRow.createCell(2).setCellValue(recordBOs.get(0).getStudentName());
            currentRow.createCell(3).setCellValue(recordBOs.get(0).getCourse());

            //遍历该生 每一个签到记录的记录情况
            for (int j = 0; j < recordBOs.size(); j++) {
                StudentSignRecordBO record = recordBOs.get(j);
                String state = record.getState().equals(StatusConfig.RECORD_SUCCESS) ? "成功" : "失败";
                currentRow.createCell(studentInfoTitle.length + j).setCellValue(state);
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return outputStream.toByteArray();
    }


    public void get_Download_Record(String filePath, Integer courseSignId) throws IOException {
        //根据courseSignId找到每一次签到的学生记录
        //int courseSignId = 105;
        List<SignRecordDO> signRecordDOList = signDAO.getSignRecordBySignId(courseSignId);
        Download_Record_local_Excel(filePath, signRecordDOList);
    }



    /**
     * <br/>Cast time : 87782
     *
     * @param filePath 文件路径
     * @throws IOException
     */
    public void Download_Record_local_Excel(String filePath, List<SignRecordDO> signRecordDOList) throws IOException {

        XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new File(filePath)));
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workbook1, 100);
        Sheet first = sxssfWorkbook.getSheetAt(0);

        String[] recordInfoTitle = {"id", "course_sign_id", "student_id", "longitude",
                "latitude", "state", "accuracy", "device_no", "sign_time"};

        Row row = first.createRow(0);
        for (int i = 0; i < recordInfoTitle.length; i++)
            row.createCell(i).setCellValue(recordInfoTitle[i]);


        for (int i = 0; i < signRecordDOList.size(); i++) {

            row = first.createRow(i + 1);
            LinkedList<String> linkList = signRecordDOList.get(i).getLinkList();
            for (int j = 0; j < recordInfoTitle.length; j++) {
                CellUtil.createCell(row, j, linkList.get(j));
            }
        }
        FileOutputStream out = new FileOutputStream(filePath);
        sxssfWorkbook.write(out);
        out.close();
    }


    public void transferSignRecord(Integer teacherId) {

        //获取当前学期id
        String semesterId = semesterService.getSemesterIdByName(SemesterIdUtil.getSemesterName(), teacherId);
        //根据老师id获得课程id List
        List<Integer> course = courseDAO.getCourseIdListByTeacherId(teacherId, Integer.valueOf(semesterId));
        logger.info(course.toString());

        boolean flag = true;

        for (int i = 0; i < course.size(); i++) {

            //根据该课程id获取该课程发起的签到记录
            List<Integer> signIdByCourseId = signDAO.getSignIDByCourseId(course.get(i));
            for (int j = 0; j < signIdByCourseId.size(); j++) {
                //判断该签到Id是否以在 evaluate_sign_record表中
                int signId = signIdByCourseId.get(j);
                List<SignRecordDO> evaluateSignRecord = signDAO.getEvaluateSignRecord(signId);

                if (evaluateSignRecord.size() == 0) {  //证明还没更新到 evaluateSignRecord表中
                    List<SignRecordDO> signRecordBySignId = signDAO.getSignRecordBySignId(signId);
                    if (signRecordBySignId.size() == 0)
                        break;

                    logger.info("signRecordBySignId里面个数:" + signRecordBySignId.size());
                    /*
                    for (SignRecordDO s:
                         signRecordBySignId) {
                        System.out.println("hahah"+s);

                    }*/
                    logger.info("signId:" + signId + "  从course_sign_record表复制完毕，正要添加到 evaluate_sign_record表中...");
                    signDAO.insertEvaluateSignRecordList(signRecordBySignId);
                    logger.info("signId:" + signId + "复制完毕....");

                }
            }
        }


    }

    /*   原始分析方法
    /分析此次签到结果，将异常签到学生 改变状态state
    public void evaluateSignResult(Integer signId) throws IOException {

        //signRecordDOs.addAll(signDAO.getSignRecordBySignId(signId));

        //去重
        List<SignRecordDO> signRecordDOList = signDAO.getEvaluateSignRecord(signId);
        // 某一算法需要去重
       // Set<SignRecordDO> set = new HashSet<>(signRecordDOList);
        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        //signRecordDOs.addAll(set);
        signRecordDOs.addAll(signRecordDOList);


        if (signRecordDOs.size() == 0) {
            return;
        }

        logger.info("未修改前state均为：3 且有经纬度");
        /*for (SignRecordDO s : signRecordDOs)
            logger.info("未修改前：" + s);*/
    //StatisticsTool_LOF.pickAbnormalPoint(signRecordDOs);      //选择异常点后，设置state状态，然后在signRecordDOs中设置状态
    // StatisticsTool_GeoHash_Gauss1.pickAbnormalPoint(signRecordDOs);
    //StatisticsTool_GeoHash.pickAbnormalPoint(signRecordDOs);
    //StatisticsTool_GeoHash2.pickAbnormalPoint(signRecordDOs);
        /*for (SignRecordDO s : signRecordDOs)
            logger.info("修改后：" + s);*/
        /*  LOF专用

        //把一些异常的点 重新设置 签到状态
        List<UpdateSignRecordDTO> newDataList = new ArrayList<>();
        for (SignRecordDO oldData :
                signRecordDOs) {
            UpdateSignRecordDTO newData = new UpdateSignRecordDTO();
            //logger.info("state: " + oldData.getState() + ",id: " + oldData.getId());
            newData.setState(oldData.getState());
            newData.setId(oldData.getId());

            newDataList.add(newData);
        }
        //signDAO.updateEvaluateSignRecordStatus(newDataList);
        //把数据写入 excel表格中
        logger.info("把数据写入 excel表格中");
        String path = "D:\\software\\IntelliJ IDEA 2018.2.5\\workspace\\Sign-Server\\src\\main\\resources\\excelFile\\test"+signId+".xlsx";


        Download_Record_local_Excel(path,signRecordDOList);

        */

}