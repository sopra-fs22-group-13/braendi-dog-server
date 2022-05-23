package ch.uzh.ifi.hase.soprafs22.heartbeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;

public class HeartBeatManager {
    private static HeartBeatManager instance = null;

    private Map<String, PlayerHeartBeat> playerHeartBeatMap = new HashMap<>();

    UserService userService = SpringContext.getBean(UserService.class);

    private HeartBeatManager() {
    }

    public static HeartBeatManager getInstance() {
        if (instance == null) {
            instance = new HeartBeatManager();
        }

        return instance;
    }

    public void addHeartBeat(String playerToken, HeartBeatType type) {
        PlayerHeartBeat phb = playerHeartBeatMap.get(playerToken);
        if (phb == null) {
            // create a new one
            phb = new PlayerHeartBeat(4000L);
            playerHeartBeatMap.put(playerToken, phb);
            updateOnlineStatus(playerToken, true);
        }

        // add heartbeat
        phb.updateHeartBeat(type);
    }

    public String[] getAllActivePlayerTokens() {
        return playerHeartBeatMap.keySet().toArray(new String[0]);
    }

    public Map<HeartBeatType, Boolean> getHeartBeatsOfPlayer(String playerToken) {
        PlayerHeartBeat phb = playerHeartBeatMap.get(playerToken);
        if (phb == null)
            return null;

        Map<HeartBeatType, Boolean> res = phb.isValidHeartBeat();
        if (isAllFalse(res)) {
            // player went offline
            updateOnlineStatus(playerToken, false);
            playerHeartBeatMap.remove(playerToken);
        }

        return res;
    }


    public void updateOnlineStatus(String token, Boolean online) {
        User user = userService.getUserByToken(token);
        if(online){
            userService.setUserOnline(user);
        }else{
            userService.setUserOffline(user);
        }
    }

    private boolean isAllFalse(Map<HeartBeatType, Boolean> map) {
        boolean allFalse = true;
        for (Boolean b : map.values()) {
            if (b)
                allFalse = false;
        }

        return allFalse;
    }
}
