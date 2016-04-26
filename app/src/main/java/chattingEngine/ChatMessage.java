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
    /*public void setFileList(JSONArray filenames){
       try {
            for (int i = 0; i < filenames.length(); i++) {
                fileList.add(filenames.getString(i));
            }
        }catch(Exception e){
            System.out.println("Unable to store file lists: "+e.toString());
            fileList=null;
        }
   }
   public List<String> getFileList(){
        return this.fileList;
   }*/
}
