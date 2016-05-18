package network;

import java.util.List;
import java.util.Map;

/**
 * Created by yashesh on 6/8/2015.
 */
public class NetworkResponse {

    int responseCode;
    Map<String,List<String>> responseHeaders;
    String responseString;


    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
}
