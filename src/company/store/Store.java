package company.store;

import company.store.forklift.Forklift;
import company.store.shelve.Shelve;
import company.store.request.Request;
import company.store.shelve.goods.Goods;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.util.*;

public class Store {

	private List<Shelve> shelvesList;
	private List<Goods> goodsList;
	private List<Forklift> freeForkliftsList;
	private List<Forklift> workingForkliftsList;
	private List<Request> requestsList;
	private List<Request> doneRequestsList;
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

	public int GetWidth() { return width; }

	public int GetHeight() { return height; }

	public int GetMapValue(int row, int coll)
	{
		if (row >= height || coll >= width) {
			return -1;
		}
		return map[row][coll];
	}

	public boolean setMap(String filePath) {
		BufferedReader br = null;
		try {
			System.out.println(filePath);
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			System.err.println("File not found, try again"); // TODO write this in GUI
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


}
