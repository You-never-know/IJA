package controller;

import company.store.Store;
import company.store.shelve.goods.Goods;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class Controller {

    private Store store;

    public void SetStore(Store store) {
        this.store = store;
    }

    public void ShowGoodsContent (MouseEvent event) {
        System.out.println(event.getX());
    }

    @FXML
    private TableColumn<?, ?> shoping_list_goods;

    @FXML
    private TableColumn<?, ?> selected_name;

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
    private TableColumn<?, ?> shoping_list_count;

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

    }

    @FXML
    void submit_shoping_list(MouseEvent event) {

    }

    @FXML
    void clear_shoping_list(MouseEvent event) {

    }

    @FXML
    void add_barrier(MouseEvent event) {

    }

    @FXML
    void remove_barrier(MouseEvent event) {

    }


}
