/**
 * 
 */
package com.tabusearch;

import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.Solution;

import com.vrptw.*;

/**
 * @author Guido Pio
 * 
 */
@SuppressWarnings("serial")
public class MySwapMove implements Move {
	private Instance	instance;
	private Customer	customer;
	private int			deleteRouteNr;
	private int			deletePositionIndex;
	private int			insertRouteNr;
	private int			insertPositionIndex;

	/**
	 * @param instance
	 * @param customer
	 * @param deleteRouteNr
	 * @param insertPositionIndex
	 * @param insertRouteNr
	 */
	public MySwapMove(Instance instance, Customer customer, int deleteRouteNr,
			int insertPositionIndex, int insertRouteNr) {
		this.instance = instance;
		this.customer = customer;
		this.deleteRouteNr = deleteRouteNr;
		this.insertPositionIndex = insertPositionIndex;
		this.insertRouteNr = insertRouteNr;
	}

	/*
	 * Update the cost of the solution and of the routes involved, based on the move made. Remember
	 * that modify the solution, it doesn't create a new one.
	 */
	@Override
	public void operateOn(Solution soln) {
		MySolution mySol = (MySolution) soln;
		// obtain the routes in which I'm going to make the swap
		Route insertRoute = mySol.getRoutes(this.getInsertRouteNr());
		Route deleteRoute = mySol.getRoutes(this.getDeleteRouteNr());

		evaluateNewRoutes(insertRoute, deleteRoute, this.customer, this.insertPositionIndex);
		mySol.calculateCost();
	}

	private void evaluateNewRoutes(Route insertRoute, Route deleteRoute, Customer customer,
			int insertPositionIndex) {
		List<Customer> customers = insertRoute.getCustomers();
		customers.add(insertPositionIndex, customer);
		insertRoute.calculateCost(instance.getVehicleCapacity(), instance.getAlpha(),
				instance.getBeta(), instance.getGamma());
		customers = deleteRoute.getCustomers();
		customers.remove(customer);
		if (customers.size() != 0) {
		deleteRoute.setCustomers(customers);
		deleteRoute.calculateCost(instance.getVehicleCapacity(), instance.getAlpha(),
				instance.getBeta(), instance.getGamma());
		} else {
			deleteRoute = null;
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
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer
	 *            the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the deleteRouteNr
	 */
	public int getDeleteRouteNr() {
		return deleteRouteNr;
	}

	/**
	 * @param deleteRouteNr
	 *            the deleteRouteNr to set
	 */
	public void setDeleteRouteNr(int deleteRouteNr) {
		this.deleteRouteNr = deleteRouteNr;
	}

	/**
	 * @return the deletePositionIndex
	 */
	public int getDeletePositionIndex() {
		return deletePositionIndex;
	}

	/**
	 * @param deletePositionIndex
	 *            the deletePositionIndex to set
	 */
	public void setDeletePositionIndex(int deletePositionIndex) {
		this.deletePositionIndex = deletePositionIndex;
	}

	/**
	 * @return the insertRouteNr
	 */
	public int getInsertRouteNr() {
		return insertRouteNr;
	}

	/**
	 * @param insertRouteNr
	 *            the insertRouteNr to set
	 */
	public void setInsertRouteNr(int insertRouteNr) {
		this.insertRouteNr = insertRouteNr;
	}

	/**
	 * @return the insertPositionIndex
	 */
	public int getInsertPositionIndex() {
		return insertPositionIndex;
	}

	/**
	 * @param insertPositionIndex
	 *            the insertPositionIndex to set
	 */
	public void setInsertPositionIndex(int insertPositionIndex) {
		this.insertPositionIndex = insertPositionIndex;
	}

}
