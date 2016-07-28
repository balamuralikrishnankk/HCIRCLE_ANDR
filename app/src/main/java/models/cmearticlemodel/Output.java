package models.cmearticlemodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Output {

    @SerializedName("Id")
    private String id;
    @SerializedName("CreateAt")
    private Double createAt;
    @SerializedName("UpdateAt")
    private Double updateAt;
    @SerializedName("DeleteAt")
    private Double deleteAt;
    @SerializedName("Name")
    private String name;
    @SerializedName("TabId")
    private String tabId;
    @SerializedName("Textual_content")
    private String textualContent;
    @SerializedName("Images")
    private String images;
    @SerializedName("Filenames")
    private List<Filename> filenames = new ArrayList<Filename>();
    @SerializedName("Links")
    private String links;
    @SerializedName("Active")
    private String active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Double createAt) {
        this.createAt = createAt;
    }

    public Double getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Double updateAt) {
        this.updateAt = updateAt;
    }

    public Double getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Double deleteAt) {
        this.deleteAt = deleteAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getTextualContent() {
        return textualContent;
    }

    public void setTextualContent(String textualContent) {
        this.textualContent = textualContent;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public List<Filename> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<Filename> filenames) {
        this.filenames = filenames;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }


}
