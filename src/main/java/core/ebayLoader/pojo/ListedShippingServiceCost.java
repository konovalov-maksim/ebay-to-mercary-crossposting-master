package core.ebayLoader.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListedShippingServiceCost {

    @SerializedName("Value")
    @Expose
    private Double value;

    @SerializedName("CurrencyID")
    @Expose
    private String currencyId;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
}
