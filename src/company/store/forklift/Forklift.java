package company.store.forklift;

import company.store.request.action.Action;
import company.store.shelve.goods.Goods;
import company.store.request.Request;
import company.store.shelve.goods.coordinates.Coordinates;
import company.store.Store;

import java.util.ArrayList;
import java.util.List;

public class Forklift {

    private int id;
    private Request request;
    private List<Goods> goodsList;
    private Coordinates coordinates;
    private List<Coordinates> path;
    private int weightBearing;
    private int piecesBearing;
    private ForkliftStatus status;
    private Store store;

    public enum ForkliftStatus {
        TOP(3), BOTTOM(4), LEFT(5), RIGHT(6); //TODO 8 , 9, 10 bad numbers

        private int Val;

        ForkliftStatus(int Val) {
            this.Val = Val;
        }

        public int getNumVal() {
            return Val;
        }
    }

    public Forklift(){
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
        return path;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setXCoordinate(int x) {
        this.coordinates.setX(x);
    }

    public void setYCoordinate(int y) {
        this.coordinates.setY(y);
    }

    public void setStatus(ForkliftStatus status) {
        this.status = status;
    }

    public ForkliftStatus getStatus() {
        return status;
    }

    // TODO A star baby
    public void countPath(Coordinates destination) {
        Coordinates position = this.coordinates;
        List<Coordinates> open = new ArrayList<>();
        List<Coordinates> closed = new ArrayList<>();
        ArrayList<Integer> predecessorsOpen = new ArrayList<>();
        ArrayList<Integer> predecessorsClosed = new ArrayList<>();

        Coordinates top;
        Coordinates left;
        Coordinates right;
        Coordinates bottom;

        open.add(position);
        predecessorsOpen.add(null);
        System.out.println(predecessorsOpen);
        if(predecessorsOpen.get(0) == null){
            System.out.println("tadah");
         }
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
                        heuristic = tmp_heuristic;
                        toExpand = i;
                    }
                }
            }

            /* transfer to closed list */
            Coordinates expanding = open.get(toExpand);
            if(expanding == destination){
                this.path = pathRecursion(closed,predecessorsClosed,expanding);
            }
            int predecessor = predecessorsOpen.get(toExpand);
            open.remove(toExpand);
            predecessorsOpen.remove(toExpand);
            closed.add(expanding);
            predecessorsClosed.add(predecessor);

            /* expand */
            List<Coordinates> expandList = new ArrayList<>();
            top = open.get(toExpand);
            top.setY(top.getY() - 1);
            left = open.get(toExpand);
            left.setX(left.getX() - 1);
            right = open.get(toExpand);
            right.setX(right.getX() + 1);
            bottom = open.get(toExpand);
            bottom.setY(bottom.getY() + 1);

            expandList.add(top);
            expandList.add(left);
            expandList.add(right);
            expandList.add(bottom);

            for (int j = 0; j < expandList.size(); j++) {
                if (isValidCoordinate(expandList.get(j)) && isNotBlocked(expandList.get(j))) {
                    if(!open.contains(expandList.get(j)) && closed.contains(expandList.get(j))){
                        open.add(expandList.get(j));
                        predecessorsOpen.add(toExpand);
                    }else{
                        if(open.indexOf(expandList.get(j))!=-1){
                            if(open.get(open.indexOf(expandList.get(j))).getHeuristicValue(destination) > expandList.get(j).getHeuristicValue(destination)){
                                predecessorsOpen.add(open.indexOf(expandList.get(j)),toExpand);
                            }

                        }else if(closed.indexOf(expandList.get(j))!=-1){
                            if(closed.get(closed.indexOf(expandList.get(j))).getHeuristicValue(destination) > expandList.get(j).getHeuristicValue(destination)){
                                Coordinates moving = closed.get(closed.indexOf(expandList.get(j)));
                                closed.remove(expandList.get(j));
                                int movingIndex = predecessorsClosed.get(closed.indexOf(expandList.get(j)));
                                predecessorsClosed.remove(closed.indexOf(expandList.get(j)));
                                open.add(moving);
                                predecessorsOpen.add(movingIndex);
                            }
                        }
                    }
                }
            }

        }
    }

    public boolean isValidCoordinate(Coordinates coordinates) {
        if (coordinates.getX() < store.GetWidth() && coordinates.getX() >= 0 && coordinates.getY() < store.GetHeight() && coordinates.getY() >= 0) {
            return true;
        }
        return false;
    }

    public boolean isNotBlocked(Coordinates coordinates) {
        //TODO - is not block or shelve
        return true;
    }

    public List<Coordinates> pathRecursion(List<Coordinates> list, List<Integer> predecessors, Coordinates node){

        Integer parent = (predecessors.get(list.indexOf(node)));

        if(parent != null){
            pathRecursion(list, predecessors, list.get(predecessors.get(list.indexOf(node))));
        }else{
            List<Coordinates> path = new ArrayList<>();
        }
        path.add(node);
        return path;
    }

    public void doAction() {
        Action action = this.request.popFirstActionsList();
        // TODO + how to GUI?
        this.request.pushActionsDoneList(action);
    }
}