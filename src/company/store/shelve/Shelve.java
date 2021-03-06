package company.store.shelve;
/**
 * @author xmarek72
 * @author xnepra01
 */

import company.store.Store;
import company.store.shelve.goods.Goods;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.Objects;

/**
 * Class to store Goods and manipulate with them
 */
public class Shelve {

    private int id;
    private Coordinates coordinates;
    private ShelveStatus status;
    private Goods goods;
    private Store store;

    /**
     * @param id the ID of the shelve, it is unique for all shelves in one store
     * @param x  x-coordinate of the shelve in the store map
     * @param y  y-coordinate of the shelve in the store map
     */
    public Shelve(int id, int x, int y, Store store) {
        this.id = id;
        this.coordinates = new Coordinates(x, y);
        this.status = ShelveStatus.FREE;
        this.goods = null;
        this.store = store;
    }

    /**
     * Status to state if the shelve has goods on it
     */
    public enum ShelveStatus {
        FREE,
        OCCUPIED
    }


    /**
     * Set two shelves as equal when their ID's match
     *
     * @param o object that will be compared with current object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelve shelve = (Shelve) o;
        return id == shelve.id;
    }

    /**
     * @return the hashCode for the shelve
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Add goods to the shelve
     *
     * @param goods the goods that will be placed on this shelve
     */
    public void setGoods(Goods goods) {
        this.goods = goods;
        this.status = ShelveStatus.OCCUPIED;
    }

    /**
     * Removes goods from the shelve and empty goods from the store
     */
    public void removeGoods() {
        store.removeEmptyGoods();
        status = ShelveStatus.FREE;
        goods = null;
    }

    /**
     * @param coordinates where the shelve is located
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Sell wanted number of items to the customer, if all goods are sold, remove goods from this shelve
     *
     * @param count of items to sell
     * @return number of wanted goods if there is enough items left, null if the count is higher than items count
     */
    public Goods sellGoods(int count) {
        if (count > getGoodsCount()) {
            return null;
        }
        try {
            this.goods.setCount(goods.getCount() - count);
            Goods sold_goods = new Goods(this.goods);
            sold_goods.setCount(count);
            if (getGoodsCount() == 0) {
                removeGoods();
            }
            return sold_goods;
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * @return ID of the current Good, or -1 if shelve does not have any goods on it
     */
    public int getGoodsId() {
        if (goods != null) {
            return this.goods.getId();
        }
        return -1;
    }


    /**
     * @return number of goods or 0 if shelve has no goods on it
     */
    public int getGoodsCount() {
        if (goods != null) {
            return this.goods.getCount();
        }
        return 0;
    }

    /**
     * @return weight of one item or 0.0 if there are no goods on the shelve
     */
    public double getGoodsWeight() {
        if (goods != null) {
            return this.goods.getItemWeight();
        }
        return 0.0;
    }

    /**
     * @return coordinates of this shelve
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return status of the shelve (EMPTY, OCCUPIED)
     */
    public ShelveStatus getStatus() {
        return this.status;
    }

    /**
     * @return goods that are on the shelve
     */
    public Goods getGoods() {
        return goods;
    }
}