package com.uzpeng.sign.web;

import com.uzpeng.sign.bo.ErrorBO;
import com.uzpeng.sign.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 */
@Controller
public class ErrorController {
    @Autowired
    private Environment environment;

    @RequestMapping(value = "/errors", method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String handleError(HttpServletResponse response){
        response.setStatus(403);
        ErrorBO errorBO = new ErrorBO();
        errorBO.setMsg("Parameter Invalid Request!");
        errorBO.setDoc(environment.getProperty("link.doc"));

        return SerializeUtil.toJson(errorBO, ErrorBO.class);
    }

}
