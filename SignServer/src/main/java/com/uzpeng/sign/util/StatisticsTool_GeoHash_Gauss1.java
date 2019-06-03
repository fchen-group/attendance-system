package com.uzpeng.sign.util;

import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.util.outlier_detection.Geo_Hash.GeoHash;
import com.uzpeng.sign.util.outlier_detection.Geo_Hash.LocationBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatisticsTool_GeoHash_Gauss1 {


    private int hashLength = 9; //经纬度转化为geohash长度
    private static  int latLength = 8; //纬度转化为二进制长度
    private static  int lngLength = 8; //经度转化为二进制长度

    private  static  double minLat;//每格纬度的单位大小
    private static  double minLng;//每个经度的单位大小



    public static void pickAbnormalPoint(CopyOnWriteArrayList<SignRecordDO> records) {

        setMinLatLng();

        List<Double> tude = new ArrayList<>();

        for (SignRecordDO signDo :
                records) {

            boolean[] bools = getGeoBinary(signDo.getLatitude(), signDo.getLongitude());   // true fasle true true

            //GeoHash g = new GeoHash(signDo.getLatitude(), signDo.getLongitude());
            tude.add(Change2To10(bools));
            // hm.put(signDo.getId(),Change2To10(bools));
        }
        double tudeMean = getMean(tude);
        double tudeSD = getSD(tude, tudeMean);

        double MIN_VALUE = tudeMean - 3 * tudeSD;
        double MAX_VALUE = tudeMean + 3 * tudeSD;

        System.out.println("X_MIN_VALUE:" + MIN_VALUE + "   X_MAX_VALUE:" + MAX_VALUE);
        double Y = getNormalDistributionProbabilityDestiny(MAX_VALUE, tudeMean, tudeSD);
        System.out.println("Y_" + Y);

        for (int i = 0; i < records.size(); i++) {

            System.out.println("根据X直接判断。则是否在区间之间");
            if (tude.get(i) <= MAX_VALUE && tude.get(i) >= MIN_VALUE)
                System.out.println(records.get(i).getId() + "   " + tude.get(i) + "正常。");
            else {
                System.out.println(records.get(i).getId() + "   " + tude.get(i) + "异常。");
            }

            System.out.println("根据Y大小判断");
            double x = getNormalDistributionProbabilityDestiny(tude.get(i), tudeMean, tudeSD);
            if (x >=Y) {
                System.out.println(records.get(i).getId() + "   " + x + "正常。");
            } else {
                System.out.println(records.get(i).getId() + "   " + x + "异常。");
            }
        }

    }

   //geo函数------------------------------------


    private static  void setMinLatLng() {
        minLat = LocationBean.MAXLAT - LocationBean.MINLAT;
        for (int i = 0; i < latLength; i++) {
            minLat /= 2.0;
        }
        minLng = LocationBean.MAXLNG - LocationBean.MINLNG;
        for (int i = 0; i < lngLength; i++) {
            minLng /= 2.0;
        }
    }

    private static  boolean[] getGeoBinary(double lat, double lng) {
        boolean[] latArray = getHashArray(lat, LocationBean.MINLAT, LocationBean.MAXLAT, latLength);
        boolean[] lngArray = getHashArray(lng, LocationBean.MINLNG, LocationBean.MAXLNG, lngLength);
        return merge(latArray, lngArray);
    }


    private static boolean[] merge(boolean[] latArray, boolean[] lngArray) {
        if (latArray == null || lngArray == null) {
            return null;
        }
        boolean[] result = new boolean[lngArray.length + latArray.length];
        Arrays.fill(result, false);
        for (int i = 0; i < lngArray.length; i++) {
            result[2 * i] = lngArray[i];
        }
        for (int i = 0; i < latArray.length; i++) {
            result[2 * i + 1] = latArray[i];
        }

        return result;
    }

    private static boolean[] getHashArray(double value, double min, double max, int length) {
        if (value < min || value > max) {
            return null;
        }
        if (length < 1) {
            return null;
        }
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            double mid = (min + max) / 2.0;
            if (value > mid) {
                result[i] = true;
                min = mid;
            } else {
                result[i] = false;
                max = mid;
            }
        }
        return result;
    }



    public static double Change2To10(boolean[] bools) {

        int num = 0;
        for (int i = 0; i < bools.length; i++) {
            num <<= 1;
            if (bools[i])
                num += 1;
        }

        return (double) num;
    }


    //gauss函数------------------------------------------------
    //求均值mean
    private static double getMean(List<Double> data) {

        CopyOnWriteArrayList<Double> safeData = new CopyOnWriteArrayList<>();
        safeData.addAll(data);

        double result = 0;
        for (Double d : safeData) {
            result += d;
        }

        return result / data.size();
    }

    //求平方差sd（已开根号）   正常方差是没有开方的
    private static double getSD(List<Double> data) {
        return getSD(data, getMean(data));
    }

    private static double getSD(List<Double> data, double mean) {

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
    private static double getNormalDistributionProbabilityDestiny(double data, double mean, double sd) {
        double base = 1 / (Math.sqrt(2 * Math.PI) * sd);
        double exponent = -((Math.pow((data - mean), 2)) / (2 * Math.pow(sd, 2)));

        return base * Math.pow(Math.E, exponent);
    }

    public static void main(String[] args) {

        CopyOnWriteArrayList<SignRecordDO> signRecordDOs = new CopyOnWriteArrayList<>();

        SignRecordDO  a = new SignRecordDO();
        a.setLongitude(8.0); a.setLatitude(6.0);
        SignRecordDO  b = new SignRecordDO();
        b.setLongitude(8.0); b.setLatitude(6.0);
        SignRecordDO  c = new SignRecordDO();
        c.setLongitude(7.0); c.setLatitude(7.0);
        SignRecordDO  d = new SignRecordDO();
        d.setLongitude(7.0); d.setLatitude(6.0);
        SignRecordDO  e = new SignRecordDO();
        e.setLongitude(100.0); e.setLatitude(80.04);
        SignRecordDO  f = new SignRecordDO();
        f.setLongitude(8.0); f.setLatitude(7.0);
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

        signRecordDOs.add(a);signRecordDOs.add(b);signRecordDOs.add(c);signRecordDOs.add(d);
        signRecordDOs.add(e);signRecordDOs.add(f);

        pickAbnormalPoint(signRecordDOs);


    }


}
