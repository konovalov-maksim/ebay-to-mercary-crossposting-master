package core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Item {

    private String id;
    private String title;
    private String description;
    private Double ebayPrice;
    private Double ebayShippingPrice;
    private Integer price;
    private Integer shippingPrice;
    private List<String> imagesUrls = new ArrayList<>();
    private List<File> images = new ArrayList<>();
    private String[] tags = new String[]{null, null, null};
    private Condition condition;
    private Category category;
    private String ebayUrl;
    private String mercariUrl;
    private boolean isUploaded = false;
    private String status;

    public Item() {
    }

    public Item(String id) {
        this.id = id;
    }

    public boolean isValid() {
        if (title == null || title.length() == 0 || title.length() > 40 ) return false;
        if (description == null || description.length() == 0 || description.length() > 1000 ) return false;
        if (getFinalPrice() < 5) return false;
        if (images == null || images.isEmpty() || images.size() > 12) return false;
        if (condition == null) return false;
        if (category == null) return false;
        return true;
    }

    public Integer getFinalPrice() {
        return getPrice() + getShippingPrice();
    }

    @Override
    public String toString() {
        return id + " - " + (title != null ? title : "Unnamed item");
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImagesNum() {
        return images != null ? images.size() : 0;
    }

    public Integer getConditionId() {
        return condition != null ? condition.getId() : null;
    }

    public String getConditionName() {
        return condition != null ? condition.getName() : null;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public Category getCategory() {
        return category;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTagsString() {
        return tags != null ? Arrays.stream(tags)
                .filter(t -> t != null && !t.isEmpty())
                .collect(Collectors.joining(", ")) : "";
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
        return Arrays.stream(tags).filter(t -> t != null && !t.isEmpty()).collect(Collectors.toList());
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

    public Integer getPrice() {
        return price != null ? price : 0;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTag0() {
        return tags[0];
    }

    public void setTag0(String value) {
        tags[0] = value;
    }

    public String getTag1() {
        return tags[1];
    }

    public void setTag1(String value) {
        tags[1] = value;
    }

    public String getTag2() {
        return tags[2];
    }

    public void setTag2(String value) {
        tags[2] = value;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getEbayUrl() {
        return ebayUrl;
    }

    public void setEbayUrl(String ebayUrl) {
        this.ebayUrl = ebayUrl;
    }

    public String getMercariUrl() {
        return mercariUrl;
    }

    public void setMercariUrl(String mercariUrl) {
        this.mercariUrl = mercariUrl;
    }

    public Double getEbayShippingPrice() {
        return ebayShippingPrice;
    }

    public void setEbayShippingPrice(Double ebayShippingPrice) {
        this.ebayShippingPrice = ebayShippingPrice;
    }

    public Integer getShippingPrice() {
        return shippingPrice != null ? shippingPrice : 0;
    }

    public void setShippingPrice(Integer shippingPrice) {
        this.shippingPrice = shippingPrice;
    }
}
