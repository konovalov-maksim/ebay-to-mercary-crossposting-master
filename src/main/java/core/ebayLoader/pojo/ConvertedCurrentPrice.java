
package core.ebayLoader.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConvertedCurrentPrice {

    @SerializedName("Value")
    @Expose
    private Double value;
    @SerializedName("CurrencyID")
    @Expose
    private String currencyID;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(String currencyID) {
        this.currencyID = currencyID;
    }
}
