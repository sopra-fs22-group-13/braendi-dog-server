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

import java.util.Objects;

/**
 * Helper class that gets the SendBird Api key and Url
 */
public class ApiAuth {

    public boolean vcEnabled()
    {
        String vc;
        try{
            vc = System.getProperty("api.enabled");
            if(vc == null)
            {
                throw new Exception();
            }

        }catch(Exception e)
        {
            //backup keys that are stored in a dirty java class
            vc = System.getenv("api.enabled");
        }

        //failsave
        if(vc == null) {
            vc = "false";
        }
        return Objects.equals(vc, "true") || Objects.equals(vc, "True");
    }

    /**
     * Gets the SendBird API key
     * @return
     */
    public String getKey()
    {
        String apikey;
        try{
            apikey = System.getProperty("api.key");
            if(apikey == null)
            {
                throw new Exception();
            }

        }catch(Exception e)
        {
            //backup keys that are stored in a dirty java class
            apikey = System.getenv("api.key");
        }

        //failsave
        if(apikey == null) {
            apikey = "key";
        }
        return apikey;
    }

    /**
     * Gets the SendBird AppId / Url
     * @return
     */
    public String getId()
    {
        String apiUrl;
        try{
            apiUrl = System.getProperty("api.url");

            if(apiUrl == null)
            {
                throw new Exception();
            }

        }catch(Exception e)
        {
            //backup keys that are stored in a dirty java class
            apiUrl = System.getenv("api.url");
        }

        //failsave
        if(apiUrl == null) {
            apiUrl = "url";
        }
        return apiUrl;
    }

}
