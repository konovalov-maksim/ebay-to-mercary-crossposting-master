package core.ebayLoader.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShippingCostSummary {

    @SerializedName("ShippingServiceCost")
    @Expose
    private ShippingServiceCost shippingServiceCost;

    @SerializedName("ShippingType")
    @Expose
    private String shippingType;

    @SerializedName("ListedShippingServiceCost")
    @Expose
    private ListedShippingServiceCost listedShippingServiceCost;

    public ShippingServiceCost getShippingServiceCost() {
        return shippingServiceCost;
    }

    public void setShippingServiceCost(ShippingServiceCost shippingServiceCost) {
        this.shippingServiceCost = shippingServiceCost;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    public ListedShippingServiceCost getListedShippingServiceCost() {
        return listedShippingServiceCost;
    }

    public void setListedShippingServiceCost(ListedShippingServiceCost listedShippingServiceCost) {
        this.listedShippingServiceCost = listedShippingServiceCost;
    }
}
