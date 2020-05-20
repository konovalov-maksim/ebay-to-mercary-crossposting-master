package core.ebayLoader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.Logger;
import core.ebayLoader.pojo.SellerItem;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SellerItemsSeeker implements Runnable {

    private final String TOKEN;
    private final String sellerName;
    private final SellerItemsSeekingListener sellerItemsSeekingListener;
    private Logger logger;

    private final List<SellerItem> sellerItems = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    public SellerItemsSeeker(
            String TOKEN,
            String sellerName,
            SellerItemsSeekingListener sellerItemsSeekingListener) {
        this.TOKEN = TOKEN;
        this.sellerName = sellerName;
        this.sellerItemsSeekingListener = sellerItemsSeekingListener;
    }

    public void run() {
        log("Seeking for seller's items...");
        loadSellerItems(1);
    }

    private void loadSellerItems(int pageNum) {
        try {
            Request request = new Request.Builder().url(getRequestUrl(pageNum)).build();
            Response response = client.newCall(request).execute();
            String responseBody = response.peekBody(Long.MAX_VALUE).string();
            JsonObject rootJson = new Gson().fromJson(responseBody, JsonObject.class);
            extractSellerItems(rootJson);
            int totalPages = extractTotalPages(rootJson);
            if (pageNum < totalPages) {
                loadSellerItems(pageNum + 1);
            } else {
                sellerItemsSeekingListener.onSearchingComplete(sellerItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("Failed to load seller's items");
        }
    }

    private void extractSellerItems(JsonObject rootJson) {
        JsonArray items = rootJson
                .get("findItemsAdvancedResponse").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("searchResult").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("item").getAsJsonArray();
        for (JsonElement itemEl : items) {
            JsonObject item = itemEl.getAsJsonObject();
            String itemId = item.get("itemId").getAsJsonArray().get(0).getAsString();
            String categoryName = item
                    .get("primaryCategory").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("categoryName").getAsJsonArray()
                    .get(0).getAsString();
            sellerItems.add(new SellerItem(itemId, categoryName));
        }
    }

    private int extractTotalPages(JsonObject rootJson) {
        JsonArray paginationOutput = rootJson
                .get("findItemsAdvancedResponse").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("paginationOutput").getAsJsonArray();
        String totalPagesStr = paginationOutput
                .get(0).getAsJsonObject()
                .get("totalPages").getAsString();
        return Integer.parseInt(totalPagesStr);
    }

    private HttpUrl getRequestUrl(int pageNum) {
        return HttpUrl.parse("https://svcs.ebay.com/services/search/FindingService/v1").newBuilder()
                .addQueryParameter("operation-name", "findItemsAdvanced")
                .addQueryParameter("service-version", "1.0.0")
                .addQueryParameter("security-appname", TOKEN)
                .addQueryParameter("response-data-format", "JSON")
                .addQueryParameter("rest-payload", "true")
                .addQueryParameter("paginationInput.entriesPerPage", "100")
                .addQueryParameter("paginationInput.pageNumber", String.valueOf(pageNum))
                .addQueryParameter("itemFilter(0).name", "Seller")
                .addQueryParameter("itemFilter(0).value(0)", sellerName)
                .addQueryParameter("siteid", "0")
                .build();
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private void log(String message) {
        if (logger != null) logger.log(message);
    }

    public interface SellerItemsSeekingListener{
        void onSearchingComplete(List<SellerItem> sellerItems);
    }
}
