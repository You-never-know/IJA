package company.store.request;

import company.store.shelve.goods.Goods;
import company.store.request.action.Action;
import java.util.ArrayList;
import java.util.List;

public class Request {

    List<Action> actionsList;
    List<Action> actionsDoneList;

    public Request(List<Action> actionsList) {
        this.actionsList = actionsList;
        this.actionsDoneList = new ArrayList<>();
    }

    public Action popFirstActionsList() {
        Action action = this.actionsList.get(0);
        this.actionsList.remove(0);
        return action;
    }

    public List<Action> getActionsList() {
        return actionsList;
    }

    public void pushActionsDoneList(Action action) {
        this.actionsDoneList.add(action);
    }
}
