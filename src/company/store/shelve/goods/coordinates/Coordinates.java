package company.store.shelve.goods.coordinates;

import java.lang.Math;

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getMoveValue (Coordinates origin){
        return this.x - origin.getX() + this.y - origin.getY();
    }

    public double getCostValue (Coordinates destination){
        return (Math.sqrt(Math.pow(this.x - destination.getX(),2) + Math.pow(this.y - destination.getY(),2)));
    }

    public double getHeuristicValue(Coordinates coordinates){
        return this.getMoveValue(coordinates)+this.getCostValue(coordinates);
    }
}