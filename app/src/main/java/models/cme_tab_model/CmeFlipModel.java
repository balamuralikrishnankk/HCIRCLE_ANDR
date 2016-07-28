package models.cme_tab_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.SerializedName;


public class CmeFlipModel {

    @SerializedName("response")
    private List<Response> response = new ArrayList<Response>();

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }



}
