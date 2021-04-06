package company.store.shelve.goods.coordinates;

import java.lang.Math;
import java.util.Objects;

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

    public void printCoordinates() {
        System.out.println(this.getX() + ", " + this.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getMoveValue(Coordinates origin) {
        return Math.abs(this.x - origin.getX()) + Math.abs(this.y - origin.getY());
    }

    public double getCostValue(Coordinates destination) {
        return (Math.sqrt(Math.pow(Math.abs(this.x - destination.getX()), 2) + Math.pow(Math.abs(this.y - destination.getY()), 2)));
    }

    public double getHeuristicValue(Coordinates coordinates) {
        return this.getMoveValue(coordinates) + this.getCostValue(coordinates);
    }
}