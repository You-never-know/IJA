package company.store.shelve.goods;

import company.store.shelve.Shelve;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.Objects;

/**
 * Class for storing store goods
 */
public class Goods {

    private String name;
    private int id;
    private double itemWeight;
    private int Count;
    private Coordinates coordinates;
    private Shelve shelve;


    /**
     * @param name Goods class instance name
     * @param id Goods class instance id
     * @param itemWeight Goods class instance item weight
     * @param count Goods class instance item count
     * @param x int number to be set as x-coordinate of the Goods Coordinates
     * @param y int number to be set as y-coordinate of the Goods Coordinates
     * @param shelve shelve storing this Goods class instance
     */
    public Goods(String name, int id, double itemWeight, int count, int x, int y, Shelve shelve) {
        this.name = name;
        this.id = id;
        this.itemWeight = itemWeight;
        this.Count = count;
        this.coordinates = new Coordinates(x, y);
        this.shelve = shelve;
    }

    /**
     * @param goods goods to be set as new instance
     */
    public Goods(Goods goods) {
        this.name = goods.getName();
        this.id = goods.getId();
        this.itemWeight = goods.getItemWeight();
        this.Count = goods.getCount();
        this.coordinates = goods.getCoordinates();
        this.shelve = goods.getShelve();
    }

    /**
     * @param o Goods object for comparison with this Goods class instance
     * @return boolean value corresponding to equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods goods = (Goods) o;
        return id == goods.id || name.equals(goods.name);
    }

    /**
     * @return hash value of the Goods class instance object name
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * @return Goods class instance name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Goods class instance id
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return shelve storing this Goods class instance
     */
    public Shelve getShelve() {
        return this.shelve;
    }

    /**
     * @return weight of goods
     */
    public double getItemWeight() {
        return this.itemWeight;
    }

    /**
     * @return count of goods
     */
    public int getCount() {
        return this.Count;
    }

    /**
     * @param count new count of goods
     */
    public void setCount(int count) {this.Count = count; }

    /**
     * @return coordinates where this Goods class instance is located
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @param coordinates coordinates to be set as location of this Goods class instance
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}