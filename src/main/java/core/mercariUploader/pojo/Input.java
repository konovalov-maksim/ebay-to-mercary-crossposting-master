package core.mercariUploader.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import core.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Input {

    public Input(Item item, List<String> imageIds, String zipCode) {
        this.photoIds = imageIds;
        this.name = item.getTitle();
        this.price = item.getFinalPrice() * 100;
        this.description = item.getDescription();
        this.categoryId = item.getCategoryId();
        this.conditionId = item.getConditionId();
        this.zipCode = zipCode;
        this.tags = item.getTags();
        this.salesFee = item.getFinalPrice() * 10;
        this.shippingPayerId = 2;
        this.shippingClassIds = Arrays.asList(0);
        this.isAutoPriceDrop = false;
        this.minPriceForAutoPriceDrop = item.getFinalPrice() * 80;
    }

    @SerializedName("photoIds")
    @Expose
    private List<String> photoIds = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("conditionId")
    @Expose
    private Integer conditionId;
    @SerializedName("zipCode")
    @Expose
    private String zipCode;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("salesFee")
    @Expose
    private Integer salesFee;
    @SerializedName("shippingPayerId")
    @Expose
    private Integer shippingPayerId;
    @SerializedName("shippingClassIds")
    @Expose
    private List<Integer> shippingClassIds = new ArrayList<>();
    @SerializedName("isAutoPriceDrop")
    @Expose
    private Boolean isAutoPriceDrop;
    @SerializedName("minPriceForAutoPriceDrop")
    @Expose
    private Integer minPriceForAutoPriceDrop;

    public List<String> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(List<String> photoIds) {
        this.photoIds = photoIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getConditionId() {
        return conditionId;
    }

    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getSalesFee() {
        return salesFee;
    }

    public void setSalesFee(Integer salesFee) {
        this.salesFee = salesFee;
    }

    public Integer getShippingPayerId() {
        return shippingPayerId;
    }

    public void setShippingPayerId(Integer shippingPayerId) {
        this.shippingPayerId = shippingPayerId;
    }

    public List<Integer> getShippingClassIds() {
        return shippingClassIds;
    }

    public void setShippingClassIds(List<Integer> shippingClassIds) {
        this.shippingClassIds = shippingClassIds;
    }

    public Boolean getAutoPriceDrop() {
        return isAutoPriceDrop;
    }

    public void setAutoPriceDrop(Boolean autoPriceDrop) {
        isAutoPriceDrop = autoPriceDrop;
    }

    public Integer getMinPriceForAutoPriceDrop() {
        return minPriceForAutoPriceDrop;
    }

    public void setMinPriceForAutoPriceDrop(Integer minPriceForAutoPriceDrop) {
        this.minPriceForAutoPriceDrop = minPriceForAutoPriceDrop;
    }
}
