package models.cmetabmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrgUnit {

    @SerializedName("id")
    private Object id;
    @SerializedName("name")
    private String name;
    @SerializedName("tabs")
    private List<Tab> tabs = new ArrayList<Tab>();

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
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
