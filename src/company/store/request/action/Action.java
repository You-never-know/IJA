package company.store.request.action;
import company.store.shelve.goods.Goods;
public class Action {

    private Goods goods;
    private int count;


    public Action(Goods goods, int count) {

        this.goods = goods;
        this.count = count;
    }

    public Goods getGoods() {
        return goods;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
