package dao;

import java.util.ArrayList;
import java.util.Scanner;

public class AggresiveStrategy implements Strategy {
	public AggresiveStrategy(){
		strong=new Country();
	}
	public Country strong;
	/**
	 * This method calculates the number of reinforcement armies
	 * 
	 
	 * @param map         Map Object
	 * @param P			  Player
	 * @return Number of reinforcement armies
	 */
	public int calculateReinforceArmies(Map map,Player P) {
		// calculating on the basis of no of countries the player own
		int noOfArmies = P.getAssigned_countries().size() / 3;
		int reinforcementArmies = noOfArmies <= 3 ? 3 : noOfArmies;

		// calculating on the basis of continents owned

		for (Continent continent : map.getListOfContinent()) {
			if (continent.getOwner().equals(P.getName())) {
				reinforcementArmies += continent.getContinentValue();
		
			}
		}

		return reinforcementArmies;
	}
	public void reinforcement(Map map, ArrayList<Player> listPlayer,Player P) {
		P.setEndOfActions(0);
		P.setView("PhaseView");
		P.setState("Reinforcement");
		Scanner sc = new Scanner(System.in);
		// calculate reinforcement armies
		int reinforcementArmies=calculateReinforceArmies(map,P);
		int max =0;
		for(int i=0;i<P.getAssigned_countries().size();i++) {
			if(i==0) {
				max=P.getAssigned_countries().get(i).getNoOfArmies();
				strong=P.getAssigned_countries().get(i);
			}
			if(P.getAssigned_countries().get(i).getNoOfArmies()>max) {
				max=P.getAssigned_countries().get(i).getNoOfArmies();
				strong=P.getAssigned_countries().get(i);
			}
		}
		P.setActions("Reinforced " + reinforcementArmies + " armies to "+ strong.getName());
		strong.setNoOfArmies(strong.getNoOfArmies()+reinforcementArmies);
		P.setEndOfActions(1); 
		P.setActions("Reinforcement finished");
	}
	public int attack(Map map, ArrayList<Player> listPlayer,Player P) {
		P.setEndOfActions(0);
		P.setView("PhaseView");
		P.setState("Attack");
		System.out.println(strong.getName());
		if(strong.getNoOfArmies()==1) {
			System.out.println("Sorry!You cannot attack with 1 army in your strongest country");
			P.setEndOfActions(1); 
			P.setActions("Attack finished");
			return 0;
		}else {
		for(int i=0;i<strong.getNeighbors().size();i++) {
			Country neighbor=map.getCountryFromName(strong.getNeighbors().get(i));
			if(!strong.getOwner().equals(neighbor.getOwner())) {
				//attack
				Country toCountry=neighbor;
				Country fromCountry=strong;
				int attackerDice;
				int defenderDice,index = -1;
				for (int k = 0; k < listPlayer.size(); k++) {

					if (listPlayer.get(k).getName().equals(toCountry.getOwner())) {
						index = k;
						break;
					}
				}
				P.setActions("Attacking country: " + toCountry.getName() + " from country :" + fromCountry.getName());
				Player defender = listPlayer.get(index);
				while (toCountry.getNoOfArmies() != 0 && fromCountry.getNoOfArmies() != 1) {
					if (fromCountry.getNoOfArmies() > 3)
						attackerDice = 3;
					else if(fromCountry.getNoOfArmies() == 3)
						attackerDice = 2;
					else
						attackerDice = 1;
					if (toCountry.getNoOfArmies() >= 2)
						defenderDice = 2;
					else
						defenderDice = 1;
					Dice diceRoll = new Dice(attackerDice, defenderDice);
					int result[][] = diceRoll.rollAll();
					//System.out.println("Dice Roll Output:");
					//diceRoll.print(result);
					result = diceRoll.sort(result);
					int min = Math.min(attackerDice, defenderDice);
					for (int j = 0; j < min; j++) {
						if (result[0][j] > result[1][j])// attacker wins
						{
							map.setNoOfArmies(defender, defender.getNoOfArmies() - 1);
							toCountry.setNoOfArmies(toCountry.getNoOfArmies() - 1);
						} else { // defender wins
							map.setNoOfArmies(P, P.getNoOfArmies() - 1);
							fromCountry.setNoOfArmies((fromCountry.getNoOfArmies() - 1));
						}
						if (fromCountry.getNoOfArmies() == 1)
							break;
					}
				}
				if (fromCountry.getNoOfArmies() == 1) {
					System.out.println("Player :" + defender.getName() + " has defended successfully and attacking country :"
							+ fromCountry.getName() + " has only 1 army left");
					P.setActions("Player :" + defender.getName() + " has defended successfully and attacking country :"
							+ fromCountry.getName() + " has only 1 army left");
					P.setEndOfActions(1); 
					System.out.println("Attack skipped");
					P.setActions("Attack finished");
					return 0;
				}
					if (toCountry.getNoOfArmies() == 0) { // attacker has conquered the defending country.
						map.setCountryOwner(toCountry, P.getName());
						P.getAssigned_countries().add(toCountry);
						defender.getAssigned_countries().remove(toCountry);
						toCountry.setNoOfArmies(1);
						fromCountry.setNoOfArmies(fromCountry.getNoOfArmies() - 1);
						System.out.println("You have conquered country: " + toCountry.getName());
						P.setActions(P.getName() + " has conquered country: " + toCountry.getName());
						if (defender.getAssigned_countries().size() == 0) {// defender is out of the game
							listPlayer.remove(defender);
							// checking for game finish condition
							if (endGame(listPlayer) == 1)
								return 1;

						}
						else {
							String card = P.randomCard();
							//P.cards.add(card);
							P.getCards().add(card);
							System.out.println("You have received: " + card + " card");
							P.setActions(P.getName() + " has received: " + card + " card");
							P.deck.remove(card);
						}
						Continent cont = map.getContinentFromName(toCountry.getContinentName());
						int flag = 0;
						for (String country : cont.getCountries()) {
							Country c = map.getCountryFromName(country);
							if (!P.getName().equals(c.getOwner())) {
								flag = 1;
								map.setContinentOwner(cont, "FREE CONTINENTS");
								break;
							}
						}
						if (flag == 0) { // continent has been conquered
							map.setContinentOwner(cont, P.getName());
							System.out.println("You have conquered continent: " + cont.getName());
							P.setActions(P.getName()+ " has conquered continent: " + cont.getName());
						}
					}
				
			}
			if(strong.getNoOfArmies()==1) {
				P.setEndOfActions(1); 
				P.setActions("Attack finished");
				return 0;
			}
		}
		}
		P.setEndOfActions(1); 
		P.setActions("Attack finished");
		return 0;
	}
	public void fortification(Map map, ArrayList<Player> listPlayer, String command,Player P) {
		P.setEndOfActions(0);
		P.setView("PhaseView");
		P.setState("Fortification");
		P.setEndOfActions(1); 
		System.out.println("Fortification skipped");
		P.setActions("Fortification finished");
	}
	/**
	 * Function to check the end of game
	 * @param listPlayer list of players
	 * @return 1 if end of game otherwise 0.
	 */
	public int endGame(ArrayList<Player> listPlayer) {
		if (listPlayer.size() == 1)
			return 1;
		return 0;
	}
}
