package company;

import company.store.Store;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StoreManager extends Application {

	Store store;

	@Override
	public void start(Stage primaryStage) {
		store = new Store();
		store.setMap("/home/dany/Skola/IJA/projekt/IJA/inputs/map1010.in"); // TODO pridať textove okienko, kde zadá cestu k súboru, pripadne nejaké vždy určiť
		int h = store.GetHeight(); // TODO Scene Builder build the GUI first
		int w = store.GetWidth();

		BorderPane screen = new BorderPane();
		GridPane pane = new GridPane();
		pane.setPrefSize(w,h);
		VBox menu = new VBox();
		screen.setRight(menu);
		screen.setCenter(pane);


		Scene scene = new Scene(screen, 1200, 800);
		primaryStage.setTitle("My warehouse");
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
