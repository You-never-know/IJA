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
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
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
     * Create forklift square inside the given tile on the given index
     * @param tile where the forklift will be drawn
     * @param index inside the tile
     */
    public void set_up_forklift_tile(GridPane tile, int index) {
        tile.getChildren().get(index).getStyleClass().clear();
        tile.getChildren().get(index).removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.pathClicked(mouseEvent));
        tile.getChildren().get(index).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.forklift_clicked(mouseEvent));
        tile.getChildren().get(index).getStyleClass().add("forklift");
    }


    /**
     * Draw a forklift facing up
     * @param index on the grid pane
     */
    public void draw_up(int index) {
        GridPane tile = (GridPane) storePlan.getChildren().get(index);
        int[] indexes = {15,16,17,20,21,22};
        for (int i:indexes) {
            set_up_forklift_tile(tile,i);
        }
    }


    /**
     * Draw a forklift facing down
     * @param index on the grid pane
     */
    public void draw_down(int index) {
        GridPane tile = (GridPane) storePlan.getChildren().get(index);
        int[] indexes = {2,3,4,7,8,9};
        for (int i:indexes) {
            set_up_forklift_tile(tile,i);
        }
    }

    /**
     * Draw a forklift facing left
     * @param index on the grid pane
     */
    public void draw_left(int index) {
        GridPane tile = (GridPane) storePlan.getChildren().get(index);
        int[] indexes = {0,1,5,6,10,11};
        for (int i:indexes) {
            set_up_forklift_tile(tile,i);
        }
    }

    /**
     * Draw a forklift facing right
     * @param index on the grid pane
     */
    public void draw_right(int index) {
        GridPane tile = (GridPane) storePlan.getChildren().get(index);
        int[] indexes = {13,14,18,19,23,24};
        for (int i:indexes) {
            set_up_forklift_tile(tile,i);
        }
    }

    /**
     * Clear the tile
     * @param index on the grid pane
     */
    private void clear(int index) {
        GridPane tile = (GridPane) storePlan.getChildren().get(index);
        for (int i = 0; i < 25; i++) {
            tile.getChildren().get(i).getStyleClass().clear();
            tile.getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.forklift_clicked(mouseEvent));
            tile.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.pathClicked(mouseEvent));
            tile.getChildren().get(i).getStyleClass().add("path");
        }
    }


    /**
     * Create a string representing the style class
     *
     * @param status of the map
     */
    public void drawTile(Store.MapCoordinateStatus status, int index) {
        switch (status) {
            case FORKLIFT_UP:
                draw_up(index);
                break;
            case FORKLIFT_DOWN:
                draw_down(index);
                break;
            case FORKLIFT_LEFT:
                draw_left(index);
                break;
            case FORKLIFT_RIGHT:
                draw_right(index);
                break;
            case FORKLIFTS_LEFT_RIGHT:
                draw_left(index);
                draw_right(index);
                break;
            case FORKLIFTS_UP_DOWN:
                draw_up(index);
                draw_down(index);
                break;
            default:
                clear(index);
                break;
        }
    }


    /**
     * Free path that has been visited but now is free
     */
    public void FreeVisitedPath() {
        if (visited_indexes.size() == 0) {
            return;
        }
        for (Coordinates i : visited_indexes) {
            int map_value = store.getMapValue(i.getX(), i.getY());
            if (map_value == 0) {
                int inner_index = i.getX() * store.getHeight() + i.getY();
                clear(inner_index);
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
        drawTile(status, index);
    }

    /**
     * Draw home tile on the map
     */
    public void draw_home() {
        int number_of_sub_grids = 25;
        for (int i = 0; i < number_of_sub_grids; i++) {
            GridPane home = (GridPane)storePlan.getChildren().get(0);
            home.getChildren().get(i).getStyleClass().remove("path");
            home.getChildren().get(i).getStyleClass().add("home");
        }
    }


    private GridPane createPath() {
        GridPane path = new GridPane();
        Pane pane = (Pane) root.getItems().get(0);
        int number_of_rows = 5;
        int number_of_cols = 5;
        NumberBinding rects_height = Bindings.max(pane.heightProperty(), 0);
        NumberBinding rects_width = Bindings.max(0, pane.widthProperty());
        NumberBinding width = rects_width.divide((double) store.getWidth()).divide((double)number_of_cols-0.01).subtract(1.0);
        NumberBinding height = rects_height.divide((double) store.getHeight()).divide((double)number_of_rows-0.01).subtract(1.0);
        for (int col = 0; col < number_of_cols; col++) {
            for (int row = 0; row < number_of_rows; row++) {
                Rectangle rec = new Rectangle(20, 40);
                rec.widthProperty().bind(width);
                rec.heightProperty().bind(height);
                rec.getStyleClass().add("path");
                rec.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.pathClicked(mouseEvent));
                path.addColumn(col, rec);
            }
        }
        return path;
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
        NumberBinding width = rects_width.divide((double) w).subtract(1);
        NumberBinding height = rects_height.divide((double) h).subtract(1);
        int shelve_id = 1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (store.getMapValue(i, j) == 0) {
                    GridPane path = createPath();
                    storePlan.addColumn(i, path);
                } else {
                    Rectangle rec = new Rectangle(20, 40);
                    rec.widthProperty().bind(width);
                    rec.heightProperty().bind(height);
                    store.createShelve(shelve_id, i, j);
                    shelve_id++;
                    rec.getStyleClass().add("shelve");
                    rec.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.showGoodsContent(mouseEvent));
                    storePlan.addColumn(i, rec);
                }
            }
        }
        // remove an old map
        pane.getChildren().remove(0);
        pane.getChildren().add(storePlan);
        draw_home();
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
