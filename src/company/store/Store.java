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

public class Store {

    private List<Shelve> shelvesList;
    private List<Goods> goodsList;
    private List<Forklift> freeForkliftsList;
    private List<Forklift> workingForkliftsList;
    private List<Request> requestsList;
    private List<Request> doneRequestsList;
    private StoreManager manager;
    private int[][] map;
    private int width;
    private int height;
    private Coordinates homeCoordinates;

    public Store() {
        shelvesList = new ArrayList<>();
        goodsList = new ArrayList<>();
        freeForkliftsList = new ArrayList<>();
        workingForkliftsList = new ArrayList<>();
        requestsList = new ArrayList<>();
        doneRequestsList = new ArrayList<>();
        width = 0;
        height = 0;
        homeCoordinates = new Coordinates(0, 0);
    }

    public void main() {
        if (!setGoods(manager.getGoodsPath())) {
            setGoods(manager.getDefaultGoodsPath());
        }

        while (true) {

            if (workingForkliftsList.size() != 0) {
                for (Forklift forklift : workingForkliftsList) {
                    delegateRequest();  // TODO new requests is this enough?
                    if (forklift.getPath().size() == 0 || forklift.getActionInProgress() == null) {
                        forklift.setFirstActionInProgress();
                        forklift.countPath(getGoodsShelve(forklift.getActionInProgress()).getCoordinates()); // TODO uh oh?
                    }
                    if(forklift.getFirstPath().equals(homeCoordinates)){
                        // TODO empty forklift goods list, action done, request done if action count 0 and actionList empty?
                    }
                    // move
                    if (getMapValue(forklift.getFirstPath().getX(), forklift.getFirstPath().getY()) == MapCoordinateStatus.SHELVE.Val) { //is shelve -> do action
                        //TODO check if right shelve
                        forklift.doAction();
                        if (forklift.getRequest().getActionsList().size() != 0) { // request has some actions left
                            forklift.setFirstActionInProgress();
                            forklift.countPath(getGoodsShelve(forklift.getActionInProgress()).getCoordinates());
                            forklift.printPath();
                        } else { // request done
                            forklift.countPath(homeCoordinates);
                            forklift.printPath();
                        }
                        if(forklift.getPath() == null){
                            // TODO add request at the end of waiting list
                            forklift.countPath(homeCoordinates);
                            // TODO what if blocked path to home fml
                        }
                    } else if (getMapValue(forklift.getFirstPath().getX(), forklift.getFirstPath().getY()) == MapCoordinateStatus.BLOCK.Val) {
                        forklift.countPath(getGoodsShelve(forklift.getActionInProgress()).getCoordinates());
                        forklift.moveForward();
                        setMapValue(forklift.getCoordinates().getX(), forklift.getCoordinates().getY(), statusMapper(forklift.getStatus()));
                    } else { //is free path or forklift
                        forklift.moveForward();

                    }

                }
            }
        }
    }

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

    public void setManager(StoreManager manager) {
        this.manager = manager;
    }

    public StoreManager getManager() {
        return manager;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMapValue(int x, int y) {
        if (y >= height || x >= width) {
            return -1;
        }
        return map[x][y];
    }

    public boolean setMapValue(int x, int y, MapCoordinateStatus mapStatus) {
        if (y >= height || x >= width) {
            return false;
        }
        map[x][y] = mapStatus.getNumVal();
        return true;
    }

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

    public void updateMapValueRemove(int x, int y, Forklift.ForkliftStatus forkliftStatus) {
        if (y >= height || x >= width) {
            return;
        }
        map[x][y] -= forkliftStatus.getNumVal();
    }

    public void createShelve(int id, int x, int y) {
        Shelve shelf = new Shelve(id, x, y, this);
        shelvesList.add(shelf);
    }

    public Shelve getGoodsShelve(Action action) {
        String name = action.getName();
        int ID = action.getId();
        for (Goods good : goodsList) {
            if (good.getName().compareTo(name) == 0 || good.getId() == ID) {
                return good.getShelve();
            }
        }
        return null;
    }

    public void removeEmptyGoods() {
        for (int i = 0; i < goodsList.size(); i++) {
            Goods remove = goodsList.get(i);
            if (remove.getCount() == 0) {
                goodsList.remove(i);
            }
        }
    }

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

    public Shelve getShelve(int x, int y) {
        for (Shelve shelve : shelvesList) {
            if (shelve.getCoordinates().getX() == x && shelve.getCoordinates().getY() == y) {
                return shelve;
            }
        }
        return null;
    }

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

    public void cleanShelves() {
        this.shelvesList.clear();
    }

    public void addRequest(Request request) {
        requestsList.add(request);
    }

    public boolean delegateRequest() {
        if (requestsList.size() == 0 || freeForkliftsList.size() == 0) {
            return false;
        }
        Forklift forklift = freeForkliftsList.get(0);
        freeForkliftsList.remove(0);
        Request request = requestsList.get(0);
        requestsList.remove(0);
        forklift.setRequest(request);
        workingForkliftsList.add(forklift);
        return true;
    }

    public void setRequestAsDone(Request req) {
        doneRequestsList.add(0, req);
    }

    public boolean setMap(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            manager.logMessageTA("Map file not found, try again");
            return false;
        }
        String[] strSizeArray = null;
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

    public boolean setGoods(String filePath) {
        BufferedReader br = null;
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
                Integer ID = 0;
                Integer count = 0;
                String[] weight = item[2].strip().split(" ");
                Double good_weight = 0.0;
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

    public Coordinates getHomeCoordinates() {
        return homeCoordinates;
    }
}
