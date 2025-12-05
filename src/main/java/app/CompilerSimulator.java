package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class CompilerSimulator extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Define the name of your FXML file
            // Make sure "CompilerView.fxml" is in src/main/resources
            String fxmlFile = "view/CompilerView.fxml";

            // 2. Locate the file
            URL fxmlUrl = getClass().getResource("/" + fxmlFile);

            // 3. Fallback check (sometimes needed depending on project structure)
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource(fxmlFile);
            }

            // 4. Error Handling: Check if file was actually found
            if (fxmlUrl == null) {
                System.err.println("\n---------------------------------------------------------");
                System.err.println("CRITICAL ERROR: FXML File Not Found!");
                System.err.println("Looking for: " + fxmlFile);
                System.err.println("Please ensure it is located in 'src/main/resources/'");
                System.err.println("---------------------------------------------------------\n");
                return;
            }

            // 5. Load the UI from the FXML file
            Parent root = FXMLLoader.load(fxmlUrl);

            // 6. Set up the Scene and Show
            Scene scene = new Scene(root);
            primaryStage.setTitle("Mini Compiler - Variable Declaration Analysis");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error loading the FXML file. Check your Controller class name in the FXML.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}