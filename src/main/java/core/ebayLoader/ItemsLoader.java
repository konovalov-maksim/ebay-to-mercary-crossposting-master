package core.ebayLoader;

import com.google.gson.Gson;
import core.Condition;
import core.Item;
import core.Logger;
import core.ebayLoader.pojo.EbayItem;
import core.ebayLoader.pojo.EbayResponse;
import core.ebayLoader.pojo.ShippingServiceCost;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemsLoader implements Runnable {

    private final String TOKEN;
    private Logger logger;
    private List<HttpUrl> requestUrls;
    private List<Item> items = new ArrayList<>();
    private LoadingListener loadingListener;

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
    private Path path;

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
        loadingListener.onAllItemsLoaded();
    }

    private void extractItems(String responseBody) {
        try {
            EbayResponse response = new Gson().fromJson(responseBody, EbayResponse.class);
            List<EbayItem> ebayItems = response.getEbayItems();
            for (EbayItem ebayItem : ebayItems) {
                Item item = convertEbayItem(ebayItem);
                items.add(item);
                if (!item.getImagesUrls().isEmpty()) {
                    ImagesLoader imagesLoader = new ImagesLoader(client, item, path);
                    imagesLoader.setLoadingListener(loadingListener);
                    imagesLoader.setLogger(logger);
                    imagesLoader.loadImages();
                    loadingListener.onItemInfoLoaded(item);
                } else {
                    item.setStatus("Item info loading complete. No images found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("Error: unable to parse Ebay response");
        }
    }

    private Item convertEbayItem(EbayItem ebayItem) {
        Item item = new Item();
        item.setId(ebayItem.getItemID());
        item.setTitle(ebayItem.getTitle());
        item.setImagesUrls(ebayItem.getPictureURL());
        Double ebayPrice = ebayItem.getConvertedCurrentPrice().getValue();
        item.setEbayPrice(ebayPrice);
        item.setPrice((int) Math.round(ebayPrice));
        ShippingServiceCost listedShippingServiceCost = ebayItem.getShippingCostSummary().getShippingServiceCost();
        double ebayShippingPrice = listedShippingServiceCost != null ? listedShippingServiceCost.getValue() : 0d;
        item.setEbayShippingPrice(ebayShippingPrice);
        item.setShippingPrice((int) Math.round(ebayShippingPrice));
        String description = Jsoup.parse(ebayItem.getDescription()).text();
        item.setDescription(description);
        Integer conditionId = ebayItem.getConditionID();
        if (conditionId != null) {
            if (conditionId == 1000) item.setCondition(new Condition(1));
            else if (conditionId <= 2750) item.setCondition(new Condition(2));
            else if (conditionId <= 5000) item.setCondition(new Condition(3));
            else if (conditionId <= 6000) item.setCondition(new Condition(4));
            else if (conditionId == 7000) item.setCondition(new Condition(5));
        }
        String ebayUrl = ebayItem.getViewItemURLForNaturalSearch();
        item.setEbayUrl(ebayUrl);
        return item;
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
                .addQueryParameter("includeSelector", "Description,ShippingCosts")
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

    public Path getPath() {
        return path;
    }

    public void setImagesDirPath(Path path) {
        this.path = path;
    }

    public LoadingListener getLoadingListener() {
        return loadingListener;
    }

    public void setLoadingListener(LoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }
}
