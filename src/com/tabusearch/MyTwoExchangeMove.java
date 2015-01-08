/**
 * 
 */
package com.tabusearch;

import java.util.ArrayList;
import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.Solution;

import com.vrptw.Customer;
import com.vrptw.Instance;
import com.vrptw.Route;

/**
 * This class represents a move between two routes in which, choosing a customer from each route, it
 * will be created two new routes based on the customers of the two routes.
 */
@SuppressWarnings("serial")
public class MyTwoExchangeMove implements Move {

	private Instance	instance;
	private Customer	firstCustomer;
	private Customer	secondCustomer;
	private int			firstRouteIndex;
	private int			secondRouteIndex;

	public MyTwoExchangeMove(Instance instance, Customer firstCustomer, Customer secondCustomer,
			int firstRouteIndex, int secondRouteIndex) {
		this.instance = instance;
		this.firstCustomer = firstCustomer;
		this.secondCustomer = secondCustomer;
		this.firstRouteIndex = firstRouteIndex;
		this.secondRouteIndex = secondRouteIndex;
	}

	/*
	 * (non-Javadoc)
	 * @see org.coinor.opents.Move#operateOn(org.coinor.opents.Solution)
	 */
	@Override
	public void operateOn(Solution soln) {
		MySolution sol = (MySolution) soln;
		Route routeFirst = sol.getRoutes(firstRouteIndex);
		Route routeSecond = sol.getRoutes(secondRouteIndex);
		// create the new routes with the new cost for each one
		evaluateNewRoutes(routeFirst, routeSecond, this.getFirstCustomer(),
				this.getSecondCustomer());
		sol.calculateCost();
		;
	}

	/**
	 * This method creates the two new routes based on the customers of the routes given as
	 * parameters
	 * 
	 * @param routeFirst
	 *            a route of the solution
	 * @param routeSecond
	 *            a route of the solution
	 * @param firstCustomer
	 *            the customer of the move in the first route
	 * @param secondCustomer
	 *            the customer of the move in the second route
	 */
	private void evaluateNewRoutes(Route routeFirst, Route routeSecond, Customer firstCustomer,
			Customer secondCustomer) {

		List<Customer> newCustomersFirst = new ArrayList<>();
		List<Customer> newCustomersSecond = new ArrayList<>();
		List<Customer> oldCustomersFirst = routeFirst.getCustomers();
		List<Customer> oldCustomersSecond = routeSecond.getCustomers();

		// scan the first route customers that are before the customer of the move
		for (int i = 0; i < oldCustomersFirst.indexOf(firstCustomer); i++) {
			Customer tmp = oldCustomersFirst.get(i);
			newCustomersFirst.add(tmp);
		}
		newCustomersFirst.add(firstCustomer);
		// scan the second route customers after the customer of the move
		for (int i = (oldCustomersSecond.indexOf(secondCustomer) + 1); i < oldCustomersSecond
				.size(); i++) {
			Customer tmp = oldCustomersSecond.get(i);
			newCustomersFirst.add(tmp);
		}

		// scan the second route customers before the customer of the move
		for (int i = 0; i < oldCustomersSecond.indexOf(secondCustomer); i++) {
			Customer tmp = oldCustomersSecond.get(i);
			newCustomersSecond.add(tmp);
		}
		newCustomersSecond.add(secondCustomer);
		// scan the first route customers after the customer of the move
		for (int i = (oldCustomersFirst.indexOf(firstCustomer) + 1); i < oldCustomersFirst.size(); i++) {
			Customer tmp = oldCustomersFirst.get(i);
			newCustomersSecond.add(tmp);
		}

		// set the new customers list for each route and evaluate the new cost
		routeFirst.setCustomers(newCustomersFirst);
		routeFirst.calculateCost(routeFirst.getAssignedVehicle().getCapacity(),
				instance.getAlpha(), instance.getBeta(), instance.getGamma());
		routeSecond.setCustomers(newCustomersSecond);
		routeSecond.calculateCost(routeSecond.getAssignedVehicle().getCapacity(),
				instance.getAlpha(), instance.getBeta(), instance.getGamma());
	}

	@Override
	public String toString() {
		StringBuffer input = new StringBuffer();
		input.append("--- TwoExchangeMove ---" + "\n");
		input.append("Customer " + this.firstCustomer.getNumber() + " in Route "
				+ this.firstRouteIndex + "\n");
		input.append("Customer " + this.secondCustomer.getNumber() + " in Route "
				+ this.secondRouteIndex + "\n");
		input.append("------" + "\n");
		return input.toString();
	}

	/**
	 * Identify a move for the SimpleTabuList. Doesn't consider same customers from different routes
	 * 
	 * @return hash code that identify the move
	 */
	public int hashCode() {
		int firstCustN = firstCustomer.getNumber();
		int secondCustN = secondCustomer.getNumber();
		if (firstCustN > secondCustN) {
			secondCustN *= 10000;
			return (firstCustN + secondCustN);
		} else {
			firstCustN *= 10000;
			return (firstCustN + secondCustN);
		}
	}

	/**
	 * @return the instance
	 */
	public Instance getInstance() {
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	/**
	 * @return the firstCustomer
	 */
	public Customer getFirstCustomer() {
		return firstCustomer;
	}

	/**
	 * @param firstCustomer
	 *            the firstCustomer to set
	 */
	public void setFirstCustomer(Customer firstCustomer) {
		this.firstCustomer = firstCustomer;
	}

	/**
	 * @return the secondCustomer
	 */
	public Customer getSecondCustomer() {
		return secondCustomer;
	}

	/**
	 * @param secondCustomer
	 *            the secondCustomer to set
	 */
	public void setSecondCustomer(Customer secondCustomer) {
		this.secondCustomer = secondCustomer;
	}

	/**
	 * @return the firstRouteIndex
	 */
	public int getFirstRouteIndex() {
		return firstRouteIndex;
	}

	/**
	 * @param firstRouteIndex
	 *            the firstRouteIndex to set
	 */
	public void setFirstRouteIndex(int firstRouteIndex) {
		this.firstRouteIndex = firstRouteIndex;
	}

	/**
	 * @return the secondRouteIndex
	 */
	public int getSecondRouteIndex() {
		return secondRouteIndex;
	}

	/**
	 * @param secondRouteIndex
	 *            the secondRouteIndex to set
	 */
	public void setSecondRouteIndex(int secondRouteIndex) {
		this.secondRouteIndex = secondRouteIndex;
	}

}
