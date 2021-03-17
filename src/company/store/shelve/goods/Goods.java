package company.store.shelve.goods;

import company.store.shelve.goods.coordinates.Coordinates;

public class Goods {

    private String name;
    private int id;
    private int itemWeight;
    private int count;
    private Coordinates coordinates;


    public Goods(String name, int id, int itemWeight, int count, int x, int y) {
        this.name = name;
        this.id = id;
        this.itemWeight = itemWeight;
        this.count = count;
        this.coordinates = new Coordinates(x,y);
    }

    public void sellItem(int sold) {
        // TODO < 0 ?
        this.count = this.count - sold;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getItemWeight() {
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