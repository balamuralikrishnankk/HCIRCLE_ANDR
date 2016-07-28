package models.news_tab_model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrgUnit {

    @SerializedName("ou_id")
    private String ouId;
    @SerializedName("name")
    private String name;
    @SerializedName("tabs")
    private List<Tab> tabs = new ArrayList<Tab>();

    public String getOuId() {
        return ouId;
    }

    public void setOuId(String ouId) {
        this.ouId = ouId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }


}
