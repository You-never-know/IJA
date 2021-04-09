package company;

import controller.Controller;
import company.store.Store;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for connecting program with its GUI
 */
public class StoreManager extends Application {

    private Store store;
    private String classPath = StoreManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private String defaultMapPath = classPath + "../data/map1525.in";
    private String mapPath = defaultMapPath;
    private String defaultGoodsPath = classPath + "../data/goods.in";
    private String goodsPath = defaultGoodsPath;
    private GridPane storePlan;
    private Controller controller;
    private FXMLLoader loader;
    private SplitPane root;

    /**
     * Start the app, load GUI
     * @param primaryStage Stage that will be displayed in the app
     */
    @Override
    public void start(Stage primaryStage) {
        SetUpManager();
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        controller = loader.getController();
        controller.setStore(store);
        setUPStore();
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(String.valueOf(StoreManager.class.getResource("../controller/style.css")));
        primaryStage.setTitle("Warehouse manager");
        primaryStage.setScene(scene);
        new Thread(() -> {
            store.main();
        }).start();
        primaryStage.show();
    }

    /**
     * Initialize StoreManager
     */
    public void SetUpManager() {
        store = new Store();
        store.setManager(this);
        loader = new FXMLLoader();
        loader.setLocation(StoreManager.class.getResource("../controller/WarehouseGUI.fxml"));
    }

    /**
     * @param path Set path to the Map file
     */
    public void setMapPath(String path) {
        mapPath = classPath + path;
    }

    /**
     * @param path Set path to the goods file
     */
    public void setGoodsPath(String path) {
        this.goodsPath = classPath + path;
    }

    /**
     * @return Path to the goods, that has been set by user
     */
    public String getGoodsPath() {
        return this.goodsPath;
    }

    /**
     * @return Default path to goods file
     */
    public String getDefaultGoodsPath() {
        return defaultGoodsPath;
    }

    /**
     * Show that a shelve is occupied in GUI
     * @param index index in GridPane where the good shall be displayed
     */
    public void setUPGoods(int index) {
        storePlan.getChildren().get(index).getStyleClass().add("full_shelve");
    }

    /**
     * Create visual representation of the Store in the GUI
     */
    public void setUPStore() {
        if (!store.setMap(mapPath)) {
            this.mapPath = defaultMapPath;
            return;
        }
        store.cleanShelves();
        int h = store.getHeight();
        int w = store.getWidth();
        storePlan = new GridPane();
        Pane pane = (Pane) root.getItems().get(0);
        NumberBinding rects_height = Bindings.max(pane.heightProperty(), 0);
        NumberBinding rects_width = Bindings.max(0, pane.widthProperty());
        int shelve_id = 1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Rectangle rec = new Rectangle(20, 40);
                rec.widthProperty().bind(rects_width.divide((double) w).subtract(1));
                rec.heightProperty().bind(rects_height.divide((double) h).subtract(1));
                if (store.getMapValue(i, j) == 0) {
                    rec.getStyleClass().add("path");
                    rec.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            controller.pathClicked(mouseEvent);
                        }
                    });
                } else {
                    store.createShelve(shelve_id, i, j);
                    shelve_id++;
                    rec.getStyleClass().add("shelve");
                    rec.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            controller.showGoodsContent(mouseEvent);
                        }
                    });
                }
                storePlan.addColumn(i, rec);
            }
        }
        pane.getChildren().remove(0);
        pane.getChildren().add(storePlan);
    }

    public static void main(String[] args) {
        launch(args);
        Runtime.getRuntime().exit(0);
    }

    /**
     * Display message in the GUI
     * @param msg Message to be displayed
     */
    public void logMessageTA(String msg){
        controller.logMessage(msg);
    }
}
