package gui;

import com.google.gson.*;
import core.Category;
import okhttp3.Cookie;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {

    private static DataManager instance;

    private DataManager() {}

    public static synchronized DataManager getInstance() {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public List<Category> getCategories() throws IOException, URISyntaxException {
        URI uri = getClass().getResource("/json/categories.json").toURI();
        String json = Files.lines(Paths.get(uri)).collect(Collectors.joining());
        Category[] categories = new Gson().fromJson(json, Category[].class);
        return Arrays.asList(categories);
    }
    Path settingsPath = Paths.get("").toAbsolutePath().resolve("settings.json");

    public Settings loadSettings() throws IOException {
        String json = Files.lines(settingsPath).collect(Collectors.joining());
        return new Gson().fromJson(json, Settings.class);
    }

    public void saveSettings(Settings settings) throws IOException {
        String json = new Gson().toJson(settings);
        Files.write(settingsPath,
                Collections.singletonList(json),
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
    }

    Path cookiesPath = Paths.get("").toAbsolutePath().resolve("cookies.json");

    public List<Cookie> loadCookies() throws IOException {
        List<Cookie> cookies = new ArrayList<>();
        String json = Files.lines(cookiesPath, StandardCharsets.UTF_8).collect(Collectors.joining());
        System.out.println(json);
        JsonArray jsonCookies = new Gson().fromJson(json, JsonArray.class);
        for (JsonElement jsonCookieEl : jsonCookies) {
            try {
                JsonObject jsonCookie = jsonCookieEl.getAsJsonObject();
                Cookie.Builder builder = new Cookie.Builder()
                        .name(jsonCookie.get("name").getAsString())
                        .value(jsonCookie.get("value").getAsString())
                        .domain(jsonCookie.get("domain").getAsString())
                        .expiresAt(jsonCookie.get("expiresAt").getAsLong());
                if (jsonCookie.get("secure") != null)
                    if (jsonCookie.get("secure").getAsBoolean()) builder.secure();
                if (jsonCookie.get("httpOnly") != null)
                    if (jsonCookie.get("httpOnly").getAsBoolean()) builder.httpOnly();
                Cookie cookie = builder.build();

                if (cookie.expiresAt() > System.currentTimeMillis())
                    cookies.add(cookie);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cookies;
    }

    public synchronized void saveCookies(List<Cookie> cookies) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(cookiesPath,
                gson.toJson(cookies).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
