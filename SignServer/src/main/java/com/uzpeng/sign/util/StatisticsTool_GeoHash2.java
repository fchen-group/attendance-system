package com.uzpeng.sign.util;

import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.service.SignService;
import com.uzpeng.sign.util.outlier_detection.Geo_Hash.GeoHash;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatisticsTool_GeoHash2 {

    private  static final Logger logger = LoggerFactory.getLogger(StatisticsTool_GeoHash2.class);
    /*
        将同一个geohash字符串中，取均值，再将其与其他值计算.真正经纬度计算的。
        毕竟geohash会将初始值划分为8部分，误差为29m.
    */

    public static HashMap<String, Integer> hm = null;
    public static Integer Max_DIATANCE = 200;


    //（1正式）使用该方法对签到数据状态进行修改
    public static void pickAbnormalPointFormal(CopyOnWriteArrayList<SignRecordDO> records) {

        hm = new HashMap<>();
        HashMap<SignRecordDO, String> signHM = new HashMap<>();
        ArrayList al = new ArrayList();

        for (SignRecordDO signDo :
                records) {
            GeoHash gh = new GeoHash(signDo.getLatitude(), signDo.getLongitude());
            String geoStr = gh.getGeoHashBase32();
            addHashMap(geoStr);
            signHM.put(signDo, geoStr);
            al.add(geoStr);
        }

        /*List<Map.Entry<String, Integer>> list1 = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
        for (int i = 0; i < list1.size(); i++) {
            System.out.println(list1.get(i).getKey() + "  " + list1.get(i).getValue());
        }*/

        String maxGeoStr = getMAX_VALUE();
        Integer numGeoStr = hm.get(maxGeoStr);
        //System.out.println("过程中获取的最大MaxStr" + maxGeoStr+"  值为："+numGeoStr);

        //求出  编码为MaxStr的所有经纬度坐标均值
        List<Map.Entry<SignRecordDO, String>> list2 = new ArrayList<Map.Entry<SignRecordDO, String>>(signHM.entrySet());
        double sum_longitude = 0.0;
        double sum_latitude = 0.0;
        for (int i = 0; i < list2.size(); i++) {
            if(list2.get(i).getValue().equals(maxGeoStr)){
                sum_longitude+=list2.get(i).getKey().getLongitude();
                sum_latitude+=list2.get(i).getKey().getLatitude();
            }
        }
        double mean_longitude = sum_longitude/numGeoStr;
        double mean_latitude = sum_latitude/numGeoStr;
       // System.out.println("此次均值位置:("+mean_longitude+","+mean_latitude+")");

        for (int i = 0; i < list2.size(); i++) {
            SignRecordDO recordDO = list2.get(i).getKey();
            double distance = get_distance.getDistance(mean_longitude, mean_latitude,
                    recordDO.getLongitude(), recordDO.getLatitude());
            if(distance > Max_DIATANCE){
                recordDO.setState(2);
                logger.info("signId："+recordDO.getId()+"    提供的地理位置有异常》距离为:  "+distance);
            }
        }
    }

    //（2算法分析） 使用该方法进行算法分析
    public static void pickAbnormalPoint(CopyOnWriteArrayList<SignRecordDO> records,Integer signId) {

        hm = new HashMap<>();
        HashMap<SignRecordDO, String> signHM = new HashMap<>();

        ArrayList al = new ArrayList();


        //GeoHash MaxGeoHash = null;
        for (SignRecordDO signDo :
                records) {

            GeoHash gh = new GeoHash(signDo.getLatitude(), signDo.getLongitude());
            String geoStr = gh.getGeoHashBase32();
            addHashMap(geoStr);
           /* if (getMAX_VALUE().equals(geoStr))
                MaxGeoHash = gh;*/
            signHM.put(signDo, geoStr);
            al.add(geoStr);



        }

        //System.out.println("最后获取的最大MaxStr" + getMAX_VALUE());    // ws100tsc  10
        List<Map.Entry<String, Integer>> list1 = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
        for (int i = 0; i < list1.size(); i++) {
            System.out.println(list1.get(i).getKey() + "  " + list1.get(i).getValue());
        }

        String maxGeoStr = getMAX_VALUE();
        Integer numGeoStr = hm.get(maxGeoStr);
        System.out.println("过程中获取的最大MaxStr" + maxGeoStr+"  值为："+numGeoStr);

        //求出 代表着最大MaxStr的坐标均值
        List<Map.Entry<SignRecordDO, String>> list2 = new ArrayList<Map.Entry<SignRecordDO, String>>(signHM.entrySet());
        double sum_longitude = 0.0;
        double sum_latitude = 0.0;
        //System.out.println("list2个数："+list2.size()+"原始records个数:"+records.size());
        for (int i = 0; i < list2.size(); i++) {
            if(list2.get(i).getValue().equals(maxGeoStr)){
                sum_longitude+=list2.get(i).getKey().getLongitude();
                sum_latitude+=list2.get(i).getKey().getLatitude();
            }
        }

        double mean_longitude = sum_longitude/numGeoStr;
        double mean_latitude = sum_latitude/numGeoStr;
        System.out.println("此次均值位置:("+mean_longitude+","+mean_latitude+")");

        //下载到表格集合
        ArrayList<ArrayList<String>> downLoad = new ArrayList<>();


        //测试 那几个最大值相等的距离跟均值位置的距离
        System.out.println("那几个最大值相等的距离跟均值位置的距离");
        for (int i = 0; i < list2.size(); i++) {
            SignRecordDO recordDO = list2.get(i).getKey();
            if(list2.get(i).getValue().equals(maxGeoStr)){
                System.out.println("记录id:"+recordDO.getId()+"    距离："+get_distance.getDistance(mean_longitude,mean_latitude,
                        recordDO.getLongitude(),recordDO.getLatitude())
                        +"        ("+recordDO.getLongitude()+","+recordDO.getLatitude()+")  " + list2.get(i).getValue()
                        +"  学生id: "+ recordDO.getStudentId()
                );
            }

            //下载表格
            ArrayList<String> temp = new ArrayList<>();
            temp.add(recordDO.getId()+"");
            temp.add(get_distance.getDistance(mean_longitude,mean_latitude,
                    recordDO.getLongitude(),recordDO.getLatitude())+"");
            temp.add(list2.get(i).getValue());
            downLoad.add(temp);
        }

        //计算剩下的那些均值的区别
        System.out.println("剩下的那些均值的区别");
        for (int i = 0; i < list2.size(); i++) {
            SignRecordDO recordDO = list2.get(i).getKey();
            if(!list2.get(i).getValue().equals(maxGeoStr)){
                System.out.println("记录id:"+recordDO.getId()+"   距离："+get_distance.getDistance(mean_longitude,mean_latitude,
                        recordDO.getLongitude(),recordDO.getLatitude())
                +"  ("+recordDO.getLongitude()+","+recordDO.getLatitude()+")  " + list2.get(i).getValue()
                                +"  学生id: "+ recordDO.getStudentId()
                );
            }

            //下载表格
            ArrayList<String> temp = new ArrayList<>();
            temp.add(recordDO.getId()+"");
            temp.add(get_distance.getDistance(mean_longitude,mean_latitude,
                    recordDO.getLongitude(),recordDO.getLatitude())+"");
            temp.add(list2.get(i).getValue());
            downLoad.add(temp);
        }

        String path = "D:\\software\\IntelliJ IDEA 2018.2.5\\workspace\\Sign-Server\\src\\main\\resources\\excelFile\\test_geoHash2_"+signId+".xlsx";
        //Download_local_Excel_GeoHash2(path,downLoad);

        System.out.println("----------------------------------分割线");
    }

    //（3论文阶段-数据规律分析） 使用该方法进行算法分析
    public static void pickAbnormalPoint2(CopyOnWriteArrayList<SignRecordDO> records,Integer signId) {

        hm = new HashMap<>();
        HashMap<SignRecordDO, String> signHM = new HashMap<>();

        ArrayList al = new ArrayList();

        for (SignRecordDO signDo :
                records) {
            GeoHash gh = new GeoHash(signDo.getLatitude(), signDo.getLongitude());
            String geoStr = gh.getGeoHashBase32();
            addHashMap(geoStr);
            signHM.put(signDo, geoStr);
            al.add(geoStr);

        }

        List<Map.Entry<String, Integer>> list1 = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
        for (int i = 0; i < list1.size(); i++) {
            System.out.println(list1.get(i).getKey() + "  " + list1.get(i).getValue());
        }

        String maxGeoStr = getMAX_VALUE();
        Integer numGeoStr = hm.get(maxGeoStr);
        System.out.println("过程中获取的最大MaxStr" + maxGeoStr+"  值为："+numGeoStr);

        //求出 代表着最大MaxStr的坐标均值
        List<Map.Entry<SignRecordDO, String>> list2 = new ArrayList<Map.Entry<SignRecordDO, String>>(signHM.entrySet());
        double sum_longitude = 0.0;
        double sum_latitude = 0.0;
        //System.out.println("list2个数："+list2.size()+"原始records个数:"+records.size());
        for (int i = 0; i < list2.size(); i++) {
            if(list2.get(i).getValue().equals(maxGeoStr)){
                sum_longitude+=list2.get(i).getKey().getLongitude();
                sum_latitude+=list2.get(i).getKey().getLatitude();
            }
        }

        double mean_longitude = sum_longitude/numGeoStr;
        double mean_latitude = sum_latitude/numGeoStr;
        System.out.println("此次均值位置:("+mean_longitude+","+mean_latitude+")");

        //下载到表格集合
        ArrayList<ArrayList<String>> downLoad = new ArrayList<>();

        System.out.println("那几个最大值相等的距离跟均值位置的距离");
        for (int i = 0; i < list2.size(); i++) {
            SignRecordDO recordDO = list2.get(i).getKey();
                System.out.println("记录id:"+recordDO.getId()+"    距离："+get_distance.getDistance(mean_longitude,mean_latitude,
                        recordDO.getLongitude(),recordDO.getLatitude())
                        +"        ("+recordDO.getLongitude()+","+recordDO.getLatitude()+")  " + list2.get(i).getValue()
                        +"  学生id: "+ recordDO.getStudentId()
                );

            //下载表格
            ArrayList<String> temp = new ArrayList<>();
            temp.add(recordDO.getId()+"");temp.add(recordDO.getCourse_sign_id()+"");temp.add(recordDO.getStudentId()+"");
            temp.add(recordDO.getLongitude()+"");temp.add(recordDO.getLatitude()+"");temp.add(recordDO.getDevice_no());

            temp.add(get_distance.getDistance(mean_longitude,mean_latitude,
                    recordDO.getLongitude(),recordDO.getLatitude())+"");
            temp.add(list2.get(i).getValue());
            downLoad.add(temp);
        }




        //测试 那几个最大值相等的距离跟均值位置的距离
       /* System.out.println("那几个最大值相等的距离跟均值位置的距离");
        for (int i = 0; i < list2.size(); i++) {
            SignRecordDO recordDO = list2.get(i).getKey();
            if(list2.get(i).getValue().equals(maxGeoStr)){
                System.out.println("记录id:"+recordDO.getId()+"    距离："+get_distance.getDistance(mean_longitude,mean_latitude,
                        recordDO.getLongitude(),recordDO.getLatitude())
                        +"        ("+recordDO.getLongitude()+","+recordDO.getLatitude()+")  " + list2.get(i).getValue()
                        +"  学生id: "+ recordDO.getStudentId()
                );
            }

            //下载表格
            ArrayList<String> temp = new ArrayList<>();
            temp.add(recordDO.getId()+"");
            temp.add(get_distance.getDistance(mean_longitude,mean_latitude,
                    recordDO.getLongitude(),recordDO.getLatitude())+"");
            temp.add(list2.get(i).getValue());
            downLoad.add(temp);
        }

        //计算剩下的那些均值的区别
        System.out.println("剩下的那些均值的区别");
        for (int i = 0; i < list2.size(); i++) {
            SignRecordDO recordDO = list2.get(i).getKey();
            if(!list2.get(i).getValue().equals(maxGeoStr)){
                System.out.println("记录id:"+recordDO.getId()+"   距离："+get_distance.getDistance(mean_longitude,mean_latitude,
                        recordDO.getLongitude(),recordDO.getLatitude())
                        +"  ("+recordDO.getLongitude()+","+recordDO.getLatitude()+")  " + list2.get(i).getValue()
                        +"  学生id: "+ recordDO.getStudentId()
                );
            }

            //下载表格
            ArrayList<String> temp = new ArrayList<>();
            temp.add(recordDO.getId()+"");
            temp.add(get_distance.getDistance(mean_longitude,mean_latitude,
                    recordDO.getLongitude(),recordDO.getLatitude())+"");
            temp.add(list2.get(i).getValue());
            downLoad.add(temp);
        }
        */

        String path = "D:\\software\\IntelliJ IDEA 2018.2.5\\workspace\\Sign-Server\\src\\main\\resources\\excelFile\\test_geoHash2_rule_"+signId+".xlsx";
        Download_local_Excel_GeoHash2(path,downLoad);

        System.out.println("----------------------------------分割线");
    }


    public static  void Download_local_Excel_GeoHash2(String filePath, ArrayList<ArrayList<String>> downLoad) {
        try {
            XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workbook1, 100);
            Sheet first = sxssfWorkbook.getSheetAt(0);

            //String[] recordInfoTitle = {"id","distance", "geoHash"};
            String[] recordInfoTitle = {"id","course_sign_id","student_id","longitude","latitude","device_no","distance", "geoHash"};

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

    public static void addHashMap(String str) {

        if (hm.containsKey(str)) {
            Integer n = hm.get(str);
            hm.put(str, ++n);
        } else {
            hm.put(str, 1);
        }
    }

    public static String getMAX_VALUE() {

        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            //降序排序
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        //System.out.println("Magnitude:"+list.get(0).getKey() +"  "+list.get(0).getValue());
        return list.get(0).getKey();

    }


    public static void main(String[] args) {

        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();

        SignRecordDO a = new SignRecordDO();
        a.setLongitude(8.0);
        a.setLatitude(6.0);
        SignRecordDO b = new SignRecordDO();
        b.setLongitude(8.0);
        b.setLatitude(6.0);
        SignRecordDO c = new SignRecordDO();
        c.setLongitude(7.0);
        c.setLatitude(7.0);
        SignRecordDO d = new SignRecordDO();
        d.setLongitude(7.0);
        d.setLatitude(6.0);
        SignRecordDO e = new SignRecordDO();
        e.setLongitude(100.0);
        e.setLatitude(80.04);
        SignRecordDO f = new SignRecordDO();
        f.setLongitude(8.0);
        f.setLatitude(7.0);
        /* SignRecordDO  a = new SignRecordDO();
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
        f.setLongitude(113.932182); f.setLatitude(22.529262);*/

        signRecordDOs.add(a);
        signRecordDOs.add(b);
        signRecordDOs.add(c);
        signRecordDOs.add(d);
        signRecordDOs.add(e);
        signRecordDOs.add(f);

        pickAbnormalPoint(signRecordDOs,0);


    }

}
