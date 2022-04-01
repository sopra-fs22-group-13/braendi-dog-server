package ch.uzh.ifi.hase.soprafs22.voicechat;

public class ApiAuth {

    public String getKey()
    {
        String apikey;
        try{
            apikey = System.getProperty("SendBirdApiKey");
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

    public String getId()
    {
        String apiUrl;
        try{
            apiUrl = System.getProperty("SendBirdApiUrl");

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
