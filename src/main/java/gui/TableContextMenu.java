package gui;

import core.Item;
import javafx.beans.binding.Bindings;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.awt.*;
import java.net.URI;

public class TableContextMenu extends ContextMenu {

    public TableContextMenu(TableView<Item> table) {

        table.getSelectionModel().setCellSelectionEnabled(false);

        //Remove Item
        MenuItem removeItem = new MenuItem("Remove item");
        removeItem.setOnAction(a -> table.getItems().removeAll(table.getSelectionModel().getSelectedItems()));
        removeItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        //Trim title
        MenuItem trimTitleItem = new MenuItem("Trim title");
        trimTitleItem.setOnAction(a -> {
            table.getSelectionModel().getSelectedItems().forEach(Item::trimTitle);
            table.refresh();
        });
        trimTitleItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));

        //Show on Ebay
        MenuItem showOnEbayItem = new MenuItem("Show on Ebay");
        showOnEbayItem.setOnAction(a -> {
            String ebayUrl = table.getSelectionModel().getSelectedItem().getEbayUrl();
            if (ebayUrl != null) openUrl(ebayUrl);
        });
        showOnEbayItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

        //Show on Mercari
        MenuItem showOnMercariItem = new MenuItem("Show on Mercari");
        showOnMercariItem.setOnAction(a -> {
            String mercariUrl = table.getSelectionModel().getSelectedItem().getMercariUrl();
            if (mercariUrl != null) openUrl(mercariUrl);
        });
        showOnMercariItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));


        this.getItems().addAll(removeItem, trimTitleItem, showOnEbayItem, showOnMercariItem);

        //������ ���� ������� ������ ��� �������� ����� �������
        table.setRowFactory(c -> {
            TableRow<Item> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(this));
            return row;
        });
    }

    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
