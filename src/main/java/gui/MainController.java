package gui;

import core.*;
import core.ebayLoader.ItemsLoader;
import core.ebayLoader.LoadingListener;
import core.mercariUploader.ItemsUploader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.Cookie;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController implements Initializable, Logger, ItemsUploader.UploadingListener, LoadingListener {

    @FXML private TabPane paramsTp;

    @FXML private HBox imagesHb;
    @FXML private TextField titleTf;
    @FXML private TextField priceTf;
    @FXML private TextField tag0Tf;
    @FXML private TextField tag1Tf;
    @FXML private TextField tag2Tf;
    @FXML private TextArea descriptionTa;
    @FXML private ComboBox<Condition> conditionCb;
    @FXML private TreeView<Category> categoriesTv;


    @FXML private TableView<Item> table;
    @FXML private TextArea consoleTa;
    @FXML private TableColumn<Item, String> idCol;
    @FXML private TableColumn<Item, String> titleCol;
    @FXML private TableColumn<Item, String> descriptionCol;
    @FXML private TableColumn<Item, String> categoryCol;
    @FXML private TableColumn<Item, String> conditionCol;
    @FXML private TableColumn<Item, Double> ebayPriceCol;
    @FXML private TableColumn<Item, Integer> priceCol;
    @FXML private TableColumn<Item, String> tagsCol;
    @FXML private TableColumn<Item, Integer> imagesNumCol;
    @FXML private TableColumn<Item, String> statusCol;

    private DataReader dataReader = new DataReader();
    private Map<Integer, TreeItem<Category>> categoryItems = new HashMap<>();

    private ObservableList<Item> items = FXCollections.observableArrayList();

    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("conditionName"));
        ebayPriceCol.setCellValueFactory(new PropertyValueFactory<>("ebayPriceCol"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tagsString"));
        imagesNumCol.setCellValueFactory(new PropertyValueFactory<>("imagesNum"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        items.addAll(getDebugItems());
        table.setItems(items);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showItemParams(table.getSelectionModel().getSelectedItem());
        });

        conditionCb.setItems(FXCollections.observableArrayList(Condition.getAllConditions()));
        initCategoriesTv();
    }

    @FXML
    private void loadItems() {
        try {
            String token = "MaksimKo-agregato-PRD-5388a6d5f-1b894369";
            Path imagesDirPath = Paths.get("C:\\Users\\Maksim\\Documents\\IDEAProjects\\ebayToMercaryCopier\\images");
            ItemsLoader itemsLoader = new ItemsLoader(token);
            itemsLoader.setImagesDirPath(imagesDirPath);
            itemsLoader.setLogger(this);
            itemsLoader.setLoadingListener(this);
            List<String> itemsIds = Files.readAllLines(Paths.get("C:\\Users\\Maksim\\Documents\\IDEAProjects\\ebayToMercaryCopier\\items.txt"));
            itemsLoader.setItemsIds(itemsIds);
            Thread uploaderThread = new Thread(itemsLoader);
            uploaderThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void uploadItems() {
        ItemsUploader uploader = new ItemsUploader();
        uploader.setItems(items);
        uploader.setLogger(this);
        uploader.setUploadingListener(this);
        uploader.setCookies(getDebugCookies());
        uploader.setZipCode("55309");
        //uploader.isLoggedIn();
        Thread uploaderThread = new Thread(uploader);
        uploaderThread.start();
    }

    private void showItemParams(Item item) {
        paramsTp.getSelectionModel().select(1);
        titleTf.setText(item.getTitle());
        priceTf.setText(String.valueOf(item.getPrice()));
        descriptionTa.setText(item.getDescription());
        imagesHb.getChildren().clear();
        conditionCb.setValue(item.getCondition());

        TreeItem<Category> categoryItem = categoryItems.get(item.getCategoryId());
        categoriesTv.getSelectionModel().clearSelection();
        if (categoryItem != null) {
            categoriesTv.getSelectionModel().select(categoryItem);
            categoryItem.getParent().setExpanded(true);
        }

        tag0Tf.setText(item.getTag0());
        tag1Tf.setText(item.getTag1());
        tag2Tf.setText(item.getTag2());

        for (File file : item.getImages()) {
            try (InputStream imageIs = new FileInputStream(file) ) {
                Image image = new Image(imageIs);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                imagesHb.getChildren().add(imageView);
            } catch (Exception e) {
                e.printStackTrace();
                log(item + " - unable to open item image");
                item.getImages().remove(file);
            }
        }
    }

    @FXML
    private void applyParams() {
        Item selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        selectedItem.setCondition(conditionCb.getValue());
        selectedItem.setTitle(titleTf.getText());
        selectedItem.setDescription(descriptionTa.getText());
        TreeItem<Category> categoryItem = categoriesTv.getSelectionModel().getSelectedItem();
        selectedItem.setTag0(tag0Tf.getText());
        selectedItem.setTag1(tag1Tf.getText());
        selectedItem.setTag2(tag2Tf.getText());
        if (categoryItem != null) selectedItem.setCategory(categoryItem.getValue());
        try {
            selectedItem.setPrice(Integer.valueOf(priceTf.getText()));
        } catch (NumberFormatException e) {
            showErrorAlert("Incorrect Price");
        }
        table.refresh();
    }

    @FXML
    private void openLoginStage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mercariLogin.fxml"), ResourceBundle.getBundle("bundles.strings"));
            Parent root = loader.load();
            Stage stage = new Stage();
//            stage.setResizable(false);
            stage.setTitle("Mercari login");
            stage.getIcons().add(new Image("/images/icon64.ico"));
            stage.setScene(new Scene(root));
            stage.getScene().getStylesheets().add("/style.css");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(String message) {
        String curTime = new SimpleDateFormat("MM/dd/yyyy  HH:mm:ss:SSS").format(new Date());
        Platform.runLater(() -> {
            consoleTa.setText(consoleTa.getText() + curTime + "     " + message + "\n");
            consoleTa.positionCaret(consoleTa.getLength());
        });
    }

    @Override
    public void clearLog() {
        Platform.runLater(() -> {
            consoleTa.setText("");
        });
    }

    private List<Item> getDebugItems() {
        List<Item> items = new ArrayList<>();
        Item item1 = new Item("123345");
        item1.setTitle("Men's belt");
        item1.setDescription("Amazing men's belt");
        item1.setPrice(15);
        item1.setCondition(new Condition(1));
        item1.setTag0("Belts");
        item1.setImages(getDebugImages1());

        Item item2 = new Item("2345");
        item2.setTitle("Women's belt");
        item2.setDescription("Amazing women's belt");
        item2.setPrice(10);
        item2.setTag1("accessory");
        item2.setCondition(new Condition(3));
        item2.setImages(getDebugImages2());
        items.add(item1);
        items.add(item2);
        return items;
    }

    private List<File> getDebugImages1() {
        List<File> images = new ArrayList<>();
        images.add(new File("C:\\Users\\Maksim\\Documents\\IDEAProjects\\ebayToMercaryCopier\\images\\1.jpg"));
        return images;
    }

    private List<File> getDebugImages2() {
        List<File> images = new ArrayList<>();
        images.add(new File("C:\\Users\\Maksim\\Documents\\IDEAProjects\\ebayToMercaryCopier\\images\\2.jpg"));
        images.add(new File("C:\\Users\\Maksim\\Documents\\IDEAProjects\\ebayToMercaryCopier\\images\\3.jpg"));
        images.add(new File("C:\\Users\\Maksim\\Documents\\IDEAProjects\\ebayToMercaryCopier\\images\\4.jpg"));
        return images;
    }

    private List<Cookie> getDebugCookies() {
        List<Cookie> cookies = new ArrayList<>();
        Cookie cookie1 = new Cookie.Builder()
                .name("_mwus.sig")
                .value("FrmgBD_wsb7B3kH2OgAK1vTXgt0")
                .domain("mercari.com")
                .build();
        Cookie cookie2 = new Cookie.Builder()
                .name("_MWUS")
                .value("8j2v86h8j790dmsidi6j8k9q7d")
                .domain("mercari.com")
                .build();
        Cookie cookie3 = new Cookie.Builder()
                .name("G_ENABLED_IDPS")
                .value("google")
                .domain("mercari.com")
                .build();
        Cookie cookie4 = new Cookie.Builder()
                .name("_mwus")
                .value("eyJhY2Nlc3NUb2tlbiI6ImV5SmhiR2NpT2lKSVV6STFOaUlzSW5SNWNDSTZJa3BYVkNKOS5leUppSWpvaVpHTTJZVFUxTkdZek9XVTBZalE0TWpFMFpEZ3pORFF4TVRVNVpXSTVObUkxTURreE56WXpZemRoWkdWa05HTTVOV013WldJek9EUTRPV1ZqWkRNeU1UUXhPRGt6TURRNU5EazJOVEJpTmpFNU5EQXhaV1F5T0dZNFl6azVNMkZpTUdOaE1UUmhabU5rTm1FeVpUazNNR016T0dZMVpqUXpZelE1TlRnNFl6aGxOek5pT1dVd09XSmxPREJtWVROa05EVmlOV0ZpWXpCa05XVTRPRFF3TWpoaU1qbGpaR1ZpTldFeE1URmhOVFprT1RBNE9ETXpOVE01TkRka1l6TXpJaXdpWkdGMFlTSTZleUoxZFdsa0lqb2laMmc2ZHpwbVlqTXpNV0prTWkxbE1XVXdMVFJqWmpndE9EUTVZaTB5T0RabVlXWXhZakpqWTJRaUxDSjFjMlZ5U1dRaU9qVXpOamN6TkRJeU9Td2lZV05qWlhOelZHOXJaVzRpT2lJeU9tUTNZek01WTJNM1pUUTVZV1UyTnpVek5HSmlNR1V3TlRNM1lUWTFNR0ppTXpOaE9EVTNNVFprWkRBMVpUbG1aV1ZrWldZNFpURmxNMlZqT0dZMlpqSWlmU3dpWlhod0lqb3hOVGcyT0RjeE5EZzFMQ0pwWVhRaU9qRTFPRFl5TmpZMk9EVjkuV1JXUFQzTnJ0cVFJZFFnZW1ucVNzVFJIaGkzOTRRc0RhMl9rLXZYNW5jbyIsInJlZnJlc2hUb2tlbiI6ImV5SmhiR2NpT2lKSVV6STFOaUlzSW5SNWNDSTZJa3BYVkNKOS5leUppSWpvaVpHTTJZVFUxTkdZek9XVTBZalE0TWpFMFpEZ3pORFF4TVRVNVpXSTVObUkxTURreE56WXpZemRoWkdWa05HTTVOV013WldJek9EUTRPV1ZqWkRNeU1UUXhPRGt6TURRNU5EazJOVEJpTmpFNU5EQXhaV1F5T0dZNFl6azVNMkZpTUdOaE1UUmhabU5rTm1FeVpUazNNR016T0dZMVpqUXpZelE1TlRnNFl6aGxOek5pT1dVd09XSmxPREJtWVROa05EVmlOV0ZpWXpCa05XVTRPRFF3TWpoaU1qbGpaR1ZpTldFeE1URmhOVFprT1RBNE9ETXpOVE01TkRka1l6TXpJaXdpWkdGMFlTSTZleUoxYzJWeVNXUWlPalV6Tmpjek5ESXlPU3dpZFhWcFpDSTZJbWRvT25jNlptSXpNekZpWkRJdFpURmxNQzAwWTJZNExUZzBPV0l0TWpnMlptRm1NV0l5WTJOa0luMHNJbWxoZENJNk1UVTROakkyTmpZNE5YMC51NTFOcmRDckgwUVNIakF3Y0wxU2lZYXVyaTZQd3Z4cExsNzBzWVdZclFrIiwib3B0aW1pemVFeHBlcmltZW50cyI6W3sidmFyaWFudCI6MCwiZXhwZXJpbWVudCI6Im9iSzh4N0RMVFhHRV9ZN25xbEJfX0EiLCJuYW1lIjoibGlrZV90b19yZWdfaG9sZG91dCIsImV4cGlyZWREYXRlIjoxNTk0MDE5Njk2fSx7InZhcmlhbnQiOjAsImV4cGVyaW1lbnQiOiJRRnNxZnFNUlQtU283akNheFh3a0VnIiwibmFtZSI6ImdldF90aGVfYXBwX2FnYWluc3Rfc2VsbF9ub3ciLCJleHBpcmVkRGF0ZSI6MTU5NDAxOTY5Nn0seyJ2YXJpYW50IjoxLCJleHBlcmltZW50IjoiYkhGMGJUZ1ZRVks0ZHlsQWMtRjZ3ZyIsIm5hbWUiOiJsdXhfaXRlbV9iYW5uZXIiLCJleHBpcmVkRGF0ZSI6MTU5NDAxOTY5Nn0seyJ2YXJpYW50IjowLCJleHBlcmltZW50IjoiQXE1aENKVUdTa0t0M0h3Ym5iWlBEUSIsIm5hbWUiOiJmcmVlX3NoaXBwaW5nX3RodW1iIiwiZXhwaXJlZERhdGUiOjE1OTQwMTk2OTZ9LHsidmFyaWFudCI6MiwiZXhwZXJpbWVudCI6IlJFdk11ekxWU2w2c1NhYzdKQUNqcVEiLCJuYW1lIjoicGF5cGFsX2NyZWRpdCIsImV4cGlyZWREYXRlIjoxNTk0MDE5Njk2fSx7InZhcmlhbnQiOjMsImV4cGVyaW1lbnQiOiJhLTdYaHVJdlFpT2RfSEUweFlNeFNRIiwibmFtZSI6ImdldC10aGUtYXBwLWRlc2t0b3AtMjAyMCIsImV4cGlyZWREYXRlIjoxNTk0MDE5Njk2fSx7InZhcmlhbnQiOjMsImV4cGVyaW1lbnQiOiIzcHBkLUxEVlRxR3dzMzFwTkRtcHZRIiwibmFtZSI6ImdldC10aGUtYXBwLW1vYmlsZS0yMDIwIiwiZXhwaXJlZERhdGUiOjE1OTQwMTk2OTZ9XSwiY3NyZlNlY3JldCI6IlNRdGMyMHNsNEEzX18wYUR4MW11SHlRdiIsInVzZXJJZCI6NTM2NzM0MjI5fQ==")
                .domain("mercari.com")
                .build();
        cookies.add(cookie1);
        cookies.add(cookie2);
        cookies.add(cookie3);
        cookies.add(cookie4);
        return cookies;
    }

    @Override
    public void onItemUploaded(Item item) {
        table.refresh();
    }

    @Override
    public void onAllItemsUploaded() {
        log("--- Items uploading to Mercari completed ---");
    }

    @Override
    public void onItemInfoLoaded(Item item) {
        items.add(item);
    }

    @Override
    public void onItemImagesLoaded(Item item) {
        table.refresh();
    }

    @Override
    public void onAllItemsLoaded() {
        log("--- Items loading from Ebay completed ---");
    }

    private void initCategoriesTv() {
        try {
            List<Category> categories = dataReader.getCategories();
            TreeItem<Category> rootItem = new TreeItem<>(new Category());
            rootItem.getChildren().addAll(findChildren(null, categories));
            categoriesTv.setRoot(rootItem);
            categoriesTv.setShowRoot(false);
        } catch (Exception e) {
            e.printStackTrace();
            log("Unable to load categories list");
        }
    }

    private List<TreeItem<Category>> findChildren(Integer categoryId, List<Category> categories) {
        List<TreeItem<Category>> children = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            Category childCategory = categories.get(i);
            Integer parentId = childCategory.getParentId();
            if ((categoryId == null && parentId == null) || (parentId != null && parentId.equals(categoryId))) {
                TreeItem<Category> childItem = new TreeItem<>(childCategory);
                children.add(childItem);
                childItem.getChildren().addAll(findChildren(childCategory.getId(), categories));
                categoryItems.put(childCategory.getId(), childItem);
            }
        }
        return children;
    }

    public static void showErrorAlert( String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setGraphic(new ImageView("/images/error.png"));
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
