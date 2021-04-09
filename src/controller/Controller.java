package controller;

import company.store.Store;
import company.store.request.Request;
import company.store.request.action.Action;
import company.store.shelve.Shelve;
import company.store.shelve.goods.Goods;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Store store;
    private Node rect = null;
    private boolean addBarrierClicked;
    private boolean removeBarrierClicked;

    public void setStore(Store store) {
        this.store = store;
    }

    private ArrayList<Action> action_list = new ArrayList<>();

    public void showGoodsContent(MouseEvent event) {
        if (addBarrierClicked == true) {
            addBarrierClicked = false;
            logMessage("Cannot add barrier to the shelve");
        }
        else if ( removeBarrierClicked == true) {
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

    public void pathClicked(MouseEvent event) {
        if (rect != null) {
            rect.getStyleClass().remove("selected_shelve");
            selected_table.getItems().clear();
        }
        if (addBarrierClicked || removeBarrierClicked) {
            rect = event.getPickResult().getIntersectedNode();
            Integer x = GridPane.getColumnIndex(rect);
            Integer y = GridPane.getRowIndex(rect);
            if (addBarrierClicked) {
                rect.getStyleClass().clear();
                rect.getStyleClass().add("blocked");
                store.setMapValue(x,y, Store.MapCoordinateStatus.BLOCK);
                addBarrierClicked = false;
            }
            else {
                rect.getStyleClass().clear();
                rect.getStyleClass().add("path");
                store.setMapValue(x, y, Store.MapCoordinateStatus.PATH);
                removeBarrierClicked = false;
            }
        }

    }

    @FXML
    private TableView<Goods> selected_table = new TableView<>();

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

    @FXML
    void loadMap(MouseEvent event) {
        load_map_button.setDisable(true);
        String m_path = map_path.getText();
        store.getManager().setMapPath(m_path);
        store.getManager().setUPStore();
        load_map_button.setDisable(false);
    }

    @FXML
    void loadGoods(MouseEvent event) {
        load_goods_button.setDisable(true);
        String g_path = goods_path.getText();
        store.getManager().setGoodsPath(g_path);
        g_path = store.getManager().getGoodsPath();
        store.setGoods(g_path);
        load_goods_button.setDisable(false);
    }

    @FXML
    void addItemToList(MouseEvent event) {
        add_item_button.setDisable(true);
        String count = add_goods_count.getText();
        int c;
        try {
            c = Integer.parseInt(count.strip());
        } catch (NumberFormatException e) {
            logMessage("Wrong input data format");
            add_item_button.setDisable(false);
            return;
        }
        Action action;
        String good = add_goods_name.getText();
        Integer ID = 0;
        try {
            ID = Integer.parseInt(good.strip());
        } catch (Exception e) {
            ;
        }
        if (good.equals(String.valueOf(ID))) {
            action = new Action(ID, c);
            Shelve shelve = store.getGoodsShelve(action);
            if (shelve == null) {
                logMessage("Item is not in warehouse, can not be added to shopping list");
                add_item_button.setDisable(false);
                return;
            }
            good = shelve.getGoods().getName();
        }
        action = new Action(good, c);
        if (store.getGoodsShelve(action) == null) {
            logMessage("Item is not in warehouse, can not be added to shopping list");
            add_item_button.setDisable(false);
            return;
        } else if ((store.getGoodsCount(action)) < (c + actionListGoodsCount(action) + store.getGoodsRequestsListCount(action))) {
            logMessage("Sorry, only " + (store.getGoodsCount(action) - actionListGoodsCount(action) - store.getGoodsRequestsListCount(action))  + " more piece(s) available");
            add_item_button.setDisable(false);
            return;
        }
        int index = action_list.indexOf(action);
        if (index == -1) {
            action_list.add(action);
            Action copy = new Action(action);
            shopping_list.getItems().add(copy);
        }
        else {
            Action existing_action = action_list.get(index);
            existing_action.setCount(existing_action.getCount() + c);
            existing_action = shopping_list.getItems().get(index);
            existing_action.setCount(existing_action.getCount() + c);
            shopping_list.getItems().remove(index);
            shopping_list.getItems().add(existing_action);
        }
        add_item_button.setDisable(false);
    }

    public int actionListGoodsCount(Action action) {
        int count = 0;
        String name = action.getName();
        for (Action act: action_list) {
            if (act.getName().compareTo(name) == 0) {
                count += act.getCount();
            }
        }
        return count;
    }

    @FXML
    void submitShoppingList(MouseEvent event) {
        submit_list_button.setDisable(true);
        Request request = new Request(action_list);
        store.addRequest(request);
        action_list.clear();
        shopping_list.getItems().clear();
        submit_list_button.setDisable(false);
    }

    @FXML
    void clearShoppingList(MouseEvent event) {
        clear_list_button.setDisable(true);
        action_list.clear();
        shopping_list.getItems().clear();
        clear_list_button.setDisable(false);
    }

    @FXML
    void addBarrier(MouseEvent event) {
        add_barrier_button.setDisable(true);
        removeBarrierClicked = false;
        addBarrierClicked = true;
        add_barrier_button.setDisable(false);
    }

    @FXML
    void removeBarrier(MouseEvent event) {
        remove_barrier_button.setDisable(true);
        addBarrierClicked = false;
        removeBarrierClicked = true;
        remove_barrier_button.setDisable(false);
    }

    @FXML
    public void logMessage(String msg) {

        if (log_label.getText().isEmpty() || log_label.getText().isBlank()) {
            log_label.setText(msg);
        } else {
            log_label.setText(msg + "\n" + log_label.getText());
        }
    }

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
    }
}
