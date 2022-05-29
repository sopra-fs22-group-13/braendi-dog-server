/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package ch.uzh.ifi.hase.soprafs22.springContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * A helper functions that allows us to get the Spring context instances from non-spring instances,
 * so we do not have to use @autowire
 */
@Component
public class SpringContextRuntime implements ApplicationContextAware, ISpringContext {
    private static ApplicationContext context;
    private static SpringContextRuntime instance;

    public SpringContextRuntime()
    {
        instance = this;
    }

    public static SpringContextRuntime getInstance()
    {
        return instance;
    }

    /**
     * Gets the spring instance of a class
     * @param beanClass the class from which the instance should be retrieved
     * @param <T>
     * @return instance of the object where: return instanceof beanClass = true
     */
    public <T extends Object> T getBean(Class<T> beanClass)
    {
        return context.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        SpringContextRuntime.context = context;
    }

}
