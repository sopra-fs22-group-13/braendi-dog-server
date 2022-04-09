package ch.uzh.ifi.hase.soprafs22.springContext;

/**
 * a Helper class that redirects a SpringContext.getBean call to an implementation (by default SpringContextRuntime)
 */
public class SpringContext {

    private static ISpringContext springContext = SpringContextRuntime.getInstance();
    private static ISpringContext runtimeSpringContext = SpringContextRuntime.getInstance();
    public static ISpringContext getSpringContextObject()
    {
        return springContext;
    }

    public static void setSpringContextObject(ISpringContext springContextObject)
    {
        springContext = springContextObject;
    }

    public static void resetSpringContextObject()
    {
        springContext = runtimeSpringContext;
    }

    public static <T extends Object> T getBean(Class<T> beanClass)
    {
        return  springContext.getBean(beanClass);
    }
}
