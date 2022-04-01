package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.voicechat.requests.RequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManager {
    private final RequestGenerator requestGenerator = new RequestGenerator();
    private final Logger log = LoggerFactory.getLogger(UserManager.class);

    public String getUser(String id)
    {
        String resultJson = requestGenerator.getRequest(String.format("/users/%s", id), null, false);
        return resultJson;
    }

    public String createUser(String userId, String userName)
    {
        String resultJson =  requestGenerator.postRequest("users", String.format("{\"user_id\":\"%s\", \"nickname\":\"%s\", \"profile_url\":\"s\", \"issue_access_token\":true}", userId, userName), false);
        return resultJson;
    }

    public boolean deleteUser(String userId)
    {
        boolean result = requestGenerator.deleteRequest(String.format("users/%s", userId), false);
        return  result;
    }
}
