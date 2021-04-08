package company.store.request.action;

public class Action {

    private String Name;
    private Integer ID;
    private int Count;

    public Action (Action action) {
        this.ID = action.getID();
        this.Name = action.getName();
        this.Count = action.getCount();
    }


    public Action (int ID, int count) {
        this.ID = ID;
        this.Name = "";
        this.Count = count;
    }

    public Action(String goods, int count) {
        this.Name = goods;
        this.Count = count;
        this.ID = -1;
    }

    public String getName() {
        return Name;
    }

    public Integer getID() { return ID; }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        this.Count = count;
    }
}
