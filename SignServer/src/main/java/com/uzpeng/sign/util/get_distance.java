package com.uzpeng.sign.util;

import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.util.outlier_detection.Geo_Hash.GeoHash;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class get_distance {

    //地球平均半径
    private static final double EARTH_RADIUS = 6378137;

    //把经纬度转为度（°）
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a / 2), 2)
                                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
                )
        );
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    //获取签到结果集中  存在数据最多的 地理位置

    public static HashMap<String, Integer> hm = null;
    public static  ArrayList<Double> getMeanLongLatitude(CopyOnWriteArrayList<SignRecordDO> records){

        hm = new HashMap<>();
        HashMap<SignRecordDO, String> signHM = new HashMap<>();

        for (SignRecordDO signDo :
                records) {
            GeoHash gh = new GeoHash(signDo.getLatitude(), signDo.getLongitude());
            String geoStr = gh.getGeoHashBase32();
            addHashMap(geoStr);
           /* if (getMAX_VALUE().equals(geoStr))
                MaxGeoHash = gh;*/
            signHM.put(signDo, geoStr);
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

        ArrayList<Double> al = new ArrayList<>();
        al.add(mean_longitude);
        al.add(mean_latitude);

        return al;
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
        System.out.println(getDistance(113.9333724975586,22.529293060302734,
                113.934736,22.5311));

        //教师大部分正确坐标1 22.528903 113.932128
        //                   2 22.528932  113.932121
        //自自修室              22.5325          113.936666

        //System.out.println(getDistance(113.948717, 22.535964, 113.9488560, 22.535247));
        //System.out.println(getDistance(113.949022, 22.535868, 113.949184, 22.535497));
        //,,,
       // System.out.println(getDistance(113.936666, 22.5325, 113.932121, 22.528932));
       // System.out.println(getDistance(113.935029, 22.530543, 113.934674, 22.530913));  //同个班级的两个聚集点
    }

   /* private static final double EARTH_RADIUS = 6378.137;//地球半径,单位千米
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }


    public static double getDistance(double lat1,double lng1,double lat2,double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;

    }

    public static void main(String[] args)
    {
        //教师大部分正确坐标1 22.528903 113.932128
        //                   2 22.528932  113.932121
        //自自修室              22.5325          113.936666
        System.out.println(get_distance.getDistance(22.75424,112.76535 , 23.014171, 113.10111));

       // System.out.println(get_distance.getDistance(22.528903,113.932128,22.5325,113.936666));
       // System.out.println(get_distance.getDistance(22.529245376586914,113.93232727050781,22.528932,113.932121));
        System.out.println(get_distance.getDistance(22.535247,113.948856,22.535964,113.948717));

    }*/


}
