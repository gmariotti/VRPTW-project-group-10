package com.vrptw;

import java.util.ArrayList;
import java.util.List;

public class Route {
	private int index; // Number of the route
	private Cost cost; // cost of the route
	private Vehicle assignedVehicle; // vehicle assigned to the route
	private Depot depot; // depot the route starts from
	private List<Customer> customers; // list of customers served in the route

	/**
	 * Constructor of the route
	 */
	public Route() {
		cost = new Cost();
		customers = new ArrayList<>();
	}

	/**
	 * Constructor of the route from a route
	 * 
	 * @param route
	 */
	public Route(Route route) {

		this.setIndex(route.getIndex());
		this.setCost(route.getCost());
		this.setAssignedVehicle(route.getAssignedVehicle());
		this.setDepot(route.getDepot());
		this.setCustomers(route.getCustomers());
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the cost
	 */
	public Cost getCost() {
		return cost;
	}

	/**
	 * @param cost
	 *            the cost to set
	 */
	public void setCost(Cost cost) {
		this.cost = cost;
	}

	/**
	 * @return the assignedVehicle
	 */
	public Vehicle getAssignedVehicle() {
		return assignedVehicle;
	}

	/**
	 * @param assignedVehicle
	 *            the assignedVehicle to set
	 */
	public void setAssignedVehicle(Vehicle assignedVehicle) {
		this.assignedVehicle = assignedVehicle;
	}

	/**
	 * @return the depot
	 */
	public Depot getDepot() {
		return depot;
	}

	/**
	 * @param depot
	 *            the depot to set
	 */
	public void setDepot(Depot depot) {
		this.depot = depot;
	}

	/**
	 * @return the customers
	 */
	public List<Customer> getCustomers() {
		return customers;
	}

	/**
	 * Get a customer from the list
	 * 
	 * @param index
	 * @return
	 */
	public Customer getCustomers(int index) {
		return customers.get(index);
	}

	/**
	 * @param customers
	 *            the customers to set
	 */
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	/**
	 * Set a customer in a certain position. If index is -1, the customer is
	 * added at the end of the list
	 * 
	 * @param customer
	 * @param index
	 */
	public void setCustomers(Customer customer, int index) {
		if (index == -1) {
			this.customers.add(customer);
		} else {
			this.customers.set(index, customer);
		}
	}
}
