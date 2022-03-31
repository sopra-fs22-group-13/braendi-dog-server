package ch.uzh.ifi.hase.soprafs22.websocket;

import ch.uzh.ifi.hase.soprafs22.Application;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    public static <T extends Object> T getBean(Class<T> beanClass)
    {
        return context.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        SpringContext.context = context;
    }

}
