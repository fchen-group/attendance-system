package com.uzpeng.sign.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uzpeng.sign.bo.ErrorBO;
import com.uzpeng.sign.support.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class CommonResponseHandler {

    private static final  Gson gson = new GsonBuilder().create();

    public static String handleResponse(String status, String msg, String link){

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus(status);
        commonResponse.setMsg(msg);
        commonResponse.setLink(link);

        return gson.toJson(commonResponse);
    }

    public static String handleResponse(Object obj, Class clazz){
        return handleResponse("success", obj, clazz);
    }

    public static String handleResponse(String status, Object obj, Class clazz){
        StringBuilder builder = new StringBuilder();

        String json = gson.toJson(obj, clazz);
        builder.append(json.substring(0, 1));
        builder.append("\"status\":\"").append(status).append("\",");
        builder.append(json.substring(1));

        return builder.toString();
    }

    public static String handleNoAuthentication(HttpServletResponse response){
        response.setStatus(403);
        ErrorBO errorBO = new ErrorBO();
        errorBO.setStatus("403");
        errorBO.setMsg("Authentication Invalid");
//        errorBO.setMsg(environment.getProperty("link.doc"));
        return gson.toJson(errorBO, ErrorBO.class);
    }

    public static String handleException(HttpServletResponse response){
        response.setStatus(500);
        ErrorBO errorBO = new ErrorBO();
        errorBO.setStatus("failed");
        errorBO.setMsg("Internal Error! Please contact developers");
        return SerializeUtil.toJson(errorBO, ErrorBO.class);
    }
}
