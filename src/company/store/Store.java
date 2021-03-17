package company.store;

import company.store.forklift.Forklift;
import company.store.shelve.Shelve;
import company.store.request.Request;
import company.store.shelve.goods.Goods;

import java.util.List;

public class Store {

	private List<Shelve> shelvesList;
	private List<Goods> goodsList; // TODO had to be #map from pdf skeleton... is this needed? Why?
	private List<Forklift> freeForkliftsList;
	private List<Forklift> workingForkliftsList;
	private List<Request> requestsList;
	private List<Request> doneRequestsList;
	private int[][] map;

	public Store () {
	;
	}

	public void setMap(String filePath) {
		// TODO import reader
		String[] strSizeArray = (filePath.readLine()).split(" ");
		int xSize = Integer.parseInt(strSizeArray[0]);
		int ySize = Integer.parseInt(strSizeArray[1]);
		// TODO catch errors
		this.map = new int[xSize][ySize];
		// TODO load map from file
		line = filePath.readLine();
		while (line!= null){
			String[] strMapContent = (filePath.readLine()).split(" ");
			if(strMapContent.length != xSize){
				// TODO err
			}
			// TODO loop: str to int + assign to map array
			line = filePath.readLine();
		}
	}
}
