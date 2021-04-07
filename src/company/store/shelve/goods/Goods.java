package company.store.shelve.goods;

import company.store.shelve.Shelve;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.Objects;

public class Goods {

    private String name;
    private int id;
    private double itemWeight;
    private int count;
    private Coordinates coordinates;
    private Shelve shelve;


    public Goods(String name, int id, double itemWeight, int count, int x, int y, Shelve shelve) {
        this.name = name;
        this.id = id;
        this.itemWeight = itemWeight;
        this.count = count;
        this.coordinates = new Coordinates(x, y);
        this.shelve = shelve;
    }

    public void sellItem(int sold) {
        // TODO < 0 ?
        this.count = this.count - sold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods goods = (Goods) o;
        return id == goods.id || name.equals(goods.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public Shelve getShelve() {
        return this.shelve;
    }

    public double getItemWeight() {
        return this.itemWeight;
    }

    public int getCount() {
        return this.count;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}