package core.mercariUploader.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import core.Item;

import java.util.List;

public class UploadItemRequestBody {

    public UploadItemRequestBody(Item item, List<String> imagesIds, String zipCode) {
        this.variables = new Variables(item, imagesIds, zipCode);
        this.operationName = "createListing";
        this.query = "mutation createListing($input: CreateListingInput!) {\n  createListing(input: $input) " +
                "{\n    id\n    seller {\n      id\n      numSellItems\n      __typename\n    }" +
                "\n    __typename\n  }\n}\n";
    }

    @SerializedName("operationName")
    @Expose
    private String operationName;
    @SerializedName("variables")
    @Expose
    private Variables variables;
    @SerializedName("query")
    @Expose
    private String query;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
