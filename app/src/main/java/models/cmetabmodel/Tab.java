package models.cmetabmodel;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Tab {

    @SerializedName("id")
    private String id;
    @SerializedName("tab_name")
    private String tabName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

}
