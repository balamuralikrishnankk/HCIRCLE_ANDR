package models.cmearticlemodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CmeArticleModel {

    @SerializedName("state")
    private Boolean state;
    @SerializedName("output")
    private List<Output> output = new ArrayList<Output>();

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public List<Output> getOutput() {
        return output;
    }

    public void setOutput(List<Output> output) {
        this.output = output;
    }



}
