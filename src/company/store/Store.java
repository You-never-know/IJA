package company.store;

import company.StoreManager;
import company.store.forklift.Forklift;
import company.store.request.Request;
import company.store.request.action.Action;
import company.store.shelve.Shelve;
import company.store.shelve.goods.Goods;
import company.store.shelve.goods.coordinates.Coordinates;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Main class of the Store, assigns actions to the other components, holds all the data needed for its operation
 */
public class Store {

    private final List<Shelve> shelvesList;
    private final List<Goods> goodsList;
    private final List<Forklift> freeForkliftsList;
    private final List<Forklift> workingForkliftsList;
    private final List<Forklift> blockedForkliftsList;
    private final List<Request> requestsList;
    private final List<Request> doneRequestsList;
    private StoreManager manager;
    private int[][] map;
    private int width;
    private int height;
    private final Coordinates homeCoordinates;
    private int base_sleep;
    private double speed_of_time;

    /**
     * Initialize the Store
     */
    public Store() {
        shelvesList = new ArrayList<>();
        goodsList = new ArrayList<>();
        freeForkliftsList = new ArrayList<>();
        workingForkliftsList = new ArrayList<>();
        blockedForkliftsList = new ArrayList<>();
        requestsList = new ArrayList<>();
        doneRequestsList = new ArrayList<>();
        width = 0;
        height = 0;
        homeCoordinates = new Coordinates(0, 0);
        base_sleep = 1000;
        speed_of_time = 1.0;
    }

    /**
     * Main method where requests are processed
     */
    public void main() {

        if (!setGoods(manager.getGoodsPath())) {
            setGoods(manager.getDefaultGoodsPath());
        }

        initForklifts(4);

        while (true) {
            delegateRequest();
            if (workingForkliftsList.size() > 0) {
                // TODO blocked path to home in every way

                for (Forklift forklift : workingForkliftsList) {
                    if (forklift.getPath().size() == 0 && forklift.getActionInProgress() == null && forklift.getRequest().getActionsList().size() != 0) {
                        forklift.setFirstActionInProgress();
                    }
                    if (forklift.getPath().size() == 0) {
                        forklift.countPath(this.homeCoordinates);
                        if (forklift.getPath().size() == 0) {
                            toBlocklist(forklift);
                            logMessageStore("Forklift n." + forklift.getId() + " is blocked");
                            break;
                        }
                    }

                    if (forklift.getFirstPath().equals(homeCoordinates)) {
                        forklift.moveToHome();
                        forklift.unloadGoods();
                        if (forklift.equals(manager.get_forklift())) {
                            manager.redraw_path_of_forklift(forklift);
                        }
                        if (forklift.getRequest().getActionsList().size() != 0) {
                            if (forklift.getPath().size() == 0) {
                                forklift.countPath(this.getGoodsShelve(forklift.getActionInProgress()).getCoordinates());
                            }
                            break;
                        } else {
                            this.setRequestAsDone(forklift.nullGetRequest());
                            forklift.nullActionInProgress();
                            setForkliftFree(forklift);
                            break;
                        }
                    }
                    // move
                    if (getMapValue(forklift.getFirstPath().getX(), forklift.getFirstPath().getY()) == MapCoordinateStatus.SHELVE.Val) { //is shelve -> do action
                        forklift.doAction();
                        if ((forklift.getRequest().getActionsList().size() != 0) && (forklift.getActionInProgress() == null)) { // request has some actions left
                            forklift.setFirstActionInProgress();

                        }
                        if (forklift.getPath() == null) {
                            forklift.countPath(homeCoordinates);
                        }
                    } else if (getMapValue(forklift.getFirstPath().getX(), forklift.getFirstPath().getY()) == MapCoordinateStatus.BLOCK.Val) {
                        //System.out.println("ERR?");
                        forklift.getPath().get(forklift.getPath().size() - 1).printCoordinates();
                        if (forklift.getPath().get(forklift.getPath().size() - 1) == this.getHomeCoordinates()) {
                            forklift.countPath(this.homeCoordinates);
                            if (forklift.getPath().size() == 0) {
                                toBlocklist(forklift);
                                logMessageStore("Forklift n." + forklift.getId() + " is blocked");
                                break;
                            }
                        } else {
                            forklift.countPath(getGoodsShelve(forklift.getActionInProgress()).getCoordinates());
                            if (forklift.getPath().size() == 0) {
                                forklift.countPath(this.homeCoordinates);
                                if (forklift.getPath().size() == 0) {
                                    toBlocklist(forklift);
                                    logMessageStore("Forklift n." + forklift.getId() + " is blocked");
                                    break;
                                }
                            }
                        }
                        forklift.moveForward();
                    } else { //is free path or forklift
                        forklift.moveForward();
                    }
                }
                manager.FreeVisitedPath();
            }
            for (Forklift forklift : workingForkliftsList) {
                manager.draw_forklift(forklift);
                if (forklift.equals(manager.get_forklift())) {
                    manager.redraw_path_of_forklift(forklift);
                }
            }
            if (workingForkliftsList.size() > 0) {
                try {
                    if (speed_of_time == 0) {
                        speed_of_time = 1.0 / 32.0;
                    }
                    Thread.sleep((long) Math.ceil(base_sleep / speed_of_time));
                } catch (InterruptedException e) {

                }
            } else {
                try {
                    Thread.sleep(base_sleep);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    /**
     * Enumeration of states for map
     */
    public enum MapCoordinateStatus {
        FREE_PATH(0), SHELVE(1), BLOCK(2), FORKLIFT_UP(3), FORKLIFT_DOWN(4), FORKLIFT_LEFT(5),
        FORKLIFT_RIGHT(6), FORKLIFTS_UP_DOWN(7), FORKLIFTS_LEFT_RIGHT(11);

        private int Val;

        MapCoordinateStatus(int Val) {
            this.Val = Val;
        }

        public int getNumVal() {
            return Val;
        }
    }


    /**
     * @param forkliftStatus ForkliftStatus to be mapped to MapCoordinateStatus
     * @return MapCoordinateStatus corresponding to ForkliftStatus given
     */
    public MapCoordinateStatus statusMapper(Forklift.ForkliftStatus forkliftStatus) {
        if (forkliftStatus == Forklift.ForkliftStatus.UP) {
            return MapCoordinateStatus.FORKLIFT_UP;
        } else if (forkliftStatus == Forklift.ForkliftStatus.DOWN) {
            return MapCoordinateStatus.FORKLIFT_DOWN;
        } else if (forkliftStatus == Forklift.ForkliftStatus.LEFT) {
            return MapCoordinateStatus.FORKLIFT_LEFT;
        } else if (forkliftStatus == Forklift.ForkliftStatus.RIGHT) {
            return MapCoordinateStatus.FORKLIFT_RIGHT;
        }
        return null;
    }

    /**
     * @param manager StoreManger assigned to this store
     */
    public void setManager(StoreManager manager) {
        this.manager = manager;
    }

    /**
     * @return StoreManager of the shop
     */
    public StoreManager getManager() {
        return manager;
    }

    /**
     * @return Width of the map
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Height of the map
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param speed of time
     */
    public void setSpeed_of_time(double speed) {
        this.speed_of_time = speed;
    }

    /**
     * @param x x-coordinate in the map
     * @param y y-coordinate in the map
     * @return Value in the map on given coordinates
     */
    public int getMapValue(int x, int y) {
        if (y >= height || x >= width) {
            return -1;
        }
        return map[x][y];
    }

    /**
     * Update a value in a map
     *
     * @param x         x-coordinate in the map
     * @param y         y-coordinate in the map
     * @param mapStatus New value for the map
     */
    public boolean setMapValue(int x, int y, MapCoordinateStatus mapStatus) {
        if (y >= height || x >= width) {
            return false;
        }
        map[x][y] = mapStatus.getNumVal();
        return true;
    }

    /**
     * Add value according to current state to the x, y position on map
     *
     * @param x              x-coordinate in the map
     * @param y              y-coordinate in the map
     * @param forkliftStatus New value for the map by forklift
     */
    public void updateMapValueAdd(int x, int y, Forklift.ForkliftStatus forkliftStatus) {
        if (y >= height || x >= width) {
            return;
        }
        if (map[x][y] == MapCoordinateStatus.FREE_PATH.getNumVal()) {
            map[x][y] = statusMapper(forkliftStatus).getNumVal();
            return;
        }
        if ((forkliftStatus == Forklift.ForkliftStatus.UP && map[x][y] == MapCoordinateStatus.FORKLIFT_DOWN.getNumVal()) ||
                (forkliftStatus == Forklift.ForkliftStatus.DOWN && map[x][y] == MapCoordinateStatus.FORKLIFT_UP.getNumVal())) {
            map[x][y] = MapCoordinateStatus.FORKLIFTS_UP_DOWN.getNumVal();
            return;
        }

        if ((forkliftStatus == Forklift.ForkliftStatus.LEFT && map[x][y] == MapCoordinateStatus.FORKLIFT_RIGHT.getNumVal()) ||
                (forkliftStatus == Forklift.ForkliftStatus.RIGHT && map[x][y] == MapCoordinateStatus.FORKLIFT_LEFT.getNumVal())) {
            map[x][y] = MapCoordinateStatus.FORKLIFTS_LEFT_RIGHT.getNumVal();
        }
    }

    /**
     * Remove value according to current state to the x, y position on map
     *
     * @param x              x-coordinate in the map
     * @param y              y-coordinate in the map
     * @param forkliftStatus New value for the map by forklift
     */
    public void updateMapValueRemove(int x, int y, Forklift.ForkliftStatus forkliftStatus) {
        if (y >= height || x >= width) {
            return;
        }
        map[x][y] -= forkliftStatus.getNumVal();
    }

    /**
     * @param id ID of the shelve
     * @param x  x-coordinate of the shelve
     * @param y  y-coordinate of the shelve
     */
    public void createShelve(int id, int x, int y) {
        Shelve shelf = new Shelve(id, x, y, this);
        shelvesList.add(shelf);
    }

    /**
     * @param action Action that specifies searched Goods
     * @return Shelve where the Goods specified by Action are stored or null if no such Goods are in the store
     */
    public Shelve getGoodsShelve(Action action) {
        try {
            String name = action.getName();
            int ID = action.getId();
            for (Goods good : goodsList) {
                if (good.getName().compareTo(name) == 0 || good.getId() == ID) {
                    return good.getShelve();
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /**
     * Remove goods that have 0 pieces left from the Store
     */
    public void removeEmptyGoods() {
        for (int i = 0; i < goodsList.size(); i++) {
            Goods remove = goodsList.get(i);
            if (remove.getCount() == 0) {
                Coordinates place = remove.getCoordinates();
                manager.freeShelve(place.getX() * height + place.getY());
                goodsList.remove(i);
            }
        }
    }

    /**
     * @param action Action that specifies searched Goods
     * @return Count of the Goods specified by the action in the store
     */
    public int getGoodsCount(Action action) {
        int count = 0;
        String name = action.getName();
        int ID = action.getId();
        for (Goods good : goodsList) {
            if (good.getName().compareTo(name) == 0 || good.getId() == ID) {
                count += good.getCount();
            }
        }
        return count;
    }

    /**
     * @param action Action that specifies searched Goods
     * @return Count of the Goods specified by action in not yet assigned requests
     */
    public int getGoodsRequestsListCount(Action action) {
        int count = 0;
        String name = action.getName();
        int ID = action.getId();
        for (Request request : requestsList) {
            for (Action actionFromList : request.getActionsList()) {
                if (actionFromList.getName().compareTo(name) == 0 || actionFromList.getId() == ID) {
                    count += actionFromList.getCount();
                }
            }
        }
        return count;
    }

    /**
     * @param action Action that specifies searched Goods
     * @return Count of the Goods specified by action in already assigned requests
     */
    public int getGoodsInForkliftsCount(Action action) {
        int count = 0;
        for (Forklift forklift : workingForkliftsList) {
            Request request = forklift.getRequest();
            List<Action> list = request.getActionsList();
            int index = list.indexOf(action);
            if (index != -1) {
                count += list.get(index).getCount();
            }
        }
        return count;
    }

    /**
     * @param x x-coordinate of the shelve in the shop
     * @param y y-coordinate of the shelve in the shop
     * @return Shelve Shelve located on these coordinates or null, if no shelve exists there
     */
    public Shelve getShelve(int x, int y) {
        for (Shelve shelve : shelvesList) {
            if (shelve.getCoordinates().getX() == x && shelve.getCoordinates().getY() == y) {
                return shelve;
            }
        }
        return null;
    }

    /**
     * Return a free shelve for Goods
     *
     * @return Shelve where Goods will be placed
     */
    private Shelve pickShelve() {
        int i = ThreadLocalRandom.current().nextInt(0, shelvesList.size());
        Shelve s = shelvesList.get(i);
        if (s.getStatus() == Shelve.ShelveStatus.FREE) {
            return s;
        }
        i = ThreadLocalRandom.current().nextInt(0, shelvesList.size());
        s = shelvesList.get(i);
        if (s.getStatus() == Shelve.ShelveStatus.FREE) {
            return s;
        }

        for (Shelve sh : shelvesList) {
            if (sh.getStatus() == Shelve.ShelveStatus.FREE) {
                return sh;
            }
        }
        return null;
    }

    /**
     * Remove all shelves from the store and goods that were on them
     */
    public void cleanShelves() {
        this.shelvesList.clear();
        this.goodsList.clear();
    }

    /**
     * Logs in app message given
     */
    public void logMessageStore(String msg) {
        manager.logMessageTA(msg);
    }

    /**
     * Add request to the request list to be done by forklift
     *
     * @param request Request to be done
     */
    public void addRequest(Request request) {
        requestsList.add(request);
    }

    /**
     * Check if there are any requests and free forklifts,if there are give request to a free forklift
     *
     * @return true if Request was given to a free forklift
     */
    public boolean delegateRequest() {
        if (requestsList.size() == 0 || freeForkliftsList.size() == 0) {
            return false;
        }
        System.out.println("Delegating request");
        Forklift forklift = freeForkliftsList.get(0);
        freeForkliftsList.remove(0);
        Request request = requestsList.get(0);
        requestsList.remove(0);
        forklift.setRequest(request);
        forklift.setFirstActionInProgress();
        workingForkliftsList.add(forklift);
        return true;
    }

    /**
     * Add request at the end of the store request list
     *
     * @param forklift Forklift whose request is to be queued
     */
    public void queueRequest(Forklift forklift) {
        this.requestsList.add(forklift.nullGetRequest());
        setForkliftFree(forklift);
    }

    /**
     * @param count Number of forklifts to be made
     */
    public void initForklifts(int count) {
        for (int i = 0; i < count; i++) {
            Forklift newForklift = new Forklift(i, 0, 0, this, i + 4);
            freeForkliftsList.add(newForklift);
        }
    }

    /**
     * @param forklift Forklift to be marked as free
     */
    public void setForkliftFree(Forklift forklift) {
        this.workingForkliftsList.remove(forklift);
        this.freeForkliftsList.add(forklift);
    }

    public void checkBlockedForklifts() {
        for (int i = 0; i < blockedForkliftsList.size(); i++) {
            blockedForkliftsList.get(i).tryUnblockForklift(); // TODo is this ok?
            continue;

        }
    }

    public void toBlocklist(Forklift forklift) {
        this.workingForkliftsList.remove(forklift);
        this.blockedForkliftsList.add(forklift);
    }

    public void fromBlocklist(Forklift forklift) {
        this.workingForkliftsList.add(forklift);
        this.blockedForkliftsList.remove(forklift);
    }

    /**
     * Mark request as done
     *
     * @param req Request that has been completed
     */
    public void setRequestAsDone(Request req) {
        doneRequestsList.add(0, req);
    }

    /**
     * Load store from the given file
     *
     * @param filePath Path to the map file
     * @return True if store was successfully set
     */
    public boolean setMap(String filePath) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            manager.logMessageTA("Map file not found, try again");
            return false;
        }
        String[] strSizeArray;
        try {
            strSizeArray = (br.readLine()).split(" ");
            width = Integer.parseInt(strSizeArray[0]);
            height = Integer.parseInt(strSizeArray[1]);
        } catch (IOException e) {
            manager.logMessageTA("Error while reading a file");
            return false;
        } catch (NumberFormatException e) {
            manager.logMessageTA("Wrong format of the input file");
            return false;
        }
        this.map = new int[width][height];
        String line;
        int lineCounter = 0;
        try {
            while ((line = br.readLine()) != null) {
                if (lineCounter >= height) {
                    manager.logMessageTA("Wrong number of rows given, check the input map file");
                    return false;
                }
                String[] strMapContent = line.split(" ");
                if (strMapContent.length != width) {
                    manager.logMessageTA("Wrong number of cols given, check the input map file");
                    return false;
                }
                for (int x = 0; x < width; x++) {
                    this.map[x][lineCounter] = Integer.parseInt(strMapContent[x]);
                }
                lineCounter++;
            }
        } catch (IOException e) {
            manager.logMessageTA("Error while reading a file");
            return false;
        } catch (NumberFormatException e) {
            manager.logMessageTA("Wrong format of the input file");
            return false;
        }
        return true;
    }

    /**
     * Load goods from the given file
     *
     * @param filePath Path to the goods file
     * @return True if goods were successfully set
     */
    public boolean setGoods(String filePath) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            manager.logMessageTA("Goods file not found, try again");
            return false;
        }
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] item = line.split(";");
                Shelve shelve = pickShelve();
                if (shelve == null) {
                    manager.logMessageTA("Not enough space for all goods");
                    break;
                }
                if (item.length != 4) {
                    manager.logMessageTA("Wrong format of the item in the Goods file");
                    continue;
                }
                String name = item[0];
                int ID;
                int count;
                String[] weight = item[2].strip().split(" ");
                double good_weight;
                if (weight.length != 2) {
                    manager.logMessageTA("Wrong format of the item in the Goods file");
                    continue;
                }
                if (weight[1].strip() == "kg") {
                    manager.logMessageTA("Wrong format of the item in the Goods file");
                    continue;
                }
                try {
                    ID = Integer.parseInt(item[1].strip());
                    count = Integer.parseInt(item[3].strip());
                    good_weight = Double.parseDouble(weight[0].strip());
                } catch (NumberFormatException e) {
                    manager.logMessageTA("Wrong format of the item in the Goods file");
                    continue;
                }
                int x = shelve.getCoordinates().getX();
                int y = shelve.getCoordinates().getY();
                Goods good = new Goods(name, ID, good_weight, count, x, y, shelve);
                shelve.setGoods(good);
                goodsList.add(good);
                manager.setUPGoods(x * height + y);
            }
        } catch (IOException e) {
            manager.logMessageTA("Error while reading a file");
            return false;
        }
        return true;
    }

    /**
     * Load shopping list from the given file
     *
     * @param filePath Path to the shopping list file
     * @return True if shopping list was successfully loaded
     */
    public boolean loadShoppingList(String filePath) {
        filePath = manager.getClassPath() + filePath;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            manager.logMessageTA("Shopping list file not found, try again");
            return false;
        }
        try {
            String line;
            ArrayList<Action> actionList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] item = line.split(";");
                Action action;
                if (item.length != 2) {
                    manager.logMessageTA("Shopping list not acceptable");
                    return false;
                }
                // Count
                int count;
                try {
                    count = Integer.parseInt(item[1].strip());
                } catch (NumberFormatException e) {
                    manager.logMessageTA("Shopping list not acceptable");
                    return false;
                }
                if (count < 1) {
                    manager.logMessageTA("Shopping list not acceptable");
                    return false;
                }
                // name or ID
                int ID = 0;
                try {
                    ID = Integer.parseInt(item[0]);
                } catch (Exception e) {
                    ;
                }
                if (ID != 0) {
                    action = new Action(ID, count);
                } else {
                    action = new Action(item[0].strip(), count);
                }
                Shelve shelve = getGoodsShelve(action);
                if (shelve == null) {
                    manager.logMessageTA("Shopping list not acceptable");
                    return false;
                }
                action.setId(shelve.getGoodsId());
                action.setName(shelve.getGoods().getName());
                if ((getGoodsCount(action)) < (count +

                       manager.getController().actionListGoodsCount(action,actionList) + getGoodsRequestsListCount(action) + getGoodsInForkliftsCount(action))) {
                    manager.logMessageTA("Not enough goods for the shopping list");
                    return false;
                }
                int index = actionList.indexOf(action);
                if (index == -1) {
                    actionList.add(action);
                } else {
                    Action existing_action = actionList.get(index);
                    existing_action.setCount(existing_action.getCount() + count);
                }
            }
            Request new_request = new Request(actionList);
            addRequest(new_request);
            return true;
        } catch (IOException e) {
            manager.logMessageTA("Error while reading a file");
            return false;
        }
    }


    /**
     * @return Coordinates of the starting point in the map
     */
    public Coordinates getHomeCoordinates() {
        return homeCoordinates;
    }
}
