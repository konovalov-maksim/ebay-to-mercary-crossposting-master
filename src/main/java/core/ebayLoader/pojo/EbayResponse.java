
package core.ebayLoader.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EbayResponse {

    @SerializedName("Timestamp")
    @Expose
    private String timestamp;
    @SerializedName("Ack")
    @Expose
    private String ack;
    @SerializedName("Build")
    @Expose
    private String build;
    @SerializedName("Version")
    @Expose
    private String version;
    @SerializedName("Item")
    @Expose
    private List<EbayItem> ebayItems = null;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<EbayItem> getEbayItems() {
        return ebayItems;
    }

    public void setEbayItems(List<EbayItem> ebayItems) {
        this.ebayItems = ebayItems;
    }
}
