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

package ch.uzh.ifi.hase.soprafs22.voicechat.requests;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParameterStringBuilderTest {

    @Test
    void getParamsString() {
        Map<String, String> m = new HashMap<>();
        m.put("testParam1", "testValue1");
        m.put("testParam2", "testValue2");

        String result = null;
        try {
            result = ParameterStringBuilder.getParamsString(m);
        }
        catch (UnsupportedEncodingException e) {
            fail();
        }
        assertEquals("testParam1=testValue1&testParam2=testValue2", result);
    }

    @Test
    void getParamsStringEmpty() {
        Map<String, String> m = new HashMap<>();

        String result = null;
        try {
            result = ParameterStringBuilder.getParamsString(m);
        }
        catch (UnsupportedEncodingException e) {
            fail();
        }
        assertEquals("", result);
    }

}