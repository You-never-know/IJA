package company.store.forklift;

import company.store.request.action.Action;
import company.store.shelve.goods.Goods;
import company.store.request.Request;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.List;

public class Forklift {

    private int id;
    private Request request;
    private List<Goods> goodsList;
    private Coordinates coordinates;
    private List<Coordinates> path;
    private int weightBearing;
    private int piecesBearing;
    private ForkliftStatus status;

    public enum ForkliftStatus{
        TOP(3), BOTTOM(4), LEFT(5), RIGHT(6); //TODO 8 , 9, 10 bad numbers

        private int Val;

        ForkliftStatus(int Val) {
            this.Val = Val;
        }

        public int getNumVal() {
            return Val;
        }
    }

    public Forklift(int id, int x, int y) {
        this.id = id;
        this.coordinates = new Coordinates(x,y);
        this.status = null;
    }

    public List<Coordinates> getPath() {
        return path;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setXCoordinate(int x){
        this.coordinates.setX(x);
    }

    public void setYCoordinate(int y){
        this.coordinates.setY(y);
    }

    public void setStatus(ForkliftStatus status) {
        this.status = status;
    }

    public ForkliftStatus getStatus() {
        return status;
    }

    /* TODO A star baby
    public List<Coordinates> countPath(){
        ;
    }
    */

    public void doAction(){
        Action action = this.request.popFirstActionsList();
        // TODO + how to GUI?
        this.request.pushActionsDoneList(action);
    }
}