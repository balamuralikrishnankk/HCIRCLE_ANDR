package models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by atul on 19/5/16.
 */
public class BookMarkListModel implements Serializable{

    @SerializedName("Id")
    private String Id;
    @SerializedName("CreateAt")
    private String CreateAt;
    @SerializedName("UpdateAt")
    private String UpdateAt;
    @SerializedName("DeleteAt")
    private String DeleteAt;
    @SerializedName("UserId")
    private String UserId;
    @SerializedName("ChannelId")
    private String ChannelId;
    @SerializedName("RootId")
    private String RootId;
    @SerializedName("ParentId")
    private String ParentId;
    @SerializedName("OriginalId")
    private String OriginalId;
    @SerializedName("Message")
    private String Message;
    @SerializedName("Type")
    private String Type;
    @SerializedName("Props")
    private String Props;
    @SerializedName("Hashtags")
    private String Hashtags;
    @SerializedName("Filenames")
    private String Filenames;

    public String getId() {
        return Id;
    }
    public void setId(String Id) {
        this.Id = Id;
    }
    public String getCreateAt() {
        return CreateAt;
    }
    public void setCreateAt(String CreateAt) {
        this.CreateAt = CreateAt;
    }
    public String getUpdateAt() {
        return UpdateAt;
    }
    public void setUpdateAt(String UpdateAt) {
        this.UpdateAt = UpdateAt;
    }
    public String getDeleteAt() {
        return DeleteAt;
    }
    public void setDeleteAt(String DeleteAt) {
        this.DeleteAt = DeleteAt;
    }
    public String getUserId() {
        return UserId;
    }
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
    public String getChannelId() {
        return ChannelId;
    }
    public void setChannelId(String ChannelId) {
        this.ChannelId = ChannelId;
    }
    public String getRootId() {
        return RootId;
    }
    public void setRootId(String RootId) {
        this.RootId = RootId;
    }
    public String getParentId() {
        return ParentId;
    }
    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }
    public String getOriginalId() {
        return OriginalId;
    }
    public void setOriginalId(String OriginalId) {
        this.OriginalId = OriginalId;
    }
    public String getMessage() {
        return Message;
    }
    public void setMessage(String Message) {
        this.Message = Message;
    }
    public String getType() {
        return Type;
    }
    public void setType(String Type) {
        this.Type = Type;
    }
    public String getProps() {
        return Props;
    }
    public void setProps(String Props) {
        this.Props = Props;
    }
    public String getHashtags() {
        return Hashtags;
    }

    public void setHashtags(String Hashtags) {
        this.Hashtags = Hashtags;
    }

    public String getFilenames() {
        return Filenames;
    }

    public void setFilenames(String Filenames) {
        this.Filenames = Filenames;
    }

}