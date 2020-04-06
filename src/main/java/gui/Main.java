package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        ResourceBundle rb = ResourceBundle.getBundle("bundles.strings");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"), rb);
        Parent root = loader.load();
        root.getStylesheets().add("/style.css");
        stage.setTitle("Ebay to Mercari copier");
        stage.getIcons().add(new Image("/images/icon64.png"));
        stage.setScene(new Scene(root));

        stage.show();
    }

}
