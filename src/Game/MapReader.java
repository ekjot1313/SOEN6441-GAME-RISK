package Game;

import Game.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MapReader {
	public Map map;
	private File fileObject;
	private BufferedReader bufferReaderForFile;
	private String currentLine;
	public HashMap<Integer, List<Integer>> mapOfWorld = new HashMap<Integer, List<Integer>>();
	
	

	public void parseMapFile(String filePath) {
		map = new Map();
		map.listOfContinent = new ArrayList<Continent>();
		map.listOfCountries = new ArrayList<Country>();
		try {

			bufferReaderForFile = new BufferedReader(new FileReader(new File(filePath)));
			while ((currentLine = bufferReaderForFile.readLine()) != null) {

				if (currentLine.contains("[continents]")) {
					loadContinents();
				}
				
				if (currentLine.contains("[countries]")) {
					loadCountries();
				}
				
				if (currentLine.contains("[borders]")) {
					loadBorders();
				}

			}

		
			System.out.println(mapOfWorld.toString());
			// validate map call
			int notConnected = validateMap();
			System.out.println();
			if (notConnected == 0) {
				System.out.println("Valid Map");
				// display
				display(map);
			} else
				System.out.println("Invalid map");

		} catch (Exception e) {
			if (e.toString().contains("FileNotFoundException"))
				System.out.println("Invalid filename");
		}

	}

	private void loadBorders() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		while ((currentLine = bufferReaderForFile.readLine()) != null && !currentLine.contains("[")) {
			// System.out.println(currentLine);
			if (currentLine.length() == 0) {
				continue;
			}
			String[] neighbourDetails = currentLine.split(" ");
			for (int i = 0; i < neighbourDetails.length - 1; i++) {
				map.listOfCountries.get(Integer.parseInt(neighbourDetails[0]) - 1).neighbours
						.add(map.listOfCountries.get(Integer.parseInt(neighbourDetails[i + 1]) - 1));

			}
		}
	
	}

	private void loadCountries() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		while ((currentLine = bufferReaderForFile.readLine()) != null && !currentLine.contains("[")) {

			// System.out.println(currentLine);
			if (currentLine.length() == 0) {
				continue;
			}
			String[] countryDetails = currentLine.split(" ");

			Country country = new Country();
			country.setName(countryDetails[1]);
			country.setContinentName(map.listOfContinent.get((Integer.parseInt(countryDetails[2])) - 1));
			map.listOfCountries.add(country);
			map.listOfContinent.get((Integer.parseInt(countryDetails[2])) - 1).getCountries().add(country);
		}
	
		
	}

	private void loadContinents() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		while ((currentLine = bufferReaderForFile.readLine()) != null && !currentLine.contains("[")) {

			// System.out.println(currentLine);
			if (currentLine.length() == 0) {
				continue;
			}
			String[] continentDetails = currentLine.split(" ");

			Continent continent = new Continent();
			continent.setName(continentDetails[0]);
			continent.setContinentValue(Integer.parseInt(continentDetails[1]));
			map.listOfContinent.add(continent);

		}
	
	}

	/**
	 * This method display/print the map
	 * 
	 * @param map2
	 */
	public void display(Map map2) {
		// TODO Auto-generated method stub

		for (Continent c : map2.listOfContinent) {
			System.out.println("Continent :" + c.getName());
			for (Country c1 : c.getCountries()) {
				System.out.print("Country :" + c1.getName() + ": Neighbours->");

				for (Country c2 : c1.getNeighbours()) {
					System.out.print(c2.getName() + "||");
				}
				System.out.println();
			}
			System.out.println();
		}

	}

	public int validateMap() {
		// traversing
		int notConnected = 0;
		if (checkDuplicates() == 0) {
			
			// graph creation

			for (int i = 0; i < map.listOfCountries.size(); i++) {
				List<Integer> templist = new ArrayList<Integer>();
				for (int j = 0; j < map.listOfCountries.get(i).getNeighbours().size(); j++)
					templist.add(map.listOfCountries.indexOf(map.listOfCountries.get(i).neighbours.get(j)));
				mapOfWorld.put(i, templist);

			}

			Boolean[] visited = new Boolean[mapOfWorld.keySet().size()];
			for (int i = 0; i < visited.length; i++) {
				visited[i] = false;
			}

			LinkedList<Integer> queue = new LinkedList<Integer>();
			queue.add(0);
			visited[0] = true;
			// System.out.println(queue.poll().name);

			while (queue.size() > 0) {
				Integer c1 = queue.poll();
				Iterator i = mapOfWorld.get(c1).listIterator();
				while (i.hasNext()) {
					int n = (int) i.next();
					if (visited[n] == false) {
						visited[n] = true;
						queue.add(n);
					}

				}

			}

			for (int i = 0; i < visited.length; i++) {
				System.out.print(i + "=" + visited[i] + " ||");
				if (!visited[i]) {
					notConnected = 1;
					System.out.println();
					System.out.println("Not a connected graph");
					break;
				}
			}

		} else
			notConnected = 1;

		return notConnected;
	}

	public int checkDuplicates() {
		int duplicate = 0;
		for (int i = 0; i < (map.listOfContinent.size() - 1); i++)
			for (int j = i + 1; j < map.listOfContinent.size(); j++)
				if ((map.listOfContinent.get(i).getName()).equalsIgnoreCase(map.listOfContinent.get(j).getName())) {
					duplicate = 1;
					System.out.println("Duplicate Continent :" + map.listOfContinent.get(i).getName());
					break;
				}
		if (duplicate == 0)
			for (int i = 0; i < (map.listOfCountries.size() - 1); i++)
				for (int j = i + 1; j < map.listOfCountries.size(); j++)
					if ((map.listOfCountries.get(i).getName()).equalsIgnoreCase(map.listOfCountries.get(j).getName())) {
						duplicate = 1;
						System.out.println("Duplicate Country :" + map.listOfCountries.get(i).getName());
						break;
					}
		return duplicate;
	}

	public static void main(String args[]) {
		MapReader m = new MapReader();
		String filename = "ameroki.map";
		System.out.println(System.getProperty("user.dir"));
		String currentPath = System.getProperty("user.dir");
		currentPath += "\\src\\Maps\\" + filename;
		m.parseMapFile(currentPath);
	}

	/**
	 * This method returns the currently loaded map
	 * 
	 * @return
	 */
	public Map getMap() {
		return this.map;
	}

}