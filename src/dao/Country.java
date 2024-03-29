package dao;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains details of the country
 * 
 * @author Piyush
 *
 */
public class Country {
	/**
	 * To store name of the country
	 */
	private String name;
	/**
	 * To store the owner of the Country (Player)
	 */
	private String owner;
	/**
	 * To store the country's continent name
	 */
	private String continentName;
	/**
	 * To store the number of armies in the country
	 */
	private int noOfArmies;
	/**
	 * To store the list of neighbors of the country
	 */
	private List<String> neighbors;

	/**
	 * Constructor initializes list of neighbors
	 */
	public Country() {
		this.neighbors = new ArrayList<String>();
		this.owner = "FREE COUNTRIES";
	}

	/**
	 * Copy Constructor
	 * 
	 * @param c Country Object
	 */
	public Country(Country c) {
		this.name = c.name;
		this.owner = c.owner;
		this.continentName = c.continentName;
		this.neighbors = new ArrayList<String>(c.getNeighbors());
	}

	/**
	 * This method returns name of the country
	 * 
	 * @return Country Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the name of the country.
	 * 
	 * @param name Country Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method returns the owner of the country.
	 * 
	 * @return Country Owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * This method sets the owner of the country.
	 * 
	 * @param owner Country Owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * This method returns the continent of the country.
	 * 
	 * @return Continent to which country belong to
	 */
	public String getContinentName() {
		return continentName;
	}

	/**
	 * This method sets the continent of the country.
	 * 
	 * @param continentName Continent to which country belong to
	 */
	public void setContinentName(String continentName) {
		this.continentName = continentName;
	}

	/**
	 * This method returns the number of armies present in the country.
	 * 
	 * @return Number of armies in the country
	 */
	public int getNoOfArmies() {
		return noOfArmies;
	}

	/**
	 * This method sets the number of armies of the country.
	 * 
	 * @param noOfArmies Number of armies in the country
	 */
	public void setNoOfArmies(int noOfArmies) {
		if (noOfArmies >= 0) {
			this.noOfArmies = noOfArmies;
		}
	}

	/**
	 * This method returns the list of the neighbors of the country.
	 * 
	 * @return List of neighbors of the country
	 */
	public List<String> getNeighbors() {
		return neighbors;
	}

	/**
	 * This method sets the list of the neighbors of the country.
	 * 
	 * @param neighbours List of neighbors of the country
	 */
	public void setNeighbors(List<String> neighbours) {
		this.neighbors = neighbours;
	}

}
