package company.store;

import company.StoreManager;
import company.store.forklift.Forklift;
import company.store.request.Request;
import company.store.request.action.Action;
import company.store.shelve.Shelve;
import company.store.shelve.goods.Goods;

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

	public Store () {
		shelvesList = new ArrayList<>();
		goodsList = new ArrayList<>();
		freeForkliftsList = new ArrayList<>();
		workingForkliftsList = new ArrayList<>();
		requestsList = new ArrayList<>();
		doneRequestsList = new ArrayList<>();
		width = 0;
		height = 0;
	}

	public void main () {
		if (! setGoods(manager.get_goods_path())) {
			setGoods(manager.get_default_goods_path());
		}

		while (true) {
			;
		}
	}

	public void setManager(StoreManager manager) { this.manager = manager; }
	public StoreManager getManager() { return manager; }

	public int GetWidth() { return width; }

	public int GetHeight() { return height; }

	public int GetMapValue(int x, int y)
	{
		if (y >= height || x >= width) {
			return -1;
		}
		return map[x][y];
	}

	public void create_shelf(int id, int x, int y) {
		Shelve shelf = new Shelve(id,x,y);
		shelvesList.add(shelf);
	}

	public Shelve get_goods_shelve(Action action) {
		String name = action.getName();
		int ID = action.getID();
		for (Goods good: goodsList) {
			if (good.getName().compareTo(name) == 0 || good.getId() == ID) {
				return good.getShelve();
			}
		}
		return null;
	}

	public int getGoodsCount(Action action) {
		int count = 0;
		String name = action.getName();
		int ID = action.getID();
		for (Goods good: goodsList) {
			if (good.getName().compareTo(name) == 0 || good.getId() == ID) {
				count += good.getCount();
			}
		}
		return count;
	}

	public int getGoodsRequestsListCount(Action action) {
		int count = 0;
		String name = action.getName();
		int ID = action.getID();
		for (Request request : requestsList) {
			for (Action actionFromList : request.getActionsList()) {
				if (actionFromList.getName().compareTo(name) == 0 || actionFromList.getID() == ID) {
					count += actionFromList.getCount();
				}
			}
		}
		return count;
	}

	public Shelve get_shelve(int x, int y) {
		for (Shelve shelve:shelvesList) {
			if (shelve.getCoordinates().getX() == x && shelve.getCoordinates().getY() == y) {
				return shelve;
			}
		}
		return null;
	}

	private Shelve pick_shelf() {
		int i = ThreadLocalRandom.current().nextInt(0, shelvesList.size());
		Shelve s = shelvesList.get(i);
		if (s.getStatus() == Shelve.ShelveStatus.FREE) { return s; }
		i = ThreadLocalRandom.current().nextInt(0, shelvesList.size());
		s = shelvesList.get(i);
		if (s.getStatus() == Shelve.ShelveStatus.FREE) { return s; }

		for (Shelve sh: shelvesList) {
			if (sh.getStatus() == Shelve.ShelveStatus.FREE) { return sh; }
		}
		return null;
	}

	public void cleanShelves () {
		this.shelvesList.clear();
	}

	public void add_request (Request request) {
		requestsList.add(request);
	}

	public boolean delegate_request() {
		if (requestsList.size() == 0 || freeForkliftsList.size() == 0) {
			return false;
		}
		Forklift forklift = freeForkliftsList.get(0);
		freeForkliftsList.remove(0);
		Request request = requestsList.get(0);
		requestsList.remove(0);

		// TODO add the request to the forklift and start the process of doing it, maybe in a new Thread, so more forklifts can work at the same time
		// add the request to the forklift
		//new Thread(() -> { do_the_request });
		return true;
	}

	public boolean processRequests(){
		while(requestsList.size() != 0){
			for (Forklift forklift: workingForkliftsList) {
				if(forklift.getPath().size() == 0 || forklift.getActionInProgress() == null){
					forklift.setFirstActionInProgress();
					forklift.countPath(get_goods_shelve(forklift.getActionInProgress()).getCoordinates()); // TODO uh oh?
				}

				// move
				//TODO crossing ?!

				if(GetMapValue(forklift.getFirstPath().getX(), forklift.getFirstPath().getY()) == 0){
					forklift.moveForward();
				}else if(GetMapValue(forklift.getFirstPath().getX(), forklift.getFirstPath().getY()) == 1){ //TODO check if right shelve
					forklift.doAction();
					if(forklift.getRequest().getActionsList().size() != 0){
						forklift.setFirstActionInProgress();
						forklift.countPath(get_goods_shelve(forklift.getActionInProgress()).getCoordinates());
						//TODO Bearings
					}else{
						//TODO go home : problem : no method for path to coordinates without action - rework
						forklift.countPath(get_goods_shelve(forklift.getActionInProgress()).getCoordinates());
					}

				}else if(GetMapValue(forklift.getFirstPath().getX(), forklift.getFirstPath().getY()) == 2){
					forklift.countPath(get_goods_shelve(forklift.getActionInProgress()).getCoordinates());
					forklift.moveForward();
				}

			}
		}
				return true;
	}

	public void set_request_as_done(Request req) {
		doneRequestsList.add(0,req);
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
				Shelve shelve = pick_shelf();
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
				Goods good = new Goods(name,ID,good_weight,count,x,y,shelve);
				shelve.setGoods(good);
				goodsList.add(good);
				manager.setUP_Goods(x*height + y);
			}
		} catch (IOException e) {
			manager.logMessageTA("Error while reading a file");
			return false;
		}
		return true;
	}
}
