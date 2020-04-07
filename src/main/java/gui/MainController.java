package gui;

import core.Item;
import core.Logger;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController implements Initializable, Logger {

    @FXML private WebView loginWv;
    @FXML private TableView<Item> table;
    @FXML private TextArea consoleTa;
    @FXML private TableColumn<Item, String> idCol;
    @FXML private TableColumn<Item, String> titleCol;
    @FXML private TableColumn<Item, String> descriptionCol;
    @FXML private TableColumn<Item, Double> priceCol;


    private ObservableList<Item> items = FXCollections.observableArrayList();

    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.setItems(items);
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
            consoleTa.setText(consoleTa.getText() + curTime + "     " + consoleTa + "\n");
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
        item1.setPrice(15.12);
        item1.setTags(Arrays.asList("belts", "accessory"));

        Item item2 = new Item("2345");
        item2.setTitle("Men's belt");
        item2.setDescription("Amazing men's belt");
        item2.setPrice(15.12);
        item2.setTags(Arrays.asList("belts", "accessory"));

        items.add(item1);
        items.add(item2);
        return items;
    }
}
