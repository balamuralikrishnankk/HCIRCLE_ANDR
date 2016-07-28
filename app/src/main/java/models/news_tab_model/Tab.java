package models.news_tab_model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class Tab {

    @SerializedName("tab_id")
    private String tabId;
    @SerializedName("tab_name")
    private String tabName;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }


}
