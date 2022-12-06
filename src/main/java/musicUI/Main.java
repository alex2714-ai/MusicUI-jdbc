package musicUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.stage.Stage;
import musicUI.model.DataSource;

import java.io.IOException;


public class Main extends Application {
    private boolean exceptionInInit = false;
    @Override
    public void start(Stage stage) throws IOException {
        if (exceptionInInit) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Fatal Error, Couldn't connect to database");

            alert.showAndWait();

            // make sure platform shuts down even though primary stage was never shown
            Platform.exit();
        } else {
            // normal startup
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view.fxml"));
            Parent root=fxmlLoader.load();
            Controller controller=fxmlLoader.getController();
            controller.listArtist();
            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Music DataBase");
            stage.setScene(scene);
            stage.show();
        }




    }

    @Override
    public void init() throws Exception {
        try {

            super.init();
            if(!DataSource.getInstance().openConnection()){
                throw new Exception();
            }
        } catch (Exception e) {
            exceptionInInit = true;
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DataSource.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch();
    }
}