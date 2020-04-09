package core.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import core.Item;

import java.util.List;

public class Variables {

    public Variables(Item item, List<String> imagesIds, String zipCode) {
        this.input = new Input(item, imagesIds, zipCode);
    }

    @SerializedName("input")
    @Expose
    private Input input;

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }
}
