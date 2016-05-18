package chattingEngine;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 26-Jan-16.
 */
public class ChatMessage {
    private String id;
    private boolean isMe;
    private String message;
    private String userId;
    private String dateTime;
    private String sender_name;
    private String fileInfo;
    private String fileList;
    private String no_of_likes;
    private boolean isLikedByYou;
    private boolean isBookmarkedByYou;


    public boolean isBookmarkedByYou() {
        return isBookmarkedByYou;
    }

    public void setIsBookmarkedByYou(boolean isBookmarkedByYou) {
        this.isBookmarkedByYou = isBookmarkedByYou;
    }

    public boolean isProfile() {
        return profile;
    }

    public void setProfile(boolean profile) {
        this.profile = profile;
    }

    private boolean profile;

    public boolean isLikedByYou() {
        return isLikedByYou;
    }

    public void setIsLikedByYou(boolean isLikedByYou) {
        this.isLikedByYou = isLikedByYou;
    }

    public String getNo_of_likes() {
        return no_of_likes;
    }

    public void setNo_of_likes(String no_of_likes) {
        this.no_of_likes = no_of_likes;
    }


    private String no_of_reply;

    public String getNo_of_reply() {
        return no_of_reply;
    }

    public void setNo_of_reply(String no_of_reply) {
        this.no_of_reply = no_of_reply;
    }


    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    private String parent_id;

    public String getFileList() {
        return this.fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }


    public ChatMessage(){
        message=null;
        fileInfo=null;
        fileList=null;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setSenderName(String sender){this.sender_name=sender;}
    public String getSenderName(){return this.sender_name;}
    public void setFileInfo(String info){this.fileInfo=info;}
    public String getFileInfo(){return this.fileInfo;}
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
