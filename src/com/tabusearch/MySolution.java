/**
 * 
 */
package com.tabusearch;

import java.io.FileWriter;
import java.util.*;

import org.coinor.opents.SolutionAdapter;

import com.vrptw.*;

/**
 * This class is used to represent a Solution. A solution is a Route object.
 */
@SuppressWarnings("serial")
public class MySolution extends SolutionAdapter {

	private Route[]		routes;
	private Instance	instance;
	private int			maxVehicleNumber	= 0;
	private double		maxVehicleCapacity	= 0;
	private int			customersNumber		= 0;
	private double[][]	distances;
	private Depot		depot;
	private Cost		cost;

	private double		alpha;
	private double		beta;
	private double		gamma;

	/**
	 * Default constructor for MySolution Class. It does nothing. If you want to generate an initial
	 * solution, please : - first create a MySolution by calling this constructor - then call
	 * generateInitialSolution().
	 * 
	 * @param instance
	 *            The instance we want to consider to create the solution.
	 */
	public MySolution(Instance instance) {
		super();
		this.instance = instance;
		this.maxVehicleNumber = instance.getVehiclesNr();
		this.maxVehicleCapacity = instance.getVehicleCapacity();
		this.customersNumber = instance.getCustomersNr();
		this.distances = instance.getDistances();
		this.depot = instance.getDepot();
		this.routes = new Route[this.maxVehicleNumber];
		this.cost = new Cost();
	}

	/**
	 * This method is used to generate a new initial solution.
	 * 
	 * @param nothing
	 *            .
	 */
	public void generateInitialSolution() {
		/*
		 * Initialize the customers arraylist, creating a new customer identical to the one in the
		 * instace.customers list. In this way we avoid any modification on the main list of
		 * customer
		 */
		List<Customer> customers = new ArrayList<Customer>();
		for (Customer tmp : this.instance.getCustomers()) {
			Customer customer = new Customer(tmp);
			customers.add(customer);
		}

		/*
		 * At the beginning, we generate a solution by : - selecting a vehicle - selecting a
		 * customer, and assign it to the vehicle until it's full - get another vehicle, and do the
		 * same with the remaining customers. We do not care about time window (yet).
		 */

		int vehicleNumber = 1; // First vehicle (i is the variable counting the vehicles)
		double load = 0; // First load of a vehicle (need to be less than maxCapacity)

		Boolean stop = Boolean.FALSE; // The stopping condition will be true when there are no
										// customers left.

		while (!stop) {

			// Temporary list of customers
			List<Customer> customersPerVehicle = new ArrayList<Customer>();

			// Create a new vehicle
			Vehicle vehicle = new Vehicle();
			vehicle.setVehicleNr(vehicleNumber);

			// set the initial load to 0
			load = 0;

			// we calculate the minimum of the distances between depot and customers
			// so we need an array : distances between the depot and all customers
			int row = 0; // depot
			int column = 0; // all customers
			double[] array = new double[this.distances.length];

			Boolean full = Boolean.FALSE;
			while (!full && customers.size() > 0) {

				// fixed row (customer to consider), variable columns (all the other customers)
				for (column = 0; column < array.length; column++) {
					if (column == row) {
						array[column] = Double.POSITIVE_INFINITY;
					} else {
						array[column] = this.distances[row][column];
					}
				}
				// we calculate the min distance between the considered customer and all others
				double minDistance[] = minimum(array);

				// we get the index of the nearest customer
				int index = (int) minDistance[1];

				// we select the customer with the minimum distance (by his index)
				Customer customer = customers.get(index);

				// If we can add this customer to the vehicle (final load is less than maxCapacity)
				if ((customer.getLoad() + load) < this.maxVehicleCapacity) {

					// then we add the customer
					customersPerVehicle.add(customer);
					// we increment the total load
					load = customer.getLoad() + load;
					// and we remove it from the list (not to serve twice the same customer)
					customers.remove(customer);

				} else { // it means that the vehicle is full
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
			route.setIndex(vehicleNumber);

			// Add this Route to the array of routes
			this.routes[vehicleNumber-1] = route;

			// empty the temporary list of customers
			/*
			 * for (Customer customer : customersPerVehicle) { customersPerVehicle.remove(customer);
			 * } I don't know why he uses it, but it creates an error
			 */

			vehicleNumber++; // increment vehicle number

			// if we need more vehicles than specified, solution is infeasible
			if (vehicleNumber >= this.maxVehicleNumber) {
				stop = Boolean.TRUE;
			}

		} // end while if no customers remaining, otherwise loop

	} // end function

	/**
	 * This function calculate the minimum of an array.
	 * 
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
	 * 
	 * @param solution
	 *            The MySolution we want to clone. If null, we consider the current MySolution.
	 * @return a MySolution object
	 */

	public void print() {
		Route[] routes = this.getRoutes();
		String solution = "";
		for (Route route : routes) {
			if (route == null) {
				continue;
			}
			solution += "R#" + route.getIndex() + " V#" + route.getAssignedVehicle().getVehicleNr()
					+ " ";
			List<Customer> customers = route.getCustomers();
			for (Customer cust : customers) {
				solution += "C#" + cust.getNumber() + " ";
			}
			solution += "\n";
		}
		System.out.println(solution);
		try {
			FileWriter fw = new FileWriter(System.getProperty("user.dir")
					+ "/output/routeOfSolution.txt", true);
			fw.write(solution);
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Cost getCost() {
		return this.cost;
	}

	/**
	 * @return the routes
	 */
	public Route[] getRoutes() {
		// to avoid an array with null elements
		List<Route> list = new ArrayList<>();
		for (Route route : this.routes) {
			if (route == null) {
				break;
			}
			list.add(route);
		}
		return list.toArray(new Route[list.size()]);
	}

	/**
	 * 
	 * @param index
	 *            the position of the route (index is also the vehicle number !)
	 * @return the route searched
	 */
	public Route getRoutes(int index) {
		return routes[index];
	}

	/**
	 * @param routes
	 *            the routes to set
	 */
	public void setRoutes(Route[] routes) {
		this.routes = routes;
	}

	/**
	 * 
	 * @param route
	 *            the route to set
	 * @param index
	 *            the position in which as to be set
	 */
	public void setRoutes(Route route, int index) {
		this.routes[index] = route;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getBeta() {
		return beta;
	}

	public double getGamma() {
		return gamma;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
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
	 * @return the maxVehicleNumber
	 */
	public int getMaxVehicleNumber() {
		return maxVehicleNumber;
	}

	/**
	 * @param maxVehicleNumber
	 *            the maxVehicleNumber to set
	 */
	public void setMaxVehicleNumber(int maxVehicleNumber) {
		this.maxVehicleNumber = maxVehicleNumber;
	}

	/**
	 * @return the maxVehicleCapacity
	 */
	public double getMaxVehicleCapacity() {
		return maxVehicleCapacity;
	}

	/**
	 * @param maxVehicleCapacity
	 *            the maxVehicleCapacity to set
	 */
	public void setMaxVehicleCapacity(double maxVehicleCapacity) {
		this.maxVehicleCapacity = maxVehicleCapacity;
	}

	/**
	 * @return the customersNumber
	 */
	public int getCustomersNumber() {
		return customersNumber;
	}

	/**
	 * @param customersNumber
	 *            the customersNumber to set
	 */
	public void setCustomersNumber(int customersNumber) {
		this.customersNumber = customersNumber;
	}

	/**
	 * @return the distances
	 */
	public double[][] getDistances() {
		return distances;
	}

	/**
	 * @param distances
	 *            the distances to set
	 */
	public void setDistances(double[][] distances) {
		this.distances = distances;
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
	 * @param cost
	 *            the cost to set
	 */
	public void setCost(Cost cost) {
		this.cost = cost;
	}
	
	
	
	public Object clone()
	{
		MySolution clonedSolution = new MySolution(instance);
		List<Customer> customers;
		int i = 0;
		
		clonedSolution.setCost(new Cost(this.cost));
		clonedSolution.alpha = this.alpha;
		clonedSolution.beta = this.beta;
		clonedSolution.gamma = this.gamma;
		
		for(Route route : this.routes)											
		{
			if(route == null) { break; }
			customers = route.getCustomers();
			
			clonedSolution.routes[i] = new Route();
			// clone route information
			clonedSolution.routes[i].setIndex(route.getIndex());
			clonedSolution.routes[i].setDepot(clonedSolution.depot);
			clonedSolution.routes[i].setAssignedVehicle(route.getAssignedVehicle());
			clonedSolution.routes[i].setCost(new Cost(route.getCost()));
			
			for(Customer customer : customers)									
			{
				// clone customers
				clonedSolution.routes[i].addCustomer(new Customer(customer), -1);
			}
			i++;
		}

		return clonedSolution;
	}

}
