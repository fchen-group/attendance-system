package com.uzpeng.sign.util;

import com.uzpeng.sign.config.StatusConfig;
import com.uzpeng.sign.domain.SignRecordDO;
import com.uzpeng.sign.service.SignService;
import com.uzpeng.sign.util.outlier_detection.LOF.DataNode;
import com.uzpeng.sign.util.outlier_detection.LOF.OutlierNodeDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatisticsTool_LOF {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsTool_LOF.class);

    public static void pickAbnormalPoint(CopyOnWriteArrayList<SignRecordDO> records){  //选取异常点

        java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.######");
        ArrayList<DataNode> dpoints = new ArrayList<DataNode>();

        Map<String,SignRecordDO> hm = new HashMap<>();

        for(SignRecordDO signRecordDO :records){
            double[] temp = { signRecordDO.getLongitude(), signRecordDO.getLatitude() };
            dpoints.add(new DataNode(signRecordDO.getId()+"", temp));
            hm.put(signRecordDO.getId()+"",signRecordDO);
        }

        OutlierNodeDetect lof = new OutlierNodeDetect();
        List<DataNode> nodeList = lof.getOutlierNode(dpoints);

        for (DataNode node : nodeList) {
            logger.info( df.format(node.getLof()) + "  " +hm.get(node.getNodeName()).getSignRecordDO());
            hm.get(node.getNodeName()).setAccuracy(node.getLof());
            if(node.getLof() > 1) {
                logger.info(node.getNodeName()+"   ---- 可能是异常点");
                //设置 State
                hm.get(node.getNodeName()).setState(StatusConfig.RECORD_FAILED);
            }
        }

    }

}
