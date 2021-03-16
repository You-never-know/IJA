// TODO HTH include goods?
// TODO Refactor as Interface?
public class Shelve {

    private int id;
    private int[] coordinates;
    private ShelveStatus status;
    public Goods goods;


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