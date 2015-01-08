package com.vrptw;

import java.util.ArrayList;
import java.util.List;

public class Route {
	private int				index;				// Number of the route
	private Cost			cost;				// cost of the route
	private Vehicle			assignedVehicle;	// vehicle assigned to the route
	private Depot			depot;				// depot the route starts from
	private List<Customer>	customers;			// list of customers served in the route

	public Route() {
		cost = new Cost();
		customers = new ArrayList<>();
	}

	public Route(Route route) {
		this.setIndex(route.getIndex());
		this.setCost(route.getCost());
		this.setAssignedVehicle(route.getAssignedVehicle());
		this.setDepot(route.getDepot());
		this.setCustomers(route.getCustomers());
	}

	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("--- Route[" + index + "] ---" + "\n");
		print.append("Capacity=" + cost.getLoad() + " ServiceTime=" + cost.getServiceTime()
				+ " TravelTime=" + cost.getTravelTime() + " WaitingTime=" + cost.getWaitingTime()
				+ " Totaltime=" + cost.getTotal() + "\n");
		print.append(cost);
		print.append("------" + "\n");
		return print.toString();
	}

	/**
	 * Method that allows the calculation of the cost of a route. Not perfect yet as it doesn't
	 * allow the modification of the parameters alpha, beta and gamma. Also the max vehicle capacity
	 * has to be passed as parameters.
	 * 
	 * @param maxAllowedLoad
	 * @return
	 */
	public void calculateCost(double maxAllowedLoad, double alpha, double beta, double gamma) {
		Cost cost = new Cost();
		Customer previousCustomer;
		Customer currentCustomer = customers.get(0);

		// this should be fixed by adding a getDistance method to the depot
		cost.setTravelTime(currentCustomer.getDistance(depot.getXCoordinate(),
				depot.getYCoordinate()));
		cost.setLoad(currentCustomer.getLoad());
		cost.setServiceTime(currentCustomer.getServiceDuration());

		currentCustomer.setArriveTime(depot.getStartTw() + cost.getTravelTime());

		currentCustomer.setWaitingTime(Math.max(0,
				currentCustomer.getStartTw() - currentCustomer.getArriveTime()));
		cost.setWaitingTime(currentCustomer.getWaitingTime());

		currentCustomer.setTwViol(Math.max(0,
				currentCustomer.getArriveTime() - currentCustomer.getEndTw()));
		cost.addTwViol(currentCustomer.getTwViol());

		for (int i = 1; i < customers.size(); i++) {
			previousCustomer = currentCustomer;
			currentCustomer = customers.get(i);

			cost.setTravelTime(cost.getTravelTime()
					+ previousCustomer.getDistance(currentCustomer.getXCoordinate(),
							currentCustomer.getYCoordinate()));
			cost.setLoad(cost.getLoad() + currentCustomer.getLoad());
			cost.setServiceTime(cost.getServiceTime() + currentCustomer.getServiceDuration());

			currentCustomer.setArriveTime(previousCustomer.getDepartureTime()
					+ previousCustomer.getDistance(currentCustomer.getXCoordinate(),
							currentCustomer.getYCoordinate()));

			currentCustomer.setWaitingTime(Math.max(0, currentCustomer.getStartTw()
					- currentCustomer.getArriveTime()));
			cost.setWaitingTime(cost.getWaitingTime() + currentCustomer.getWaitingTime());

			currentCustomer.setTwViol(Math.max(0,
					currentCustomer.getArriveTime() - currentCustomer.getEndTw()));
			cost.addTwViol(cost.getTwViol() + currentCustomer.getTwViol());
		}

		cost.setTravelTime(cost.getTravelTime()
				+ currentCustomer.getDistance(depot.getXCoordinate(), depot.getYCoordinate()));
		cost.setReturnToDepotTime(cost.getTravelTime());
		cost.setDepotTwViol(Math.max(0, cost.getReturnToDepotTime() - depot.getEndTw()));
		cost.addTwViol(cost.getTwViol() + cost.getDepotTwViol());

		cost.setLoadViol(Math.max(0, cost.getLoad() - maxAllowedLoad));
		cost.calculateTotal(alpha, beta, gamma);

		this.setCost(cost);
	}

	/**
	 * Two routes are equal if they have the same index
	 * 
	 * @param route
	 * @return
	 */
	public boolean equals(Route route) {
		return this.getIndex() == route.getIndex();
	}

	/**
	 * Add a customer in the specified index.
	 * 
	 * @param customer
	 *            - The Customer object to be added.
	 * @param index
	 *            - The index in which to add the customer. Setting this value to -1 adds the
	 *            element in the end of the list
	 */
	public void addCustomer(Customer customer, int index) {
		if (index == -1) {
			customers.add(customer);
		} else {
			customers.add(index, customer);
		}
	}

	/**
	 * Finds the index of a specific customer in the route which calls the function.
	 * 
	 * @param customer
	 *            - The customer whose index we want to find.
	 * @return The index in the list of customers to visit where the customer can be found.
	 */
	public int indexOfCustomer(Customer customer) {
		return customers.indexOf(customer);
	}

	/**
	 * Method that creates a route object containing only the fixed data of route.
	 * 
	 * @return A Route object with the same index, assigned vehicle and depot as the calling object.
	 */
	public Route copyRouteInformation() {
		Route route = new Route();

		route.index = this.index;

		/*
		 * Passing directly the depot reference instead of a copy is safe as the depot object isn't
		 * supposed to be modified.
		 */
		route.depot = this.depot;
		route.assignedVehicle = new Vehicle(this.assignedVehicle.getVehicleNr(),
				this.assignedVehicle.getCapacity(), this.assignedVehicle.getDuration());

		return route;
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
	 * @return Returns the customer in position i of this route
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
	 * Set a customer in the specified index.
	 * 
	 * @param customer
	 *            - The Customer object to be added.
	 * @param index
	 *            - The index of the customer to be changed to the new customer.
	 */
	public void setCustomers(Customer customer, int index) {
		this.customers.set(index, customer);
	}

}
