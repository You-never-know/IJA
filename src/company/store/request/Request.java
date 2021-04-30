package company.store.request;

import company.store.request.action.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing requests
 */
public class Request {

    List<Action> actionsList;
    List<Action> actionsDoneList;

    /**
     * @param actionsList list of actions to be done in this request
     */
    public Request(List<Action> actionsList) {
        this.actionsList = new ArrayList<>(actionsList);
        this.actionsDoneList = new ArrayList<>();
    }

    /**
     * @return action from the top of the actionsList with its removing from the list
     */
    public Action popFirstActionsList() {
        Action action = this.actionsList.get(0);
        this.actionsList.remove(0);
        return action;
    }

    /**
     * @return actionsList - list of actions to be done
     */
    public List<Action> getActionsList() {
        return actionsList;
    }

    /**
     * @return action from the top of the actionsList
     */
    public Action getFirstAction(){
        try{
            return this.actionsList.get(0);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * @param action action to added to list of completed actions actionsDoneList
     */
    public void pushActionsDoneList(Action action) {
        this.actionsDoneList.add(action);
    }
}
