package mapWorks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import dao.Continent;
import dao.Country;
import dao.Map;

/**
 * This Class is used to save the given Map object to .map file
 * 
 * @author Ekjot
 * @author Hartaj
 *
 */
public class MapSaver {
	
	/**
	 * This method saves the map to .map file
	 * 
	 * @param map      Map Object
	 * @param fileName Name of the file
	 * @throws IOException for Buffered Reader
	 */
	public void saveMap(Map map, String fileName) throws IOException {
		
		String message1;
		String message2;
		String mapName;
		message1 = map.getMessage1();
		message2 = map.getMessage2();
		mapName = map.getMapName();
		String currentPath = System.getProperty("user.dir") + "\\Maps\\";
		String mapPath = currentPath + fileName + ".map";
		BufferedWriter bwFile = new BufferedWriter(new FileWriter(mapPath));
		String content = "";
		content += (message1 + "\r\n");
		content += ("\r\nname " + mapName + " Map\r\n");
		content += ("\r\n" + message2 + "\r\n");
		content += ("\r\n[continents]\r\n");
		for (Continent continent : map.getListOfContinent()) {
			content += (continent.getName() + " " + continent.getContinentValue() + " 00000\r\n");
		}
		content += ("\r\n[countries]\r\n");
		String borders = "";
		for (Country country : map.getListOfCountries()) {
			int countryIndex = map.getListOfCountries().indexOf(country) + 1;
			int continentIndex = map.getListOfContinent().indexOf(map.getContinentFromName(country.getContinentName()))
					+ 1;

			content += (countryIndex + " " + country.getName() + " " + continentIndex + "\r\n");
			borders += (countryIndex + "");
			for (String neighborName : country.getNeighbors()) {
				int neighborIndex = map.getListOfCountries().indexOf(map.getCountryFromName(neighborName)) + 1;
				borders += (" " + neighborIndex);
			}
			borders += ("\r\n");
		}
		content += ("\r\n[borders]\r\n" + borders);
		bwFile.write(content);
		bwFile.close();
		System.out.println("Map file saved as: " + fileName + ".map");
	}

}
