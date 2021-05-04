package controller;

import company.store.Store;
import company.store.forklift.Forklift;
import company.store.request.Request;
import company.store.request.action.Action;
import company.store.shelve.Shelve;
import company.store.shelve.goods.Goods;
import company.store.shelve.goods.coordinates.Coordinates;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the GUI
 */
public class Controller implements Initializable {

    private Store store;
    private Node rect = null;
    private boolean addBarrierClicked;
    private boolean removeBarrierClicked;
    private double speed_of_time;
    private double max_speed;
    private double min_speed;

    /**
     * @param store where the actions will be performed
     */
    public void setStore(Store store) {
        this.store = store;
    }

    private ArrayList<Action> action_list = new ArrayList<>();

    /**
     * Show content on the selected shelve
     *
     * @param event Mouse click
     */
    public void showGoodsContent(MouseEvent event) {
        store.getManager().set_forklift(null);
        store.getManager().FreeForkliftPath();
        if (addBarrierClicked) {
            addBarrierClicked = false;
            logMessage("Cannot add barrier to the shelve");
        } else if (removeBarrierClicked) {
            removeBarrierClicked = false;
            logMessage("Cannot remove barrier from shelve");
        }
        if (rect != null) {
            rect.getStyleClass().remove("selected_shelve");
        }
        rect = event.getPickResult().getIntersectedNode();
        rect.getStyleClass().add("selected_shelve");
        Integer x = GridPane.getColumnIndex(rect);
        Integer y = GridPane.getRowIndex(rect);
        Shelve shelve = store.getShelve(x, y);
        if (shelve == null) {
            return;
        }
        Goods goods = shelve.getGoods();
        selected_table.getItems().clear();
        if (goods != null) {
            selected_table.getItems().add(goods);
        }
    }

    /**
     * Do an according action when path is clicked, set/remove barrier if option selected
     *
     * @param event Mouse clicked
     */
    public void pathClicked(MouseEvent event) {
        store.getManager().set_forklift(null);
        store.getManager().FreeForkliftPath();
        if (rect != null) {
            rect.getStyleClass().remove("selected_shelve");
            selected_table.getItems().clear();
        }
        if (addBarrierClicked || removeBarrierClicked) {
            rect = (GridPane) event.getPickResult().getIntersectedNode().getParent();
            Integer x = GridPane.getColumnIndex(rect);
            Integer y = GridPane.getRowIndex(rect);
            if (x <= 1 && y <= 1) {
                logMessage("Cannot add or remove barrier at the loading bay");
                addBarrierClicked = false;
                removeBarrierClicked = false;
                return;
            }
            if (addBarrierClicked) {
                if (store.getMapValue(x, y) == 0) {
                    store.setMapValue(x, y, Store.MapCoordinateStatus.BLOCK);
                    for (int i = 0; i < 25; i++) {
                    ((GridPane) rect).getChildren().get(i).getStyleClass().clear();
                    ((GridPane) rect).getChildren().get(i).getStyleClass().add("blocked");
                }
                } else {
                    logMessage("Cannot add barrier here");
                }
                addBarrierClicked = false;
            } else {
            	if (store.getMapValue(x, y) == 2) {
                	for (int i = 0; i < 25; i++) {
                	    	((GridPane) rect).getChildren().get(i).getStyleClass().clear();
                	    	((GridPane) rect).getChildren().get(i).getStyleClass().add("path");
               	}
                		store.setMapValue(x, y, Store.MapCoordinateStatus.FREE_PATH);
                		store.checkBlockedForklifts();
                } 
                removeBarrierClicked = false;
            }
        }

    }


    /**
     * Display path and load of the forklift
     * @param event Mouse clicked
     */
    public void forklift_clicked(MouseEvent event, Forklift forklift) {
        if (forklift == null) {
            return;
        }
        store.getManager().FreeForkliftPath();
        store.getManager().set_forklift(forklift);
        ArrayList<Coordinates> path = new ArrayList<>();
        path.addAll(forklift.getPath());
        if (path.size() > 0) {
            path.remove(path.size() - 1);
        }
        for (Coordinates c:path) {
            store.getManager().draw_path(c);
        }
        List<Goods> forklift_goods = forklift.getGoodsList();
        selected_table.getItems().clear();
        for (Goods item: forklift_goods) {
            selected_table.getItems().add(item);
        }
        selected_table.refresh();
    }

    @FXML
    public TableView<Goods> selected_table = new TableView<>();

    @FXML
    private TableColumn<Goods, String> selected_name = new TableColumn<>("Name");

    @FXML
    private TableColumn<Goods, Double> selected_weight = new TableColumn<>("Weight");

    @FXML
    private TableColumn<Goods, Integer> selected_count = new TableColumn<>("Count");

    @FXML
    private TableColumn<Goods, Integer> selected_id = new TableColumn<>("ID");

    @FXML
    private TextField map_path;

    @FXML
    private Button load_map_button;

    @FXML
    private TextField shopping_list_path;

    @FXML
    private Button load_shopping_list_button;

    @FXML
    private TextField add_goods_count;

    @FXML
    private Button add_item_button;

    @FXML
    private Button add_barrier_button;

    @FXML
    private Button submit_list_button;

    @FXML
    private Button load_goods_button;

    @FXML
    private Button clear_list_button;

    @FXML
    private TableView<Action> shopping_list = new TableView<>();

    @FXML
    private TableColumn<Action, String> shopping_list_goods = new TableColumn<>("Goods");

    @FXML
    private TableColumn<Action, Integer> shopping_list_count = new TableColumn<>("Count");

    @FXML
    private Button remove_barrier_button;

    @FXML
    private TextField goods_path;

    @FXML
    private TextField add_goods_name;

    @FXML
    private Button time_down;

    @FXML
    private Button time_up;

    @FXML
    private TextField time_label;

    @FXML
    private TextArea log_label;


    /**
     * Speed up the animations
     *
     * @param event Button clicked
     */
    @FXML
    void speedUP(MouseEvent event) {
        time_up.setDisable(true);
        if (speed_of_time < max_speed) {
            speed_of_time *= 2.0;
            time_label.setText(String.valueOf(speed_of_time) + "x");
            store.setSpeed_of_time(speed_of_time);
        } else {
            logMessage("Max speed reached");
        }
        time_up.setDisable(false);
    }

    /**
     * Slow down the animation
     *
     * @param event Button clicked
     */
    @FXML
    void slowDown(MouseEvent event) {
        time_down.setDisable(true);
        if (speed_of_time > min_speed) {
            speed_of_time /= 2.0;
            time_label.setText(String.valueOf(speed_of_time) + "x");
            store.setSpeed_of_time(speed_of_time);
        } else {
            logMessage("Minimum speed reached");
        }
        time_down.setDisable(false);
    }

    /**
     * Set a new selected map from a file given in GUI
     *
     * @param event Button clicked
     */
    @FXML
    void loadMap(MouseEvent event) {
        load_map_button.setDisable(true);
        String m_path = map_path.getText();
        store.getManager().setMapPath(m_path);
        store.getManager().setUPStore();
        load_map_button.setDisable(false);
    }

    /**
     * Load new goods from a file given in GUI
     *
     * @param event Mouse clicked
     */
    @FXML
    void loadGoods(MouseEvent event) {
        load_goods_button.setDisable(true);
        String g_path = goods_path.getText();
        store.getManager().setGoodsPath(g_path);
        g_path = store.getManager().getGoodsPath();
        store.setGoods(g_path);
        load_goods_button.setDisable(false);
    }

    /**
     * Load new shopping list from a file given in GUI
     *
     * @param event Mouse clicked
     */
    @FXML
    void loadShoppingList(MouseEvent event) {
        load_shopping_list_button.setDisable(true);
        String g_path = shopping_list_path.getText();
        store.loadShoppingList(g_path);
        load_shopping_list_button.setDisable(false);
    }

    /**
     * Add item to the shopping list, check if item is in store and if there are enough items
     *
     * @param event Mouse clicked
     */
    @FXML
    void addItemToList(MouseEvent event) {
        add_item_button.setDisable(true);
        String count_string = add_goods_count.getText().strip();
        String good = add_goods_name.getText().strip();
        Action action;
        int count;
        try {
            count = Integer.parseInt(count_string);
        } catch (NumberFormatException e) {
            logMessage("Wrong input data format");
            add_item_button.setDisable(false);
            return;
        }
        if (count < 1) {
            logMessage("Incorrect count given");
            add_item_button.setDisable(false);
            return;
        }
        int ID = 0;
        try {
            ID = Integer.parseInt(good);
        } catch (Exception e) {
            ;
        }
        if (ID != 0) {
            action = new Action(ID, count);
        } else {
            action = new Action(good, count);
        }
        Shelve shelve = store.getGoodsShelve(action);
        if (shelve == null) {
            logMessage("Item is not in warehouse, unable to add to shopping list");
            add_item_button.setDisable(false);
            return;
        }
        action.setId(shelve.getGoodsId());
        action.setName(shelve.getGoods().getName());

        if ((store.getGoodsCount(action)) < (count +

                actionListGoodsCount(action, action_list) + store.getGoodsRequestsListCount(action) + store.getGoodsInForkliftsCount(action))) {
            logMessage("Sorry, only " + (store.getGoodsCount(action) - actionListGoodsCount(action, action_list) - store.getGoodsRequestsListCount(action) - store.getGoodsInForkliftsCount(action)) + " more piece(s) available");
            add_item_button.setDisable(false);
            return;
        }

        int index = action_list.indexOf(action);
        if (index == -1) {
            action_list.add(action);
            Action copy = new Action(action);
            shopping_list.getItems().add(copy);
        } else {
            Action existing_action = action_list.get(index);
            existing_action.setCount(existing_action.getCount() + count);
            // -----------------------------------------------------------
            existing_action = shopping_list.getItems().get(index);
            existing_action.setCount(existing_action.getCount() + count);
            //----------------------------------------------------------
            shopping_list.refresh();
        }
        add_item_button.setDisable(false);
    }

    /**
     * Count all items with the same name as in action, that are already in the shopping list
     *
     * @param action action containing a name of the good
     * @return number of items already requested
     */
    public int actionListGoodsCount(Action action, ArrayList<Action> act_list) {
        int count = 0;
        String name = action.getName();
        for (Action act : act_list) {
            if (act.getName().compareTo(name) == 0) {
                count += act.getCount();
            }
        }
        return count;
    }

    /**
     * Send the shopping list to the store as a request
     *
     * @param event Button clicked
     */
    @FXML
    void submitShoppingList(MouseEvent event) {
        if (action_list.size() == 0) {
            return;
        }
        submit_list_button.setDisable(true);
        Request request = new Request(action_list);
        store.addRequest(request);
        action_list.clear();
        shopping_list.getItems().clear();
        submit_list_button.setDisable(false);
        System.out.println("ShoppingList submitted");
    }

    /**
     * Clear the shopping list
     *
     * @param event Button clicked
     */
    @FXML
    void clearShoppingList(MouseEvent event) {
        clear_list_button.setDisable(true);
        action_list.clear();
        shopping_list.getItems().clear();
        clear_list_button.setDisable(false);
    }

    /**
     * Make next click on the map add a barrier
     *
     * @param event Button clicked
     */
    @FXML
    void addBarrier(MouseEvent event) {
        add_barrier_button.setDisable(true);
        removeBarrierClicked = false;
        addBarrierClicked = true;
        add_barrier_button.setDisable(false);
    }

    /**
     * Make next click on the map remove a barrier
     *
     * @param event Button clicked
     */
    @FXML
    void removeBarrier(MouseEvent event) {
        remove_barrier_button.setDisable(true);
        addBarrierClicked = false;
        removeBarrierClicked = true;
        remove_barrier_button.setDisable(false);
    }

    /**
     * Display an error message in GUI
     *
     * @param msg Message that will be displayed
     */
    @FXML
    public void logMessage(String msg) {

        if (log_label.getText().isEmpty() || log_label.getText().isBlank()) {
            log_label.setText(msg);
        } else {
            log_label.setText(msg + "\n" + log_label.getText());
        }
    }

    /**
     * Initialize the controller
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selected_id.setCellValueFactory(new PropertyValueFactory<>("Id"));
        selected_count.setCellValueFactory(new PropertyValueFactory<>("Count"));
        selected_weight.setCellValueFactory(new PropertyValueFactory<>("ItemWeight"));
        selected_name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        shopping_list_goods.setCellValueFactory(new PropertyValueFactory<>("Name"));
        shopping_list_count.setCellValueFactory(new PropertyValueFactory<>("Count"));
        addBarrierClicked = false;
        removeBarrierClicked = false;
        speed_of_time = 1.0;
        max_speed = 32.0;
        min_speed = 1.0 / 32.0;
    }
}
