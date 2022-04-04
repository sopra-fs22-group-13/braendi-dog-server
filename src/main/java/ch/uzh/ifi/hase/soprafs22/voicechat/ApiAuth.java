package ch.uzh.ifi.hase.soprafs22.voicechat;

/**
 * Helper class that gets the SendBird Api key and Url
 */
public class ApiAuth {

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
