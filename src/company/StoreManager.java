package company;

import controller.Controller;
import company.store.Store;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class StoreManager extends Application {

    Store store;
    String default_map_path = StoreManager.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "../inputs/map1525.in";
    String map_path = default_map_path;
    String goods_path;

    @Override
    public void start(Stage primaryStage) {
        SplitPane root = null;
        try {
            root = FXMLLoader.load(StoreManager.class.getResource("../controller/WarehouseGUI.fxml"));
        } catch (IOException e) {
            System.err.println(e); // TODO write this in GUI
            System.exit(1);
        }
        root = setUP_Store(root);
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Warehouse manager");
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getStylesheets().add(String.valueOf(StoreManager.class.getResource("../controller/style.css")));
    }

    public void set_map_path(String path) {
        map_path = path;
    }

    public SplitPane setUP_Store(SplitPane root) {
        store = new Store();
        if (!store.setMap(map_path)) {
            store.setMap(default_map_path);
        }
        int h = store.GetHeight();
        int w = store.GetWidth();
        GridPane warehouse = new GridPane();
        Pane pane = (Pane) root.getItems().get(0);
        NumberBinding rects_height = Bindings.max(pane.heightProperty(),0);
        NumberBinding rects_width = Bindings.max(0,pane.widthProperty());
        warehouse.setStyle("-fx-background-color: white;");
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Rectangle rec = new Rectangle(20,40);
                rec.widthProperty().bind(rects_width.divide((double)w).subtract(1));
                rec.heightProperty().bind(rects_height.divide((double)h).subtract(1));
                if (store.GetMapValue(i,j) == 0) {
                    rec.getStyleClass().add("path");
                }
                else {
                    rec.getStyleClass().add("shelve");
                }
                warehouse.addColumn(i,rec);
            }
        }

        //warehouse.setGridLinesVisible(true);
       // System.out.println(warehouse.getChildren());
        pane.getChildren().add(warehouse);
        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
