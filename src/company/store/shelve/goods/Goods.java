package company.store.shelve.goods;


// TODO Refactor as Interface? -> .class ?
public class Goods {

    private String name;
    private int id;
    private int itemWeight;
    private int count;
    private int[] coordinates;


    public Goods(String name, int id, int itemWeight, int count, int[] coordinates) {
        this.name = name;
        this.id = id;
        this.itemWeight = itemWeight;
        this.count = count;
        this.coordinates = coordinates;
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

    public int[] getCoordinates() {
        return coordinates;
    }

}