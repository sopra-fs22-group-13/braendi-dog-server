package ch.uzh.ifi.hase.soprafs22.mocks;

import ch.uzh.ifi.hase.soprafs22.springContext.ISpringContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Mocks the springContextRuntime helper.
 * You can define what instances this should return by calling returnForClass(..)
 */
public class MockSpringContext implements ISpringContext {

    private Map<Class, Object> storedMappings = new HashMap<>();

    public Object getBean(Class beanClass)
    {
        Object objToReturn = storedMappings.get(beanClass);

        if(objToReturn == null)
        {
            throw new NullPointerException(String.format("there is no mapping set for the class %s, did you forget to add it?", beanClass.toString()));
        }

        return objToReturn;
    }

    /**
     * Specifies what instance will be return upon asking for the beanClass
     */
    public void returnForClass(Class beanClass, Object instanceToReturn)
    {
        storedMappings.put(beanClass, instanceToReturn);
    }
}
