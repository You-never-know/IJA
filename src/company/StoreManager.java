package company;

import company.store.shelve.goods.coordinates.Coordinates;
import controller.Controller;
import company.store.Store;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class for connecting program with its GUI
 */
public class StoreManager extends Application {

    private Store store;
    private final String classPath = StoreManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private final String defaultMapPath = classPath + "../data/map1525.in";
    private String mapPath = defaultMapPath;
    private final String defaultGoodsPath = classPath + "../data/goods.in";
    private String goodsPath = defaultGoodsPath;
    private GridPane storePlan;
    private Controller controller;
    private FXMLLoader loader;
    private SplitPane root;
    private ArrayList<Coordinates> visited_indexes;
    public ReentrantLock lock;

    /**
     * Start the app, load GUI
     *
     * @param primaryStage Stage that will be displayed in the app
     */
    @Override
    public void start(Stage primaryStage) {
        SetUpManager();
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(StoreManager.class.getResourceAsStream("../controller/WMico.png"))));
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        controller = loader.getController();
        controller.setStore(store);
        setUPStore();
        draw_home();
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(String.valueOf(StoreManager.class.getResource("../controller/style.css")));
        primaryStage.setTitle("Warehouse manager");
        primaryStage.setScene(scene);
        new Thread(() -> store.main()).start();
        primaryStage.show();
    }

    /**
     * Initialize StoreManager
     */
    public void SetUpManager() {
        store = new Store();
        visited_indexes = new ArrayList<>();
        lock = new ReentrantLock();
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
     *
     * @param index index in GridPane where the good shall be displayed
     */
    public void setUPGoods(int index) {
        storePlan.getChildren().get(index).getStyleClass().add("full_shelve");
    }

    /**
     * Show that a shelve is free in GUI
     *
     * @param index index in GridPane where the good shall be displayed
     */
    public void freeShelve(int index) {
        storePlan.getChildren().get(index).getStyleClass().remove("full_shelve");
        storePlan.getChildren().get(index).getStyleClass().add("shelve");
    }


    /**
     * Create a string representing the style class
     *
     * @param status of the map
     * @return style class string
     */
    public String GetStyle(Store.MapCoordinateStatus status) {
        String style;
        switch (status) {
            case FORKLIFT_UP:
                style = "forklift_up";
                break;
            case FORKLIFT_DOWN:
                style = "forklift_down";
                break;
            case FORKLIFT_LEFT:
                style = "forklift_left";
                break;
            case FORKLIFT_RIGHT:
                style = "forklift_right";
                break;
            case FORKLIFTS_LEFT_RIGHT:
                style = "forklift_left_and_right";
                break;
            case FORKLIFTS_UP_DOWN:
                style = "forklift_up_and_down";
                break;
            default:
                style = "path";
                break;
        }
        return style;
    }


    /**
     * Free path that has been visited but now is free
     */
    public void FreeVisitedPath() {
        if (visited_indexes.size() == 0) {
            return;
        }
        for (Coordinates i : visited_indexes) {
            lock.lock();
            int map_value = store.getMapValue(i.getX(), i.getY());
            lock.unlock();
            System.out.println(map_value);
            if (map_value == 0) {
                int inner_index = i.getX() * store.getHeight() + i.getY();
                Store.MapCoordinateStatus status_inner = Store.MapCoordinateStatus.values()[map_value];
                String inner_style = GetStyle(status_inner);
                storePlan.getChildren().get(inner_index).getStyleClass().remove(inner_style);
                storePlan.getChildren().get(inner_index).getStyleClass().add("path");
                visited_indexes.remove(i);
            }
        }
    }


    /**
     * Draw forklift on the map
     *
     * @param coords of the forklift on the map
     * @param status of the forklift
     */
    public void draw_forklift(Coordinates coords, Store.MapCoordinateStatus status) {
        int index = coords.getX() * store.getHeight() + coords.getY();
        if (index == 0) {
            return;
        }
        visited_indexes.add(new Coordinates(coords.getX(), coords.getY()));
        String style = GetStyle(status);
        try {
            storePlan.getChildren().get(index).getStyleClass().remove("path");
            storePlan.getChildren().get(index).getStyleClass().add(style);
        } catch (Exception e) {

        }
    }

    /**
     * Draw home tile on the map
     */
    public void draw_home() {
        storePlan.getChildren().get(0).getStyleClass().remove("path");
        storePlan.getChildren().get(0).getStyleClass().add("home");
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
                    rec.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.pathClicked(mouseEvent));
                } else {
                    store.createShelve(shelve_id, i, j);
                    shelve_id++;
                    rec.getStyleClass().add("shelve");
                    rec.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.showGoodsContent(mouseEvent));
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
     *
     * @param msg Message to be displayed
     */
    public void logMessageTA(String msg) {
        controller.logMessage(msg);
    }
}
