package models.news_tab_model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class NewsTabModel {

    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
