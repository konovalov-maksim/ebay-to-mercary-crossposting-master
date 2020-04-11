package core;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataReader {

    public List<Category> getCategories() throws IOException, URISyntaxException {
        URI uri = getClass().getResource("/json/categories.json").toURI();
        String json = Files.lines(Paths.get(uri)).collect(Collectors.joining());
        Category[] categories = new Gson().fromJson(json, Category[].class);
        return Arrays.asList(categories);
    }
}
