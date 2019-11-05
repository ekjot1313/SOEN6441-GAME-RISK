package pattern;

import java.util.ArrayList;

import view.CardExchangeView;

/**
 * This is the Observable class which will be extended by the subject.
 * @author divya_000
 *
 */
public class Observable {

	/**
	 * @param observers arraylist of observer objects
	 */
	ArrayList<Observer> observers = new ArrayList<Observer>();
	
	/**
	 * This method attaches the observer to observable class
	 * @param o object of observer class
	 */
	public void attach(Observer o) {
		observers.add(o);
	}
	/**
	 * This method detaches the observer from observable class
	 * @param o object of observer class
	 */
	public void detach(Observer o) {
		observers.remove(o);
		if(CardExchangeView.window.frame !=null) {
		CardExchangeView.window.frame.setVisible(false);
		CardExchangeView.window.frame = null;
		}
		
	}
	
	/**
	 * This method notifies the attached observers
	 * @param obj object of observable class
	 */
	public void notify(Observable obj) {
		for(int i=0;i<observers.size();i++) {
			((Observer) observers.get(i)).update(obj);
		}
	}
}
