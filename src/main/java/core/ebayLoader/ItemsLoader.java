package core.ebayLoader;

import com.google.gson.Gson;
import core.Item;
import core.ebayLoader.pojo.EbayItem;
import core.ebayLoader.pojo.EbayResponse;
import core.mercariUploader.Logger;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemsLoader implements Runnable {

    private final String TOKEN;
    private Logger logger;
    private boolean isRunning = false;
    private List<HttpUrl> requestUrls;
    private List<Item> items = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();


    public ItemsLoader(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public void setItemsIds(List<String> itemsIds) {
        requestUrls = getRequestUrls(itemsIds);
    }

    @Override
    public void run() {
        loadItems();
    }

    private void loadItems() {
        for (HttpUrl url : requestUrls) {
            try {
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();
                String responseBody = response.peekBody(Long.MAX_VALUE).string();
                System.out.println(responseBody);
                extractItems(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
                log("Items loading error");
            }
        }
    }

    private void extractItems(String responseBody) {
        try {
            EbayResponse response = new Gson().fromJson(responseBody, EbayResponse.class);
            List<EbayItem> ebayItems = response.getEbayItems();
            for (EbayItem ebayItem : ebayItems) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            log("Error: unable to parse Ebay response");
        }
    }


    private List<HttpUrl> getRequestUrls(List<String> itemsIds) {
        List<HttpUrl> requestUrls = new ArrayList<>();
        final int LIMIT = 20;
        int start = 0;
        int end = Math.min(itemsIds.size(), start + LIMIT);
        do {
            requestUrls.add(getRequestUrl(itemsIds.subList(start, end)));
            start += LIMIT;
            end = Math.min(itemsIds.size(), end + LIMIT);
        } while (start < itemsIds.size());
        return requestUrls;
    }

    private HttpUrl getRequestUrl(List<String> itemsIds) {
        String itemsIdsCommaSep = String.join(",", itemsIds);
        return HttpUrl.parse("https://open.api.ebay.com/shopping").newBuilder()
                .addQueryParameter("callname", "GetMultipleItems")
                .addQueryParameter("responseencoding", "JSON")
                .addQueryParameter("appid", TOKEN)
                .addQueryParameter("siteid", "0")
                .addQueryParameter("version", "967")
                .addQueryParameter("ItemID", itemsIdsCommaSep)
                .build();
    }

    private void log(String message) {
        if (logger != null) logger.log(message);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
