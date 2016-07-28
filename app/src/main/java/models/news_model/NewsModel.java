
package models.news_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

public class NewsModel {

    @SerializedName("response")
    private List<Response> response = new ArrayList<Response>();

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }


}
