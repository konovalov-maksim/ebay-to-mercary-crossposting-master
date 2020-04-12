package gui;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("ebayToken")
    @Expose
    private String ebayToken;
    @SerializedName("mercariEmail")
    @Expose
    private String mercariEmail;
    @SerializedName("mercariPass")
    @Expose
    private String mercariPass;
    @SerializedName("zipCode")
    @Expose
    private String zipCode;

    public String getEbayToken() {
        return ebayToken;
    }

    public void setEbayToken(String ebayToken) {
        this.ebayToken = ebayToken;
    }

    public String getMercariEmail() {
        return mercariEmail;
    }

    public void setMercariEmail(String mercariEmail) {
        this.mercariEmail = mercariEmail;
    }

    public String getMercariPass() {
        return mercariPass;
    }

    public void setMercariPass(String mercariPass) {
        this.mercariPass = mercariPass;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}