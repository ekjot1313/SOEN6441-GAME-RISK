package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import dao.Country;
import dao.Map;
import dao.Player;
import mapWorks.MapReader;

/**
 * This class handles gameplay and contains three methods each for
 * reinforcement, fortification and attack phase.
 * 
 * @author Piyush
 * @author Mitalee Naik
 * @author Divya_000
 * @since 1.0.0
 *
 */
public class GamePlay {
	/**
	 * This method calculates the number of reinforcement armies
	 * 
	 * @param listPlayer  List of Players
	 * @param map         Map Object
	 * @param playerIndex Index of the player in the list
	 * @return Number of reinforcement armies
	 */
	public int calculateReinforceArmies(ArrayList<Player> listPlayer, Map map, int playerIndex) {
		int noOfArmies = listPlayer.get(playerIndex).getAssigned_countries().size() / 3;
		int reinforcementArmies = noOfArmies <= 3 ? 3 : noOfArmies;
		// System.out.println("reinforcementArmies "+ reinforcementArmies);
		return reinforcementArmies;
	}

	/**
	 * This method is used for reinforcement phase.
	 * 
	 * @param listPlayer  List of players
	 * @param map         Map object which contains the map details like continents
	 *                    and countries.
	 * @param playerIndex Index of a particular player in the List of player passed
	 *                    from main function
	 */
	public void reinforcement(ArrayList<Player> listPlayer, Map map, int playerIndex) {

		Scanner sc = new Scanner(System.in);
		// calculate reinforcement armies
		int reinforcementArmies = calculateReinforceArmies(listPlayer, map, playerIndex);

		// loop over playerlist and assign reinforcement armies
		while (reinforcementArmies != 0) {
			System.out.println("Reinforcement armies to be assigned :" + reinforcementArmies);
			System.out.println("Type reinforce <countryname> <num>  to assign armies\n Type showmap");
			String input = sc.nextLine();
			String[] inputArray = input.split(" ");
			if (input.equals("showmap")) {
				MapReader mr = new MapReader();
				mr.displayAll(map);
			} else if (inputArray.length == 3 && inputArray[0].equals("reinforce")) {
				int armiesTobeplaced = Integer.parseInt(inputArray[2]);
				int countryFound = 0;

				ArrayList<Country> countryTempList = (ArrayList<Country>) listPlayer.get(playerIndex)
						.getAssigned_countries();
				for (Country c : countryTempList) {
					if (c.getName().equals(inputArray[1]))
						countryFound = 1;
				}
				if (countryFound == 0)
					System.out.println("Country is not assigned to player ");
				else {
					if (armiesTobeplaced <= reinforcementArmies && armiesTobeplaced > 0) { // check reinforce command and country is valid and assigned to player
																							
						listPlayer.get(playerIndex)
								.setNoOfArmies(listPlayer.get(playerIndex).getNoOfArmies() + armiesTobeplaced);
						map.getCountryFromName(inputArray[1]).setNoOfArmies(
								map.getCountryFromName(inputArray[1]).getNoOfArmies() + armiesTobeplaced);
						reinforcementArmies -= armiesTobeplaced;
						System.out.println("Reinforcement armies placed successfully");
					} else
						System.out.println(
								"Number of armies to be assigned should be in the range : 1 -" + reinforcementArmies);
				}
			} else
				System.out.println("Invalid command .Type again");
		}
		sc.close();
	}

	/**
	 * This function is used for fortification phase.
	 * 
	 * @param listPlayer  List of players
	 * @param map         Map object which contains the map details like continents
	 *                    and countries.
	 * @param playerIndex Index of a particular player in the List of player passed
	 *                    from main function
	 */
	public void fortification(ArrayList<Player> listPlayer, Map map, int playerIndex) {
		Scanner sc = new Scanner(System.in);
		int flag = 0;
		do {

			System.out.println("Type fortify <from country name> <to country name> <number of armies> or fortify none (choose to not do a move)\n Type showmap");
			String in = sc.nextLine();
			String input[] = in.split(" ");
			if (in.equals("showmap")) {
				MapReader mr = new MapReader();
				mr.displayAll(map);
			} else if (input.length == 4 && input[0].equals("fortify")) {
				HashMap<Integer, List<Integer>> mapOfAssignedCountries = new HashMap<Integer, List<Integer>>();
				for (int i = 0; i < listPlayer.get(playerIndex).getAssigned_countries().size(); i++) {
					List<Integer> templist = new ArrayList<Integer>();
					for (int j = 0; j < listPlayer.get(playerIndex).getAssigned_countries().get(i).getNeighbors()
							.size(); j++) {
						if (map.getCountryFromName(
								listPlayer.get(playerIndex).getAssigned_countries().get(i).getNeighbors().get(j))
								.getOwner().equals(listPlayer.get(playerIndex).getName())) {
							for (int k = 0; k < listPlayer.get(playerIndex).getAssigned_countries().size(); k++) {
								if (listPlayer.get(playerIndex).getAssigned_countries().get(k)
										.equals(map.getCountryFromName(listPlayer.get(playerIndex)
												.getAssigned_countries().get(i).getNeighbors().get(j)))) {
									templist.add(k);
								}
							}
						}
					}
					mapOfAssignedCountries.put(i, templist);
				}
				int source = -1, destination = -1, validPath = 0;
				for (int k = 0; k < listPlayer.get(playerIndex).getAssigned_countries().size(); k++) {
					if (listPlayer.get(playerIndex).getAssigned_countries().get(k).getName().equals(input[1])) {
						source = k;
					}
					if (listPlayer.get(playerIndex).getAssigned_countries().get(k).getName().equals(input[2])) {
						destination = k;
					}
				}
				if (source == -1 || destination == -1) {
					if (source == -1 && destination == -1)
						System.out.println("Sorry!From Country:" + input[1] + " and To Country :" + input[2]+ " doesn't belong to you");
					else if (source == -1)
						System.out.println("Sorry!From Country :" + input[1] + " doesn't belong to you");
					else
						System.out.println("Sorry!To Country :" + input[2] + " doesn't belong to you");
				} else {
					Boolean[] visited = new Boolean[mapOfAssignedCountries.keySet().size()];
					for (int i = 0; i < visited.length; i++) {
						visited[i] = false;
					}
					LinkedList<Integer> queue = new LinkedList<Integer>();
					queue.add(source);
					visited[source] = true;
					while (queue.size() > 0) {
						Integer c1 = queue.poll();
						Iterator<Integer> i = mapOfAssignedCountries.get(c1).listIterator();
						while (i.hasNext()) {
							int n = (int) i.next();

							if (n == destination) {
								validPath = 1;
								break;
							}
							if (visited[n] == false) {
								visited[n] = true;
								queue.add(n);
							}
						}
						if (validPath == 1)
							break;
					}
					if (validPath == 1) {
						if (Integer.parseInt(input[3]) > 0 && (Integer.parseInt(input[3]) < listPlayer.get(playerIndex)
								.getAssigned_countries().get(source).getNoOfArmies())) {
							listPlayer.get(playerIndex).getAssigned_countries().get(source).setNoOfArmies(
									(listPlayer.get(playerIndex).getAssigned_countries().get(source).getNoOfArmies())
											- Integer.parseInt(input[3]));
							listPlayer.get(playerIndex).getAssigned_countries().get(destination)
									.setNoOfArmies((listPlayer.get(playerIndex).getAssigned_countries().get(destination)
											.getNoOfArmies()) + Integer.parseInt(input[3]));
							System.out.println("Fortification successful");
							sc.close();
							return;
						} else
							System.out.println("Invalid no of armies specified, for these two countries it can be 1-"+ (listPlayer.get(playerIndex).getAssigned_countries().get(source).getNoOfArmies()- 1));
					} else {
							System.out.println("There's no path between the mentioned countries.(You can move any number of armies from one of the owned countries to the other, provided that there is a path between these two countries that is composed of countries owned by you)");
					}
				}
			} else if (in.equals("fortify none")) {
				System.out.println("Skipped fortification");
				sc.close();
				return;
			} else
				System.out.println("Invalid command,type again");
		} while (flag == 0);

		sc.close();
	}

}