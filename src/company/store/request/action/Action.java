package company.store.request.action;

public class Action {

    private String name;
    private Integer ID;
    private int count;


    public Action (int ID, int count) {
        this.ID = ID;
        this.name = "";
        this.count = count;
    }

    public Action(String goods, int count) {
        this.name = goods;
        this.count = count;
        this.ID = -1;
    }

    public String getName() {
        return name;
    }

    public Integer getID() { return ID; }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
