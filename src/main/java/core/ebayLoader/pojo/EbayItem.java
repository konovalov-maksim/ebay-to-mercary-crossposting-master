
package core.ebayLoader.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EbayItem {

    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("EndTime")
    @Expose
    private String endTime;
    @SerializedName("ViewItemURLForNaturalSearch")
    @Expose
    private String viewItemURLForNaturalSearch;
    @SerializedName("ListingType")
    @Expose
    private String listingType;
    @SerializedName("Location")
    @Expose
    private String location;
    @SerializedName("PictureURL")
    @Expose
    private List<String> pictureURL = null;
    @SerializedName("PrimaryCategoryID")
    @Expose
    private String primaryCategoryID;
    @SerializedName("PrimaryCategoryName")
    @Expose
    private String primaryCategoryName;
    @SerializedName("BidCount")
    @Expose
    private Integer bidCount;
    @SerializedName("ConvertedCurrentPrice")
    @Expose
    private ConvertedCurrentPrice convertedCurrentPrice;
    @SerializedName("ListingStatus")
    @Expose
    private String listingStatus;
    @SerializedName("TimeLeft")
    @Expose
    private String timeLeft;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("HitCount")
    @Expose
    private Integer hitCount;
    @SerializedName("Country")
    @Expose
    private String country;
    @SerializedName("AutoPay")
    @Expose
    private Boolean autoPay;
    @SerializedName("ConditionID")
    @Expose
    private Integer conditionID;
    @SerializedName("ConditionDisplayName")
    @Expose
    private String conditionDisplayName;
    @SerializedName("QuantityAvailableHint")
    @Expose
    private String quantityAvailableHint;
    @SerializedName("QuantityThreshold")
    @Expose
    private Integer quantityThreshold;
    @SerializedName("GalleryURL")
    @Expose
    private String galleryURL;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getViewItemURLForNaturalSearch() {
        return viewItemURLForNaturalSearch;
    }

    public void setViewItemURLForNaturalSearch(String viewItemURLForNaturalSearch) {
        this.viewItemURLForNaturalSearch = viewItemURLForNaturalSearch;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(List<String> pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getPrimaryCategoryID() {
        return primaryCategoryID;
    }

    public void setPrimaryCategoryID(String primaryCategoryID) {
        this.primaryCategoryID = primaryCategoryID;
    }

    public String getPrimaryCategoryName() {
        return primaryCategoryName;
    }

    public void setPrimaryCategoryName(String primaryCategoryName) {
        this.primaryCategoryName = primaryCategoryName;
    }

    public Integer getBidCount() {
        return bidCount;
    }

    public void setBidCount(Integer bidCount) {
        this.bidCount = bidCount;
    }

    public ConvertedCurrentPrice getConvertedCurrentPrice() {
        return convertedCurrentPrice;
    }

    public void setConvertedCurrentPrice(ConvertedCurrentPrice convertedCurrentPrice) {
        this.convertedCurrentPrice = convertedCurrentPrice;
    }

    public String getListingStatus() {
        return listingStatus;
    }

    public void setListingStatus(String listingStatus) {
        this.listingStatus = listingStatus;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }

    public Integer getConditionID() {
        return conditionID;
    }

    public void setConditionID(Integer conditionID) {
        this.conditionID = conditionID;
    }

    public String getConditionDisplayName() {
        return conditionDisplayName;
    }

    public void setConditionDisplayName(String conditionDisplayName) {
        this.conditionDisplayName = conditionDisplayName;
    }

    public String getQuantityAvailableHint() {
        return quantityAvailableHint;
    }

    public void setQuantityAvailableHint(String quantityAvailableHint) {
        this.quantityAvailableHint = quantityAvailableHint;
    }

    public Integer getQuantityThreshold() {
        return quantityThreshold;
    }

    public void setQuantityThreshold(Integer quantityThreshold) {
        this.quantityThreshold = quantityThreshold;
    }

    public String getGalleryURL() {
        return galleryURL;
    }

    public void setGalleryURL(String galleryURL) {
        this.galleryURL = galleryURL;
    }
}
