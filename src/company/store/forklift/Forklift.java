package company.store.forklift;

import company.store.request.action.Action;
import company.store.shelve.goods.Goods;
import company.store.request.Request;
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

    public int[] getCoordinates() {
        return coordinates;
    }

    public int[][] getPath() {
        return path;
    }

    /* TODO A star baby
    public int[][] countPath(){
        ;
    }
    */

    public void doAction(){
        Action action = this.request.popFirstActionsList();
        // TODO + how to GUI?
        this.request.pushActionsDoneList(action);
    }
}