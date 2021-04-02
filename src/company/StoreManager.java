package company;

import company.store.Store;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class StoreManager extends Application {

	@Override
	public void start(Stage primaryStage) {
		GridPane root = new GridPane();
		Scene scene = new Scene(root, 1200, 800);
		primaryStage.setTitle("My warehouse");
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
