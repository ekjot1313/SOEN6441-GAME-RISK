/**
 *
 */
package test.pattern.strategy;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import dao.Continent;
import dao.Country;
import dao.Map;
import dao.Player;
import pattern.strategy.RandomStrategy;

/**
 * @author ekjot
 *
 */
public class RandomStrategyTest {

	/**
	 * Object of Map
	 */
	Map testMap;

	Continent asia;
	/**
	 * Object of Country india
	 */
	Country india;
	/**
	 * Object of Country pakistan
	 */
	Country pakistan;
	/**
	 * Object of Country china
	 */
	Country china;
	/**
	 * Object of Player A
	 */
	Player A;
	/**
	 * Object of Player B
	 */
	Player B;
	/**
	 * ArrayList to store the list of players
	 */
	ArrayList<Player> listOfPlayers;

	/**
	 * @throws java.lang.Exception exceptions
	 */
	@Before
	public void setUp() throws Exception {

		testMap = new Map();
		asia = new Continent();
		india = new Country();
		pakistan = new Country();
		china = new Country();
		A = new Player();
		B = new Player();

		india.setName("india");
		india.setContinentName("asia");
		india.setNoOfArmies(20);
		india.setOwner("A");
		india.getNeighbors().add("pakistan");
		india.getNeighbors().add("china");

		pakistan.setName("pakistan");
		pakistan.setContinentName("asia");
		pakistan.setNoOfArmies(20);
		pakistan.setOwner("A");
		pakistan.getNeighbors().add("india");
		china.setName("china");
		china.setContinentName("asia");
		china.setNoOfArmies(40);
		china.setOwner("B");
		china.getNeighbors().add("india");
		testMap.getListOfCountries().add(india);
		testMap.getListOfCountries().add(pakistan);
		testMap.getListOfCountries().add(china);
		asia = new Continent();
		testMap.getListOfContinent().add(asia);

		A.setName("A");
		A.setNoOfArmies(40);
		A.getAssigned_countries().add(india);
		A.getAssigned_countries().add(pakistan);
		A.setStrategy(new RandomStrategy());
		B.setName("B");
		B.setNoOfArmies(40);
		B.setStrategy(new RandomStrategy());
		B.getAssigned_countries().add(china);
		listOfPlayers = (ArrayList<Player>) testMap.getListOfPlayers();

		testMap.getListOfPlayers().add(A);
		testMap.getListOfPlayers().add(B);

	}

	/**
	 * Test method for
	 * {@link pattern.strategy.RandomStrategy#reinforcement(dao.Map, java.util.ArrayList, dao.Player)}.
	 */
	@Test
	public void testReinforcement() {

		// simple test without card exchange
		int initialArmies = 0;
		int expected = A.getStrategy().calculateReinforceArmies(testMap, A);
		for (Country country : A.getAssigned_countries()) {

			initialArmies += country.getNoOfArmies();
		}

		A.executeReinforcement(testMap, listOfPlayers);

		int reinforcedArmies = 0;
		for (Country country : A.getAssigned_countries()) {
			reinforcedArmies += country.getNoOfArmies();
		}
		reinforcedArmies -= initialArmies;
		assertTrue(reinforcedArmies == expected);

		// test with 3 same card types
		initialArmies = 0;

		for (Country country : A.getAssigned_countries()) {

			initialArmies += country.getNoOfArmies();
		}

		Player.deck = new ArrayList<String>();

		A.getCards().add("Test1 infantry");
		A.getCards().add("Test2 infantry");
		A.getCards().add("Test3 infantry");

		expected = A.getStrategy().calculateReinforceArmies(testMap, A);
		A.executeReinforcement(testMap, listOfPlayers);

		reinforcedArmies = 0;
		for (Country country : A.getAssigned_countries()) {
			reinforcedArmies += country.getNoOfArmies();
		}
		reinforcedArmies -= initialArmies;

		assertTrue(Math.abs((reinforcedArmies - expected)) % 5 == 0);

		// test with 3 different card types

		initialArmies = 0;

		for (Country country : A.getAssigned_countries()) {

			initialArmies += country.getNoOfArmies();
		}

		Player.deck = new ArrayList<String>();

		A.getCards().add("Test1 infantry");
		A.getCards().add("Test2 cavalry");
		A.getCards().add("Test3 artillery");
		expected = A.getStrategy().calculateReinforceArmies(testMap, A);
		A.executeReinforcement(testMap, listOfPlayers);

		reinforcedArmies = 0;
		for (Country country : A.getAssigned_countries()) {
			reinforcedArmies += country.getNoOfArmies();
		}
		reinforcedArmies -= initialArmies;
		assertTrue(Math.abs((reinforcedArmies - expected)) % 5 == 0);

	}

	/**
	 * Test method for
	 * {@link pattern.strategy.RandomStrategy#attack(dao.Map, java.util.ArrayList, dao.Player)}.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testAttack() throws Exception {
		A.executeAttack(testMap, listOfPlayers);
		assertTrue(true);
	}

	/**
	 * Test method for
	 * {@link pattern.strategy.RandomStrategy#fortification(dao.Map, java.util.ArrayList, java.lang.String, dao.Player)}.
	 */
	@Test
	public void testFortification() {
		A.executeFortification(testMap, listOfPlayers, "");
		assertTrue(true);
	}

	/**
	 * Test method for
	 * {@link pattern.strategy.RandomStrategy#calculateReinforceArmies(dao.Map, dao.Player)}.
	 */
	@Test
	public void testCalculateReinforceArmies() {
		// based on only countries
		int armies = A.getStrategy().calculateReinforceArmies(testMap, A);

		assertTrue(armies == 3);

		// including continent
		asia.setOwner(A.getName());
		asia.setContinentValue(10);
		armies = A.getStrategy().calculateReinforceArmies(testMap, A);

		assertTrue(armies == 13);

		// including 5 different cards
		Player.deck = new ArrayList<String>();
		A.getCards().add("Test1 infantry");
		A.getCards().add("Test2 cavalry");
		A.getCards().add("Test3 artillery");
		A.getCards().add("Test4 infantry");
		A.getCards().add("Test5 cavalry");
		armies = A.getStrategy().calculateReinforceArmies(testMap, A);
		assertTrue(armies == 10 || armies == 18 || armies == 20 || armies == 28);
	}

	/**
	 * Test method for
	 * {@link pattern.strategy.RandomStrategy#attackDeadlock(dao.Map, dao.Player)}.
	 */
	@Test
	public void testAttackDeadlock() {
		// if all countries has only 1 army left
		china.setNoOfArmies(1);
		B.setNoOfArmies(1);
		int result = B.getStrategy().attackDeadlock(testMap, B);
		assertTrue(result == 1);

		// if have neighbor country of other player
		result = A.getStrategy().attackDeadlock(testMap, A);
		assertTrue(result == 0);

		// if don't have neighbor country of other player
		china.setOwner(A.getName());
		result = A.getStrategy().attackDeadlock(testMap, A);
		assertTrue(result == 1);

	}

	/**
	 * Test method for
	 * {@link pattern.strategy.RandomStrategy#endGame(java.util.ArrayList)}.
	 * 
	 * @throws Exception exceptions
	 */
	@Test
	public void testEndGame() throws Exception {
		listOfPlayers.remove(B);
		assertTrue(A.getStrategy().endGame(listOfPlayers) == 1);
	}

	/**
	 * Test method for
	 * {@link pattern.strategy.RandomStrategy#validate(java.lang.String, dao.Map, dao.Player)}.
	 */
	@Test
	public void testValidate() {
		assertTrue(A.getStrategy().validate("", testMap, A) == 0);
	}

}
