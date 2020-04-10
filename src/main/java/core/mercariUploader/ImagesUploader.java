package core.mercariUploader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.Logger;
import okhttp3.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesUploader {

    private OkHttpClient client;
    private Headers headers;
    private final String BOUNDARY = "-----------------------------422147847219820842572672630635";
    private final String OPERATIONS = "{\"operationName\":\"uploadTempListingPhotos\",\"variables\":" +
            "{\"input\":{\"photos\":[null,null]}},\"query\":\"mutation uploadTempListingPhotos(" +
            "$input: UploadTempListingPhotosInput!) {\\n  uploadTempListingPhotos(input: $input) " +
            "{\\n    uploadIds\\n    __typename\\n  }\\n}\\n\"}";

    private Logger logger;

    ImagesUploader(OkHttpClient client) {
        this.client = client;
        headers = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0")
                .add("Accept", "*/*")
                .add("Host", "www.mercari.com")
                .add("Referer", "https://www.mercari.com/sell")
                .add("Origin", "https://www.mercari.com/")
                .add("TE", "Trailers")
                .add("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
                .build();
    }

    List<String> uploadImages(List<File> images) {
        try {
            String map = getMap(images.size());
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder(BOUNDARY)
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("operations", OPERATIONS)
                    .addFormDataPart("map", map);
            for (int i = 0; i < images.size(); i++) {
                requestBodyBuilder.addFormDataPart(
                        String.valueOf(i + 1),
                        "blob",
                        RequestBody.create(images.get(i), MediaType.parse("image/jpeg"))
                );
            }
            RequestBody requestBody = requestBodyBuilder.build();
            Request request = new Request.Builder()
                    .url("https://www.mercari.com/v1/api")
                    .post(requestBody)
                    .headers(headers)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.peekBody(Long.MAX_VALUE).string();
            return extractIdsFromResponse(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> extractIdsFromResponse(String responseBody) {
        List<String> imagesIds = new ArrayList<>();
        JsonObject response = new Gson().fromJson(responseBody, JsonObject.class);
        JsonArray uploadIds = response
                .get("data").getAsJsonObject()
                .get("uploadTempListingPhotos").getAsJsonObject()
                .get("uploadIds").getAsJsonArray();
        for (JsonElement uploadId : uploadIds)
            imagesIds.add(uploadId.getAsString());
        return imagesIds;
    }

    private String getMap(int size) {
        //example: {"1":["variables.input.photos.0"],"2":["variables.input.photos.1"]}
        StringBuilder map = new StringBuilder("{");
        for (int i = 0; i < size; i++) {
            map.append("\"").append(i + 1).append("\":[\"variables.input.photos.").append(i).append("\"],");
        }
        return map.toString().replaceAll(",$", "}");
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
