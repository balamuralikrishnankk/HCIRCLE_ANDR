package models.cme_chapter_model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class Filename {

    @SerializedName("Id")
    private String id;
    @SerializedName("caption")
    private String caption;
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("file_type")
    private String fileType;
    @SerializedName("attachment_url")
    private String attachmentUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }


}
