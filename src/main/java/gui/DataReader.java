package gui;

import com.google.gson.Gson;
import core.Category;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DataReader {

    Path settingsPath = Paths.get("").toAbsolutePath().resolve("settings.json");

    public List<Category> getCategories() throws IOException, URISyntaxException {
        URI uri = getClass().getResource("/json/categories.json").toURI();
        String json = Files.lines(Paths.get(uri)).collect(Collectors.joining());
        Category[] categories = new Gson().fromJson(json, Category[].class);
        return Arrays.asList(categories);
    }

    public Settings loadSettings() throws IOException {
        String json = Files.lines(settingsPath).collect(Collectors.joining());
        return new Gson().fromJson(json, Settings.class);
    }

    public void saveSettings(Settings settings) throws IOException, URISyntaxException {
        String json = new Gson().toJson(settings);
        Files.write(settingsPath,
                Collections.singletonList(json),
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
    }
}
