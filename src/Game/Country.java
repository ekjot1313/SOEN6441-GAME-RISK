package Game;
import java.util.*;
/**
 * Country class to set and get data members of this class .
 * @author Piyush
 *
 */
public class Country {
	private String name;
	private String owner;
	private String continentName;
	private int noOfArmies;
	private List<String> neighbors;
	
	public Country() {
		this.neighbors=new ArrayList<String>();
	}
	


	
	public String getName() {
		return name;
	}
	/**
	 * This method sets the name of the country.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * This method returns the owner of the country.
	 * @return
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * This method sets the owner of the country.
	 * @param owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	/**
	 * This method returns the continent of the country.
	 * @return
	 */
	public String getContinentName() {
		return continentName;
	}
	/**
	 * This method sets the continent of the country.
	 * @param continentName
	 */
	public void setContinentName(String continentName) {
		this.continentName = continentName;
	}
	/**
	 *  This method returns the no of armies present in the country.
	 * @return
	 */
	public int getNoOfArmies() {
		return noOfArmies;
	}
	/**
	 * This method sets the no of armies of the country.
	 * @param noOfArmies
	 */
	public void setNoOfArmies(int noOfArmies) {
		this.noOfArmies = noOfArmies;
	}
	/**
	 * This method returns the list of the neighbours of the country.
	 * @return
	 */
	public List<String> getNeighbors() {
		return neighbors;
	}
	/**
	 *  This method sets the list of the neighbours of the country.
	 * @param neighbours
	 */
	public void setNeighbors(List<String> neighbours) {
		this.neighbors = neighbours;
	}
	

}
