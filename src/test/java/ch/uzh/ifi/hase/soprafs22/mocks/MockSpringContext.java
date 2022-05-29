/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

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
