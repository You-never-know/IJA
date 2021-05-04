package company.store.request.action;
/**
 * @author xnepra01
 */
import java.util.Objects;

/**
 * Class for storing actions of requests
 */
public class Action {

    private String name;
    private Integer id;
    private int count;

    /**
     * @param action action to be set as new instance
     */
    public Action (Action action) {
        this.id = action.getId();
        this.name = action.getName();
        this.count = action.getCount();
    }


    /**
     * @param id int value to be set as id of the Action class instance object
     * @param count int value to be set as count of the Action class instance object
     */
    public Action (int id, int count) {
        this.id = id;
        this.name = "";
        this.count = count;
    }

    /**
     * @param goods string to be set as name of the Action class instance object
     * @param count int value to be set as count of the Action class instance object
     */
    public Action(String goods, int count) {
        this.name = goods;
        this.count = count;
        this.id = -1;
    }

    /**
     * @param o Action object for comparison with this Action class instance
     * @return boolean value corresponding to equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return name.equals(action.name) && id.equals(action.id);
    }

    /**
     * @return hash value of the Action class instance object name and id
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    /**
     * @return name of goods bonded to action
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of goods bonded to action
     */
    public void setName(String name) { this.name = name; }

    /**
     * @return id of goods bonded to action
     */
    public Integer getId() { return id; }

    /**
     * @param ID id of goods bonded to action
     */
    public void setId(int ID) { this.id = ID; }

    /**
     * @return count of goods bonded to action
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count int value to be set as count of the Action class instance object
     */
    public void setCount(int count) {
        this.count = count;
    }
}
