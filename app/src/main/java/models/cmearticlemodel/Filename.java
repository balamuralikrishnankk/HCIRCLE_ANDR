package models.cmearticlemodel;

import com.google.gson.annotations.SerializedName;

public class Filename {

    @SerializedName("Id")
    private String id;
    @SerializedName("article_id")
    private String articleId;
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("caption")
    private String caption;
    @SerializedName("attachment_url")
    private String attachmentUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }



}
