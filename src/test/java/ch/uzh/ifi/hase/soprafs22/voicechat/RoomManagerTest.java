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

package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.voicechat.requests.RequestGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RoomManagerTest {

    @Spy
    RequestGenerator requestGenerator;

    @Mock
    HttpURLConnection connection;

    RoomManager rm = Mockito.spy(new RoomManager());

    @BeforeEach
    void setup()
    {

        try {
            int expected = 200;
            lenient().doReturn(connection).when(requestGenerator).createConnection(Mockito.any());
            lenient().doReturn(expected).when(connection).getResponseCode();
            lenient().doReturn(new ByteArrayOutputStream()).when(connection).getOutputStream();
            //Hello
            byte[] input = {0x48, 0x65, 0x6c, 0x6c , 0x6f};
            //Error
            byte[] error = {0x45, 0x72, 0x72, 0x6f , 0x72};

            lenient().doReturn(new ByteArrayInputStream(input)).when(connection).getInputStream();
            lenient().doReturn(new ByteArrayInputStream(error)).when(connection).getErrorStream();

        }
        catch (IOException e) {
            fail();
        }
    }

    @Test
    void createRoom() {
        String res = rm.createRoom();
        assertEquals("Failed", res);
    }

    @Test
    void getRoom() {
        String res = rm.getRoom("SOMEID");
        assertEquals("Failed", res);
    }

    @Test
    void deleteRoom() {
        Boolean res = rm.deleteRoom("SOMEID");
        assertFalse(res);
    }
}