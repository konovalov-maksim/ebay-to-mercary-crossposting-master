package gui;

import core.Item;
import core.Logger;
import core.ItemsUploader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.Cookie;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController implements Initializable, Logger, ItemsUploader.UploadingListener {

    @FXML private TableView<Item> table;
    @FXML private TextArea consoleTa;
    @FXML private TableColumn<Item, String> idCol;
    @FXML private TableColumn<Item, String> titleCol;
    @FXML private TableColumn<Item, String> descriptionCol;
    @FXML private TableColumn<Item, Double> priceCol;
    @FXML private TableColumn<Item, String> statusCol;


    private ObservableList<Item> items = FXCollections.observableArrayList();

    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        items.addAll(getDebugItems());
        table.setItems(items);
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
        item1.setConditionId(2);
        item1.setCategoryId(391);
        item1.setTags(Arrays.asList("belts", "accessory"));
        item1.setImages(getDebugImages1());

        Item item2 = new Item("2345");
        item2.setTitle("Women's belt");
        item2.setDescription("Amazing women's belt");
        item2.setPrice(10);
        item2.setConditionId(2);
        item2.setCategoryId(391);
        item2.setTags(Arrays.asList("belts", "accessory"));
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
}
