package gui;

import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

public class TableContextMenu extends ContextMenu {

    public <S> TableContextMenu(TableView<S> table) {
        final int IMG_SIZE = 16;

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().setCellSelectionEnabled(false);

        //Remove Item"
        MenuItem removeItem = new MenuItem("Remove item");
        ImageView delIcon = new ImageView("/images/remove.png");
        delIcon.setFitHeight(IMG_SIZE);
        delIcon.setFitWidth(IMG_SIZE);
        removeItem.setGraphic(delIcon);
        removeItem.setOnAction(a -> table.getItems().removeAll(table.getSelectionModel().getSelectedItems()));
        removeItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        this.getItems().addAll(removeItem);

        //Делаем меню видимым только для непустых строк таблицы
        table.setRowFactory(c -> {
            TableRow<S> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(this));
            return row;
        });
    }

    public MenuItem getRemoveItem() {
        return this.getItems().get(0);
    }

}
