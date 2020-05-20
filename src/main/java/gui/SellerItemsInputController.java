package gui;

import core.Logger;
import core.ebayLoader.SellerItemsSeeker;
import core.ebayLoader.pojo.SellerCategory;
import core.ebayLoader.pojo.SellerItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class SellerItemsInputController implements Initializable, SellerItemsSeeker.SellerItemsSeekingListener {

    @FXML private TextField sellerNameTf;
    @FXML private ProgressBar progressBar;

    @FXML private TableView<SellerCategory> categoriesTable;
    @FXML private TableColumn<SellerCategory, String> categoryNameCol;
    @FXML private TableColumn<SellerCategory, Integer> itemsCountCol;

    private final ObservableList<SellerCategory> categories = FXCollections.observableArrayList();

    private final DataManager dataManager = DataManager.getInstance();
    private Settings settings;
    private ItemsInputCallback itemsInputCallback;
    private Logger logger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            settings = dataManager.loadSettings();
        } catch (IOException e) {
            e.printStackTrace();
            log("Unable to load settings");
        }

        progressBar.setProgress(0);
        categoriesTable.setItems(categories);
        categoryNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemsCountCol.setCellValueFactory(new PropertyValueFactory<>("itemsCount"));
        categoryNameCol.prefWidthProperty().bind(categoriesTable.widthProperty().multiply(0.8));
        itemsCountCol.prefWidthProperty().bind(categoriesTable.widthProperty().multiply(0.2));
        categoriesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void findItems() {
        String sellerName = sellerNameTf.getText();
        if (sellerName == null || sellerName.isEmpty()) {
            return;
        }
        String token = settings.getEbayToken();
        SellerItemsSeeker sellerItemsSeeker = new SellerItemsSeeker(token, sellerName, this);
        sellerItemsSeeker.setLogger(logger);
        new Thread(sellerItemsSeeker).start();
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    @Override
    public void onSearchingComplete(List<SellerItem> sellerItems) {
        Platform.runLater(() -> progressBar.setProgress(1));
        log(sellerItems.size() + " items found");
        sortByCategories(sellerItems);
        categoriesTable.refresh();
    }

    private void sortByCategories(List<SellerItem> sellerItems) {
        for (SellerItem item : sellerItems) {
            SellerCategory category = categories.stream()
                    .filter(c -> item.getCategoryName().equals(c.getName()))
                    .findAny()
                    .orElse(null);
            if (category == null) {
                category = new SellerCategory(item.getCategoryName());
                categories.add(category);
            }
            category.addSellerItem(item);
        }
    }

    @FXML
    private void addSelectedItems() {
        returnFoundItems(categoriesTable.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void addAllItems() {
        returnFoundItems(categories);
    }

    private void returnFoundItems(List<SellerCategory> selectedCategories) {
        List<String> itemsIds = new ArrayList<>();
        for (SellerCategory category : selectedCategories) {
            List<String> categoryItemsIds = category.getSellerItems().stream()
                    .map(SellerItem::getId)
                    .collect(Collectors.toList());
            itemsIds.addAll(categoryItemsIds);
        }
        ((Stage) categoriesTable.getScene().getWindow()).close();
        if (itemsInputCallback != null) itemsInputCallback.onItemsIdsReceived(itemsIds);
    }

    private void log(String message) {
        if (logger != null) logger.log(message);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setItemsInputCallback(ItemsInputCallback itemsInputCallback) {
        this.itemsInputCallback = itemsInputCallback;
    }

    public interface ItemsInputCallback {
        void onItemsIdsReceived(List<String> itemsIds);
    }

}
