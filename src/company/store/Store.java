package company.store;

import company.store.forklift.Forklift;
import company.store.shelve.Shelve;
import company.store.request.Request;
import company.store.shelve.goods.Goods;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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
			this.map = new int[xSize][ySize];
			// TODO load map from file
			String line;
			while ((line = br.readLine()) != null) {
				String[] strMapContent = line.split(" ");
				if(strMapContent.length != xSize){
					// TODO err
				}
				// TODO loop: str to int + assign to map array
			}
		} catch(Exception e) {
			// TODO catch errors
		}
	}


}
