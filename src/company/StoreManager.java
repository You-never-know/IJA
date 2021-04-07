package company;

import company.store.forklift.Forklift;
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

    private Store store;
    private String class_path = StoreManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private String default_map_path = class_path + "../inputs/map1525.in";
    private String map_path = default_map_path;
    private String default_goods_path = class_path + "../inputs/goods.in";
    private String goods_path = default_goods_path;
    private GridPane store_plan;
    private Controller controller;
    private FXMLLoader loader;
    private SplitPane root;

    @Override
    public void start(Stage primaryStage) {
        SetUpManager();
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println(e); // TODO write this in GUI
            System.exit(1);
        }
        controller = (Controller) loader.getController();
        controller.SetStore(store);
        setUP_Store();
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(String.valueOf(StoreManager.class.getResource("../controller/style.css")));
        primaryStage.setTitle("Warehouse manager");
        primaryStage.setScene(scene);
        new Thread(() -> {
            store.main();
        }).start();
        primaryStage.show();
    }

    public void SetUpManager() {
        store = new Store();
        store.setManager(this);
        loader = new FXMLLoader();
        loader.setLocation(StoreManager.class.getResource("../controller/WarehouseGUI.fxml"));
    }

    public void set_map_path(String path) {
        map_path = class_path + path;
    }

    public String get_map_path() {
        return map_path;
    }

    public String get_default_map_path() {
        return default_map_path;
    }

    public void set_goods_path(String path) {
        this.goods_path = class_path + path;
    }

    public String get_goods_path() {
        return this.goods_path;
    }

    public String get_default_goods_path() {
        return default_goods_path;
    }

    public void test_forklift() {
        Forklift fork = new Forklift(1, 2, 0, store);
        // volaj funkcie ake len chces

    }

    public void setUP_Goods(int index) {
        store_plan.getChildren().get(index).getStyleClass().add("full_shelve");
    }

    public void setUP_Store() {
        if (!store.setMap(map_path)) {
            this.map_path = default_map_path;
            return;
        }
        int h = store.GetHeight();
        int w = store.GetWidth();
        store_plan = new GridPane();
        Pane pane = (Pane) root.getItems().get(0);
        NumberBinding rects_height = Bindings.max(pane.heightProperty(), 0);
        NumberBinding rects_width = Bindings.max(0, pane.widthProperty());
        int shelve_id = 1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Rectangle rec = new Rectangle(20, 40);
                rec.widthProperty().bind(rects_width.divide((double) w).subtract(1));
                rec.heightProperty().bind(rects_height.divide((double) h).subtract(1));
                if (store.GetMapValue(i, j) == 0) {
                    rec.getStyleClass().add("path");
                } else {
                    store.create_shelf(shelve_id, i, j);
                    shelve_id++;
                    rec.getStyleClass().add("shelve");
                }
                store_plan.addColumn(i, rec);
            }
        }
        pane.getChildren().remove(0);
        pane.getChildren().add(store_plan);
    }


    public static void main(String[] args) {
        launch(args);
        Runtime.getRuntime().exit(0);
    }
}
