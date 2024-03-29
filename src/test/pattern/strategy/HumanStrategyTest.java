
package test.pattern.strategy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import dao.Continent;
import dao.Country;
import dao.Map;
import dao.Player;
import pattern.strategy.HumanStrategy;
import pattern.strategy.Strategy;

/**
 * Class to test the Player Class 
 * @author Piyush
 *
 */
public class HumanStrategyTest {
	/**
	 * Object of Map 
	 */
	static Map testMap=new Map();
	/**
	 * Object of Country india 
	 */
	static Country india =new Country();
	/**
	 * Object of Country pakistan
	 */
	static Country pakistan =new Country();
	/**
	 * Object of Country china
	 */
	static Country china =new Country();
	/**
	 * Object of Player A
	 */
	static Player A = new Player();
	/**
	 * Object of Player B
	 */
	static Player B = new Player();
	/**
	 * ArrayList to store the list of players
	 */
	static ArrayList<Player> listOfPlayers;
	
	/**
	 * Set up method to initialize the objects
	 */
	@Before 
	public void before() {
		
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
		Continent asia=new Continent();
		asia.setName("asia");
		testMap.getListOfContinent().add(asia);
		A.setStrategy(new HumanStrategy());
		A.setName("A");
		A.setNoOfArmies(40);
		A.getAssigned_countries().add(india);
		A.getAssigned_countries().add(pakistan);
		B.setStrategy(new HumanStrategy());
		B.setName("B");
		B.setNoOfArmies(40);
		B.getAssigned_countries().add(china);
		listOfPlayers=new ArrayList<Player>();
		listOfPlayers.add(A);
		listOfPlayers.add(B);
	}
	
	/**
	 * Test method for {@link pattern.strategy.HumanStrategy#validate(String, Map, Player)}
	 */
	@Test
	public void testValidate() {
		String command="attack india china -allout";
		int result=A.getStrategy().validate(command, testMap,A);
		assertEquals(1, result);
		
		System.out.println("for Player A-> attack india china -1 ");
		command="attack india china -1";
		result=A.getStrategy().validate(command, testMap,A);
		assertEquals(0, result);
		
		System.out.println("\nfor Player A-> attack china india -allout ");
		command="attack china india -allout";
		result=A.getStrategy().validate(command, testMap,A);
		assertEquals(0, result);
		
		System.out.println("\nfor Player A-> attack pakistan india -allout");
		command="attack pakistan india -allout";
		result=A.getStrategy().validate(command, testMap,A);
		assertEquals(0, result);
		
		System.out.println("\nfor Player A-> attack pakistan china -allout");
		command="attack pakistan china -allout";
		result=A.getStrategy().validate(command, testMap,A);
		assertEquals(0, result);
		
		System.out.println("\nfor Player A-> attack pakistan china -allout");
		command="attack pakistan china -allout";
		result=B.getStrategy().validate(command, testMap,B);
		assertEquals(0, result);
		
		System.out.println("\nfor Player B-> attack china pakistan -allout ");
		command="attack china pakistan -allout";
		result=B.getStrategy().validate(command, testMap,B);
		assertEquals(0, result);
		
		System.out.println("\nfor Player B-> attack china india 4");
		command="attack china india 4";
		result=B.getStrategy().validate(command, testMap,B);
		assertEquals(0, result);
		A.getAssigned_countries().clear();
		B.getAssigned_countries().clear();
	}
	
	/**
	 * Test method for {@link pattern.strategy.HumanStrategy#attackMove(String, Country, Country, int, Player)}.
	 */
	@Test
	public void testAttackMove() {
		//if china wins india-
		
		china.setNoOfArmies(20);
		india.setNoOfArmies(1);
		india.setOwner("B");
		india.setNoOfArmies(0);
		String command="attackmove 45";
		System.out.println("\nFor-> attackmove 45");
		int result=B.getStrategy().attackMove(command, china, india,3, B);
		assertEquals(0, result);
		System.out.println("\nFor-> attackmove -1");
		command="attackmove -1";
		result=B.getStrategy().attackMove(command, china, india,2,B);
		assertEquals(0, result);
		System.out.println("\nFor-> attackmove 2");
		command="attackmove 2";
		result=B.getStrategy().attackMove(command, china, india,3,B);
		assertEquals(0, result);
		
		System.out.println("\nFor-> attackmove 5");
		command="attackmove 19";
		result=B.getStrategy().attackMove(command, china, india,3,B);
		assertEquals(1, result);
		assertEquals(19, india.getNoOfArmies());
		assertEquals(1, china.getNoOfArmies());
		A.getAssigned_countries().clear();
		B.getAssigned_countries().clear();
	}
	/**
	 * Test method for {@link pattern.strategy.HumanStrategy#endGame(ArrayList)}.
	 * @throws Exception exception
	 */
	@Test
	public void testAttackAndEndGame() throws Exception {
		
		china.setNoOfArmies(1);
		india.setNoOfArmies(1000);
		B.setNoOfArmies(1);
		System.out.println(A.getAssigned_countries().size());
		System.out.println(B.getAssigned_countries().size());
		A.setTestCommand("attack india china -allout");
		int result=A.executeAttack(testMap,listOfPlayers);
		
		assertEquals(1,result);
		result=A.getStrategy().endGame(listOfPlayers);
		assertEquals(1,result);
		A.getAssigned_countries().clear();
		B.getAssigned_countries().clear();
	}
	/**
	 * Test method for {@link pattern.strategy.HumanStrategy#attackDeadlock(Map, Player)}.
	 */
	@Test
	public void testAttackDeadlock() {
		
		int result=A.getStrategy().attackDeadlock(testMap,A);
		assertEquals(0,result);
		india.setNoOfArmies(1);
		result=A.getStrategy().attackDeadlock(testMap,A);
		assertEquals(1,result);
		
		china.setNoOfArmies(1);
		B.setNoOfArmies(1);
		result=B.getStrategy().attackDeadlock(testMap,B);
		assertEquals(1,result);
		A.getAssigned_countries().clear();
		B.getAssigned_countries().clear();
	}
	/**
	 * Test method for {@link pattern.strategy.HumanStrategy#fortification(Map, ArrayList, String, Player)}
	 */
	@Test
	public void testFortification() {
		System.out.println("\nFor Player A-> fortify pakistan china 10");
		A.executeFortification(testMap, listOfPlayers, "fortify pakistan china 10");
		assertEquals(20,pakistan.getNoOfArmies());
		assertEquals(40,china.getNoOfArmies());
		
		System.out.println("\nFor Player A-> fortify -none");
		A.executeFortification(testMap, listOfPlayers, "fortify -none");
		assertEquals(20,pakistan.getNoOfArmies());
		assertEquals(20,india.getNoOfArmies());
		
		System.out.println("\nFor Player A-> fortify china india 5");
		A.executeFortification(testMap, listOfPlayers, "fortify china india 5");
		assertEquals(20,pakistan.getNoOfArmies());
		assertEquals(20,india.getNoOfArmies());
		assertEquals(40,china.getNoOfArmies());
		
		System.out.println("\nFor Player A-> fortify pakistan india 30");
		A.executeFortification(testMap, listOfPlayers, "fortify pakistan india 30");
		assertEquals(20,india.getNoOfArmies());
		assertEquals(20,pakistan.getNoOfArmies());
	
		System.out.println("\nFor Player A-> fortify pakistan india 10");
		A.executeFortification(testMap, listOfPlayers, "fortify pakistan india 10");
		assertEquals(30,india.getNoOfArmies());
		assertEquals(10,pakistan.getNoOfArmies());
		
		System.out.println("\nFor Player A-> fortify pakistan india 10");
		pakistan.setNoOfArmies(1);
		A.executeFortification(testMap, listOfPlayers, "fortify pakistan india 10");
		assertEquals(30,india.getNoOfArmies());
		assertEquals(1,pakistan.getNoOfArmies());
		
		System.out.println("\nFor Player A-> fortify pakistan india 10 5");
		pakistan.setNoOfArmies(1);
		A.executeFortification(testMap, listOfPlayers, "fortify pakistan india 10 5");
		assertEquals(30,india.getNoOfArmies());
		assertEquals(1,pakistan.getNoOfArmies());
		A.getAssigned_countries().clear();
		B.getAssigned_countries().clear();
		
	}
	/**
	 * Method to check calculation of number of reinforcement armies for a player
	 */
	@Test
	public void testcalculateReinforceArmies() {
		int result = A.getStrategy().calculateReinforceArmies(testMap,A);
		assertEquals(3, result);
		
		china.setOwner("A");
		Continent asia=new Continent();
		asia.setContinentValue(10);
		asia.setOwner("A");
		testMap.getListOfContinent().add(asia);
		result = A.getStrategy().calculateReinforceArmies(testMap,A);
		assertEquals(13, result);
		A.getAssigned_countries().clear();
		B.getAssigned_countries().clear();
	}
	/**
	 * Method to check calculation of number of reinforcement armies for a player
	 */
	@Test
	public void testReinforcement() {
		india.setNoOfArmies(18);
		pakistan.setNoOfArmies(22);
		A.test=1;
		A.setTestCommand("reinforce india 3");
		A.executeReinforcement(testMap, listOfPlayers);
		assertEquals(21, india.getNoOfArmies()); 
		assertEquals(22, pakistan.getNoOfArmies());
		assertEquals(43, A.getNoOfArmies());
		
		A.setTestCommand("reinforce pakistan 3");
		A.executeReinforcement(testMap, listOfPlayers);
		assertEquals(21, india.getNoOfArmies()); 
		assertEquals(25, pakistan.getNoOfArmies());
		assertEquals(46, A.getNoOfArmies());
			}
	
}
