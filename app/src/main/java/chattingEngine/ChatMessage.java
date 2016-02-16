package chattingEngine;

/**
 * Created by Lenovo on 26-Jan-16.
 */
public class ChatMessage {
    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private String sender_name;
    private String fileInfo;
    public ChatMessage(){
        message=null;
        fileInfo=null;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
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
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
