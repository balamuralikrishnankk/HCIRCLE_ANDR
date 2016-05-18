package models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by atul on 14/5/16.
 */
public class BookMarkModels implements Serializable {

    @SerializedName("post_id")
    private String postId;
    @SerializedName("status")
    private Boolean status;
    @SerializedName("message")
    private String message;

    public String getBookmark_type() {
        return bookmark_type;
    }

    public void setBookmark_type(String bookmark_type) {
        this.bookmark_type = bookmark_type;
    }

    @SerializedName("bookmark_type")
    private String bookmark_type;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
