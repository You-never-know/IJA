package company.store.forklift;

import company.store.shelve.goods.Goods;
import company.store.request.Request;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Forklift {

    private int id;
    private Request request;
    List<Goods> goodsList;
    private int[] coordinates;
    private int [][] path;
    private int weightBearing;
    private int piecesBearing;


    public Forklift(int id, int[] coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }

}