package models.news_model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class Item {

    @SerializedName("Id")
    private String id;
    @SerializedName("CreateAt")
    private double createAt;
    @SerializedName("title")
    private String title;
    @SerializedName("headline")
    private String headline;
    @SerializedName("Details")
    private String details;
    @SerializedName("Image")
    private String image;
    @SerializedName("image_url")
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCreateAt() {
        return createAt;
    }

    public void setCreateAt(double createAt) {
        this.createAt = createAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
