package com.uzpeng.sign.exception;

import com.uzpeng.sign.bo.ErrorBO;
import com.uzpeng.sign.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@Component
@ControllerAdvice
public class CommonExceptionHandler extends ExceptionHandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);
    @Autowired
    private Environment env;

    @ExceptionHandler({Exception.class})
    private String HandleException(HttpServletResponse response, Exception exception){
        try {
            logger.error("catch exception -> "+exception.getMessage());
            exception.printStackTrace();
            if(exception instanceof BaseException){
                response.setStatus(((BaseException) exception).getStatus());
                Writer writer = response.getWriter();

                logger.error("base exception");
                ErrorBO errorBO = new ErrorBO();
                errorBO.setStatus(env.getProperty("status.failed"));
                errorBO.setMsg(((BaseException) exception).getMsg());
                errorBO.setDoc(env.getProperty("link.doc"));
                writer.write(SerializeUtil.toJson(errorBO, ErrorBO.class));
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                Writer writer = response.getWriter();

                logger.info("not base exception");
                ErrorBO errorBO = new ErrorBO();
                errorBO.setStatus(env.getProperty("status.failed"));
                errorBO.setMsg(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
                errorBO.setDoc(env.getProperty("link.doc"));
                writer.write(SerializeUtil.toJson(errorBO, ErrorBO.class));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
