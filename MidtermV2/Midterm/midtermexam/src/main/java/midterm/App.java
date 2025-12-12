package midterm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interface.fxml"));
        Parent root = loader.load();

        // Set the scene
        Scene scene = new Scene(root);

        // Set stage properties
        primaryStage.setTitle("Forbidden Words Search");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch JavaFX application
        launch(args);
    }
}
