package gameengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import dao.Map;
import dao.Player;
import game.ArmyAllocator;
import game.PlayerAllocator;
import mapworks.MapEditor;
import pattern.adapter.ConquestReaderWriter;
import pattern.adapter.DominationReaderWriter;
import pattern.adapter.MapReaderWriterAdapter;
import pattern.builder.Director;
import pattern.builder.Game;
import pattern.builder.LoadGameBuilder;
import pattern.observer.CardExchangeView;
import pattern.observer.PWDView;
import pattern.observer.PhaseView;
import pattern.strategy.AggressiveStrategy;
import pattern.strategy.BenevolentStrategy;
import pattern.strategy.CheaterStrategy;
import pattern.strategy.RandomStrategy;
import pattern.strategy.Strategy;

/**
 * This is the main class
 * 
 * @author Mitalee Naik
 * @since 1.0.0
 */
public class Main {
	/**
	 * To store phase domination view
	 */
	static PWDView pwdView = null;

	static Map map;

	/**
	 * The execution of game begins from this method
	 * 
	 * @param args arguments
	 * @throws Exception exceptions
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to RISK GAME!");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Select game mode :Tournament or Single Game ");
			String gameMode = sc.nextLine();

			if (gameMode.equalsIgnoreCase("Single Game")) {
				System.out.println(
						"Type \nloadgame <filename> \nloadmap <filename> -load an existing map \neditmap <filename> -edit an existing map or create a new map");
				String[] commands = sc.nextLine().split(" ");
				if (commands.length == 1 && !commands[0].equals("exit")) {
					System.out.println("Invalid command .Type exit to end game");
				} else {
					switch (commands[0]) {
					case "loadmap":
						int isSuccess = loadmap(commands[1]);
						if (isSuccess == 1)
							gameplayer();
						else
							continue;
						System.exit(0);

					case "editmap":
						editmap(commands[1]);
						break;

					case "loadgame":

						String currentPath = System.getProperty("user.dir");
						currentPath += "\\SavedGame\\";
						currentPath += commands[1] + ".txt";
						File newFile = new File(currentPath);
						if (newFile.exists()) {
							map = new Map();
							pwdView = new PWDView(true);
							map.attach(pwdView);
							Director d = new Director();
							d.setGbuilder(new LoadGameBuilder());
							d.constructGame(commands[1], map, " ", " ");

							Game game = d.getGame();
							map = game.getMap();
							loadSavedGame(game);
						} else {
							System.out.println("Invalid filename");
							continue;
						}
					case "exit":
						sc.close();
						return;
					default:
						System.out.println("Invalid command . Type exit to end game");
					}
				}
			} else if (gameMode.equalsIgnoreCase("Tournament")) {
				try {
					tournamentModeInit();
				} catch (Exception e) {
					System.out.println("Invalid Command");
				}

			} else {
				System.out.println("Invalid Command");
			}
		}
	}

	/**
	 * This method is used for loading a saved game after user selects the
	 * 'loadgame' option
	 * 
	 * @param game Game Object
	 * @throws Exception exceptions
	 */
	private static void loadSavedGame(Game game) throws Exception {
		PhaseView pv = new PhaseView();
		CardExchangeView cev = new CardExchangeView();
		String currentPlayer = game.getCurrentPlayer();
		String currentPhase = game.getCurrentPhase();

		System.out.println("Current Player: " + currentPlayer + " Current Phase: " + currentPhase);

		Player p = new Player();

		boolean isRemaining = true;
		int playerIndex = 0;
		int gameOver = 0;

		for (int j = 0; j < map.getListOfPlayers().size(); j++) {
			if (map.getListOfPlayers().get(j).getName().equals(currentPlayer)) {
				p = map.getListOfPlayers().get(j);
			}
		}

		if (currentPhase.equalsIgnoreCase("reinforcement")) {
			p.attach(pv);
			p.attach(cev);
			p.executeReinforcement(map, (ArrayList<Player>) (map.getListOfPlayers()));
			Thread.sleep(1500);
			p.detach(cev);
			cev.close();
			p.executeAttack(map, (ArrayList<Player>) (map.getListOfPlayers()));
			Thread.sleep(1500);
			p.executeFortification(map, (ArrayList<Player>) (map.getListOfPlayers()), null);
			Thread.sleep(1500);
			p.detach(pv);
		} else if (currentPhase.equalsIgnoreCase("attack")) {
			p.attach(pv);
			p.executeAttack(map, (ArrayList<Player>) (map.getListOfPlayers()));
			Thread.sleep(1500);
			p.executeFortification(map, (ArrayList<Player>) (map.getListOfPlayers()), null);
			Thread.sleep(1500);
			p.detach(pv);
		} else if (currentPhase.equalsIgnoreCase("fortification")) {
			p.executeFortification(map, (ArrayList<Player>) (map.getListOfPlayers()), null);
			Thread.sleep(1500);
			p.detach(pv);
		}

		while (true) {

			if (isRemaining) {
				playerIndex = map.getListOfPlayers().indexOf(p);
				playerIndex++;
			} else {
				playerIndex = 0;
			}
			for (int i = playerIndex; i < map.getListOfPlayers().size(); i++) {

				p = map.getListOfPlayers().get(i);
				System.out.println("_______________________________________________________");
				System.out.println("Player " + p.getName() + " Reinforcement phase begins");
				p.attach(pv);

				p.attach(cev);
				p.executeReinforcement(map, (ArrayList<Player>) map.getListOfPlayers());
				Thread.sleep(1500);
				p.detach(cev);
				cev.close();
				System.out.println("_______________________________________________________");
				System.out.println("Player " + p.getName() + " Attack phase begins");
				Player current = p;
				gameOver = p.executeAttack(map, (ArrayList<Player>) map.getListOfPlayers());
				Thread.sleep(1500);
				if (gameOver == 1)
					break;
				int index = map.getListOfPlayers().indexOf(current);
				i = index;
				System.out.println("_______________________________________________________");
				System.out.println("Player " + p.getName() + " Fortification phase begins");
				p.executeFortification(map, (ArrayList<Player>) map.getListOfPlayers(), null);
				Thread.sleep(1500);
				p.detach(pv);

			}
			isRemaining = false;
			if (gameOver == 1)
				break;
		}
		System.out.println("Game Over");
		System.out.println("Winner is Player: " + map.getListOfPlayers().get(0).getName());

		// detach and close PWDView
		map.detach(pwdView);
		pwdView.close();

	}

	/**
	 * This method is used for tournament initial setup
	 * 
	 * @throws Exception exceptions
	 */
	private static void tournamentModeInit() throws Exception {
		Scanner sc = new Scanner(System.in);
		String[] listOfMapFiles;
		String[] listOfPlayerStrategies;
		int numberOfGames = 0;
		int maxTurns = 0;
		// Initial setup
		while (true) {

			System.out.println(
					"Type tournament -M listofmapfiles -P listofplayerstrategies -G numberofgames -D maxnumberofturns");
			String tournamentCommand = sc.nextLine();

			String[] tournamentCommandArray = tournamentCommand.split(" ");

			if (tournamentCommandArray[0].equals("tournament") && tournamentCommandArray.length == 9) {
				if (tournamentCommandArray[1].equals("-M") && tournamentCommandArray[3].equals("-P")
						&& tournamentCommandArray[5].equals("-G") && tournamentCommandArray[7].equals("-D")) {
					try {
						listOfMapFiles = tournamentCommandArray[2].split(",");
						if (!(listOfMapFiles.length >= 1 && listOfMapFiles.length <= 5))
							throw new Exception();
						listOfPlayerStrategies = tournamentCommandArray[4].split(",");
						if (!(listOfPlayerStrategies.length >= 2 && listOfPlayerStrategies.length <= 4))
							throw new Exception();
						numberOfGames = Integer.parseInt(tournamentCommandArray[6]);
						if (!(numberOfGames >= 1 && numberOfGames <= 5))
							throw new Exception();
						maxTurns = Integer.parseInt(tournamentCommandArray[8]);
						if (!(maxTurns >= 10 && maxTurns <= 50))
							throw new Exception();
						tournamentMode(listOfMapFiles, listOfPlayerStrategies, numberOfGames, maxTurns);
					} catch (Exception e) {
						System.out.println("Invalid command .Type again");
					}
					break;

				} else {
					System.out.println("Invalid command .Type again");
				}

			} else {
				System.out.println("Invalid command .Type again");
			}
		}

	}

	/**
	 * This method is used to play the game in tournament mode
	 * 
	 * @param listOfMapFiles         List of Maps
	 * @param listOfPlayerStrategies List of Player Strategies
	 * @param numberOfGames          Number of Games
	 * @param maxTurns               Maximum number of turns
	 * @return Winner Array			 the winner array
	 * @throws Exception			 exception
	 */
	public static String[][] tournamentMode(String[] listOfMapFiles, String[] listOfPlayerStrategies, int numberOfGames,
			int maxTurns) throws Exception {

		// if command is invalid
		if (isInvalidTournamentCommand(listOfMapFiles, listOfPlayerStrategies, numberOfGames, maxTurns)) {
			return null;
		}

		// if command is valid

		// TournamentMode Begins
		String[][] winnerArray = new String[listOfMapFiles.length][numberOfGames];
		int m = 0;
		for (String filename : listOfMapFiles) {
			// For each map
			// Gameplayer logic
			for (int j = 0; j < numberOfGames; j++) {
				map = new Map();
				ArrayList<Player> listOfPlayers = new ArrayList<Player>();
				map.setListOfPlayers(listOfPlayers);
				int isSuccess = loadmap(filename);
				if (isSuccess == 1) {
					for (String playerType : listOfPlayerStrategies) {
						Player p = new Player();
						p.deck = new ArrayList<String>();
						ArrayList<String> deck = createDeck(map);
						Player.deck = deck;
						p.setName(playerType);
						if (getStrategyByName(playerType) == null)
							throw new Exception();
						p.setStrategy(getStrategyByName(playerType));
						map.addPlayer(p);
						listOfPlayers.add(p);
					}
					PlayerAllocator pa = new PlayerAllocator();
					ArmyAllocator aa = new ArmyAllocator();
					pa.listOfPlayers = listOfPlayers;
					pa.populateCountries(map);
					aa.calculateTotalArmies((ArrayList<Player>) pa.listOfPlayers, map);
					aa.placeAll((ArrayList<Player>) pa.listOfPlayers, map);
					boolean isDraw = true;
					for (int k = 0; k < maxTurns; k++) {
						PhaseView pv = new PhaseView();
						CardExchangeView cev = new CardExchangeView();
						int gameOver = 0;
						for (int i = 0; i < pa.listOfPlayers.size(); i++) {
							System.out.println("_______________________________________________________");
							System.out.println(
									"Player " + pa.listOfPlayers.get(i).getName() + " reinforcement phase begins");
							pa.listOfPlayers.get(i).attach(pv);

							pa.listOfPlayers.get(i).attach(cev);
							pa.listOfPlayers.get(i).executeReinforcement(map, (ArrayList<Player>) pa.listOfPlayers);
							Thread.sleep(1500);
							Player current = pa.listOfPlayers.get(i);
							pa.listOfPlayers.get(i).detach(cev);
							cev.close();
							System.out.println("_______________________________________________________");
							System.out.println("Player " + pa.listOfPlayers.get(i).getName() + " Attack phase begins");
							gameOver = pa.listOfPlayers.get(i).executeAttack(map, (ArrayList<Player>) pa.listOfPlayers);
							Thread.sleep(1500);
							if (gameOver == 1)
								break;
							int index = pa.listOfPlayers.indexOf(current);
							i = index;
							System.out.println("_______________________________________________________");
							System.out.println(
									"Player " + pa.listOfPlayers.get(i).getName() + " Fortification phase begins");
							pa.listOfPlayers.get(i).executeFortification(map, (ArrayList<Player>) pa.listOfPlayers,
									null);
							Thread.sleep(1500);
							pa.listOfPlayers.get(i).detach(pv);

						}
						if (gameOver == 1) {
							System.out.println("Game Over");
							System.out.println("Winner is Player: " + pa.listOfPlayers.get(0).getName());
							winnerArray[m][j] = pa.listOfPlayers.get(0).getName();
							isDraw = false;
							break;
						}

					}

					if (isDraw) {
						System.out.println("Draw");
						winnerArray[m][j] = "Draw";
					}
				}

			}
			m++;
		}

		System.out.print("\t");
		for (int i = 0; i < numberOfGames; i++) {
			System.out.print("Game " + (i + 1) + "\t");
		}

		System.out.println();
		for (int i = 0; i < winnerArray.length; i++) {
			System.out.print("Map " + (i + 1) + "\t");
			for (int j = 0; j < winnerArray[i].length; j++) {
				System.out.print(winnerArray[i][j] + "\t");
			}
			System.out.println();
		}

		return winnerArray;

	}

	/**
	 * This method checks if given tournament command is invalid or not
	 * 
	 * @param listOfMapFiles         List of Maps
	 * @param listOfPlayerStrategies List of Player Strategies
	 * @param numberOfGames          Number of Games
	 * @param maxTurns               Maximum number of turns
	 * @return true if command is invalid
	 * @throws Exception			 exceptions
	 */
	private static boolean isInvalidTournamentCommand(String[] listOfMapFiles, String[] listOfPlayerStrategies,
			int numberOfGames, int maxTurns) throws Exception {
		// TODO Auto-generated method stub

		// lists cannot be null
		if (listOfMapFiles == null || listOfPlayerStrategies == null) {
			System.out.println("M and P cannot be null.");
			return true;
		}

		// listOfMapFiles must be from 1 to 5
		if (listOfMapFiles.length < 1 || listOfMapFiles.length > 5) {
			System.out.println("M should be in range 1-5");
			return true;
		}

		// listOfPlayerStrategies must be from 2 to 4
		if (listOfPlayerStrategies.length < 2 || listOfPlayerStrategies.length > 4) {
			System.out.println("P should be in range 1-5");
			return true;
		}

		// numberOfGames must be from 1 to 5
		if (numberOfGames < 1 || numberOfGames > 5) {
			System.out.println("G should be in range 1-5");
			return true;
		}
		// maxTurns must be from 10 to 50
		if (maxTurns < 10 || maxTurns > 50) {
			System.out.println("D should be in range 10-50");
			return true;
		}

		// listOfMapFiles must have all different values and cannot be null
		for (String map : listOfMapFiles) {

			boolean repeated = false;
			for (String otherMap : listOfMapFiles) {
				if (otherMap.isEmpty()) {
					System.out.println("M cannot have Empty value.");
					return true;
				}
				if (map.equals(otherMap)) {
					if (repeated) {
						System.out.println("M cannot have repeated values.");
						return true;
					}
					repeated = true;
				}
			}
		}

		// listOfPlayerStrategies must have all different values and cannot be null and
		// must be computer
		for (String strategy : listOfPlayerStrategies) {
			boolean repeated = false;
			for (String otherStrategy : listOfPlayerStrategies) {
				if (otherStrategy.isEmpty()) {
					System.out.println("P cannot have Empty value.");
					return true;
				}
				if (!(otherStrategy.equalsIgnoreCase("aggressive") || otherStrategy.equalsIgnoreCase("benevolent")
						|| otherStrategy.equalsIgnoreCase("cheater") || otherStrategy.equalsIgnoreCase("random"))) {
					System.out.println("P must be Aggressive, Benevolent, Cheater or Random.");
					return true;
				}
				if (strategy.equals(otherStrategy)) {
					if (repeated) {
						System.out.println("P cannot have repeated values.");
						return true;
					}
					repeated = true;
				}
			}
		}

		// listOfMapFiles cannot have invalid file
		for (String filename : listOfMapFiles) {
			if (loadmap(filename) == 0) {
				System.out.println("Error while loading: " + filename);
				return true;
			}
		}

		// if command is valid
		return false;
	}

	/**
	 * This method returns Strategy Object according to the passed string
	 * 
	 * @param playerType Type of player
	 * @return Strategy Object
	 */
	private static Strategy getStrategyByName(String playerType) {
		// TODO Auto-generated method stub
		if (playerType.equalsIgnoreCase("Aggressive"))
			return new AggressiveStrategy();
		if (playerType.equalsIgnoreCase("Random"))
			return new RandomStrategy();
		if (playerType.equalsIgnoreCase("Cheater"))
			return new CheaterStrategy();
		if (playerType.equalsIgnoreCase("Benevolent"))
			return new BenevolentStrategy();
		return null;
	}

	/**
	 * This method is called when user gives 'editmap' command
	 * 
	 * @param filename Map file to be edited
	 * @throws Exception exception
	 */
	private static void editmap(String filename) throws Exception {
		MapEditor mpe = new MapEditor();
		System.out.println(System.getProperty("user.dir"));
		String currentPath = System.getProperty("user.dir");
		currentPath += "\\Maps\\" + filename + ".map";
		File newFile = new File(currentPath);
		map = new Map(); // to clear buffer map
		DominationReaderWriter drw = new DominationReaderWriter();
		if (newFile.exists()) {
			// check if file if Domination or Conquest
			BufferedReader bufferReaderForFile = new BufferedReader(new FileReader(newFile));
			String firstLine = bufferReaderForFile.readLine();

			ConquestReaderWriter crw = new ConquestReaderWriter();

			if (!firstLine.startsWith(";")) {
				drw = new MapReaderWriterAdapter(crw);
			}

			int mapParseStatus = drw.parseMapFile(map, newFile);
			try {
				editMapCommands();
				System.out.println("Type savemap");
				map = mpe.mapEditorInit(map, drw);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Map file does not exist .New File created .");
			editMapCommands();
			try {
				map = mpe.mapEditorInit(map, drw);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is called when user gives 'loadmap' command
	 * 
	 * @param filename Map file to be loaded
	 * @throws Exception exception
	 * @return 1 if loaded else 0
	 */
	private static int loadmap(String filename) throws Exception {
		// TODO Auto-generated method stub
		String currentPath = System.getProperty("user.dir");
		currentPath += "\\Maps\\";
		System.out.println(currentPath);
		currentPath += filename + ".map";
		File newFile = new File(currentPath);
		if (newFile.exists()) {

			pwdView = new PWDView(true);
			map = new Map(); // to clear buffer map
			map.attach(pwdView);

			// check if file if Domination or Conquest
			BufferedReader bufferReaderForFile = new BufferedReader(new FileReader(newFile));
			String firstLine = bufferReaderForFile.readLine();

			DominationReaderWriter drw = new DominationReaderWriter();
			ConquestReaderWriter crw = new ConquestReaderWriter();

			if (!firstLine.startsWith(";")) {
				drw = new MapReaderWriterAdapter(crw);
			}

			int mapParseStatus = drw.parseMapFile(map, newFile);

			// to check whether map is parsed successfully
			if (mapParseStatus == 1) {
				System.out.println("Map is loaded successfully.");
				return 1;

			} else {
				System.out.println("Error while loading map");
				return 0;
			}

		} else {
			System.out.println("File Not found .");
			return 0;
		}
	}

	/**
	 * This method is called to add or remove a player, assign countries to players
	 * and allow them to place armies The game phases (reinforcement, attack and
	 * fortification) start from this function
	 * @throws Exception exception
	 */
	private static void gameplayer() throws Exception {
		// Create Deck of cards
		ArrayList<String> deck = createDeck(map);
		Player.deck = deck;
		PlayerAllocator pa = new PlayerAllocator();
		ArmyAllocator aa = new ArmyAllocator();
		int gameOver = 0;
		pa.allocate(map, null);
		pa.populateCountries(map);
		int assignedArmies = aa.calculateTotalArmies((ArrayList<Player>) pa.listOfPlayers, map);
		aa.placeArmy(assignedArmies, (ArrayList<Player>) pa.listOfPlayers, map, 0);
		while (true) {
			PhaseView pv = new PhaseView();
			CardExchangeView cev = new CardExchangeView();

			for (int i = 0; i < pa.listOfPlayers.size(); i++) {
				System.out.println("_______________________________________________________");
				System.out.println("Player " + pa.listOfPlayers.get(i).getName() + " reinforcement phase begins");
				pa.listOfPlayers.get(i).attach(pv);

				pa.listOfPlayers.get(i).attach(cev);
				pa.listOfPlayers.get(i).executeReinforcement(map, (ArrayList<Player>) pa.listOfPlayers);
				Thread.sleep(1500);
				pa.listOfPlayers.get(i).detach(cev);
				cev.close();
				System.out.println("_______________________________________________________");
				System.out.println("Player " + pa.listOfPlayers.get(i).getName() + " Attack phase begins");
				Player current = pa.listOfPlayers.get(i);
				gameOver = pa.listOfPlayers.get(i).executeAttack(map, (ArrayList<Player>) pa.listOfPlayers);
				Thread.sleep(1500);
				if (gameOver == 1)
					break;
				int index = pa.listOfPlayers.indexOf(current);
				i = index;
				System.out.println("_______________________________________________________");
				System.out.println("Player " + pa.listOfPlayers.get(i).getName() + " Fortification phase begins");
				pa.listOfPlayers.get(i).executeFortification(map, (ArrayList<Player>) pa.listOfPlayers, null);
				Thread.sleep(1500);
				pa.listOfPlayers.get(i).detach(pv);

			}
			if (gameOver == 1)
				break;
		}
		System.out.println("Game Over");
		System.out.println("Winner is Player: " + pa.listOfPlayers.get(0).getName());

		// detach and close PWDView
		map.detach(pwdView);
		pwdView.close();

	}

	/**
	 * This method is used to create a deck of cards based on countries
	 * 
	 * @param map Map Object
	 * @return Card List
	 */
	private static ArrayList<String> createDeck(Map map) {
		// TODO Auto-generated method stub
		List<String> typeOfCards = new ArrayList<String>();
		typeOfCards.add("infantry");
		typeOfCards.add("cavalry");
		typeOfCards.add("artillery");
		int numberofCountries = map.getListOfCountries().size();
		int equalDis = numberofCountries / 3;
		int iCounter = equalDis;
		int cCounter = equalDis;
		int aCounter = equalDis;
		ArrayList<String> cardList = new ArrayList<String>();

		Random r = new Random();
		for (int i = 0; i < equalDis * 3; i++) {
			String card;
			card = map.getListOfCountries().get(i).getName();
			int j = r.nextInt(typeOfCards.size());
			card += " " + typeOfCards.get(j);
			cardList.add(card);
			if (typeOfCards.get(j).equals("infantry")) {
				iCounter++;
				if (iCounter == equalDis)
					typeOfCards.remove("infantry");
			}
			if (typeOfCards.get(j).equals("cavalry")) {
				cCounter++;
				if (cCounter == equalDis)
					typeOfCards.remove("cavalry");
			}
			if (typeOfCards.get(j).equals("artillery")) {
				aCounter++;
				if (aCounter == equalDis)
					typeOfCards.remove("artillery");
			}

		}
		int remaining = numberofCountries - equalDis * 3;
		if (remaining == 1)
			cardList.add(map.getListOfCountries().get(equalDis * 3).getName() + " " + "infantry");
		if (remaining > 1)
			cardList.add(map.getListOfCountries().get(equalDis * 3 + 1).getName() + " " + "cavalry");

		return cardList;
	}

	/**
	 * This method contains the commands provided after 'editmap' option is selected
	 */
	private static void editMapCommands() {

		System.out.println("Type editcontinent -add <continentname> <continentvalue> -remove <continentname>");
		System.out.println("Type editcountry -add <countryname> <continentname> -remove <countryname>");
		System.out.println(
				"Type editneighbor -add <countryname> <neighborcountryname> -remove <countryname> <neighborcountryname>");
		System.out.println("Type showmap");
		System.out.println("Type validatemap");
	}
}
