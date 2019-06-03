package com.uzpeng.sign.util.outlier_detection.Geo_Hash;


/**
 * @Description: GeoHash实现经纬度的转化
 */

import com.uzpeng.sign.domain.SignRecordDO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class GeoHash {

/*    public static String Encode(double latitude, double longitude, int precision)
    {
        boolean even = true;
        int bit = 0;
        int ch = 0;
        String geohash = "";

        double[] lat = { -90.0, 90.0 };
        double[] lon = { -180.0, 180.0 };

        if (precision < 1 || precision > 20) precision = 12;

        while (geohash.length() < precision)
        {
            double mid;

            if (even)
            {
                mid = (lon[0] + lon[1]) / 2;
                if (longitude > mid)
                {
                    ch |= Bits[bit];
                    lon[0] = mid;
                }
                else
                    lon[1] = mid;
            }
            else
            {
                mid = (lat[0] + lat[1]) / 2;
                if (latitude > mid)
                {
                    ch |= Bits[bit];
                    lat[0] = mid;
                }
                else
                    lat[1] = mid;
            }

            even = !even;
            if (bit < 4)
                bit++;
            else
            {
                geohash += Base32[ch];
                bit = 0;
                ch = 0;
            }
        }
        return geohash;
    }*/

    private LocationBean location;
    /**
     * 1 2500km;2 630km;3 78km;4 30km
     * 5 2.4km; 6 610m; 7 76m; 8 19m
     * 9 2m
     * 可以对取位数不同参数，进行对比判断
     */
    private int hashLength = 7; //经纬度转化为geohash长度
    private int latLength = 20; //纬度转化为二进制长度
    private int lngLength = 20; //经度转化为二进制长度

    private double minLat;//每格纬度的单位大小
    private double minLng;//每个经度的单位大小
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public GeoHash(double lat, double lng) {
        location = new LocationBean(lat, lng);
        setMinLatLng();
    }

    public int gethashLength() {
        return hashLength;
    }

    private void setMinLatLng() {
        minLat = LocationBean.MAXLAT - LocationBean.MINLAT;
        for (int i = 0; i < latLength; i++) {
            minLat /= 2.0;
        }
        minLng = LocationBean.MAXLNG - LocationBean.MINLNG;
        for (int i = 0; i < lngLength; i++) {
            minLng /= 2.0;
        }
    }

    public List<String> getGeoHashBase32For9() {
        double leftLat = location.getLat() - minLat;
        double rightLat = location.getLat() + minLat;
        double upLng = location.getLng() - minLng;
        double downLng = location.getLng() + minLng;
        List<String> base32For9 = new ArrayList<String>();
        //左侧从上到下 3个
        String leftUp = getGeoHashBase32(leftLat, upLng);
        if (!(leftUp == null || "".equals(leftUp))) {
            base32For9.add(leftUp);
        }
        String leftMid = getGeoHashBase32(leftLat, location.getLng());
        if (!(leftMid == null || "".equals(leftMid))) {
            base32For9.add(leftMid);
        }
        String leftDown = getGeoHashBase32(leftLat, downLng);
        if (!(leftDown == null || "".equals(leftDown))) {
            base32For9.add(leftDown);
        }
        //中间从上到下 3个
        String midUp = getGeoHashBase32(location.getLat(), upLng);
        if (!(midUp == null || "".equals(midUp))) {
            base32For9.add(midUp);
        }
        String midMid = getGeoHashBase32(location.getLat(), location.getLng());
        if (!(midMid == null || "".equals(midMid))) {
            base32For9.add(midMid);
        }
        String midDown = getGeoHashBase32(location.getLat(), downLng);
        if (!(midDown == null || "".equals(midDown))) {
            base32For9.add(midDown);
        }
        //右侧从上到下 3个
        String rightUp = getGeoHashBase32(rightLat, upLng);
        if (!(rightUp == null || "".equals(rightUp))) {
            base32For9.add(rightUp);
        }
        String rightMid = getGeoHashBase32(rightLat, location.getLng());
        if (!(rightMid == null || "".equals(rightMid))) {
            base32For9.add(rightMid);
        }
        String rightDown = getGeoHashBase32(rightLat, downLng);
        if (!(rightDown == null || "".equals(rightDown))) {
            base32For9.add(rightDown);
        }
        return base32For9;
    }

    public boolean sethashLength(int length) {
        if (length < 1) {
            return false;
        }
        hashLength = length;
        latLength = (length * 5) / 2;
        if (length % 2 == 0) {
            lngLength = latLength;
        } else {
            lngLength = latLength + 1;
        }
        setMinLatLng();
        return true;
    }

    public String getGeoHashBase32() {
        return getGeoHashBase32(location.getLat(), location.getLng());
    }
//

    private String getGeoHashBase32(double lat, double lng) {
        boolean[] bools = getGeoBinary(lat, lng);   // true fasle true true
        if (bools == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        /* int len = 0;
        if(bools.length > 5 * hashLength)
            len = 5 * hashLength;
        else
            len = bools.length;*/

        for (int i = 0; i < bools.length; i = i + 5) {
            boolean[] base32 = new boolean[5];
            for (int j = 0; j < 5; j++) {
                base32[j] = bools[i + j];
            }
            char cha = getBase32Char(base32);
            if (' ' == cha) {
                return null;
            }
            sb.append(cha);
        }
        return sb.toString();
    }


    private char getBase32Char(boolean[] base32) {
        if (base32 == null || base32.length != 5) {
            return ' ';
        }
        int num = 0;
        for (boolean bool : base32) {
            //System.out.println("原始num"+num);
            num <<= 1;
            //System.out.println("num: "+num);
            if (bool) {
                num += 1;
            }
        }

        return CHARS[num % CHARS.length];
    }


    private boolean[] getGeoBinary(double lat, double lng) {
        boolean[] latArray = getHashArray(lat, LocationBean.MINLAT, LocationBean.MAXLAT, latLength);  //纬度
        boolean[] lngArray = getHashArray(lng, LocationBean.MINLNG, LocationBean.MAXLNG, lngLength);  //经度
        return merge(latArray, lngArray);
    }


    private boolean[] merge(boolean[] latArray, boolean[] lngArray) {
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
        System.out.println(Arrays.toString(result));
        return result;
    }


    private boolean[] getHashArray(double value, double min, double max, int length) {
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
        }  //通过二分不断逼近这个值，找出与之相邻的二进制数值
        System.out.println(Arrays.toString(result));
        return result;
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //GeoHash g = new GeoHash(40.222012, 116.248283);
        GeoHash g = new GeoHash(22.528813,113.93201);
        System.out.println(g.getGeoHashBase32());
        g.getGeoHashBase32For9();
        // 1001  1+8
        //System.out.println(JsonUtil.parseJson(g.getGeoHashBase32For9()));
    }

}