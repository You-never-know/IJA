package company.store.shelve.goods;

import company.store.shelve.Shelve;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.Objects;

public class Goods {

    private String Name;
    private int Id;
    private double ItemWeight;
    private int Count;
    private Coordinates coordinates;
    private Shelve shelve;


    public Goods(String name, int id, double itemWeight, int count, int x, int y, Shelve shelve) {
        this.Name = name;
        this.Id = id;
        this.ItemWeight = itemWeight;
        this.Count = count;
        this.coordinates = new Coordinates(x, y);
        this.shelve = shelve;
    }

    public void sellItem(int sold) {
        // TODO < 0 ?
        this.Count = this.Count - sold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods goods = (Goods) o;
        return Id == goods.Id || Name.equals(goods.Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name);
    }

    public String getName() {
        return this.Name;
    }

    public int getId() {
        return this.Id;
    }

    public Shelve getShelve() {
        return this.shelve;
    }

    public double getItemWeight() {
        return this.ItemWeight;
    }

    public int getCount() {
        return this.Count;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}