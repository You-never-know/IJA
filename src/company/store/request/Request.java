package company.store.request;

import company.store.shelve.goods.Goods;
import company.store.request.action.Action;
import java.util.ArrayList;
import java.util.List;

public class Request {

    private int id;
    List<Action> actionsList;
    List<Action> actionsDoneList;

    public Request(int id, List<Action> actionsList) {
        this.id = id;
        this.actionsList = actionsList;
        this.actionsDoneList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Action popFirstActionsList() {
        Action action = this.actionsList.get(0);
        this.actionsList.remove(0);
        return action;
    }

    public void pushActionsDoneList(Action action) {
        this.actionsDoneList.add(action);
    }
}
