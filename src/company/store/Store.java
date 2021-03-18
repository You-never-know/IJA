package company.store;

import company.store.forklift.Forklift;
import company.store.shelve.Shelve;
import company.store.request.Request;
import company.store.shelve.goods.Goods;
import java.io.BufferedReader;
import java.io.FileReader;

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
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String[] strSizeArray = (br.readLine()).split(" ");
			int xSize = Integer.parseInt(strSizeArray[0]);
			int ySize = Integer.parseInt(strSizeArray[1]);
			//TODO err parsed not int
			this.map = new int[xSize][ySize];
			// TODO load map from file
			String line;
			int lineCounter = 0;
			while ((line = br.readLine()) != null) {
				if(lineCounter >= ySize){
					break;
					// TODO err *panik* + > VS >= ?
				}
				String[] strMapContent = line.split(" ");
				if(strMapContent.length != xSize){
					// TODO err
				}

				for (int x = 0; x < xSize; x++) {
					this.map[x][lineCounter] = Integer.parseInt(strMapContent[x]);
					//TODO err parsed not int
				}

				// TODO loop: str to int + assign to map array
				lineCounter++;
			}
		} catch(Exception e) {
			// TODO catch errors
		}
	}


}
