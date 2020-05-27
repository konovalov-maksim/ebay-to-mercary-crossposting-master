package gui;

import core.*;
import core.ebayLoader.ItemsLoader;
import core.ebayLoader.LoadingListener;
import core.mercariUploader.ItemsUploader;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import okhttp3.Cookie;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements
        Initializable,
        Logger,
        ItemsUploader.UploadingListener,
        LoadingListener,
        SellerItemsInputController.ItemsInputCallback {

    @FXML private TitledPane itemParamsTp;

    @FXML private Label titleLbl;
    @FXML private Label descriptionLbl;
    @FXML private ListView<File> imagesListView;
    @FXML private TextField titleTf;
    @FXML private TextField priceTf;
    @FXML private TextField shippingPriceTf;
    @FXML private TextField finalPriceTf;
    @FXML private TextField tag0Tf;
    @FXML private TextField tag1Tf;
    @FXML private TextField tag2Tf;
    @FXML private TextArea descriptionTa;
    @FXML private ComboBox<Condition> conditionCb;
    @FXML private TreeView<Category> categoriesTv;

    @FXML private TextField zipCodeTf;
    @FXML private TextField uploadingDelayTf;

    @FXML private TableView<Item> table;
    @FXML private TextArea consoleTa;
    @FXML private TableColumn<Item, String> idCol;
    @FXML private TableColumn<Item, String> titleCol;
    @FXML private TableColumn<Item, String> descriptionCol;
    @FXML private TableColumn<Item, String> categoryCol;
    @FXML private TableColumn<Item, String> conditionCol;
    @FXML private TableColumn<Item, Double> ebayPriceCol;
    @FXML private TableColumn<Item, Double> ebayShippingPriceCol;
    @FXML private TableColumn<Item, Integer> priceCol;
    @FXML private TableColumn<Item, Integer> shippingPriceCol;
    @FXML private TableColumn<Item, Integer> finalPriceCol;
    @FXML private TableColumn<Item, String> tagsCol;
    @FXML private TableColumn<Item, Integer> imagesNumCol;
    @FXML private TableColumn<Item, String> statusCol;
    @FXML private TableColumn<Item, Boolean> isValidCol;
    @FXML private TableColumn<Item, Boolean> isUploadedCol;

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private final DataManager dataManager = DataManager.getInstance();
    private final Map<Integer, TreeItem<Category>> categoryItems = new HashMap<>();
    private final ObservableList<Item> items = FXCollections.observableArrayList();
    private final ObservableList<File> imagesFiles = FXCollections.observableArrayList();

    private final Stage loginStage = new Stage();
    private Settings settings;

    public void initialize(URL location, ResourceBundle resources) {
        try {
            settings = dataManager.loadSettings();
            zipCodeTf.setText(settings.getZipCode());
            String uploadingDelayStr = String.valueOf(settings.getUploadingDelay() != null ? settings.getUploadingDelay() : 500);
            uploadingDelayTf.setText(uploadingDelayStr);
        } catch (IOException e) {
            e.printStackTrace();
            log("Default settings not found");
        }

        //View components initialization
        initTable();
        initCategoriesTv();
        initImagesListView();
        initPriceTfs();
        descriptionTa.setWrapText(true);
        conditionCb.setItems(FXCollections.observableArrayList(Condition.getAllConditions()));
        titleLbl.textProperty().bind(Bindings.concat("Title (")
                .concat(titleTf.textProperty().length())
                .concat("/40):"));
        descriptionLbl.textProperty().bind(Bindings.concat("Description (")
                .concat(descriptionTa.textProperty().length())
                .concat("/1000):"));
    }

    @FXML
    private void addItemsById() {
        TextAreaDialog dialog = new TextAreaDialog("", "Enter items IDs:","Adding itesm IDs", "");
        Optional result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals("")) return;
            List<String> itemsIds = Arrays.stream(((String) result.get()).split("\\r?\\n"))
                    .distinct()
                    .collect(Collectors.toList());
            loadItems(itemsIds);
        }
    }

    @FXML
    private void addItemsBySeller() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/seller_items_input.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add("/style.css");
        stage.setTitle("Ebay to Mercari Crossposting Master");
        stage.getIcons().add(new Image("/images/icon64.png"));
        stage.setScene(new Scene(root));
        SellerItemsInputController sellerItemsInputController = loader.getController();
        sellerItemsInputController.setLogger(this);
        sellerItemsInputController.setItemsInputCallback(this);
        stage.show();
    }

    @Override
    public void onItemsIdsReceived(List<String> itemsIds) {
        loadItems(itemsIds);
    }

    private void loadItems(List<String> itemsIds) {
        if (itemsIds.isEmpty()) return;
        String ebayToken = settings.getEbayToken();
        if (ebayToken == null || ebayToken.isEmpty()) {
            log("Unable to load items: Ebay token not specified");
            return;
        }
        ItemsLoader itemsLoader = new ItemsLoader(ebayToken);
        itemsLoader.setImagesDirPath(Paths.get("").toAbsolutePath().resolve("images"));
        itemsLoader.setLogger(this);
        itemsLoader.setLoadingListener(this);
        itemsLoader.setItemsIds(itemsIds);
        log("Items loading from Ebay started");
        new Thread(itemsLoader).start();
    }

    @FXML
    private void startUploading() {
        if (items.isEmpty()) {
            showAlert("No items for uploading", Alert.AlertType.ERROR);
            return;
        }
        if (items.stream().anyMatch(c -> !c.isValid())) {
            Optional result = showAlert("Parameters of some items are not completely filled. Continue anyway?",
                    Alert.AlertType.WARNING);
            if (!result.isPresent() || result.get().equals(ButtonType.CANCEL)) return;
        }
        try {
            saveSettings();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
            return;
        }
        uploadItems();
    }

    private void saveSettings() {
        try {
            Integer.parseInt(zipCodeTf.getText());
            settings.setZipCode(zipCodeTf.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect ZIP code!");
        }
        try {
            long uploadingDelay = Long.parseLong(uploadingDelayTf.getText());
            if (uploadingDelay < 0) throw new IllegalArgumentException();
            settings.setUploadingDelay(uploadingDelay);
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect uploading delay");
        }
        try {
            dataManager.saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
            log("Failed to save settings");
        }
    }

    private void uploadItems() {
        ItemsUploader uploader = new ItemsUploader();
        uploader.setLogger(this);
        uploader.setUploadingListener(this);
        uploader.setCookies(loadCookies());
        if (!uploader.isLoggedIn()) {
            log("Not logged in");
            openLoginDialog();
            return;
        }
        uploader.setItems(items);
        uploader.setZipCode(settings.getZipCode());
        uploader.setUploadingDelay(settings.getUploadingDelay());
        log("Items uploading started");
        new Thread(uploader).start();
    }

    private List<Cookie> loadCookies(){
        List<Cookie> cookies = new ArrayList<>();
        try {
            cookies = dataManager.loadCookies();
        } catch (IOException e) {
            e.printStackTrace();
            log("Unable to load cookies");
        }
        return cookies;
    }

    @FXML
    private void openLoginDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mercariLogin.fxml"),
                    ResourceBundle.getBundle("bundles.strings"));
            Parent root = loader.load();
            loginStage.setTitle("Log in - Mercari");
            loginStage.getIcons().add(new Image("/images/icon64.png"));
            loginStage.setScene(new Scene(root));

            LoginController loginController = loader.getController();
            loginController.setSettings(settings);
            loginController.loadLoginPage();
            loginController.setLogger(this);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showItemParams(Item item) {
        if (item == null) {
            clearItemParamsFields();
            return;
        }
        itemParamsTp.setExpanded(true);
        titleTf.setText(item.getTitle());
        priceTf.setText(String.valueOf(item.getPrice()));
        shippingPriceTf.setText(String.valueOf(item.getShippingPrice()));
        descriptionTa.setText(item.getDescription());
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

        imagesFiles.clear();
        imagesFiles.addAll(item.getImages());
        imagesListView.refresh();
    }

    private void clearItemParamsFields() {
        itemParamsTp.setExpanded(true);
        titleTf.clear();
        priceTf.clear();
        shippingPriceTf.clear();
        descriptionTa.clear();
        imagesFiles.clear();
        conditionCb.setValue(null);
        categoriesTv.getSelectionModel().clearSelection();
        tag0Tf.clear();
        tag1Tf.clear();
        tag2Tf.clear();
    }

    @FXML
    private void applyParams() {
        List<Item> selectedItems = table.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 1) {
            applyForOne(selectedItems.get(0));
        } else if (selectedItems.size() > 1) {
            applyForAll(selectedItems);
        }
    }

    private void applyForOne(Item selectedItem) {
        selectedItem.setCondition(conditionCb.getValue());
        selectedItem.setTitle(titleTf.getText());
        selectedItem.setDescription(descriptionTa.getText());
        TreeItem<Category> categoryItem = categoriesTv.getSelectionModel().getSelectedItem();
        if (categoryItem != null) selectedItem.setCategory(categoryItem.getValue());
        else selectedItem.setCategory(null);
        selectedItem.setTag0(tag0Tf.getText());
        selectedItem.setTag1(tag1Tf.getText());
        selectedItem.setTag2(tag2Tf.getText());
        try {
            selectedItem.setPrice(Integer.valueOf(priceTf.getText()));
        } catch (NumberFormatException e) {
            showAlert("Incorrect price!", Alert.AlertType.ERROR);
        }
        try {
            selectedItem.setShippingPrice(Integer.valueOf(shippingPriceTf.getText()));
        } catch (NumberFormatException e) {
            showAlert("Incorrect shipping price!", Alert.AlertType.ERROR);
        }
        selectedItem.getImages().clear();
        selectedItem.getImages().addAll(imagesFiles);
        table.refresh();
    }

    private void applyForAll(List<Item> selectedItems) {
        Integer price = null;
        if (priceTf.getText() != null && !priceTf.getText().isEmpty()) {
            try {
                price = Integer.valueOf(priceTf.getText());
            } catch (NumberFormatException e) {
                showAlert("Incorrect price!", Alert.AlertType.ERROR);
            }
        }
        Integer shippingPrice = null;
        if (shippingPriceTf.getText() != null && !shippingPriceTf.getText().isEmpty()) {
            try {
                shippingPrice = Integer.valueOf(shippingPriceTf.getText());
            } catch (NumberFormatException e) {
                showAlert("Incorrect shipping price!", Alert.AlertType.ERROR);
            }
        }
        for (Item selectedItem : selectedItems) {
            if (conditionCb.getValue() != null)
                selectedItem.setCondition(conditionCb.getValue());
            if (titleTf.getText() != null && !titleTf.getText().isEmpty())
                selectedItem.setTitle(titleTf.getText());
            if (descriptionTa.getText() != null && !descriptionTa.getText().isEmpty())
                selectedItem.setDescription(descriptionTa.getText());
            if (categoriesTv.getSelectionModel().getSelectedItem() != null)
                selectedItem.setCategory(categoriesTv.getSelectionModel().getSelectedItem().getValue());
            if (tag0Tf.getText() != null && !tag0Tf.getText().isEmpty())
                selectedItem.setTag0(tag0Tf.getText());
            if (tag1Tf.getText() != null && !tag1Tf.getText().isEmpty())
                selectedItem.setTag1(tag1Tf.getText());
            if (tag2Tf.getText() != null && !tag2Tf.getText().isEmpty())
                selectedItem.setTag2(tag2Tf.getText());
            if (price != null)
                selectedItem.setPrice(price);
            if (shippingPrice != null)
                selectedItem.setShippingPrice(shippingPrice);
        }
        table.refresh();
    }

    @FXML
    private void clearItems() {
        List<File> images = new ArrayList<>();
        items.forEach(i -> images.addAll(i.getImages()));
        dataManager.removeImages(images);
        items.clear();
        table.refresh();
    }

    @FXML
    private void trimTitle() {
        final int titleLimit = 40;
        String title = titleTf.getText();
        if (title.length() <= titleLimit) return;
        for (int i = titleLimit; i > 0; i--) {
            if (title.charAt(i) == ' ') {
                titleTf.setText(title.substring(0, i));
                return;
            }
        }
        titleTf.setText("");
    }

    @FXML
    private void selectAllItems() {
        if (table.getItems().isEmpty()) return;
        table.getSelectionModel().selectRange(0, table.getItems().size());
    }

    @Override
    public void log(String message) {
        String curTime = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
        Platform.runLater(() -> {
            consoleTa.setText(consoleTa.getText() + curTime + "     " + message + "\n");
            consoleTa.positionCaret(consoleTa.getLength());
        });
    }

    @Override
    public void clearLog() {
        Platform.runLater(() -> consoleTa.setText(""));
    }

    @Override
    public void onItemUploaded(Item item) {
        table.refresh();
    }

    @Override
    public void onAllItemsUploaded() {
        log("Items uploading to Mercari completed");
        Platform.runLater(() -> {
            Optional result = showAlert("Items uploading is complete. Do you want to delete downloaded images?",
                    Alert.AlertType.CONFIRMATION);
            if (!result.isPresent() || result.get().equals(ButtonType.CANCEL)) return;
            clearItems();
        });
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
        log("Items loading from Ebay completed");
    }

    private void initTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("conditionName"));
        ebayPriceCol.setCellValueFactory(new PropertyValueFactory<>("ebayPrice"));
        ebayShippingPriceCol.setCellValueFactory(new PropertyValueFactory<>("ebayShippingPrice"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        shippingPriceCol.setCellValueFactory(new PropertyValueFactory<>("shippingPrice"));
        finalPriceCol.setCellValueFactory(new PropertyValueFactory<>("finalPrice"));
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tagsString"));
        imagesNumCol.setCellValueFactory(new PropertyValueFactory<>("imagesNum"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        isValidCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isValid()));
        isValidCol.setCellFactory(tc -> new CheckBoxTableCell<>());
        isUploadedCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isUploaded()));
        isUploadedCol.setCellFactory(tc -> new CheckBoxTableCell<>());
        idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        descriptionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        conditionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        ebayPriceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        ebayShippingPriceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        priceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        shippingPriceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        finalPriceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        tagsCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        imagesNumCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        categoryCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        isValidCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        isUploadedCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        table.setItems(items);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (table.getSelectionModel().getSelectedItems().size() == 1)
                showItemParams(table.getSelectionModel().getSelectedItem());
            else
                clearItemParamsFields();
        });
        table.setTableMenuButtonVisible(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        new TableContextMenu(table);
    }

    private void initCategoriesTv() {
        try {
            List<Category> categories = dataManager.getCategories();
            TreeItem<Category> rootItem = new TreeItem<>(new Category());
            rootItem.getChildren().addAll(findChildren(null, categories));
            categoriesTv.setRoot(rootItem);
            categoriesTv.setShowRoot(false);
        } catch (Exception e) {
            e.printStackTrace();
            log("Unable to load categories list");
        }
    }

    private void initPriceTfs() {
        ChangeListener<String> priceChangeListener = (observable, oldValue, newValue) -> {
            int price = 0;
            int shippingPrice = 0;
            try {
                if (!priceTf.getText().isEmpty()) price = Integer.parseInt(priceTf.getText());
                if (!shippingPriceTf.getText().isEmpty()) shippingPrice = Integer.parseInt(shippingPriceTf.getText());
            } catch (Exception e) {
                return;
            }
            finalPriceTf.setText(String.valueOf(price + shippingPrice));
        };
        priceTf.textProperty().addListener(priceChangeListener);
        shippingPriceTf.textProperty().addListener(priceChangeListener);
    }

    private void initImagesListView() {
        imagesListView.setItems(imagesFiles);
        imagesListView.setCellFactory(param -> new ListCell<File>() {
            @Override
            protected void updateItem(File imageFile, boolean empty) {
                super.updateItem(imageFile, empty);
                if (imageFile == null) {
                    this.setGraphic(null);
                    return;
                }
                try (InputStream imageIs = new FileInputStream(imageFile) ) {
                    Image image = new Image(imageIs);
                    ImageView imageView = new ImageView(image);
                    imageView.fitHeightProperty().bind(imagesListView.heightProperty().subtract(40));
                    imageView.setPreserveRatio(true);
                    this.setGraphic(imageView);

                    this.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            imagesFiles.remove(imageFile);
                            imagesListView.refresh();
                        }
                    });
                    this.setOnDragDetected(event -> {
                        if (!this.isEmpty()) {
                            Integer index = this.getIndex();
                            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
                            db.setDragView(this.snapshot(null, null));
                            ClipboardContent cc = new ClipboardContent();
                            cc.put(SERIALIZED_MIME_TYPE, index);
                            db.setContent(cc);
                            event.consume();
                        }
                    });
                    this.setOnDragOver(event -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                            if (this.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                                event.consume();
                            }
                        }
                    });
                    this.setOnDragDropped(event -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                            int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                            File draggedPerson = imagesFiles.remove(draggedIndex);
                            int dropIndex ;
                            if (isEmpty()) {
                                dropIndex = imagesFiles.size() ;
                            } else {
                                dropIndex = this.getIndex();
                            }
                            imagesFiles.add(dropIndex, draggedPerson);
                            event.setDropCompleted(true);
                            imagesListView.getSelectionModel().select(dropIndex);
                            event.consume();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    private Optional showAlert(String message, Alert.AlertType type) {
        Alert alert;
        if (type.equals(Alert.AlertType.WARNING) || type.equals(Alert.AlertType.CONFIRMATION)) {
            alert = new Alert(type, message, ButtonType.OK, ButtonType.CANCEL);
            alert.setGraphic(new ImageView("/images/warning.png"));
            alert.setTitle("Warning");
        } else {
            alert = new Alert(type, message);
            alert.setGraphic(new ImageView("/images/error.png"));
            alert.setTitle("Error");
        }
        alert.setHeaderText(null);
        return alert.showAndWait();
    }

}
