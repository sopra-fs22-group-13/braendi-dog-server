package ch.uzh.ifi.hase.soprafs22.voicechat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiAuthTest {

    @Test
    void getKeyProp() {
        System.setProperty("api.key", "APIKEYVALUE");
        System.setProperty("api.url", "APIURLVALUE");

        ApiAuth apiAuth = new ApiAuth();
        assertEquals("APIKEYVALUE", apiAuth.getKey());
        assertEquals("APIURLVALUE", apiAuth.getId());
    }

    @Test
    void getKeyNothingSet() {
        ApiAuth apiAuth = new ApiAuth();
        assertEquals("key", apiAuth.getKey());
        assertEquals("url", apiAuth.getId());
    }

}