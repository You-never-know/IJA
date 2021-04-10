package company.store.shelve.goods.coordinates;

import java.lang.Math;
import java.util.Objects;

/**
 *  Class for storing coordinates according to warehouse map
 */
public class Coordinates {
    private int x;
    private int y;

    /**
     * @param x int number to be set as x-coordinate of the Coordinates class instance
     * @param y int number to be set as y-coordinate of the Coordinates class instance
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x-coordinate of the Coordinates class instance
     */
    public int getX() {
        return x;
    }

    /**
     * @return y-coordinate of the Coordinates class instance
     */
    public int getY() {
        return y;
    }

    /**
     * @param x int number to be set as x-coordinate of the Coordinates class instance
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @param y int number to be set as y-coordinate of the Coordinates class instance
     */
    public void setY(int y) {
        this.y = y;
    }

    public void printCoordinates() {
        System.out.println(this.getX() + ", " + this.getY());
    }

    /**
     * @param o Coordinates object for comparison with this Coordinates class instance
     * @return boolean value corresponding to equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    /**
     * @return Hash value of the Coordinates class instance object
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * @param origin Coordinates of the start position
     * @return Move value of the A* algorithm for start and current position
     */
    public int getMoveValue(Coordinates origin) {
        return Math.abs(this.x - origin.getX()) + Math.abs(this.y - origin.getY());
    }

    /**
     * @param destination Coordinates of the final position
     * @return Cost value of the A* algorithm for start and final position
     */
    public double getCostValue(Coordinates destination) {
        return (Math.sqrt(Math.pow(Math.abs(this.x - destination.getX()), 2) + Math.pow(Math.abs(this.y - destination.getY()), 2)));
    }

    /**
     * @param coordinates Coordinates for computing the heuristic value
     * @return Heuristic value of the A* algorithm for start and final position
     */
    public double getHeuristicValue(Coordinates coordinates) {
        return this.getMoveValue(coordinates) + this.getCostValue(coordinates);
    }
}