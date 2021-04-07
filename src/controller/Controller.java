package controller;

import company.store.Store;
import company.store.request.Request;
import company.store.request.action.Action;
import company.store.shelve.Shelve;
import company.store.shelve.goods.Goods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class Controller {

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
       if (goods == null) {
            ;//selected_name.g;
       }
       else {
            ;//selected_name.setCellValueFactory();
       }


    }

    @FXML
    private TableColumn<?, ?> shopping_list_goods;

    @FXML
    private TableColumn<Goods, String> selected_name;


    @FXML
    private TableColumn<?, ?> selected_weight;

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
    private TableColumn<?, ?> shopping_list_count;

    @FXML
    private TableView<?> selected_table;

    @FXML
    private Button remove_barrier_button;

    @FXML
    private TextField goods_path;

    @FXML
    private TableColumn<?, ?> selected_count;

    @FXML
    private TableColumn<?, ?> selected_id;

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
        String m_path = map_path.getText();
        store.getManager().set_map_path(m_path);
        store.getManager().setUP_Store();
    }

    @FXML
    void load_goods(MouseEvent event) {
        String g_path = goods_path.getText();
        store.getManager().set_goods_path(g_path);
        g_path = store.getManager().get_goods_path();
        System.out.println(g_path);
        store.setGoods(g_path);
    }

    @FXML
    void add_item_to_list(MouseEvent event) {
        String count = add_goods_count.getText();
        int c;
        try {
            c = Integer.parseInt(count.strip());
        } catch (NumberFormatException e) {
            System.out.println("Wrong count given");
            logMessage("Wrong count given");
            return;
        }
        Action action;
        String good = add_goods_name.getText();
        Integer ID = 0;
        try {
            ID = Integer.parseInt(good.strip());
        } catch (Exception e) {
            action = new Action(good.strip(),c);
            System.out.println(good);
            action_list.add(action);
            System.out.println(action.getName());
            System.out.println(action.getID());
            return;
        }
        action = new Action(ID, c);
        System.out.println(ID);
        action_list.add(action);
        System.out.println(action.getName());
        System.out.println(action.getID());
    }

    @FXML
    void submit_shopping_list(MouseEvent event) {
        Request request = new Request(action_list);
        store.add_request(request);
        action_list.clear();
    }

    @FXML
    void clear_shopping_list(MouseEvent event) {

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
}
