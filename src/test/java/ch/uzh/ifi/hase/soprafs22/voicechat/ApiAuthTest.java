package ch.uzh.ifi.hase.soprafs22.voicechat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiAuthTest {

    @Test
    void getKeyProp() {
        System.setProperty("api.key", "APIKEYVALUE");
        System.setProperty("api.url", "APIURLVALUE");
        System.setProperty("api.enabled", "True");


        ApiAuth apiAuth = new ApiAuth();
        assertEquals("APIKEYVALUE", apiAuth.getKey());
        assertEquals("APIURLVALUE", apiAuth.getId());
        assertTrue(apiAuth.vcEnabled());
    }

    @Test
    void getKeyNothingSet() {
        ApiAuth apiAuth = new ApiAuth();
        assertEquals("key", apiAuth.getKey());
        assertEquals("url", apiAuth.getId());
        assertFalse(apiAuth.vcEnabled());

    }

}