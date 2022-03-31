package ch.uzh.ifi.hase.soprafs22.voicechat;

import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * This requires the environment to have an api.key and an api.url value!
 */
public class RequestGenerator {

    private final Logger log = LoggerFactory.getLogger(RequestGenerator.class);


    String apikey;
    String apiUrl;

    public RequestGenerator()
    {
        try{
            apikey = System.getProperty("SendBirdApiKey");
            apiUrl = System.getProperty("SendBirdApiUrl");

            if(apiUrl == null || apikey == null)
            {
                throw new Exception();
            }

        }catch(Exception e)
        {
            //backup keys that are stored in a dirty java class
            apikey = System.getenv("api.key");
            apiUrl = System.getenv("api.url");
        }

        //failsave
        if(apikey == null || apiUrl == null) {
            apikey = "key";
            apiUrl = "url";
        }
    }

    String getRequest(String urlpart, Map<String, String> params)
    {
        try {
            URL url = new URL(String.format("https://api-%s.sendbird.com/v3/%s", apiUrl, urlpart));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

    String postRequest(String urlpart, String json)
    {
        try
        {
            URL url = new URL(String.format("https://api-%s.sendbird.com/v3/%s", apiUrl, urlpart));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

    boolean deleteRequest(String urlpart)
    {
        try {
            URL url = new URL(String.format("https://api-%s.sendbird.com/v3/%s", apiUrl, urlpart));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
