package com.uzpeng.sign.web;


import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.service.SignService;
import com.uzpeng.sign.util.CommonResponseHandler;
import com.uzpeng.sign.web.dto.CreateSignRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

//分析用途的controller
@Controller
public class EvaluateController {
    private static final Logger logger = LoggerFactory.getLogger(EvaluateController.class);

    @Autowired
    private Environment env;
    @Autowired
    private SignService signService;

    //计算机系统1  46  测试  120  146      181  验证： 202 214 228 238 240
    //            47        121  147      188  验证： 209 216 229 239 241     差不多34-40人之间
    //高斯分布
    @RequestMapping(value = "/v1/check/evaluateSignResult/Gauss/{courseSignId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String orderByStudentIdGauss(@PathVariable("courseSignId") String courseSignId) {

        logger.info("Gauss --- start 分析检测异常");
        try {
            signService.evaluateSignResultGauss(Integer.valueOf(courseSignId));
        }catch (IOException e){
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "IO失败。。", "");
        }

        logger.info("Gauss --- 分析异常结束...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }

    //GeoHash 分布
    @RequestMapping(value = "/v1/check/evaluateSignResult/GeoHash/{courseSignId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String orderByStudentIdGeoHash(@PathVariable("courseSignId") String courseSignId) {

        logger.info("GeoHash --- start 分析检测异常");
        try {
            signService.evaluateSignResultGeoHash(Integer.valueOf(courseSignId));
        }catch (IOException e){
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "IO失败。。", "");
        }

        logger.info("GeoHash --- 分析异常结束...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }

    //计算机系统1  46  47
    //GeoHash2 分布
    @RequestMapping(value = "/v1/check/evaluateSignResult/GeoHash2/{courseSignId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String orderByStudentIdGeoHashGauss2(@PathVariable("courseSignId") String courseSignId) {

        logger.info("GeoHash2 --- start 分析检测异常");
        try {
            signService.evaluateSignResultGeoHash2(Integer.valueOf(courseSignId));   //算法比较
        }catch (IOException e){
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "IO失败。。", "");
        }

        logger.info("GeoHash2 --- 分析异常结束...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }


    //计算机系统1  46  47  效果不好。论文省略
    //GeoHashGauss1 分布
    @RequestMapping(value = "/v1/check/evaluateSignResult/GeoHashGauss1/{courseSignId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String orderByStudentIdGeoHashGauss1(@PathVariable("courseSignId") String courseSignId) {

        logger.info("GeoHashGauss1 --- start 分析检测异常");
        try {
            signService.evaluateSignResultGeoHashGauss1(Integer.valueOf(courseSignId));
        }catch (IOException e){
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "IO失败。。", "");
        }

        logger.info("GeoHashGauss1 --- 分析异常结束...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }

    //计算机系统1  46  47
    //LOF分布
    @RequestMapping(value = "/v1/check/evaluateSignResult/LOF/{courseSignId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String orderByStudentIdLOF(@PathVariable("courseSignId") String courseSignId) {

        logger.info("LOF --- start 分析检测异常");
        try {
            signService.evaluateSignResultLOF(Integer.valueOf(courseSignId));
        }catch (IOException e){
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "IO失败。。", "");
        }

        logger.info("LOF --- 分析异常结束...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }

    @RequestMapping(value = "/v1/check/evaluateSignResult/{courseSignId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String ordByStudentId(@PathVariable("courseSignId") String courseSignId) {

        logger.info("start 分析检测异常");
        try {
            signService.evaluateSignResult(Integer.valueOf(courseSignId));
        }catch (IOException e){
            e.printStackTrace();
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "IO失败。。", "");
        }

        logger.info("分析异常结束...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }

    @RequestMapping(value = "/v1/check/downloadSignRecord/{courseSignId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSignRecord(@PathVariable("courseSignId") String courseSignId) {

        logger.info("start 下载数据库:签到id为"+courseSignId+"的数据记录...");
        String path = "D:\\software\\IntelliJ IDEA 2018.2.5\\workspace\\Sign-Server\\src\\main\\resources\\excelFile\\test_LOF_"+courseSignId+".xlsx";
        try {
            //signService.Download_Record_local_Excel(path,Integer.valueOf(courseSignId));
            signService.get_Download_Record(path,Integer.valueOf(courseSignId));

        }catch (IOException e){
            logger.info("下载过程出现io异常...");
            return CommonResponseHandler.handleResponse(StatusConfig.FAILED,
                    "下载失败。。", "");
        }

        logger.info("下载结束...");
        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }

    //teacherId  =2
    @RequestMapping(value = "/v1/check/transferSignRecord/{teacherId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String transferRecord(@PathVariable("teacherId") String teacherId) {

        //教师课程id= 46，47
        logger.info("开始转移 数据库中:教师id为"+teacherId+"的数据记录...");
        signService.transferSignRecord(Integer.valueOf(teacherId));
        logger.info("转移结束...");

        return CommonResponseHandler.handleResponse(StatusConfig.SUCCESS,
                env.getProperty("status.success"),  env.getProperty("link.doc"));

    }


}
