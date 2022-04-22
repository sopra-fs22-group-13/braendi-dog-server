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
            log.info(content.toString());
            return content.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
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

            log.info(url.toString());


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
            log.info(content.toString());
            return content.toString();

        }catch (Exception e)
        {
            e.printStackTrace();
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
                return true;
            }
            return false;

        }
        catch (Exception e) {
            e.printStackTrace();
            return  false;
        }

    }

}
