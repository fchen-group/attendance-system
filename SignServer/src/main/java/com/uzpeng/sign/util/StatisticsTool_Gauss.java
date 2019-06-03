package com.uzpeng.sign.util;

import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.util.outlier_detection.LOF.DataNode;
import com.uzpeng.sign.util.outlier_detection.LOF.OutlierNodeDetect;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.Min;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatisticsTool_Gauss {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsTool_Gauss.class);
    //private static final double MIN_VALUE=0.001;

    private  static Map<String, Integer> map = new HashMap<String, Integer>();

    public static void pickAbnormalPoint(CopyOnWriteArrayList<SignRecordDO> records,Integer signId){
        List<Double> longitude = new ArrayList<>();
        List<Double> latitude = new ArrayList<>();

        for (SignRecordDO c :
                records) {
            longitude.add(c.getLongitude());
            latitude.add(c.getLatitude());
        }

        double longitudeMean = getMean(longitude);
        double longitudeSD = getSD(longitude, longitudeMean);
        double latitudeMean = getMean(latitude);
        double latitudeSD = getSD(latitude, latitudeMean);

        System.out.println("longitudeMean: "+longitudeMean+",longitudeSD: "+longitudeSD+" latitudeMean:"+latitudeMean+
                ",latitudeSD"+latitudeSD);

        ArrayList<Double> meanLongLatitude = get_distance.getMeanLongLatitude(records);
        System.out.println("高斯分布中 最大聚集类的平均点：("+ meanLongLatitude.get(0)+","+meanLongLatitude.get(1)+")");
        System.out.println("两者概率相乘结果："+getNormalDistributionProbabilityDestiny(meanLongLatitude.get(0), longitudeMean, longitudeSD)*
                getNormalDistributionProbabilityDestiny(meanLongLatitude.get(1), latitudeMean, latitudeSD));

        int MIN =  0;
        int size = records.size();
        List<Double> probabilities = new ArrayList<>();
        List<Double> distance = new ArrayList<>();
        for (SignRecordDO record : records) {
            double x = getNormalDistributionProbabilityDestiny(record.getLongitude(), longitudeMean, longitudeSD);
            double y = getNormalDistributionProbabilityDestiny(record.getLatitude(), latitudeMean, latitudeSD);

            //System.out.println("x: "+x+",y: "+y);


            //x*y 即是连乘，实现 似然函数   显然概率值越小，异常值的可能性越大。
            probabilities.add(x * y);
            getOrderMagnitude(x * y);

            distance.add(get_distance.getDistance(record.getLongitude(),record.getLatitude(),meanLongLatitude.get(0),meanLongLatitude.get(1)));

        }
        double MIN_VALUE = getMIN_VALUE();
        //logger.info("此次MIN_VALUE为:"+MIN_VALUE);

        ArrayList<ArrayList<String>> downLoad = new ArrayList<>();   //测试阶段
        for (int i = 0; i < records.size(); i++) {


            ArrayList<String> al = new ArrayList<>();

            SignRecordDO record = records.get(i);
            double probability = probabilities.get(i);
            int result = probability > MIN_VALUE ? StatusConfig.RECORD_SUCCESS : StatusConfig.RECORD_FAILED;

            System.out.println("f(x):"+probability+"   ("+record.getLongitude()+","+record.getLatitude()
                        +")    ,signId:"+record.getId()+"   "+  record.getStudentId()+"   result:"+result
            +"       距离集群距离："+distance.get(i));

            //设置 State
            record.setState(result);

            al.add(probability+"");al.add(record.getLongitude()+"");al.add(record.getLatitude()+"");al.add(result+"");al.add(distance.get(i)+"");
            downLoad.add(al);

        }
        String path = "D:\\software\\IntelliJ IDEA 2018.2.5\\workspace\\Sign-Server\\src\\main\\resources\\excelFile\\test_Gauss_"+signId+"(1).xlsx";
        Download_local_Excel_Gauss(path,downLoad);
    }


    //求均值mean
    private static double getMean(List<Double> data){

        CopyOnWriteArrayList<Double> safeData = new CopyOnWriteArrayList<>();
        safeData.addAll(data);

        double result = 0;
        for (Double d : safeData) {
            result += d;
        }

        return result / data.size();
    }



    //求平方差sd（已开根号）   方差是没有开方的
    private static double getSD(List<Double> data, double mean){

        CopyOnWriteArrayList<Double> safeData = new CopyOnWriteArrayList<>();
        safeData.addAll(data);

        int size = data.size();

        double result = 0;

        for (Double d : safeData) {
            result += Math.pow((d - mean), 2);
        }

        return Math.sqrt(result / size);
    }

    //计算X的概率密度 f(x;mean,sd)
    private static double getNormalDistributionProbabilityDestiny(double data, double mean, double sd){
        //System.out.println(data+"  "+ mean+"   "+sd);
        double base = 1 / (Math.sqrt(2 * Math.PI)* sd);
        //System.out.println("base:  "+base);
        double exponent = - ((Math.pow((data - mean), 2)) / (2 * Math.pow(sd, 2)));
        //System.out.println("exponent:  "+exponent);
        return  base*(Math.pow(Math.E, exponent));
    }

    //原始公式错误
    /*private static double getNormalDistributionProbabilityDestiny1(double data, double mean, double sd){
        double base = 1 / (Math.sqrt(2 * Math.PI * sd));
        double exponent = - ((Math.pow((data - mean), 2)) / (2 * Math.pow(sd, 2)));

        return  Math.pow(base, exponent);
    }*/


    //计算数量级  以下方法均没考虑 多线程环境下
    //两种情况
    public static void getOrderMagnitude(double num){

        int bit = 0;
        if(num > 1){
            while(num != 0){
                bit++;
                num = (int)(num/10);
            }
            bit--;

        }else{
            while(num < 1){
                num*=10;
                bit++;
            }
            bit = (-bit);
        }


        if(map.containsKey(bit+"")){

            Integer n = map.get(bit + "");
            map.put(bit + "",++n);

        }
        else{
            map.put(bit + "",1);
        }
        //logger.info("num:"+num+"   bit"+bit+"  "+map.get(bit + ""));
    }

    //根据value 返回key 大哥。。。！！！！！
    public static double getMIN_VALUE(){

        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //降序排序
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Integer Magnitude = Integer.valueOf(list.get(0).getKey()) ;
        //logger.info("Magnitude:"+Magnitude +"  "+list.get(0).getValue());

        double Min;
        if(Magnitude > 0){

            Min = 1;
            while(Magnitude != 0){
                Min *= 10;
                Magnitude--;
            }
        }else{

            Min = 9.0;   // 如果普遍是0.2，0.3  即数量级是0.1--》0.09
            while(Magnitude <= 0){
                Min *= 0.1;
                Magnitude++;
            }
        }
        logger.info("Min:"+Min);
        return Min;

    }

    public static  void Download_local_Excel_Gauss(String filePath, ArrayList<ArrayList<String>> downLoad) {
        try {
            XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workbook1, 100);
            Sheet first = sxssfWorkbook.getSheetAt(0);

            String[] recordInfoTitle = {"f(x)","longitude", "latitude", "result", "distance"};

            Row row = first.createRow(0);
            for (int i = 0; i < recordInfoTitle.length; i++)
                row.createCell(i).setCellValue(recordInfoTitle[i]);


            for (int i = 0; i < downLoad.size(); i++) {

                row = first.createRow(i + 1);
                ArrayList<String> arrayList = downLoad.get(i);

                for (int j = 0; j < recordInfoTitle.length; j++) {
                    CellUtil.createCell(row, j, arrayList.get(j));
                }
            }
            FileOutputStream out = new FileOutputStream(filePath);
            sxssfWorkbook.write(out);
            out.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        //System.out.println("结果："+StatisticsTool_Gauss.getNormalDistributionProbabilityDestiny(0,0,1.5));

       // StatisticsTool_Gauss statisticsTool_Gauss = new StatisticsTool_Gauss();

        java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.####");


        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();
        SignRecordDO  a = new SignRecordDO();
        a.setLongitude(113.932165); a.setLatitude(22.52927);
        SignRecordDO  b = new SignRecordDO();
        b.setLongitude(113.932134); b.setLatitude(22.529152);
        SignRecordDO  c = new SignRecordDO();
        c.setLongitude(113.932185); c.setLatitude(22.529208);
        SignRecordDO  d = new SignRecordDO();
        d.setLongitude(113.932152); d.setLatitude(22.529241);
        SignRecordDO  e = new SignRecordDO();
        e.setLongitude(113.932081); e.setLatitude(22.529194);
        SignRecordDO  f = new SignRecordDO();
        f.setLongitude(113.932182); f.setLatitude(22.529262);

        signRecordDOs.add(a);signRecordDOs.add(b);signRecordDOs.add(c);signRecordDOs.add(d);
        signRecordDOs.add(e);signRecordDOs.add(f);

        for(int i = 0;i < signRecordDOs.size();i++)
            System.out.println(signRecordDOs.get(i).getLongitude());

        StatisticsTool_Gauss.pickAbnormalPoint(signRecordDOs,0);

    }

}

/*
SignRecordDO  a = new SignRecordDO();
        a.setLongitude(2.0); a.setLatitude(4.0);
        SignRecordDO  b = new SignRecordDO();
        b.setLongitude(1.0); b.setLatitude(4.0);
        SignRecordDO  c = new SignRecordDO();
        c.setLongitude(1.0); c.setLatitude(3.0);
        SignRecordDO  d = new SignRecordDO();
        d.setLongitude(2.0); d.setLatitude(2.0);
        SignRecordDO  e = new SignRecordDO();
        e.setLongitude(3.0); e.setLatitude(2.0);
        SignRecordDO  f = new SignRecordDO();
        f.setLongitude(3.0); f.setLatitude(4.0);
 */
