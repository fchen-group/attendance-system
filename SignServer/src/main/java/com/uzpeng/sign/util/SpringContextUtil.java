package com.uzpeng.sign.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        applicationContext = arg0;
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static Object getBeanById(String id){
        return applicationContext.getBean(id);
    }

    public static <T> T getBeanByClass(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }
}
