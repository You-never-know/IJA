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

    public void SetStore(Store store) {
        this.store = store;
    }

    private ArrayList<Action> action_list = new ArrayList<>();

    public void ShowGoodsContent (MouseEvent event) {
       Node rect = event.getPickResult().getIntersectedNode();
       Integer x = GridPane.getColumnIndex(rect);
       Integer y = GridPane.getRowIndex(rect);
       Shelve shelve = store.get_shelve(x,y);
       if (shelve == null) {
           return;
       }
       Goods goods = shelve.getGoods();
       selected_table.getItems().clear();
       if (goods != null) {
           selected_table.getItems().add(goods);
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
    void load_map(MouseEvent event) {
        load_map_button.setDisable(true);
        String m_path = map_path.getText();
        store.getManager().set_map_path(m_path);
        store.getManager().setUP_Store();
        load_map_button.setDisable(false);
    }

    @FXML
    void load_goods(MouseEvent event) {
        load_goods_button.setDisable(true);
        String g_path = goods_path.getText();
        store.getManager().set_goods_path(g_path);
        g_path = store.getManager().get_goods_path();
        store.setGoods(g_path);
        load_goods_button.setDisable(false);
    }

    @FXML
    void add_item_to_list(MouseEvent event) {
        add_item_button.setDisable(true);
        String count = add_goods_count.getText();
        int c;
        try {
            c = Integer.parseInt(count.strip());
        } catch (NumberFormatException e) {
            System.out.println("Wrong count given");
            logMessage("Wrong count given");
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
            Shelve shelve = store.get_goods_shelve(action);
            if (shelve == null) {
                logMessage("Item is not in warehouse, can not be added to shopping list");
                add_item_button.setDisable(false);
                return;
            }
            good = shelve.getGoods().getName();
        }
        action = new Action(good, c);
        if (store.get_goods_shelve(action) == null) {
            logMessage("Item is not in warehouse, can not be added to shopping list");
            add_item_button.setDisable(false);
            return;
        }
        action_list.add(action);
        shopping_list.getItems().add(action);
        add_item_button.setDisable(false);
    }

    @FXML
    void submit_shopping_list(MouseEvent event) {
        submit_list_button.setDisable(true);
        Request request = new Request(action_list);
        store.add_request(request);
        action_list.clear();
        shopping_list.getItems().clear();
        submit_list_button.setDisable(false);
    }

    @FXML
    void clear_shopping_list(MouseEvent event) {
        clear_list_button.setDisable(true);
        action_list.clear();
        shopping_list.getItems().clear();
        clear_list_button.setDisable(false);
    }

    @FXML
    void add_barrier(MouseEvent event) {

    }

    @FXML
    void remove_barrier(MouseEvent event) {

    }

    @FXML
    public void logMessage(String msg){

        if(log_label.getText().isEmpty() || log_label.getText().isBlank()){
            log_label.setText(msg);
        }
        else{
            log_label.setText( msg + "\n" + log_label.getText());
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
    }
}
