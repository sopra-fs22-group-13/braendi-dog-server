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
            byte[] input = {0x48, 0x65, 0x6c, 0x6c , 0x6f};
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