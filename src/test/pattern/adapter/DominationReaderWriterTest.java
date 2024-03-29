package test.pattern.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

import dao.Continent;
import dao.Country;
import dao.Map;
import mapworks.MapEditor;
import pattern.adapter.DominationReaderWriter;

/**
 * This class tests the functions in DominationReaderWriter.java class
 * 
 * @author divya_000
 *
 */
public class DominationReaderWriterTest {

	/**
	 * Object of Map
	 */
	static Map testMap;
	/**
	 * Object of MapEditor
	 */
	static MapEditor mapEditor;
	/**
	 * Object of DominationReaderWriter
	 */
	static DominationReaderWriter drw;
	/**
	 * List of objects of Countries
	 */
	static List<Country> testlistofCountries;
	/**
	 * List of objects of Neighbors of countries
	 */
	static List<String> testListofNeighbors1;
	/**
	 * List of objects of Neighbors of countries
	 */
	static List<String> testListofNeighbors2;
	/**
	 * List of objects of Neighbors of countries
	 */
	static List<String> testListofNeighbors3;

	/**
	 * This method is used for initialization and set up before running tests
	 */
	@BeforeClass
	public static void beforeClass() {
		mapEditor = new MapEditor();
		mapEditor.editContinent(("editcontinent -add asia 10 -add africa 14").split(" "));
		mapEditor.editCountry(
				("editcountry -add india asia -add pakistan asia -add china asia -add congo africa -add uganda africa")
						.split(" "));
		mapEditor.editNeighbor(
				("editneighbor -add india pakistan -add pakistan china -add india congo -add congo uganda").split(" "));
		testMap = mapEditor.getMap();

		drw = new DominationReaderWriter();

		Country country1 = new Country();
		country1.setName("india");

		Country country2 = new Country();
		country2.setName("china");

		Country country3 = new Country();
		country3.setName("pakistan");

		testlistofCountries = new ArrayList<Country>();
		testlistofCountries.add(country1);
		testlistofCountries.add(country2);
		testlistofCountries.add(country3);

		testListofNeighbors1 = new ArrayList<String>();
		testListofNeighbors1.add("pakistan");
		testListofNeighbors1.add("china");
		country1.setNeighbors(testListofNeighbors1);

		testListofNeighbors2 = new ArrayList<String>();
		testListofNeighbors2.add("india");
		country2.setNeighbors(testListofNeighbors2);

		testListofNeighbors3 = new ArrayList<String>();
		testListofNeighbors3.add("india");
		country3.setNeighbors(testListofNeighbors3);

	}

	/**
	 * Method to test {@link pattern.adapter.DominationReaderWriter#parseMapFile(Map, File)}
	 */
	@Test
	public void testParseMap() {
		drw = new DominationReaderWriter();
		testMap = new Map();
		String filename = "ameroki.map";
		String currentPath = System.getProperty("user.dir");
		currentPath += "\\Maps\\" + filename;
		File newFile = new File(currentPath);
		int test = drw.parseMapFile(testMap,newFile);
		assertEquals(1, test);
	}

	/**
	 * Method to test {@link pattern.adapter.DominationReaderWriter#loadContinents(Map)}
	 */
	@Test
	public void testLoadContinent() {

		drw = new DominationReaderWriter();
		testMap = new Map();
		Continent continent1 = new Continent();
		continent1.setName("asia");
		continent1.setContinentValue(11);

		List<Continent> testlistOfContinent = new ArrayList<Continent>();
		testlistOfContinent.add(continent1);

		String testname = " ";
		int testcontVal = 0;

		testname = testlistOfContinent.get(0).getName();
		testcontVal = testlistOfContinent.get(0).getContinentValue();

		try {
			String filename = "abcd.map";
			String currentPath = System.getProperty("user.dir");
			currentPath += "\\Maps\\" + filename;
			File newFile = new File(currentPath);
			drw.parseMapFile(testMap,newFile);
		} catch (Exception e) {
			// TODO: handle exception
		}

		String name = " ";
		int contVal = 0;

		name = testMap.getListOfContinent().get(0).getName();
		contVal = testMap.getListOfContinent().get(0).getContinentValue();

		assertEquals(testname, name);
		assertEquals(testcontVal, contVal);
	}

	/**
	 * Method to test {@link pattern.adapter.DominationReaderWriter#loadCountries(Map)}
	 */
	@Test
	public void testLoadCountries() {
		drw = new DominationReaderWriter();
		testMap = new Map();

		String[] testNameofCountry = new String[testlistofCountries.size()];
		for (int i = 0; i < testlistofCountries.size(); i++) {
			testNameofCountry[i] = testlistofCountries.get(i).getName();
		}

		try {
			String filename = "abcd.map";
			String currentPath = System.getProperty("user.dir");
			currentPath += "\\Maps\\" + filename;
			File newFile = new File(currentPath);
			drw.parseMapFile(testMap,newFile);
		} catch (Exception e) {
			// TODO: handle exception
		}

		String[] nameOfCountry = new String[testMap.getListOfCountries().size()];
		for (int i = 0; i < testMap.getListOfCountries().size(); i++) {
			nameOfCountry[i] = testMap.getListOfCountries().get(i).getName();
		}

		for (int i = 0; i < testNameofCountry.length; i++) {
			assertEquals(testNameofCountry[i], nameOfCountry[i]);
		}

	}

	/**
	 * Method to test {@link pattern.adapter.DominationReaderWriter#loadBorders(Map)}
	 */
	@Test
	public void testLoadBorders() {

		drw = new DominationReaderWriter();
		testMap = new Map();

		try {
			String filename = "abcd.map";
			String currentPath = System.getProperty("user.dir");
			currentPath += "\\Maps\\" + filename;
			File newFile = new File(currentPath);
			drw.parseMapFile(testMap,newFile);
		} catch (Exception e) {
			// TODO: handle exception
		}

		List<String> country1Neighbors = new ArrayList<String>();
		List<String> country2Neighbors = new ArrayList<String>();
		List<String> country3Neighbors = new ArrayList<String>();

		for (int i = 0; i < testMap.getListOfCountries().get(0).getNeighbors().size(); i++) {
			country1Neighbors.add(testMap.getListOfCountries().get(0).getNeighbors().get(i));
		}

		for (int i = 0; i < testMap.getListOfCountries().get(1).getNeighbors().size(); i++) {
			country2Neighbors.add(testMap.getListOfCountries().get(1).getNeighbors().get(i));
		}

		for (int i = 0; i < testMap.getListOfCountries().get(2).getNeighbors().size(); i++) {
			country3Neighbors.add(testMap.getListOfCountries().get(2).getNeighbors().get(i));
		}

		for (int i = 0; i < testListofNeighbors1.size(); i++)
			assertEquals(testListofNeighbors1.get(i), country1Neighbors.get(i));

		for (int i = 0; i < testListofNeighbors2.size(); i++)
			assertEquals(testListofNeighbors2.get(i), country2Neighbors.get(i));

		for (int i = 0; i < testListofNeighbors3.size(); i++)
			assertEquals(testListofNeighbors3.get(i), country3Neighbors.get(i));

	}
	
	/**
	 * Method to test {@link pattern.adapter.DominationReaderWriter#saveMap(Map, String)}
	 */
	@Test
	public void testMapSaver() {
		try {
			drw.saveMap(testMap, "testMap1");
			String currentPath = System.getProperty("user.dir");
			currentPath += "\\Maps\\" + "testMap1.map";
			File newFile = new File(currentPath);
			assertTrue(newFile.exists());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}