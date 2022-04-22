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