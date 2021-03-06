package gui;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("ebayToken")
    @Expose
    private String ebayToken;
    @SerializedName("zipCode")
    @Expose
    private String zipCode;
    @SerializedName("uploadingDelay")
    @Expose
    private Long uploadingDelay;

    public String getEbayToken() {
        return ebayToken;
    }

    public void setEbayToken(String ebayToken) {
        this.ebayToken = ebayToken;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Long getUploadingDelay() {
        return uploadingDelay;
    }

    public void setUploadingDelay(Long uploadingDelay) {
        this.uploadingDelay = uploadingDelay;
    }
}