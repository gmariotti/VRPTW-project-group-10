/**
 * 
 */
package com.tabusearch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.coinor.opents.SolutionAdapter;

import com.vrptw.*;

/**
 * This class is used to represent a Solution.
 */
@SuppressWarnings("serial")
public class MySolution extends SolutionAdapter {

	/**
	 * The 'solution' object is a hash table where:
	 * - VehicleNumber is the key,
	 * - Route is the list of the customers visited by the Vehicle.
	 */
	Map<Integer, Route> solution = new ConcurrentHashMap<Integer, Route>();
	
	/**
	 * Default constructor for MySolution Class.
	 * It does nothing.
	 * If you want to generate an initial solution, please :
	 * - first create a Solution by calling this constructor
	 * - then call generateInitialSolution(instance).
	 */
	public MySolution() {
		super();
	}
	
	/**
	 * This method is used to generate a new initial solution.
	 * @param instance The instance of the problem we want to solve.
	 */
	public void generateInitialSolution(Instance instance) {
		int maxVehicleNumber = instance.getVehicleNr();
		double maxVehicleCapacity = instance.getVehicleCapacity();
		List<Customer> customers = instance.getCustomers();
		
		/*
		 * At the beginning, we generate a solution by :
		 * - selecting a vehicle
		 * - selecting a customer, and assign it to the vehicle until it's full
		 * - get another vehicle, and do the same with the remaining customers.
		 * We do not care about time window (yet).
		 */
		
		int vehicleNumber = 1;	// First vehicle (i is the variable counting the vehicles)
		double load = 0;	// First load of a vehicle (need to be less than maxCapacity)
		
		Boolean stop = Boolean.FALSE;	// The stopping condition will be true when there are no customers left.
		
		// Temporary list of customers
		List<Customer> customersPerVehicle = new ArrayList<Customer>();
		
		while (!stop) {
			
			// Create a new vehicle
			Vehicle vehicle = new Vehicle();
			vehicle.setVehicleNr(vehicleNumber);
			
			// set the initial load to 0
			load = 0;
			
			// for each Customer in the Customer list
			for (Customer customer : customers) {
				
				// If we can add this customer to the vehicle (final load is less than maxCapacity)
				if ((customer.getLoad() + load) < maxVehicleCapacity) {
					
					// then we add the customer
					customersPerVehicle.add(customer);
					// we increment the total load
					load = customer.getLoad() + load;
					// and we remove it from the list (not to serve twice the same customer)
					customers.remove(customer);
					
				} else {	// it means that the vehicle is full
					break;	// exit from the 'For' loop
				}
			}
			
			// If there are no customers left, we can stop
			if (customers.isEmpty()) {
				stop = Boolean.TRUE;
			}
			
			// create new Route
			Route route = new Route();
			route.setAssignedVehicle(vehicle);
		
			// Add this vehicle (with its customers) to our solution (hash table)
			this.solution.put(vehicleNumber, route);
			
			// empty the temporary list of customers
			for (Customer customer : customersPerVehicle) {
				customersPerVehicle.remove(customer);
			}
			
			vehicleNumber++;	// increment vehicle number
			
			// if we need more vehicles than specified, solution is infeasible
			if (vehicleNumber >= maxVehicleNumber) {
				stop = Boolean.TRUE;
			}
			
		} // end while if no customers remaining, otherwise loop
		
	} // end function

}
