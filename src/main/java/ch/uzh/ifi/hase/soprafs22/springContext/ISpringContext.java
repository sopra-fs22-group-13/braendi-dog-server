package ch.uzh.ifi.hase.soprafs22.springContext;
import org.hibernate.cfg.NotYetImplementedException;

/**
 * Interface for SpringContextRuntime. We can replace the SpringContextRuntime with a mock version if needed.
 */
public interface ISpringContext {
    <T extends Object> T getBean(Class<T> beanClass);
}
