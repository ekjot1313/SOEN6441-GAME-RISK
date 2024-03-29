package pattern.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import dao.Continent;
import dao.Country;
import dao.Dice;
import dao.Map;
import dao.Player;

/**
 * This class is for Random Strategy, implements the Strategy interface
 *
 */
public class RandomStrategy implements Strategy {

	Random random = new Random();
	private Scanner sc;

	/**
	 * This is the method for reinforcement phase
	 * 
	 * @param map        Map Object
	 * @param listPlayer List of Players
	 * @param P          Current Player
	 */
	@Override
	public void reinforcement(Map map, ArrayList<Player> listPlayer, Player P) {
		// TODO Auto-generated method stub

		P.setEndOfActions(0);
		P.setView("PhaseViewCardExchangeView");
		P.setState("Reinforcement");

		// calculate reinforcement armies
		int reinforcementArmies = calculateReinforceArmies(map, P);

		// loop over playerlist and assign reinforcement armies
		System.out.println("Reinforcement armies to be assigned :" + reinforcementArmies);

		map.setNoOfArmies(P, P.getNoOfArmies() + reinforcementArmies);

		while (reinforcementArmies != 0) {

			String input = getRandomReinforceCommand(P, reinforcementArmies);
			String[] inputArray = input.split(" ");

			int armiesTobeplaced = Integer.parseInt(inputArray[2]);

			map.getCountryFromName(inputArray[1])
					.setNoOfArmies(map.getCountryFromName(inputArray[1]).getNoOfArmies() + armiesTobeplaced);
			reinforcementArmies -= armiesTobeplaced;
			System.out.println(
					"Reinforced " + armiesTobeplaced + " armies to " + map.getCountryFromName(inputArray[1]).getName());
			System.out.println("Remaining armies to be placed : " + reinforcementArmies);
			P.setView("PhaseView");
			P.setActions(
					"Reinforced " + armiesTobeplaced + " armies to " + map.getCountryFromName(inputArray[1]).getName());
			System.out.println(
					"Reinforced " + armiesTobeplaced + " armies to " + map.getCountryFromName(inputArray[1]).getName());

		}
		P.setEndOfActions(1);
		P.setActions("Reinforcement finished");
		System.out.println("Reinforcement finished");

	}

	/**
	 * This method is used for card exchange
	 * 
	 * @param P Player Object
	 * @return Number of armies
	 */
	private int exchangeCards(Player P) {
		int armies = 0;
		ArrayList<String> cards = P.getCards();
		String card1 = "", card2 = "", card3 = "";

		ArrayList<String> infantryCards = (ArrayList<String>) cards.stream().filter(card -> card.contains("infantry"))
				.collect(Collectors.toList());
		ArrayList<String> cavalryCards = (ArrayList<String>) cards.stream().filter(card -> card.contains("cavalry"))
				.collect(Collectors.toList());
		ArrayList<String> artilleryCards = (ArrayList<String>) cards.stream().filter(card -> card.contains("artillery"))
				.collect(Collectors.toList());

		if (infantryCards.size() >= 3) {
			card1 = infantryCards.get(0);
			card2 = infantryCards.get(1);
			card3 = infantryCards.get(2);
		} else if (cavalryCards.size() >= 3) {
			card1 = cavalryCards.get(0);
			card2 = cavalryCards.get(1);
			card3 = cavalryCards.get(2);
		} else if (artilleryCards.size() >= 3) {
			card1 = artilleryCards.get(0);
			card2 = artilleryCards.get(1);
			card3 = artilleryCards.get(2);
		} else if (infantryCards.size() >= 1 && cavalryCards.size() >= 1 && artilleryCards.size() >= 1) {
			card1 = infantryCards.get(0);
			card2 = cavalryCards.get(0);
			card3 = artilleryCards.get(0);
		}
		cards.remove(card1);
		cards.remove(card2);
		cards.remove(card3);

		P.setView("CardExchangeView");
		P.setActions("Player has exchanged " + card1 + ", " + card2 + ", " + card3);

		P.setCardExchangeCounter(P.getCardExchangeCounter() + 5);
		armies += P.getCardExchangeCounter();

		Player.deck.add(card1);
		Player.deck.add(card2);
		Player.deck.add(card3);
		return armies;
	}

	/**
	 * This method generates a random reinforcement command
	 * 
	 * @param p                      Player Object
	 * @param maxReinforcementArmies Maximum number of reinforcement armies possible
	 * @return Random reinforcement command
	 */
	private String getRandomReinforceCommand(Player p, int maxReinforcementArmies) {
		String input = "reinforce ";

		int numOfCountriesOwned = p.getAssigned_countries().size();
		int index = random.nextInt(numOfCountriesOwned);
		input += p.getAssigned_countries().get(index).getName() + " ";

		int num = random.nextInt(maxReinforcementArmies) + 1;
		input += num;

		return input;
	}

	/**
	 * This is the method for attack phase
	 * 
	 * @param map        Map Object
	 * @param listPlayer List of Players
	 * @param P          Current Player
	 * @return 1 if the game is over otherwise 0.
	 */
	@Override
	public int attack(Map map, ArrayList<Player> listPlayer, Player P) {
		P.setEndOfActions(0);
		P.setView("PhaseView");
		P.setState("Attack");

		int attackDeadlock = 0;

		// checking for deadlock
		attackDeadlock = attackDeadlock(map, P);
		if (attackDeadlock == 1) {
			P.setEndOfActions(1);
			System.out.println("Attack not possible because of the attack deadlock.");
			P.setActions("Attack not possible because of the attack deadlock." + "\n Attack Finished.");
			return 0;
		}

		String input;

		// getting all neighbors
		HashSet<String> allNeighborsSet = new HashSet<String>();
		for (Country country : P.getAssigned_countries()) {
			for (String neighbor : country.getNeighbors()) {
				if (!map.getCountryFromName(neighbor).getOwner().equals(P.getName())) {
					// if neighbor country belongs to other player
					allNeighborsSet.add(neighbor);

				}
			}
		}
		ArrayList<String> allNeighbors = new ArrayList<String>(allNeighborsSet);

		// selecting one neighbor country to attack on
		int index = random.nextInt(allNeighbors.size());
		String countryTo = allNeighbors.get(index);
		Country toCountry = map.getCountryFromName(countryTo);

		// random number of attacks possible on defending country
		int numOfAttack = random.nextInt(toCountry.getNoOfArmies()) + 1;

		System.out.println("Attacking " + numOfAttack + " times on country: " + countryTo);
		P.setActions("Attacking " + numOfAttack + " times on country: " + countryTo);

		while (numOfAttack-- > 0) {
			// checking for deadlock
			if (attackDeadlock(map, P) == 1) {
				P.setEndOfActions(1);
				System.out.println("Attack not possible because of the attack deadlock.");
				P.setActions("Attack not possible because of the attack deadlock." + "\n Attack Finished.");
				return 0;
			}

			input = getRandomAttackCommand(P, map, toCountry);

			String s[] = input.split(" ");

			if (numOfAttack == 0 || input.contains("error")) {
				System.out.println("No further attack possible.");
				P.setActions("No further attack possible.");
				P.setEndOfActions(1);
				P.setActions("Attack finished");
				return 0;
			}

			Country fromCountry = map.getCountryFromName(s[1]);
			int attackerDice = Integer.parseInt(s[3]);

			int defenderDice = getDefenderDice(toCountry, map, P);

			if (toCountry.getNoOfArmies() > 2) {
				defenderDice = 2;
			}

			String defend = toCountry.getOwner();

			Player defender = map.getPlayerFromName(defend);

			P.setActions("Attacking country: " + toCountry.getName() + " from country :" + fromCountry.getName());

			P.setActions("Player :" + defend + " has to defend country :" + s[2]);

			Dice diceRoll = new Dice(attackerDice, defenderDice);
			int result[][] = diceRoll.rollAll();
			System.out.println("Dice Roll Output:");
			diceRoll.print(result);
			result = diceRoll.sort(result);
			int min = Math.min(attackerDice, defenderDice);
			for (int i = 0; i < min; i++) {
				if (result[0][i] > result[1][i])// attacker wins
				{
					map.setNoOfArmies(defender, defender.getNoOfArmies() - 1);
					toCountry.setNoOfArmies(toCountry.getNoOfArmies() - 1);

					System.out.println("Defender lost 1 army");
					P.setActions("Defender lost 1 army");
				} else { // defender wins
					map.setNoOfArmies(P, P.getNoOfArmies() - 1);
					fromCountry.setNoOfArmies(fromCountry.getNoOfArmies() - 1);
					System.out.println("Attacker lost 1 army");
					P.setActions("Attacker lost 1 army");
				}
			}

			if (toCountry.getNoOfArmies() == 0) { // attacker has conquered the defending country.
				map.setCountryOwner(toCountry, P.getName());
				P.getAssigned_countries().add(toCountry);
				defender.getAssigned_countries().remove(toCountry);

				System.out.println("You have conquered country: " + toCountry.getName());
				P.setActions(P.getName() + " has conquered country: " + toCountry.getName());
				if (defender.getAssigned_countries().size() == 0) {// defender is out of the game
					for (int i = 0; i < defender.getCards().size(); i++) {
						P.getCards().add(defender.getCards().get(i));
					}
					listPlayer.remove(defender);
					// checking for game finish condition
					if (endGame(listPlayer) == 1)
						return 1;

				} else {
					String card = P.randomCard();
					if (!card.equals("None")) {
						P.getCards().add(card);
						System.out.println("You have received: " + card + " card");
						P.setActions(P.getName() + " has received: " + card + " card");
						Player.deck.remove(card);
					} else {
						System.out.println("No more cards available");
					}
				}

				String command = "attackmove " + defenderDice;
				attackMove(command, fromCountry, toCountry, P);

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
					P.setActions(P.getName() + " has conquered continent: " + cont.getName());
				}
				numOfAttack = 0;
			}

		}

		P.setEndOfActions(1);
		P.setActions("Attack finished");
		return 0;

	}

	/**
	 * This method returns the number of dice used by defender
	 * 
	 * @param toCountry Country which is to be defended
	 * @param map       Map Object
	 * @param P         Player Object
	 * @return Number of defender dice
	 */
	private int getDefenderDice(Country toCountry, Map map, Player P) {
		int defenderDice = 1;
		int validCommand = 0;
		String owner = toCountry.getOwner();
		Player player = map.getPlayerFromName(owner);
		Strategy strategy = player.getStrategy();
		if (strategy instanceof HumanStrategy) {

			System.out.println("Player :" + owner + " has to defend country :" + toCountry.getName()
					+ " \nType defend numdice to choose no of dices to defend your country.");
			P.setActions("Player :" + owner + " has to defend country :" + toCountry.getName());

			while (validCommand == 0) {
				sc = new Scanner(System.in);
				String input = sc.nextLine();
				String str[] = input.split(" ");
				if (str.length == 2 && str[0].equals("defend")) {
					int dice = Integer.parseInt(str[1]);
					int noOfArmies = toCountry.getNoOfArmies();
					if (dice > 0 && dice < 3 && dice <= noOfArmies) {
						defenderDice = dice;
						validCommand = 1;
					} else
						System.out.println("Incorrect number of dices");
				} else {
					System.out.println("Invalid command,type again.");
				}
			}

		} else if (strategy instanceof AggressiveStrategy || strategy instanceof CheaterStrategy) {
			int noOfArmies = toCountry.getNoOfArmies();
			if (noOfArmies >= 2)
				defenderDice = 2;
			else
				defenderDice = 1;
			validCommand = 1;
		} else if (strategy instanceof RandomStrategy) {
			int noOfArmies = toCountry.getNoOfArmies();
			if (noOfArmies == 1)
				defenderDice = 1;
			else {
				Random r = new Random();
				defenderDice = r.nextInt(2) + 1;
			}
			validCommand = 1;
		} else if (strategy instanceof BenevolentStrategy) {
			defenderDice = 1;
			validCommand = 1;
		}
		return defenderDice;
	}

	/**
	 * This method generates a random attack command
	 * 
	 * @param P         Player Object
	 * @param map       Map Object
	 * @param toCountry Country to be attacked
	 * @return Random attack command
	 */
	private String getRandomAttackCommand(Player P, Map map, Country toCountry) {
		String input = "attack ";

		// getting player's countries that are neighbor to selected country and have
		// more than 1
		// army
		HashSet<String> myCountrySet = new HashSet<String>();

		for (String country : toCountry.getNeighbors()) {
			if (map.getCountryFromName(country).getOwner().equals(P.getName())
					&& map.getCountryFromName(country).getNoOfArmies() > 1) {

				myCountrySet.add(country);

			}
		}

		ArrayList<String> allBoundaries = new ArrayList<String>(myCountrySet);

		// no country found
		if (allBoundaries.size() == 0) {
			return "error";
		}

		// randomly select one country from set
		int index = random.nextInt(allBoundaries.size());
		String countryFrom = allBoundaries.get(index);

		Country fromCountry = map.getCountryFromName(countryFrom);

		// selecting max number of dice possible
		int attackerDice = 1;
		if (fromCountry.getNoOfArmies() > 3) {
			attackerDice = 3;
		} else if (fromCountry.getNoOfArmies() == 3) {
			attackerDice = 2;
		}

		input += countryFrom + " " + toCountry.getName() + " " + attackerDice;

		return input;
	}

	/**
	 * This is the method for fortification phase
	 * 
	 * @param map        Map Object
	 * @param listPlayer List of Players
	 * @param command    Command used for testing
	 * @param P          Current Player
	 */
	@Override
	public void fortification(Map map, ArrayList<Player> listPlayer, String command, Player P) {
		// TODO Auto-generated method stub

		P.setEndOfActions(0);
		P.setView("PhaseView");
		P.setState("Fortification");

		// trying maximum upto total number of countries and quit after that
		List<Country> listOfCountries = map.getListOfCountries();
		for (int i = 0; i < listOfCountries.size(); i++) {

			// randomly selected country to fortify
			int index = random.nextInt(P.getAssigned_countries().size());
			Country selectedCountry = P.getAssigned_countries().get(index);

			// randomly selecting neighbors which belong to same player and has more than
			// one army
			HashSet<String> allNeighborsSet = new HashSet<String>();
			for (String neighbor : selectedCountry.getNeighbors()) {
				// check if it belong to same player
				if (map.getCountryFromName(neighbor).getOwner().equals(P.getName())
						&& map.getCountryFromName(neighbor).getNoOfArmies() > 1) {
					// if neighbor country belongs to other player
					allNeighborsSet.add(neighbor);

				}
			}

			ArrayList<String> allNeighbors = new ArrayList<String>(allNeighborsSet);

			// no neighbor found
			if (allNeighbors.size() == 0) {
				// try new country
				continue;
			}

			// randomly selecting other country
			index = random.nextInt(allNeighbors.size());
			String country = allNeighbors.get(index);
			Country otherCountry = map.getCountryFromName(country);

			// randomly selecting number of armies to move
			int numOfArmy = random.nextInt(otherCountry.getNoOfArmies() - 1) + 1;

			otherCountry.setNoOfArmies(otherCountry.getNoOfArmies() - numOfArmy);
			selectedCountry.setNoOfArmies(selectedCountry.getNoOfArmies() + numOfArmy);

			System.out.println("Fortified " + selectedCountry.getName() + " with " + numOfArmy + " armies from "
					+ otherCountry.getName());
			P.setActions("Fortified " + selectedCountry.getName() + " with " + numOfArmy + " armies from "
					+ otherCountry.getName());
			P.setEndOfActions(1);
			P.setActions("Fortification finished.");
			break;
		}
		if (P.getEndOfActions() == 0) {
			System.out.println("Fortification not possible.");
		}
	}

	/**
	 * This method calculates the number of reinforcement armies
	 * 
	 * 
	 * @param map Map Object
	 * @param P   Player
	 * @return Number of reinforcement armies
	 */
	public int calculateReinforceArmies(Map map, Player P) {
		// calculating on the basis of no of countries the player own
		int noOfArmies = P.getAssigned_countries().size() / 3;
		int reinforcementArmies = noOfArmies <= 3 ? 3 : noOfArmies;

		// calculating on the basis of continents owned

		for (Continent continent : map.getListOfContinent()) {
			if (continent.getOwner().equals(P.getName())) {
				reinforcementArmies += continent.getContinentValue();

			}
		}

		// adding armies in exchange of cards
		if (P.getCards().size() >= 3) {
			// is it possible to exchange cards
			if (P.cardExchangePossible()) {
				// if there are 5 cards, must exchange
				if (P.getCards().size() >= 5) {
					reinforcementArmies += exchangeCards(P);
				}
				// else, decide randomly
				else {
					// exchange
					if (random.nextInt(2) == 0) {
						reinforcementArmies += exchangeCards(P);
					}
				}
			}
		}

		return reinforcementArmies;
	}

	/**
	 * This method is used to move the armies after conquering country
	 * 
	 * @param command     Command Given
	 * @param fromCountry Name of country from which armies are moved
	 * @param toCountry   Name of country to which armies should be moved
	 * @param P           the player
	 * @return 1 if armies are successfully moved otherwise 0.
	 */
	public int attackMove(String command, Country fromCountry, Country toCountry, Player P) {
		String str[] = command.split(" ");

		int n = Integer.parseInt(str[1]);

		fromCountry.setNoOfArmies(fromCountry.getNoOfArmies() - n);
		toCountry.setNoOfArmies(n);
		P.setActions("Moving :" + n + " armies from :" + fromCountry.getName() + " to " + toCountry.getName());
		return 1;
	}

	/**
	 * This method checks for attack deadlock
	 * 
	 * @param map Object of Map
	 * @return 1 if deadlock occurred otherwise 0.
	 */
	public int attackDeadlock(Map map, Player P) {
		if (P.getNoOfArmies() == P.getAssigned_countries().size())
			return 1;
		else {
			for (Country c : P.getAssigned_countries()) {
				if (c.getNoOfArmies() != 1) {
					for (String s : c.getNeighbors()) {
						Country c1 = map.getCountryFromName(s);
						if (!(c1.getOwner().equals(P.getName()))) {
							return 0;
						}
					}
				}
			}
		}
		return 1;
	}

	/**
	 * This method is used to check the end of game
	 * 
	 * @param listPlayer List of Players
	 * @return 1 if end of game otherwise 0.
	 */
	public int endGame(ArrayList<Player> listPlayer) {
		if (listPlayer.size() == 1)
			return 1;
		return 0;
	}

	@Override
	public int validate(String command, Map testMap, Player P) {
		return 0;
	}

	@Override
	public int attackMove(String command, Country fromCountry, Country toCountry, int attackerDice, Player P) {
		return 0;
	}
}
