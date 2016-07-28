package reply_pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by atul on 22/4/16.
 */
import com.google.gson.annotations.SerializedName;

public class ReplyInnerObject implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("create_at")
    private String createAt;
    @SerializedName("update_at")
    private String updateAt;
    @SerializedName("delete_at")
    private String deleteAt;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("channel_id")
    private String channelId;
    @SerializedName("root_id")
    private String rootId;
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("original_id")
    private String originalId;
    @SerializedName("message")
    private String message;
    @SerializedName("type")
    private String type;
    @SerializedName("props")
    private Props props;
    @SerializedName("hashtags")
    private String hashtags;
    @SerializedName("filenames")
    private List<String> filenames = new ArrayList<String>();
    @SerializedName("pending_post_id")
    private String pendingPostId;
    @SerializedName("no_of_reply")
    private int no_of_reply;
    @SerializedName("no_of_likes")
    private int no_of_likes;
    @SerializedName("isLikedByYou")
    private boolean isLikedByYou;

    public boolean isBookmarkedByYou() {
        return isBookmarkedByYou;
    }

    public void setBookmarkedByYou(boolean bookmarkedByYou) {
        isBookmarkedByYou = bookmarkedByYou;
    }

    public void setLikedByYou(boolean likedByYou) {
        isLikedByYou = likedByYou;
    }

    @SerializedName("isBookmarkedByYou")
    private boolean isBookmarkedByYou;

    public boolean isProfile() {
        return profile;
    }

    public void setProfile(boolean profile) {
        this.profile = profile;
    }

    private boolean profile;

    public int getNo_of_reply() {
        return no_of_reply;
    }

    public void setNo_of_reply(int no_of_reply) {
        this.no_of_reply = no_of_reply;
    }

    public int getNo_of_likes() {
        return no_of_likes;
    }

    public void setNo_of_likes(int no_of_likes) {
        this.no_of_likes = no_of_likes;
    }

    public boolean isLikedByYou() {
        return isLikedByYou;
    }

    public void setIsLikedByYou(boolean isLikedByYou) {
        this.isLikedByYou = isLikedByYou;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(String deleteAt) {
        this.deleteAt = deleteAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Props getProps() {
        return props;
    }

    public void setProps(Props props) {
        this.props = props;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public String getPendingPostId() {
        return pendingPostId;
    }

    public void setPendingPostId(String pendingPostId) {
        this.pendingPostId = pendingPostId;
    }


}
