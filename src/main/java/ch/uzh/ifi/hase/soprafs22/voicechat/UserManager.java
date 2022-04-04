package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.voicechat.requests.RequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that provides functions for certain user-specific API calls to SendBird.
 */
public class UserManager {
    private final RequestGenerator requestGenerator = new RequestGenerator();
    private final Logger log = LoggerFactory.getLogger(UserManager.class);

    /**
     * Gets a users information by their id
     * @param id
     * @return the response as a String
     */
    public String getUser(String id)
    {
        String resultJson = requestGenerator.getRequest(String.format("/users/%s", id), null, false);
        return resultJson;
    }

    /**
     * Creates a SendBird user with a certain username
     * @param userId
     * @param userName
     * @return the response String
     */
    public String createUser(String userId, String userName)
    {
        String resultJson =  requestGenerator.postRequest("users", String.format("{\"user_id\":\"%s\", \"nickname\":\"%s\", \"profile_url\":\"s\", \"issue_access_token\":true}", userId, userName), false);
        return resultJson;
    }

    /**
     * Deletes a User by their id
     * @param userId
     * @return a boolean whether the response code is 204 (success)
     */
    public boolean deleteUser(String userId)
    {
        boolean result = requestGenerator.deleteRequest(String.format("users/%s", userId), false);
        return  result;
    }
}
