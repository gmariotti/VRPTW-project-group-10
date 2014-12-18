package com.vrptw;

import java.util.ArrayList;
import java.util.List;

public class Route {
	private int index; // Number of the route
	private Cost cost; // cost of the route
	private Vehicle assignedVehicle; // vehicle assigned to the route
	private Depot depot; // depot the route starts from
	private List<Customer> customers; // list of customers served in the route

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
		print.append("\n" + "Route[" + index + "]");
		print.append("\n" + "--------------------------------------------");
		print.append("\n" + "| Capacity=" + cost.getLoad() + " ServiceTime="
				+ cost.getServiceTime() + " TravelTime=" + cost.getTravelTime() + " WaitingTime="
				+ cost.getWaitingTime() +" Totaltime=" + cost.getTotal());
		print.append("\n" + cost);
		print.append("\n");
		return print.toString();
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
		this.customers = customers;
	}

	/**
	 * Set a customer in the specified index.
	 * 
	 * @param customer - The Customer object to be added.
	 * @param index - The index of the customer to be changed to the new customer.
	 */
	public void setCustomers(Customer customer, int index) {	
		this.customers.set(index, customer);
	}
	
	/**
	 * Add a customer in the specified index.
	 * 
	 * @param customer - The Customer object to be added.
	 * @param index - The index in which to add the customer. Setting this value to -1
	 * 				  adds the element in the end of the list
	 */
	public void addCustomer(Customer customer, int index){
		if(index == -1){
			customers.add(customer);
		}
		else{
			customers.add(index, customer);
		}
	}
}
