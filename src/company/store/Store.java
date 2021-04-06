package company.store;

import company.StoreManager;
import company.store.forklift.Forklift;
import company.store.shelve.Shelve;
import company.store.request.Request;
import company.store.shelve.goods.Goods;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.util.*;
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

		//while (true) {;}
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

	public boolean setMap(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			System.err.println("Map file not found, try again"); // TODO write this in GUI
			return false;
		}
		String[] strSizeArray = null;
		try {
			strSizeArray = (br.readLine()).split(" ");
			width = Integer.parseInt(strSizeArray[0]);
			height = Integer.parseInt(strSizeArray[1]);
		} catch (IOException e) {
			System.err.println("Error while reading a file"); // TODO write this in GUI
			return false;
		} catch (NumberFormatException e) {
			System.err.println("Wrong format of the input file"); // TODO write this in GUI
			return false;
		}
		this.map = new int[width][height];
		String line;
		int lineCounter = 0;
		try {
			while ((line = br.readLine()) != null) {
				if (lineCounter >= height) {
					System.err.println("Wrong number of rows given, check the input map file"); // TODO write this in GUI
					return false;
				}
				String[] strMapContent = line.split(" ");
				if (strMapContent.length != width) {
					System.err.println("Wrong number of cols given, check the input map file"); // TODO write this in GUI
					return false;
				}
				for (int x = 0; x < width; x++) {
					this.map[x][lineCounter] = Integer.parseInt(strMapContent[x]);
				}
				lineCounter++;
			}
		} catch (IOException e) {
			System.err.println("Error while reading a file"); // TODO write this in GUI
			return false;
		} catch (NumberFormatException e) {
			System.err.println("Wrong format of the input file"); // TODO write this in GUI
			return false;
		}
		return true;
	}

	public boolean setGoods(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			System.err.println("Goods file not found, try again"); // TODO write this in GUI
			return false;
		}

		try {
			String line;
			int max_shelves = shelvesList.size();
			while ((line = br.readLine()) != null) {
				String[] item = line.split(";");
				Shelve shelve = pick_shelf();
				if (shelve == null) {
					break;
				}
				if (item.length != 4) {
					System.out.println("Wrong format of the item in the Goods file"); // TODO write this in GUI
					continue;
				}
				String name = item[0];
				Integer ID = 0;
				Integer count = 0;
				String[] weight = item[2].strip().split(" ");
				Double good_weight = 0.0;
				if (weight.length != 2) {
					System.out.println("Wrong format of the item in the Goods file"); // TODO write this in GUI
					continue;
				}
				if (weight[1].strip() == "kg") {
					System.out.println("Wrong format of the item in the Goods file"); // TODO write this in GUI
					continue;
				}
				try {
					ID = Integer.parseInt(item[1].strip());
					count = Integer.parseInt(item[3].strip());
					good_weight = Double.parseDouble(weight[0].strip());
				} catch (NumberFormatException e) {
					System.out.println("Wrong format of the item in the Goods file"); // TODO write this in GUI
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
			System.err.println("Error while reading a file"); // TODO write this in GUI
			return false;
		}
		return true;
	}

}
