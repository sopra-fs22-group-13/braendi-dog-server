package ch.uzh.ifi.hase.soprafs22.websocket;

import ch.uzh.ifi.hase.soprafs22.Application;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * A helper functions that allows us to get the Spring context instances from non-spring instances,
 * so we do not have to use @autowire
 */
@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    /**
     * Gets the spring instance of a class
     * @param beanClass the class from which the instance should be retrieved
     * @param <T>
     * @return instance of the object where: return instanceof beanClass = true
     */
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
