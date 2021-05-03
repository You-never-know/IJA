package company.store.forklift;

import company.store.Store;
import company.store.request.Request;
import company.store.request.action.Action;
import company.store.shelve.Shelve;
import company.store.shelve.goods.Goods;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Forklift class to which requests are assigned to
 */
public class Forklift {

    private final int id;
    private Request request;
    private final List<Goods> goodsList;
    private Coordinates coordinates;
    private List<Coordinates> path;
    private final List<Coordinates> visitedCoordinates;
    private int piecesBearing;
    private int piecesBearingLeft;
    private ForkliftStatus status;
    private final Store store;
    private Action actionInProgress;

    /**
     * Enumeration of forklift states according to its movement
     */
    public enum ForkliftStatus {
        UP(3), DOWN(4), LEFT(5), RIGHT(6);

        private int Val;

        ForkliftStatus(int Val) {
            this.Val = Val;
        }

        public int getNumVal() {
            return Val;
        }
    }

    /**
     * Forklift class constructor
     *
     * @param id            The ID of the forklift, number according to creation order
     * @param x             x-coordinate of the forklift in the store map
     * @param y             y-coordinate of the forklift in the store map
     * @param store         Store where the forklift works
     * @param piecesBearing Forklift bearing
     */
    public Forklift(int id, int x, int y, Store store, int piecesBearing) {
        this.request = null;
        this.id = id;
        this.coordinates = new Coordinates(x, y);
        this.status = null;
        this.path = new ArrayList<>();
        this.store = store;
        this.visitedCoordinates = new ArrayList<>();
        this.piecesBearing = piecesBearing;
        this.piecesBearingLeft = piecesBearing;
        this.goodsList = new ArrayList<>();
        this.actionInProgress = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Forklift forklift = (Forklift) o;

        return id == forklift.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * @return Path of the forklift
     */
    public List<Coordinates> getPath() {
        return this.path;
    }

    /**
     * @return Visited path of the forklift
     */
    public List<Coordinates> getVisitedPath() {
        return this.visitedCoordinates;
    }

    /**
     * @return Removes and returns first item of the coordinates list - path of forklift
     */
    public Coordinates popFirstPath() {
        Coordinates coordinates = this.path.get(0);
        this.path.remove(0);
        return coordinates;
    }

    /**
     * @return First item of the coordinates list - path of forklift
     */
    public Coordinates getFirstPath() {
        return this.path.get(0);
    }

    /**
     * @return Request assigned to the forklift
     */
    public Request getRequest() {
        return this.request;
    }

    /**
     * @return Removes and returns request assigned to the forklift
     */
    public Request nullGetRequest() {
        Request popRequest = this.getRequest();
        this.request = null;
        return popRequest;
    }

    /**
     * @param request Request to be assigned to the forklift
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * @param coordinates Coordinates to be assigned to the forklift
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * @return Coordinates of the forklift
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setXCoordinate(int x) {
        this.coordinates.setX(x);
    }

    public void setYCoordinate(int y) {
        this.coordinates.setY(y);
    }

    /**
     * @return Id of the forklift
     */
    public int getId() {
        return id;
    }


    /**
     * @return goods list
     */
    public List<Goods> getGoodsList() {
        return goodsList;
    }

    /**
     * Unloads goods from the forklift, resets its bearing capacity
     */
    public void unloadGoods() {
        this.goodsList.clear();
        this.piecesBearingLeft = this.piecesBearing;
        System.out.println("Forklift n." + this.id + " goods successfully unloaded");
    }

    /**
     * Makes the first action of list of actions to be done in the assigned request the action in progress
     */
    public void setFirstActionInProgress() {
        if (this.request.getFirstAction() != null) {
            this.actionInProgress = new Action(this.request.getFirstAction());
            this.countPath(store.getGoodsShelve(this.actionInProgress).getCoordinates());
        } else {
            this.countPath(store.getHomeCoordinates());
        }
        if (this.getPath().size() != 0) {
            this.countSetStatus(this.getCoordinates(), this.getFirstPath());
        } else {
            store.queueRequest(this);
            store.logMessageStore("Path to goods of forklift n." + this.getId() + " is blocked");
        }
    }

    /**
     * Tries unblocking forklift
     */
    public void tryUnblockForklift() {
        if (this.request.getFirstAction() != null) {
            this.actionInProgress = new Action(this.request.getFirstAction());
            this.countPath(store.getGoodsShelve(this.actionInProgress).getCoordinates());
        } else {
            this.countPath(store.getHomeCoordinates());
        }

        if (this.getPath().size() != 0) {
            this.countSetStatus(this.getCoordinates(), this.getFirstPath());
            store.fromBlocklist(this);
        } else {

            if (this.getCoordinates().equals(store.getHomeCoordinates())) {
                store.fromBlocklist(this);
                store.queueRequest(this);
            } else {
                this.countPath(store.getHomeCoordinates());
                if (this.getPath().size() == 0) {
                    store.logMessageStore("Path to goods of forklift n." + this.getId() + " is blocked");

                }
            }
        }
    }

    /**
     * @return Action the forklift is working on
     */
    public Action getActionInProgress() {
        return this.actionInProgress;
    }

    /**
     * Action the forklift is working on is set to null
     */
    public void nullActionInProgress() {
        this.actionInProgress = null;
    }

    /**
     * @param status Status to assign to the forklift
     */
    public void setStatus(ForkliftStatus status) {
        this.status = status;
    }

    /**
     * Calculation of the forklift status based on its direction computed of coordinates given
     *
     * @param from origin coordinates
     * @param to   destination coordinates
     */
    private void countSetStatus(Coordinates from, Coordinates to) {
        if (from.getY() < to.getY()) {
            this.status = ForkliftStatus.DOWN;
        } else if (from.getY() > to.getY()) {
            this.status = ForkliftStatus.UP;
        } else if (from.getX() < to.getX()) {
            this.status = ForkliftStatus.RIGHT;
        } else if (from.getX() > to.getX()) {
            this.status = ForkliftStatus.LEFT;
        }
    }

    /**
     * Calculation of the forklift status based on its direction computed of coordinates given
     *
     * @param from origin coordinates
     * @param to   destination coordinates
     */
    private ForkliftStatus tryStatus(Coordinates from, Coordinates to) {
        if (from.getY() < to.getY()) {
            return ForkliftStatus.DOWN;
        } else if (from.getY() > to.getY()) {
            return ForkliftStatus.UP;
        } else if (from.getX() < to.getX()) {
            return ForkliftStatus.RIGHT;
        } else if (from.getX() > to.getX()) {
            return ForkliftStatus.LEFT;
        }
        return null;
    }

    /**
     * @return Status of the forklift
     */
    public ForkliftStatus getStatus() {
        return status;
    }

    /**
     * Prints path of the forklift - method used for submission sample
     */
    public void printPath() {

        System.out.println("---------------");
        System.out.println("Forklift n." + this.id + " at " + this.getCoordinates().getX() + ", " + this.getCoordinates().getY() + " counted path: ");
        for (int i = 0; i < this.path.size(); i++) {
            path.get(i).printCoordinates();
        }
        System.out.println("---------------");
    }

    /**
     * Pathfinding algorithm based on A* algorithm principle
     *
     * @param destination Destination where the forklift needs the path computed to
     */
    public void countPath(Coordinates destination) {
        Coordinates position = this.coordinates;
        List<Coordinates> open = new ArrayList<>();
        List<Coordinates> closed = new ArrayList<>();
        List<Coordinates> predecessorsOpen = new ArrayList<>();
        List<Coordinates> predecessorsClosed = new ArrayList<>();
        this.path.clear();

        Coordinates top;
        Coordinates left;
        Coordinates right;
        Coordinates bottom;

        open.add(position);
        predecessorsOpen.add(null);

        while (!open.isEmpty()) {

            /* find node to be expanded */
            int toExpand = 0;
            double heuristic = open.get(0).getHeuristicValue(destination);
            for (int i = 0; i < open.size(); i++) {
                double tmp_heuristic = open.get(i).getHeuristicValue(destination);

                if (tmp_heuristic < heuristic) {
                    heuristic = tmp_heuristic;
                    toExpand = i;
                } else if (tmp_heuristic == heuristic) {
                    if (open.get(i).getCostValue(destination) < open.get(toExpand).getCostValue(destination)) {
                        toExpand = i;
                    }
                }
            }

            Coordinates expanding = new Coordinates(open.get(toExpand).getX(), open.get(toExpand).getY());
            if (expanding.equals(destination)) {
                Coordinates predecessor;
                try {
                    predecessor = new
                            Coordinates(predecessorsOpen.get(toExpand).getX(), predecessorsOpen.get(toExpand).getY());
                } catch (NullPointerException e) {
                    this.path = new ArrayList<>();
                    return;
                }

                open.remove(toExpand);
                predecessorsOpen.remove(toExpand);
                closed.add(expanding);
                predecessorsClosed.add(predecessor);
                this.path = pathRecursion(closed, predecessorsClosed, expanding);
                this.printPath();
                return;
            }

            /* expand */
            List<Coordinates> expandList = new ArrayList<>();
            top = new Coordinates(open.get(toExpand).getX(), open.get(toExpand).getY());
            top.setY(top.getY() - 1);
            Coordinates topToAdd = new Coordinates(top.getX(), top.getY());

            left = new Coordinates(open.get(toExpand).getX(), open.get(toExpand).getY());
            left.setX(left.getX() - 1);
            Coordinates leftToAdd = new Coordinates(left.getX(), left.getY());

            right = new Coordinates(open.get(toExpand).getX(), open.get(toExpand).getY());
            right.setX(right.getX() + 1);
            Coordinates rightToAdd = new Coordinates(right.getX(), right.getY());

            bottom = new Coordinates(open.get(toExpand).getX(), open.get(toExpand).getY());
            bottom.setY(bottom.getY() + 1);
            Coordinates bottomToAdd = new Coordinates(bottom.getX(), bottom.getY());

            Coordinates predecessor = null;
            if (!this.coordinates.equals(expanding)) {
                predecessor = new
                        Coordinates(predecessorsOpen.get(toExpand).getX(), predecessorsOpen.get(toExpand).getY());
            }

            /* transfer expanded  to closed list */
            open.remove(toExpand);
            predecessorsOpen.remove(toExpand);
            closed.add(expanding);
            predecessorsClosed.add(predecessor);

            expandList.add(topToAdd);
            expandList.add(leftToAdd);
            expandList.add(rightToAdd);
            expandList.add(bottomToAdd);

            /* add new to open list */
            for (int j = 0; j < expandList.size(); j++) {

                if (isValidCoordinate(expandList.get(j)) && isNotBlocked(expandList.get(j))) {

                    if (!open.contains(expandList.get(j)) && !closed.contains(expandList.get(j))) {

                        open.add(expandList.get(j));
                        predecessorsOpen.add(expanding);
                    } else {
                        if (open.contains(expandList.get(j))) {
                            if (open.get(open.indexOf(expandList.get(j))).getHeuristicValue(destination) > expandList.get(j).getHeuristicValue(destination)) {
                                predecessorsOpen.add(open.indexOf(expandList.get(j)), expanding);
                            }

                        } else if (closed.contains(expandList.get(j))) {
                            if (closed.get(closed.indexOf(expandList.get(j))).getHeuristicValue(destination) > expandList.get(j).getHeuristicValue(destination)) {
                                Coordinates moving = closed.get(closed.indexOf(expandList.get(j)));
                                closed.remove(expandList.get(j));
                                open.add(moving);

                                Coordinates movingPredecessor = predecessorsClosed.get(closed.indexOf(expandList.get(j)));
                                predecessorsClosed.remove(closed.indexOf(expandList.get(j)));
                                predecessorsOpen.add(movingPredecessor);
                            }
                        }
                    }
                } else if (expandList.get(j).equals(destination)) {
                    open.add(expandList.get(j));
                    predecessorsOpen.add(expanding);
                }
            }
        }

        this.path = new ArrayList<>();
    }

    /**
     * @param coordinates coordinates to check
     * @return boolean value corresponding to validity
     */
    public boolean isValidCoordinate(Coordinates coordinates) {
        return coordinates.getX() < store.getWidth() && coordinates.getX() >= 0 && coordinates.getY() < store.getHeight() && coordinates.getY() >= 0;
    }

    /**
     * @param coordinates coordinates to check
     * @return boolean value corresponding to blocking
     */
    public boolean isNotBlocked(Coordinates coordinates) {
        return store.getMapValue(coordinates.getX(), coordinates.getY()) != 1 && store.getMapValue(coordinates.getX(), coordinates.getY()) != 2;
    }

    /**
     * Auxiliary function for A* pathfinding
     *
     * @param list         Closed nodes list
     * @param predecessors Predecessors of closed nodes
     * @param node         starting node
     * @return valid path
     */
    private List<Coordinates> pathRecursion(List<Coordinates> list, List<Coordinates> predecessors, Coordinates node) {
        Coordinates parent = predecessors.get(list.indexOf(node));

        if (!this.coordinates.equals(parent)) {
            pathRecursion(list, predecessors, parent);
        } else {
            List<Coordinates> path = new ArrayList<>();
        }
        path.add(node);
        return path;
    }

    /**
     * Forklift loads goods according to active action, shelve volume and its own bearing
     */
    public void doAction() {
        System.out.println("Forklift n." + this.id + " at " + this.getCoordinates().getX() + ", " + this.getCoordinates().getY());
        Coordinates shelveLocation = this.popFirstPath();

        Shelve shelve = this.store.getShelve(shelveLocation.getX(), shelveLocation.getY());
        if (actionInProgress.getCount() > shelve.getGoodsCount()) {
            int toSell;
            if (shelve.getGoodsCount() < this.piecesBearingLeft) {
                toSell = shelve.getGoodsCount();
                Goods sold = shelve.sellGoods(toSell);
                actionInProgress.setCount(actionInProgress.getCount() - toSell);
                this.goodsList.add(sold);
                System.out.println("Forklift n." + this.id + " took over goods: " + sold.getName() + " from " + shelve.getCoordinates().getX() + ", " + shelve.getCoordinates().getY());
                this.piecesBearingLeft = this.piecesBearingLeft - toSell;
                this.updatePreMapDoAction();
                this.countPath(this.store.getGoodsShelve(actionInProgress).getCoordinates());
                this.updateAfterMapDoAction();
            } else {
                toSell = this.piecesBearingLeft;
                Goods sold = shelve.sellGoods(toSell);
                actionInProgress.setCount(actionInProgress.getCount() - toSell);
                this.goodsList.add(sold);
                System.out.println("Forklift n." + this.id + " took over goods: " + sold.getName() + " from " + shelve.getCoordinates().getX() + ", " + shelve.getCoordinates().getY());
                this.piecesBearingLeft = this.piecesBearingLeft - toSell;
                this.updatePreMapDoAction();
                this.countPath(store.getHomeCoordinates());
                this.updateAfterMapDoAction();
            }

        } else if (actionInProgress.getCount() >= this.piecesBearingLeft) {
            Goods sold = shelve.sellGoods(this.piecesBearingLeft);
            actionInProgress.setCount(actionInProgress.getCount() - this.piecesBearingLeft);
            this.goodsList.add(sold);
            System.out.println("Forklift n." + this.id + " took over goods: " + sold.getName() + " from " + shelve.getCoordinates().getX() + ", " + shelve.getCoordinates().getY());
            this.piecesBearingLeft = this.piecesBearingLeft - sold.getCount();
            this.updatePreMapDoAction();
            this.countPath(store.getHomeCoordinates());
            this.updateAfterMapDoAction();
        } else {
            Goods sold = shelve.sellGoods(actionInProgress.getCount());
            actionInProgress.setCount(0);
            this.goodsList.add(sold);
            System.out.println("Forklift n." + this.id + " took over goods: " + sold.getName() + " from " + shelve.getCoordinates().getX() + ", " + shelve.getCoordinates().getY());
            this.piecesBearingLeft = this.piecesBearingLeft - sold.getCount();
            if (this.request.getActionsList().size() == 1) {
                this.updatePreMapDoAction();
                this.countPath(store.getHomeCoordinates());
                this.updateAfterMapDoAction();
            } else {
                if (actionInProgress.getCount() == 0) {
                    nullActionInProgress();
                    this.request.pushActionsDoneList(this.request.popFirstActionsList());
                }
                this.updatePreMapDoAction();
                this.countPath(store.getGoodsShelve(this.request.getFirstAction()).getCoordinates());
                this.updateAfterMapDoAction();
                return;
            }
        }
        if (actionInProgress.getCount() == 0) {
            nullActionInProgress();
            this.request.pushActionsDoneList(this.request.popFirstActionsList());
        }

    }

    /**
     * Removing map value before counting new path
     */
    public void updatePreMapDoAction(){
        if (!this.coordinates.equals(store.getHomeCoordinates())) {
            store.updateMapValueRemove(this.coordinates.getX(), this.coordinates.getY(), this.status);
        }
    }

    /**
     * Updating map value with the new path
     */
    public void updateAfterMapDoAction(){
        this.countSetStatus(this.coordinates, getFirstPath());
        store.updateMapValueAdd(this.coordinates.getX(), this.coordinates.getY(), this.status);
    }

    /**
     * Forklift moves forward: actual coordinates added to visited coordinates list, value of map reset, new statuses assigned
     */
    public void moveForward() {
        Coordinates movingTo = getFirstPath();
        int tryie = tryStatus(new Coordinates(movingTo.getX(), movingTo.getY()), this.path.get(1)).getNumVal();
        int movingToMap = store.getMapValue(movingTo.getX(), movingTo.getY());
        int res = tryie + movingToMap;

        System.out.println("MAP: ");
        System.out.println(movingToMap);
        System.out.println("TRY STATUS: ");
        System.out.println(tryie);
        System.out.println("RESULT: ");
        System.out.println(res);

        if (res != tryie && res != 11 && res != 7){
            System.out.println("NOT MOVING -> RETURNED");
            return;
        }

            Coordinates moveTo = popFirstPath();
        System.out.println("Forklift n." + this.id + " at " + this.getCoordinates().getX() + ", " + this.getCoordinates().getY());
        if (!this.coordinates.equals(store.getHomeCoordinates())) {
            store.updateMapValueRemove(this.coordinates.getX(), this.coordinates.getY(), this.status);
        }

        visitedCoordinates.add(this.coordinates);
        this.setCoordinates(new Coordinates(moveTo.getX(), moveTo.getY()));
        this.countSetStatus(this.coordinates, getFirstPath());
        store.updateMapValueAdd(this.coordinates.getX(), this.coordinates.getY(), this.status);
        System.out.println("---");

    }

    /**
     * Forklift moves forward to 0,0: value of map reset, status reset, visited coordinates clear
     */
    public void moveToHome() {
        Coordinates moveTo = popFirstPath();
        System.out.println("Forklift n." + this.id + " at " + this.getCoordinates().getX() + ", " + this.getCoordinates().getY());
        store.updateMapValueRemove(this.coordinates.getX(), this.coordinates.getY(), this.status);
        visitedCoordinates.clear();
        this.setCoordinates(new Coordinates(0, 0));
        this.setStatus(null);


    }
}
