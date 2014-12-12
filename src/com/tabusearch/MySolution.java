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
	 * - first create a MySolution by calling this constructor
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
		double[][] distances = instance.getDistances();
		Depot depot = instance.getDepot();
		
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
			
			// we calculate the minimum of the distances between depot and customers
			// so we need an array : distances between the depot and all customers
			int row = 0;	// depot
			int column = 0;	// all customers
			double[] array = new double[distances.length];
			
			Boolean full = Boolean.FALSE;
			while (!full) {
			
				// fixed row (customer to consider), variable columns (all the other customers)
				for (column = 0; column < array.length; column++) {
					if (column == row) {
						array[column] = Double.POSITIVE_INFINITY;
					} else {
						array[column] = distances[row][column];
					}
				}
				// we calculate the min distance between the considered customer and all others
				double minDistance[] = minimum(array);
				
				// we get the index of the nearest customer
				int index = (int) minDistance[1];
				
				// we select the customer with the minimum distance (by his index)
				Customer customer = customers.get(index);
				
				// If we can add this customer to the vehicle (final load is less than maxCapacity)
				if ((customer.getLoad() + load) < maxVehicleCapacity) {
					
					// then we add the customer
					customersPerVehicle.add(customer);
					// we increment the total load
					load = customer.getLoad() + load;
					// and we remove it from the list (not to serve twice the same customer)
					customers.remove(customer);
					
				} else {	// it means that the vehicle is full
					full = Boolean.TRUE;
				}
				
				// then we loop, considering the nearest customer from the selected customer
				row = index;
				
			} // end while if vehicle full, otherwise loop
			
			// If there are no customers left, we can stop
			if (customers.isEmpty()) {
				stop = Boolean.TRUE;
			}
			
			// create new Route
			Route route = new Route();
			route.setAssignedVehicle(vehicle);
			route.setCustomers(customersPerVehicle);
		
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
	
	/**
	 * This function calculate the minimum of an array.
	 * @param array
	 * @return The minimum distance, and the customer associated
	 */
	private double[] minimum(double[] array) {
		double[] result = new double[2];
		int index = 0;
		double distance = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < array.length; i++) {
			if (array[i] < distance) {
				distance = array[i];
			}
		}
		
		result[0] = distance;
		result[1] = index;
		return result;
	}

	/**
	 * This function is used to clone a Solution.
	 * @param solution The MySolution we want to clone. If null, we consider the current MySolution.
	 * @return a MySolution object
	 */
	public MySolution clone (MySolution solution) {
		MySolution newSolution = new MySolution();
		if (solution == null) {
			newSolution.solution = this.solution;
		} else {
			newSolution.solution = solution.solution;
		}
		return newSolution;
		
	}

	public Cost getCost() {
		// TODO Auto-generated method stub
		return null;
	}

	public Route[] getRoutes() {
		// TODO Auto-generated method stub
		return null;
	}

}
