package com.uzpeng.sign.util;

import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.util.outlier_detection.Geo_Hash.GeoHash;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatisticsTool_GeoHash {


    // 228-46 214-46
    //精度是79，但后一个直接断层是640
    public static HashMap<String, Integer> hm = new HashMap<>();

    public static void pickAbnormalPoint(CopyOnWriteArrayList<SignRecordDO> records, Integer signId) {


        ArrayList al = new ArrayList();
        GeoHash MaxGeoHash = null;
        for (SignRecordDO signDo :
                records) {
            GeoHash gh = new GeoHash(signDo.getLatitude(), signDo.getLongitude());
            addHashMap(gh.getGeoHashBase32());
            if (getMAX_VALUE().equals(gh.getGeoHashBase32()))
                MaxGeoHash = gh;

            al.add(gh.getGeoHashBase32());
        }

        System.out.println("最后获取的最大MaxStr" + getMAX_VALUE());
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getKey() + "  " + list.get(i).getValue());
        }
        System.out.println("过程中获取的最大MaxStr" + MaxGeoHash.getGeoHashBase32());

        //取均值 然后找出附近的9个点。
        System.out.println("该点的周围9个点：" + MaxGeoHash.getGeoHashBase32For9().toString());

        for (int i = 0; i < al.size(); i++)

            System.out.println(records.get(i).getId() + ": (" + records.get(i).getLatitude() + "," + records.get(i).getLongitude() + ")  " + al.get(i));
        System.out.println("----------------------------------分割线");
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

        pickAbnormalPoint(signRecordDOs, 0);


    }
}
