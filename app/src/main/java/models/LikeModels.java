package models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by atul on 3/5/16.
 */
public class LikeModels implements Serializable{

    @SerializedName("liked_status")
    private String likedStatus;
    @SerializedName("message")
    private String message;
    @SerializedName("post_id")
    private String post_id;
    @SerializedName("no_of_likes")
    private String no_of_likes;


    public String getNo_of_likes() {
        return no_of_likes;
    }

    public void setNo_of_likes(String no_of_likes) {
        this.no_of_likes = no_of_likes;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }



    public String getLikedStatus() {
        return likedStatus;
    }

    public void setLikedStatus(String likedStatus) {
        this.likedStatus = likedStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
