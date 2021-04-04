package company;

import controller.Controller;
import company.store.Store;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StoreManager extends Application {

	Store store;

	@Override
	public void start(Stage primaryStage) {
		// store = new Store();
		// store.setMap("/home/dany/Skola/IJA/projekt/IJA/inputs/map1010.in"); // TODO pridať textove okienko, kde zadá cestu k súboru, pripadne nejaké vždy určiť
		// int h = store.GetHeight(); // TODO Scene Builder build the GUI first
		// int w = store.GetWidth();

		Parent root = null;
		try {
			root = FXMLLoader.load(StoreManager.class.getResource("WarehouseGUI.fxml"));
		} catch (IOException e) {
			System.err.println("GUI not found"); // TODO write this in GUI
			System.exit(1);
		}
		if (root != null) {
			Scene scene = new Scene(root, 1200, 800);
			primaryStage.setTitle("My warehouse");
			primaryStage.setScene(scene);
			primaryStage.show();
		}
	}


	public static void main(String[] args) {
		launch(args);
	}
}
