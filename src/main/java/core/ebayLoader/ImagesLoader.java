package core.ebayLoader;

import core.Item;
import core.Logger;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ImagesLoader {

    private final Path path;
    private final Item item;

    private OkHttpClient client;
    private Logger logger;
    private LoadingListener loadingListener;

    private int loadingErrors;


    public ImagesLoader(OkHttpClient client, Item item, Path path) {
        this.client = client;
        this.item = item;
        this.path = path;
        File imagesDir = path.toFile();
        if (!imagesDir.exists()) imagesDir.mkdir();
    }

    public void loadImages() {
        item.setStatus("Images downloading");
        for (int i = 0; i < item.getImagesUrls().size(); i++) {
            String fileName = item.getId() + "_" + (i + 1) + ".jpg";
            Request request = new Request.Builder()
                    .url(item.getImagesUrls().get(i))
                    .header("file-name", fileName)
                    .build();
            client.newCall(request).enqueue(callback);
        }
    }

    private Callback callback = new Callback() {
        @Override
        public synchronized void onFailure(@NotNull Call call, @NotNull IOException e) {
            loadingErrors++;
            log("Image downloading error for item " + item);
            checkLoadingStatus();
        }

        @Override
        public synchronized void onResponse(@NotNull Call call, @NotNull Response response) {
            String fileName = response.request().header("file-name");
            Path imagePath = path.resolve(fileName);
            try (InputStream inputStream = response.body().byteStream()) {
                Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
                item.getImages().add(imagePath.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkLoadingStatus();
        }
    };

    private void checkLoadingStatus() {
        if (item.getImages().size() + loadingErrors == item.getImagesUrls().size()) {
            if (loadingErrors == 0) item.setStatus("Images downloading complete");
            else  item.setStatus("Images downloading complete with " + loadingErrors + " errors");
            if (loadingListener != null)
                loadingListener.onItemImagesLoaded(item);
        }
    }


    private void log(String message) {
        if (logger != null) logger.log(message);
    }

    public Path getPath() {
        return path;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public LoadingListener getLoadingListener() {
        return loadingListener;
    }

    public void setLoadingListener(LoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }
}
