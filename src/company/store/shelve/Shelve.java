package company.store.shelve;

import company.store.shelve.goods.Goods;

public class Shelve {

    private int id;
    private int[] coordinates;
    private ShelveStatus status;
    private Goods goods;

    public Shelve(int id, int[] coordinates) {

        this.id = id;
        this.coordinates = coordinates;
        this.status = ShelveStatus.FREE;
        this.goods = null;
    }

    public enum ShelveStatus {
        FREE,
        OCCUPIED
    }

    // TODO showGoods

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public void sellGoods(int count){
        this.goods.sellItem(count);
    }

    public int getGoodsId(){
        return this.goods.getId();
    }

    public int getGoodsCount(){
        return this.goods.getCount();
    }

    public int getGoodsWeight(){
        return this.goods.getItemWeight();
    }

    public int getId() {
        return id;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public ShelveStatus getStatus() {
        return this.status;
    }

    public Goods getGoods() {
        return goods;
    }
}