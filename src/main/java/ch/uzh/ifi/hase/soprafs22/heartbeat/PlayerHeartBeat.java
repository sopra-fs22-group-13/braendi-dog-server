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

package ch.uzh.ifi.hase.soprafs22.heartbeat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlayerHeartBeat {
    private Map<HeartBeatType, Long> timestamps = new HashMap<>();
    private Map<HeartBeatType, Boolean> notYetReceivedOnes = new HashMap<>();

    private final Long OKDELAY;

    PlayerHeartBeat(Long okdelay)
    {
        for (HeartBeatType hbt:HeartBeatType.values()) {
            timestamps.put(hbt, 0L);
        }
        for (HeartBeatType hbt:HeartBeatType.values()) {
            notYetReceivedOnes.put(hbt, true);
        }

        OKDELAY = okdelay;
    }

    public void updateHeartBeat(HeartBeatType heartBeatType)
    {
        timestamps.replace(heartBeatType, new Date().getTime());
    }

    public Map<HeartBeatType, Boolean> isValidHeartBeat()
    {
        boolean atLeastOneValidHeartBeat = false;
        Map<HeartBeatType, Boolean> isOkMap = new HashMap<>();
        Map<HeartBeatType, Boolean> new_notYet = new HashMap<>(notYetReceivedOnes);
        for (HeartBeatType hbt:HeartBeatType.values()) {

            Long timestamp = timestamps.get(hbt);
            Boolean notYetReceivedOne = notYetReceivedOnes.get(hbt);

            if(new Date().getTime() < timestamp + OKDELAY)
            {
                atLeastOneValidHeartBeat = true;
                isOkMap.put(hbt, true);
            }
            else if(notYetReceivedOne)
            {
                isOkMap.put(hbt, true);
                new_notYet.replace(hbt, false);
            }else
            {
                new_notYet.replace(hbt, true);
                isOkMap.put(hbt, false);
            }
        }

        notYetReceivedOnes = new_notYet;

        if(!atLeastOneValidHeartBeat)
        {
            Map<HeartBeatType, Boolean> res = new HashMap<>();
            for (HeartBeatType hbt:HeartBeatType.values()) {
                res.put(hbt, false);
            }
            return res;
        }

        return isOkMap;
    }
}
