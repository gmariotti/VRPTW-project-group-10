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

	/**
	 * Evaluate the cost of the route
	 */
	public void evaluateRoute() {
		this.cost = new Cost();
		Depot depot = this.getDepot();
		List<Customer> customers = this.getCustomers();

		/*
		 * cost from Depot to first Customer
		 */
		Customer customer = this.getCustomers(0);
		Cost cost = new Cost();
		double distance = customer.getDistance(depot.getXCoordinate(), depot.getYCoordinate());
		cost.addTravel(distance);
		customer.setArriveTime(distance);
		// I have to wait
		if (customer.getStartTw() - distance > 0) {
			cost.setWaitingTime(customer.getStartTw() - distance);
			customer.setWaitingTime(cost.getWaitingTime());
		}
		cost.setLoad(customer.getLoad());
		cost.setServiceTime(customer.getServiceDuration());

		// check if I arrive later
		if (customer.getDepartureTime() > customer.getTwViol()) {
			cost.setTwViol(customer.getDepartureTime() - customer.getTwViol());
			customer.setTwViol(cost.getTwViol());
		}
		this.cost.add(cost);

		/*
		 * cost from all Customers
		 */
		for (int i = 1; i < customers.size(); i++) {
			cost = new Cost();
			Customer tmp = customers.get(i);
			distance = tmp.getDistance(customer.getXCoordinate(), customer.getYCoordinate());
			cost.addTravel(distance);
			tmp.setArriveTime(customer.getDepartureTime() + distance);

			// I have to wait
			if (tmp.getStartTw() - tmp.getArriveTime() > 0) {
				cost.setWaitingTime(tmp.getStartTw() - tmp.getArriveTime());
				tmp.setWaitingTime(cost.getWaitingTime());
			}
			cost.setLoad(tmp.getLoad());
			cost.setServiceTime(tmp.getServiceDuration());

			// check if I arrive later
			if (tmp.getDepartureTime() > tmp.getEndTw()) {
				cost.setTwViol(tmp.getDepartureTime() - tmp.getEndTw());
				customer.setTwViol(cost.getTwViol());
			}
			this.cost.add(cost);
			customer = tmp;
		}

		/*
		 * cost from last Customer to Depot
		 */
		customer = customers.get(customers.size() - 1);
		distance = customer.getDistance(depot.getXCoordinate(), depot.getYCoordinate());

		// check if I arrive later
		if (customer.getDepartureTime() + distance > depot.getEndTw()) {
			this.cost.setDepotTwViol(customer.getDepartureTime() + distance - depot.getEndTw());
		}
		this.cost.setReturnToDepotTime(customer.getDepartureTime() + distance);
		this.cost
				.setLoadViol(this.cost.getLoad() - this.assignedVehicle.getCapacity() > 0 ? this.cost
						.getLoad() - this.assignedVehicle.getCapacity()
						: 0);
	}

	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("\n" + "Route[" + index + "]");
		print.append("\n" + "--------------------------------------------");
		print.append("\n" + "| Capacity=" + cost.getLoad() + " ServiceTime="
				+ cost.getServiceTime() + " TravelTime=" + cost.getTravelTime() + " WaitingTime="
				+ cost.getWaitingTime() + " Totaltime=" + cost.getTotal());
		print.append("\n" + cost);
		print.append("\n");
		return print.toString();
	}

	public boolean equals(Route route) {
		return this.getIndex() == route.getIndex();
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Cost getCost() {
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	public Vehicle getAssignedVehicle() {
		return assignedVehicle;
	}

	public void setAssignedVehicle(Vehicle assignedVehicle) {
		this.assignedVehicle = assignedVehicle;
	}

	public Depot getDepot() {
		return depot;
	}

	public void setDepot(Depot depot) {
		this.depot = depot;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	/**
	 * @return Returns the customer in position i of this route
	 */
	public Customer getCustomers(int index) {
		return customers.get(index);
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = new ArrayList<>(customers);
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
	 * Method that allows the calculation of the cost of a route. Not perfect yet as it
	 * doesn't allow the modification of the parameters alpha, beta and gamma. Also the
	 * max vehicle capacity has to be passed as parameters.
	 * @param maxAllowedLoad
	 * @return
	 */
	
	public void calculateCost(double maxAllowedLoad)
	{
		Cost cost = new Cost();
		Customer previousCustomer;
		Customer currentCustomer = customers.get(0);
		
		// this should be fixed by adding a getDistance method to the depot
		cost.setTravelTime(currentCustomer.getDistance(depot.getXCoordinate(), depot.getYCoordinate()));
		cost.setLoad(currentCustomer.getLoad());
		cost.setServiceTime(currentCustomer.getServiceDuration());
		
		currentCustomer.setArriveTime(depot.getStartTw() + cost.getTravelTime());
		
		currentCustomer.setWaitingTime(Math.max(0, currentCustomer.getStartTw() - currentCustomer.getArriveTime()));
		cost.setWaitingTime(currentCustomer.getWaitingTime());
		
		currentCustomer.setTwViol(Math.max(0, currentCustomer.getArriveTime() - currentCustomer.getEndTw()));
		cost.addTwViol(currentCustomer.getTwViol());	
		
		for(int i = 1; i < customers.size(); i++)
		{
			previousCustomer = currentCustomer;
			currentCustomer = customers.get(i);
			
			cost.setTravelTime(cost.getTravelTime() + previousCustomer.getDistance(currentCustomer.getXCoordinate(), currentCustomer.getYCoordinate()));
			cost.setLoad(cost.getLoad() + currentCustomer.getLoad());
			cost.setServiceTime(cost.getServiceTime() + currentCustomer.getServiceDuration());
			
			currentCustomer.setArriveTime(previousCustomer.getDepartureTime() + previousCustomer.getDistance(currentCustomer.getXCoordinate(), currentCustomer.getYCoordinate()));
			
			currentCustomer.setWaitingTime(Math.max(0, currentCustomer.getStartTw() - currentCustomer.getArriveTime()));
			cost.setWaitingTime(cost.getWaitingTime() + currentCustomer.getWaitingTime());
			
			currentCustomer.setTwViol(Math.max(0, currentCustomer.getArriveTime() - currentCustomer.getEndTw()));
			cost.addTwViol(cost.getTwViol() + currentCustomer.getTwViol());
		}
		
		cost.setTravelTime(cost.getTravelTime() + currentCustomer.getDistance(depot.getXCoordinate(), depot.getYCoordinate()));
		cost.setReturnToDepotTime(cost.getTravelTime());
		cost.setDepotTwViol(Math.max(0, cost.getReturnToDepotTime() - depot.getEndTw()));
		cost.addTwViol(cost.getTwViol() + cost.getDepotTwViol());
		
		cost.setLoadViol(Math.max(0, cost.getLoad() - maxAllowedLoad));
		cost.calculateTotal(1, 1, 1);
		
		this.setCost(cost);
	}
	
	/**
	 * 	Method that creates a route object containing only the fixed data of
	 * route.
	 * @return A Route object with the same index, assigned vehicle and depot
	 * as the calling object.
	 */
	public Route copyRouteInformation() {
		Route route = new Route();
		
		route.index = this.index;
		
		/*
		 *   Passing directly the depot reference instead of a copy is safe
		 *  as the depot object isn't supposed to be modified.
		 */
		route.depot = this.depot;
		route.assignedVehicle = new Vehicle(this.assignedVehicle.getVehicleNr(), this.assignedVehicle.getCapacity(), this.assignedVehicle.getDuration());
		
		return route;
	}
}
