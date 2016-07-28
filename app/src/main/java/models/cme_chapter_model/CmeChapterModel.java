package models.cme_chapter_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CmeChapterModel {

    @SerializedName("status")
    private Boolean status;
    @SerializedName("response")
    private List<Response> response = new ArrayList<Response>();

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }



}
