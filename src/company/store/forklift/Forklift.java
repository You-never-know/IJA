package company.store.forklift;

import company.store.Store;
import company.store.request.Request;
import company.store.request.action.Action;
import company.store.shelve.Shelve;
import company.store.shelve.goods.Goods;
import company.store.shelve.goods.coordinates.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class Forklift {

    private int id;
    private Request request;
    private List<Goods> goodsList;
    private Coordinates coordinates;
    private List<Coordinates> path;
    private List<Coordinates> visitedCoordinates;
    private int piecesBearing;
    private ForkliftStatus status;
    private Store store;
    private Action actionInProgress = null;

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

    public Forklift() {
        this.coordinates = new Coordinates(2, 0);
    }

    public Forklift(int id, int x, int y, Store store) {
        this.id = id;
        this.coordinates = new Coordinates(x, y);
        this.status = null;
        this.path = new ArrayList<>();
        this.store = store;
    }

    public List<Coordinates> getPath() {
        return this.path;
    }

    public Coordinates popFirstPath() {
        Coordinates coordinates = this.path.get(0);
        this.path.remove(0);
        return coordinates;
    }

    public Coordinates getFirstPath() {
        return this.path.get(0);
    }

    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setXCoordinate(int x) {
        this.coordinates.setX(x);
    }

    public void setYCoordinate(int y) {
        this.coordinates.setY(y);
    }

    public void setFirstActionInProgress() {
        this.actionInProgress = new Action(this.request.getFirstAction());
    }

    public Action getActionInProgress() {
        return this.actionInProgress;
    }

    public void nullActionInProgress() {
        this.actionInProgress = null;
    }

    public void setStatus(ForkliftStatus status) {
        this.status = status;
    }

    private void countSetStatus(Coordinates from, Coordinates to) {
        if (from.getY() < to.getY()) {
            this.status = ForkliftStatus.UP;
        }
        if (from.getY() > to.getY()) {
            this.status = ForkliftStatus.DOWN;
        }
        if (from.getX() < to.getX()) {
            this.status = ForkliftStatus.RIGHT;
        }
        if (from.getX() > to.getX()) {
            this.status = ForkliftStatus.LEFT;
        }

    }

    public ForkliftStatus getStatus() {
        return status;
    }

    public void printPath() {

        System.out.println("PATH: ");
        for (int i = 0; i < this.path.size(); i++) {
            path.get(i).printCoordinates();
        }
    }

    public void countPath(Coordinates destination) {
        Coordinates position = this.coordinates;
        List<Coordinates> open = new ArrayList<>();
        List<Coordinates> closed = new ArrayList<>();
        List<Coordinates> predecessorsOpen = new ArrayList<>();
        List<Coordinates> predecessorsClosed = new ArrayList<>();

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
                Coordinates predecessor = new
                        Coordinates(predecessorsOpen.get(toExpand).getX(), predecessorsOpen.get(toExpand).getY());
                open.remove(toExpand);
                predecessorsOpen.remove(toExpand);
                closed.add(expanding);
                predecessorsClosed.add(predecessor);
                this.path = pathRecursion(closed, predecessorsClosed, expanding);
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

        this.path = null;
    }

    public boolean isValidCoordinate(Coordinates coordinates) {
        return coordinates.getX() < store.getWidth() && coordinates.getX() >= 0 && coordinates.getY() < store.getHeight() && coordinates.getY() >= 0;
    }

    public boolean isNotBlocked(Coordinates coordinates) {
        return store.getMapValue(coordinates.getX(), coordinates.getY()) != 1 && store.getMapValue(coordinates.getX(), coordinates.getY()) != 2;
    }

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

    public void doAction() {
        Action action = this.request.popFirstActionsList();
        Coordinates shelveLocation = this.popFirstPath(); // TODO This does not guarantee that it will be a shelve, "store.getGoodsShelve(action)" would be better, you can get the shelve location from the shelve itself
        // TODO + how to GUI?
        Shelve shelve = this.store.getShelve(shelveLocation.getX(), shelveLocation.getY()); // TODO This will probably not work, because the coordinates may not be of some Shelve ( If I understand correctly, that path is the whole path to the shelve), "store.getGoodsShelve(action)" gives Shelve straight away
        if (action.getCount() > shelve.getGoodsCount()) {
            int toSell = 0;
            if (shelve.getGoodsCount() < this.piecesBearing) { // TODO look at the next TODO, the same principle applies here
                toSell = shelve.getGoodsCount();
            } else {
                toSell = this.piecesBearing;
            }
            Goods sold = shelve.sellGoods(toSell);
            action.setCount(action.getCount() - toSell);
            this.goodsList.add(sold);
            this.countPath(store.getHomeCoordinates());
        } else if (action.getCount() >= this.piecesBearing) { // TODO you should subtract the pieces already on the Forklift, because for example 4 pieces are already in forklift, max for forklift is for example 6, we want another 4, this would allow it, even though we would have 8 pieces in one forklift
            Goods sold = shelve.sellGoods(this.piecesBearing);
            action.setCount(action.getCount() - this.piecesBearing);
            this.goodsList.add(sold);
            this.countPath(store.getHomeCoordinates());
        } else {
            Goods sold = shelve.sellGoods(action.getCount());
            action.setCount(0);
            this.goodsList.add(sold);
            this.countPath(store.getGoodsShelve(this.request.getFirstAction()).getCoordinates());
        }

        // TODO null path due to block?

        this.request.pushActionsDoneList(action);
    }

    public void moveForward() {
        Coordinates moveTo = popFirstPath();
        store.updateMapValueRemove(this.coordinates.getX(), this.coordinates.getY(), this.status);
        visitedCoordinates.add(this.coordinates);
        this.coordinates = new Coordinates(moveTo.getX(), moveTo.getY());
        this.countSetStatus(this.coordinates, getFirstPath());
        store.updateMapValueAdd(this.coordinates.getX(), this.coordinates.getY(), this.status);

    }
}
