package core.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import core.Item;

public class Input {

    public Input(Item item, List<String> imageIds) {

    }

    private Input(List<String> photoIds,
                 String name,
                 Integer price,
                 String description,
                 Integer categoryId,
                 Integer conditionId,
                 String zipCode,
                 List<String> tags,
                 Integer salesFee,
                 Integer shippingPayerId,
                 List<Integer> shippingClassIds,
                 Boolean isAutoPriceDrop,
                 Integer minPriceForAutoPriceDrop) {
        this.photoIds = photoIds;
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.conditionId = conditionId;
        this.zipCode = zipCode;
        this.tags = tags;
        this.salesFee = salesFee;
        this.shippingPayerId = shippingPayerId;
        this.shippingClassIds = shippingClassIds;
        this.isAutoPriceDrop = isAutoPriceDrop;
        this.minPriceForAutoPriceDrop = minPriceForAutoPriceDrop;
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
    private List<Integer> shippingClassIds = null;
    @SerializedName("isAutoPriceDrop")
    @Expose
    private Boolean isAutoPriceDrop;
    @SerializedName("minPriceForAutoPriceDrop")
    @Expose
    private Integer minPriceForAutoPriceDrop;

}
