package core.mercariUploader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import core.Item;
import core.Logger;
import core.mercariUploader.pojo.UploadItemRequestBody;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemsUploader implements Runnable{

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private List<Item> items;
    private List<Cookie> cookies;
    private OkHttpClient client;
    private Headers headers;
    private Logger logger;
    private UploadingListener uploadingListener;
    private ImagesUploader imagesUploader;
    private String zipCode;

    public ItemsUploader() {
        initClient();
    }

    @Override
    public void run() {
        uploadItems();
    }

    private void uploadItems() {
        if (items == null) throw new IllegalStateException("Items not specified!");
        for (Item item : items) {
            List<String> imagesIds = imagesUploader.uploadImages(item.getImages());
            if (imagesIds.isEmpty()) {
                log("No images uploaded for item " + item);
                item.setStatus("Uploading error: no uploaded images");
                continue;
            }
            RequestBody requestBody = getRequestBody(item, imagesIds);
            Request request = new Request.Builder()
                    .url("https://www.mercari.com/v1/api")
                    .post(requestBody)
                    .headers(headers)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.peekBody(Long.MAX_VALUE).string();
                System.out.println(responseBody);
                extractResult(responseBody, item);
                uploadingListener.onItemUploaded(item);
            } catch (IOException e) {
                e.printStackTrace();
                log(item + " - uploading failed: " + e.getMessage());
                item.setStatus("Uploading error");
            }
        }
        uploadingListener.onAllItemsUploaded();
    }

    private void extractResult(String responseBody, Item item) {
        JsonObject root = new Gson().fromJson(responseBody, JsonObject.class);
        try {
            if (!root.has("errors")) {
                String id = root
                        .get("data").getAsJsonObject()
                        .get("createListing").getAsJsonObject()
                        .get("id").getAsString();
                item.setStatus("Successfully uploaded. ID " + id);
                item.setMercariUrl("https://www.mercari.com/us/item/" + id + "/");
                item.setUploaded(true);
            } else {
                String message = root
                        .get("errors").getAsJsonArray()
                        .get(0).getAsJsonObject()
                        .get("message").getAsString();
                item.setStatus("Uploading error");
                item.setUploaded(false);
                log("Item " + item + " - uploading error: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            item.setStatus("Uploading error");
            item.setUploaded(false);
            log("Item " + item + " - uploading error: " + e.getMessage());
        }
    }

    private RequestBody getRequestBody(Item item, List<String> imagesIds) {
        UploadItemRequestBody uploadItemRequestBody = new UploadItemRequestBody(item, imagesIds, zipCode);
        Gson gson = new GsonBuilder().create();
        String requestBodyJson = gson.toJson(uploadItemRequestBody);
        System.out.println(requestBodyJson);
        return RequestBody.create(requestBodyJson, JSON);
    }


    public boolean isLoggedIn() {
        Request request = new Request.Builder()
                .url("https://www.mercari.com/sell/")
                .headers(headers)
                .build();
        boolean isLoggedIn;
        try {
            Response response = client.newCall(request).execute();
            isLoggedIn = response.request().url().toString().equals("https://www.mercari.com/sell/");
        } catch (IOException e) {
            e.printStackTrace();
            isLoggedIn = false;
        }
        return isLoggedIn;
    }

    private void initClient() {
        CookieJar cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                //TODO implement cookies saving
            }

            @NotNull
            @Override
            public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                return cookies;
            }
        };
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.41.19.53", 3128));
        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
//                .proxy(proxy)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        imagesUploader = new ImagesUploader(client);
        headers = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0")
                .add("Accept", "*/*")
                .add("Host", "www.mercari.com")
                .add("Content-type", "application/json")
                .add("Referer", "https://www.mercari.com/sell/")
                .add("Origin", "https://www.mercari.com")
                .build();
    }

    public interface UploadingListener {
        void onAllItemsUploaded();
        void onItemUploaded(Item item);
    }

    private void log(String message) {
        if (logger != null) logger.log(message);
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
        imagesUploader.setLogger(logger);
    }

    public UploadingListener getUploadingListener() {
        return uploadingListener;
    }

    public void setUploadingListener(UploadingListener uploadingListener) {
        this.uploadingListener = uploadingListener;
    }

}
