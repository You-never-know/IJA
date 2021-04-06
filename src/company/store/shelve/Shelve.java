package company.store.shelve;

import company.store.shelve.goods.Goods;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.Objects;

public class Shelve {

    private int id;
    private Coordinates coordinates;
    private ShelveStatus status;
    private Goods goods;

    public Shelve(int id, int x, int y) {
        this.id = id;
        this.coordinates = new Coordinates(x,y);
        this.status = ShelveStatus.FREE;
        this.goods = null;
    }

    public enum ShelveStatus {
        FREE,
        OCCUPIED
    }

    // TODO showGoods


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelve shelve = (Shelve) o;
        return id == shelve.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
        this.status = ShelveStatus.OCCUPIED;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void sellGoods(int count) {
        this.goods.sellItem(count);
    }

    public int getGoodsId() {
        return this.goods.getId();
    }

    public int getGoodsCount() {
        return this.goods.getCount();
    }

    public double getGoodsWeight() {
        return this.goods.getItemWeight();
    }

    public int getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ShelveStatus getStatus() {
        return this.status;
    }

    public Goods getGoods() {
        return goods;
    }
}