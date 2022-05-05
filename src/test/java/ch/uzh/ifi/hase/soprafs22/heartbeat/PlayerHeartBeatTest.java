package ch.uzh.ifi.hase.soprafs22.heartbeat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerHeartBeatTest {

    PlayerHeartBeat phb;

    @BeforeEach
    void setup()
    {
        phb = new PlayerHeartBeat(100L);
    }

    void isIdentical(Map<HeartBeatType, Boolean> map1, Map<HeartBeatType, Boolean> map2)
    {
        for (HeartBeatType hbt : HeartBeatType.values()) {
            assertEquals(map1.get(hbt), map2.get(hbt));
        }
    }

    @Test
    void CorrectStatus1() {
        //no heartbeat yet at all
        //this should not be possible in practise as if the object exists, a heartbeat should be recorded

        Map<HeartBeatType, Boolean> resultHBT = new HashMap<>();
        resultHBT.put(HeartBeatType.GAME, false);
        resultHBT.put(HeartBeatType.LOBBY, false);
        resultHBT.put(HeartBeatType.MENU, false);
        resultHBT.put(HeartBeatType.NOT_SET, false);

        isIdentical(resultHBT, phb.isValidHeartBeat());
    }

    @Test
    void CorrectStatus2() {
        //no heartbeat yet at all

        Map<HeartBeatType, Boolean> resultHBT = new HashMap<>();
        resultHBT.put(HeartBeatType.GAME, true);
        resultHBT.put(HeartBeatType.LOBBY, true);
        resultHBT.put(HeartBeatType.MENU, true);
        resultHBT.put(HeartBeatType.NOT_SET, true);

        //make a MENU HeartBeat, then still everything should be valid
        phb.updateHeartBeat(HeartBeatType.MENU);

        isIdentical(resultHBT, phb.isValidHeartBeat());


        resultHBT = new HashMap<>();
        resultHBT.put(HeartBeatType.GAME, false);
        resultHBT.put(HeartBeatType.LOBBY, false);
        resultHBT.put(HeartBeatType.MENU, true);
        resultHBT.put(HeartBeatType.NOT_SET, false);

        //make one more MENU update, the rest should now be invalid
        phb.updateHeartBeat(HeartBeatType.MENU);

        isIdentical(resultHBT, phb.isValidHeartBeat());


        resultHBT = new HashMap<>();
        resultHBT.put(HeartBeatType.GAME, true);
        resultHBT.put(HeartBeatType.LOBBY, true);
        resultHBT.put(HeartBeatType.MENU, true);
        resultHBT.put(HeartBeatType.NOT_SET, true);

        //make one more MENU update, the rest should now be valid again. it is only invalid once on change
        phb.updateHeartBeat(HeartBeatType.MENU);

        isIdentical(resultHBT, phb.isValidHeartBeat());

        try {
            Thread.sleep(300);
            //everything should be false after a check
            resultHBT = new HashMap<>();
            resultHBT.put(HeartBeatType.GAME, false);
            resultHBT.put(HeartBeatType.LOBBY, false);
            resultHBT.put(HeartBeatType.MENU, false);
            resultHBT.put(HeartBeatType.NOT_SET, false);

            isIdentical(resultHBT, phb.isValidHeartBeat());

        }
        catch (InterruptedException e) {
            fail();
        }

    }

}