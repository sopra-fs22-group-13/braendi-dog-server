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

package ch.uzh.ifi.hase.soprafs22.voicechat.requests;

import ch.uzh.ifi.hase.soprafs22.voicechat.RoomManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RequestGeneratorTest {

    @Spy
    RequestGenerator requestGenerator;


    @Mock
    HttpURLConnection connection;

    @BeforeEach
    void setup()
    {
        try {
            int expected = 200;
            lenient().doReturn(connection).when(requestGenerator).createConnection(Mockito.any());
            lenient().doReturn(expected).when(connection).getResponseCode();
            lenient().doReturn(new ByteArrayOutputStream()).when(connection).getOutputStream();

            //hello
            byte[] input = {0x48, 0x65, 0x6c, 0x6c , 0x6f};
            //error
            byte[] error = {0x45, 0x72, 0x72, 0x6f , 0x72};

            lenient().doReturn(new ByteArrayInputStream(input)).when(connection).getInputStream();
            lenient().doReturn(new ByteArrayInputStream(error)).when(connection).getErrorStream();

        }
        catch (IOException e) {
        }
    }

    @Test
    void getRequestNormal() {
        String resNormal = requestGenerator.getRequest("someUrl", new HashMap<String ,String>(), false);
        assertEquals("Hello", resNormal);
    }
    @Test
    void getRequestCalls() {
        String resCalls = requestGenerator.getRequest("someUrl", new HashMap<String ,String>(), true);
        assertEquals("Hello", resCalls);
    }
    @Test
    void getRequestErrorCode() {
        try {
            doReturn(312).when(connection).getResponseCode(); //some error
        }
        catch (IOException e) {
            fail();
        }
        String resError = requestGenerator.getRequest("someUrl", new HashMap<String ,String>(), false);
        assertEquals("Hello", resError); //there is no special handling for error codes
    }

    @Test
    void postRequestNormal() {
        String resNormal = requestGenerator.postRequest("someUrl", "{}", false);
        assertEquals("Hello", resNormal);
    }
    @Test
    void postRequestCalls() {
        String resCalls = requestGenerator.postRequest("someUrl", "{}", true);
        assertEquals("Hello", resCalls);
    }
    @Test
    void postRequestErrorCode() {
        try {
            doReturn(312).when(connection).getResponseCode(); //some error
        }
        catch (IOException e) {
            fail();
        }
        String resError = requestGenerator.postRequest("someUrl", "{}", false);
        assertEquals("Error", resError);
    }

    @Test
    void deleteRequestNormal() {
        try {
            doReturn(204).when(connection).getResponseCode(); //some error
        }
        catch (IOException e) {
            fail();
        }
        Boolean resNormal = requestGenerator.deleteRequest("someUrl", false);
        assertTrue(resNormal);
    }
    @Test
    void deleteRequestCalls() {
        try {
            doReturn(204).when(connection).getResponseCode(); //some error
        }
        catch (IOException e) {
            fail();
        }
        Boolean resCalls = requestGenerator.deleteRequest("someUrl", true);
        assertTrue(resCalls);
    }
    @Test
    void deleteRequestErrorCode() {
        try {
            doReturn(234).when(connection).getResponseCode(); //some error
        }
        catch (IOException e) {
            fail();
        }
        Boolean resError = requestGenerator.deleteRequest("someUrl", true);
        assertFalse(resError);
    }
}