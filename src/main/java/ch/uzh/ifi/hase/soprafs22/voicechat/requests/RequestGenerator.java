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

import ch.uzh.ifi.hase.soprafs22.voicechat.ApiAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * This requires the environment to have an api.key and an api.url value!
 * Various requests are predefined here.
 */
public class RequestGenerator {

    private final Logger log = LoggerFactory.getLogger(RequestGenerator.class);


    String apikey;
    String apiUrl;

    public RequestGenerator()
    {
        apikey = new ApiAuth().getKey();
        apiUrl = new ApiAuth().getId();
    }

    public HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * Make a get request to either the calls or chat SendBird API
     * @param urlpart the specific resource to get
     * @param params Get params
     * @param calls is it the calls API?
     * @return the response
     */
    public String getRequest(String urlpart, Map<String, String> params, boolean calls)
    {
        try {
            URL url;
            if(calls)
            {
                url = new URL(String.format("https://api-%s.calls.sendbird.com/v1/%s", apiUrl, urlpart));

            }else
            {
                url = new URL(String.format("https://api-%s.sendbird.com/v3/%s", apiUrl, urlpart));
            }
            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("GET");

            //parameters
            if (params != null)
            {
                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(ParameterStringBuilder.getParamsString(params));
                out.flush();
                out.close();
            }

            //headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Api-Token", apikey);

            //response
            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            log.info(String.format("GET complete to %s", urlpart));
            return content.toString();
        }
        catch (Exception e) {
            return "Failed";
        }
    }

    /**
     * Make a post request to either the calls or chat SendBird API
     * @param urlpart the specific resource to post to
     * @param json post json content
     * @param calls is it the calls API?
     * @return the response
     */
    public String postRequest(String urlpart, String json, boolean calls)
    {
        try
        {
            URL url;
            if(calls)
            {
                url = new URL(String.format("https://api-%s.calls.sendbird.com/v1/%s", apiUrl, urlpart));

            }else
            {
                url = new URL(String.format("https://api-%s.sendbird.com/v3/%s", apiUrl, urlpart));
            }

            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Api-Token", apikey);

            //json data
            byte[] out = json.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            conn.setFixedLengthStreamingMode(length);
            try(OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            //response
            int status = conn.getResponseCode();

            Reader streamReader = null;
            if(status > 299)
            {
                streamReader = new InputStreamReader(conn.getErrorStream());
            }else
            {
                streamReader = new InputStreamReader(conn.getInputStream());
            }

            BufferedReader in = new BufferedReader(
                    streamReader);
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            log.info(String.format("POST complete to %s", urlpart));
            return content.toString();

        }catch (Exception e)
        {
            return "Failed";
        }
    }

    /**
     * Make a delete request to either the calls or chat SendBird API
     * @param urlpart the specific resource to delete
     * @param calls is it the calls API?
     * @return boolean: is the response code 204? (success)
     */
    public boolean deleteRequest(String urlpart, boolean calls)
    {
        try {
            URL url;
            if(calls)
            {
                url = new URL(String.format("https://api-%s.calls.sendbird.com/v1/%s", apiUrl, urlpart));

            }else
            {
                url = new URL(String.format("https://api-%s.sendbird.com/v3/%s", apiUrl, urlpart));
            }
            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("DELETE");

            //headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Api-Token", apikey);

            int status = conn.getResponseCode();

            if(status == 204)
            {
                log.info(String.format("DELETE complete to %s", urlpart));
                return true;
            }
            return false;

        }
        catch (Exception e) {
            return  false;
        }

    }

}
