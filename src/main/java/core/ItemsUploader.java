package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.pojo.UploadItemRequestBody;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemsUploader implements Runnable{

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private List<Item> items;
    private List<Cookie> cookies;
    private OkHttpClient client;
    private Headers headers;
    private int delaySec = 2;
    private Logger logger;
    private Boolean isLoggedIn;
    private ImagesUploader imagesUploader;

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
            }
            RequestBody requestBody = getRequestBody(item, imagesIds);


        }
    }

    private RequestBody getRequestBody(Item item, List<String> imagesIds) {
        UploadItemRequestBody uploadItemRequestBody = new UploadItemRequestBody(item, imagesIds);
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
        try {
            Response response = client.newCall(request).execute();
            isLoggedIn = response.request().url().toString().equals("https://www.mercari.com/sell/");
        } catch (IOException e) {
            e.printStackTrace();
            isLoggedIn = false;
        }
        return isLoggedIn;
    }

    private void onItemUploaded() {

    }

    private void onAllItemsUploaded() {

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
        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        imagesUploader = new ImagesUploader(client);
        headers = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0")
                .add("Accept", "*/*")
                .add("Host", "www.mercari.com")
                .add("content-type", "application/json")
                .add("Referer", "https://www.mercari.com/")
                .build();
    }

    private void log(String message) {
        if (logger != null) logger.log(message);
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

}
