package gui;

import core.Logger;
import core.ebayLoader.SellerItemsSeeker;
import core.ebayLoader.pojo.SellerCategory;
import core.ebayLoader.pojo.SellerItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ItemsInputController implements Initializable, SellerItemsSeeker.SellerItemsSeekingListener {

    @FXML private TextField sellerNameTf;

    @FXML private TableView<SellerCategory> categoriesTable;
    @FXML private TableColumn<SellerCategory, String> categoryNameCol;
    @FXML private TableColumn<SellerCategory, Integer> itemsCountCol;

    private final ObservableList<SellerCategory> categories = FXCollections.observableArrayList();
    private final List<SellerItem> sellerItems = new ArrayList<>();

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

        categoriesTable.setItems(categories);
        categoryNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemsCountCol.setCellValueFactory(new PropertyValueFactory<>("itemsCount"));
        categoryNameCol.prefWidthProperty().bind(categoriesTable.widthProperty().multiply(0.8));
        itemsCountCol.prefWidthProperty().bind(categoriesTable.widthProperty().multiply(0.2));
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
        //TODO show progressBar here
    }

    @Override
    public void onSearchingComplete(List<SellerItem> sellerItems) {
        //TODO hide progressBar here
        log("Loaded " + sellerItems.size() + " items");
        this.sellerItems.addAll(sellerItems);
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

    private void log(String message) {
        if (logger != null) logger.log(message);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public interface ItemsInputCallback {
        void onItemsIdsEntered(List<String> itemsIds);
    }

/*    static class SellerCategory {
        private final String name;

        private final List<SellerItem> sellerItems = new ArrayList<>();

        public SellerCategory(String name) {
            this.name = name;
        }

        public void addSellerItem(SellerItem sellerItem) {
            sellerItems.add(sellerItem);
        }

        public Integer getItemsCount() {
            return sellerItems.size();
        }

        public String getName() {
            return name;
        }

        public List<SellerItem> getSellerItems() {
            return sellerItems;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SellerCategory that = (SellerCategory) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }*/
}
