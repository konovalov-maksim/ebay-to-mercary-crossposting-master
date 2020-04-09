package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Item {

    private final String id;

    private String title;

    private String description;

    private Double ebayPrice;

    private Integer price;

    private List<String> imagesUrls = new ArrayList<>();

    private List<File> images = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    private Integer categoryId;

    private int conditionId;

    private String status;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + " - " + (title != null ? title : "Unnamed item");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getEbayPrice() {
        return ebayPrice;
    }

    public void setEbayPrice(Double ebayPrice) {
        this.ebayPrice = ebayPrice;
    }

    public List<String> getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(List<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }

    public List<File> getImages() {
        return images;
    }

    public void setImages(List<File> images) {
        this.images = images;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

}
