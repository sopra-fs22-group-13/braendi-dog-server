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
