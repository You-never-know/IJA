package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Controller {

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
    private TextField add_good_count;

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
    private TextField add_good_name;

    @FXML
    void load_map(ActionEvent event) {

    }

    @FXML
    void load_goods(ActionEvent event) {

    }

    @FXML
    void add_item_to_list(ActionEvent event) {

    }

    @FXML
    void submit_shoping_list(ActionEvent event) {

    }

    @FXML
    void clear_shoping_list(ActionEvent event) {

    }

    @FXML
    void add_barrier(ActionEvent event) {

    }

    @FXML
    void remove_barrier(ActionEvent event) {

    }

}
