package core;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MercariUploader {

    private List<Item> items;
    private List<Cookie> cookies;
    private OkHttpClient client;
    private int delaySec = 2;

    public MercariUploader() {
        initClient();
    }

    public void uploadItems() {
        if (items == null) throw new IllegalStateException("Items not specified!");
        for (Item item : items) {
            try {
                uploadImages(item.getImages());
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private String uploadImages(List<File> images) throws IOException {
        Request request = new Request.Builder()
                .build();
        Response response = client.newCall(request).execute();
        return "";
    }

    public boolean isLoggedIn() {
        return false;
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
    }
}
