package models.cmetabmodel;

import com.google.gson.annotations.SerializedName;

public class CmeTabModel {

    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }


}
