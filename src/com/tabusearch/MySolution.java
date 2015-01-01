/**
 * 
 */
package com.tabusearch;

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

	private double		alpha				= 2;
	private double		beta				= 2;
	private double		gamma				= 2;
	private double 		routeLimitFactor	= 1.5;	// used to limit the number of Customers per route

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
		// array used to determine the best choice for the next customer
		double[] timeWindowCriteria = new double[customersNumber];
		// array used to be able to tell whether a customer is placed in a route or not
		boolean[] routed = new boolean[customersNumber];
		// array to keep the indexes of all customers sorted by starting TW
		int[] customerIndexByStartTw;
		
		int maxCustomersPerRoute = (int) ((customersNumber/maxVehicleNumber) * routeLimitFactor);
		boolean stop = Boolean.FALSE;			// condition to stop looping
		
		int numberOfInitializedRoutes = 0;
		int routedCustomers = 0;
		int count = 0;
		
		List<Customer> customers = this.instance.getCustomers();
		
		customerIndexByStartTw = sortByTw(customers);
		
		while(!stop){
			Customer current;		// the current customer in the current route
			Customer potentialNext;	// potential candidate customer to follow the currentCustomer
			
			double minArriveTime;
			double maxArriveTime;
			double startTw;		// starting TW of the customer to be routed
			double endTw;		// end TW of the customer to be route
			double waitingTime;
			double distance;
			
			int customersInCurrentRoute = 0;
			double currentRouteLoad = 0;
			
			if(numberOfInitializedRoutes < this.maxVehicleNumber)	// new routes can be assigned
			{
				this.routes[numberOfInitializedRoutes] = new Route();
				this.routes[numberOfInitializedRoutes].setIndex(numberOfInitializedRoutes);
				// TODO -> fix 3rd argument of the Vehicle class constructor if we have any 
				this.routes[numberOfInitializedRoutes].setAssignedVehicle(
						new Vehicle(numberOfInitializedRoutes, this.maxVehicleCapacity, instance.getDurations()[0])
						);
				this.routes[numberOfInitializedRoutes].setDepot(this.depot);
				
				while(routed[customerIndexByStartTw[count]]) {
					count++;
				}
				
				current = new Customer(customers.get(customerIndexByStartTw[count]));
				
				while(customersInCurrentRoute < maxCustomersPerRoute) {
					double maxValue = Double.NEGATIVE_INFINITY;
					int maxIndex = -1;
					
					this.routes[numberOfInitializedRoutes].addCustomer(current, -1);
					
					routed[current.getNumber()] = true;
					routedCustomers++;
					customersInCurrentRoute++;
					currentRouteLoad += current.getLoad();
					
					// calculate the TW criteria
					for(int i = 0; i < this.customersNumber; i++)
					{
						if(routed[i]) {
							timeWindowCriteria[i] = Double.NEGATIVE_INFINITY;
							continue;
						}
						
						potentialNext = new Customer(customers.get(i));
						
						/*
						 *  The minimum arrive time at the next customer from the current customer.
						 *  So we suppose to be at current customer either at the Start TW or before.
						 */
						minArriveTime = current.getStartTw() + current.getServiceDuration()
									  + distances[current.getNumber()][potentialNext.getNumber()];
						
						endTw = potentialNext.getEndTw();
						
						if(endTw < minArriveTime)	// the potentialNext customer cannot be reached
						{
							timeWindowCriteria[i] = Double.NEGATIVE_INFINITY;
							continue;
						}
						
						/*
						 *  The maximum arrive time at the next customer from the current customer.
						 *  So we suppose to be at current customer either exactly at the end of the End TW.
						 */
						maxArriveTime = current.getEndTw() + current.getServiceDuration()
									  + distances[current.getNumber()][potentialNext.getNumber()];
						
						startTw = potentialNext.getStartTw();
						
						waitingTime = Math.max(0, startTw - minArriveTime);
						distance = distances[current.getNumber()][potentialNext.getNumber()];
						
						timeWindowCriteria[i] = Math.min(maxArriveTime, endTw) - Math.max(minArriveTime, startTw) - 1/5 * waitingTime - 1/5 * distance;
						
						// So as not to have to search for the max value later.
						// The second condition ensures there is no load violation.
						if(timeWindowCriteria[i] > maxValue && 
						   potentialNext.getLoad() + currentRouteLoad < this.maxVehicleCapacity)	
						{
							maxValue = timeWindowCriteria[i];
							maxIndex = i;
						}
					}
					
					// this means this route because none of the candidates satisfied the criteria
					if(maxIndex == -1 || maxValue == Double.NEGATIVE_INFINITY) { break; }	
					
					current = customers.get(maxIndex);
				}
				
				numberOfInitializedRoutes++;
			}
			
			// stop if all customers have been routed
			if(routedCustomers == customersNumber)
			{
				stop = true;
			}
			else if(numberOfInitializedRoutes == maxVehicleNumber){
				/*
				 *  In this case there are still customers left not added to routes, but
				 * no more routes, so we add all of them to the first route and let
				 * our moves guide us to good solution
				 */
				count = 0;
				while(count < customersNumber) {	
					while(count < customersNumber && routed[count]) { count++; }		// skip already routed customers
					
					if(count < customersNumber)	{
						current = new Customer(customers.get(count));
						this.routes[0].addCustomer(current, -1);
						routed[count] = true;
						count++;
					}
				}	
				
				stop = true;
			}
		}
	}
	
	public void generateInitialFeasibleSolution() {
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

		Boolean stop = Boolean.FALSE; // The stopping condition will be true when there are no
										// customers left.

		/*
		 * Each iteration is the creation of a route
		 */
		while (!stop) {
			Depot depot = this.getDepot();
			List<Double> customersCost = new ArrayList<>();
			Vehicle vehicle = new Vehicle(vehicleNumber, this.getMaxVehicleCapacity(), 0);
			double load = 0;
			double time = 0;
			for (Customer customer : customers) {
				// cost from the depot into customersCost
				double distanceCost = instance.getDistances()[customer.getNumber()][instance.getCustomersNr()];
				if (distanceCost < customer.getStartTw()) { // I have to wait the opening time
					customersCost.add((double) customer.getStartTw());
				} else { // I need more time to arrive compared to the opening time
					customersCost.add(distanceCost);
				}
			}
			List<Customer> routeCustomers = new ArrayList<>();
			int index = minimumCost(customersCost);
			Customer customerToAdd = customers.get(index);
			// add the customer to the route
			routeCustomers.add(customerToAdd);
			load += customerToAdd.getLoad();
			customers.remove(index); // the list is automatically reordered

			// increment the time
			time = customersCost.get(index) + customerToAdd.getServiceDuration();

			/*
			 * Iterate starting from the last customer added
			 */
			Boolean full = Boolean.FALSE;

			while (!full || customers.size() > 0) {

				customersCost = new ArrayList<>();
				for (Customer customer : customers) {
					// evaluate distance
					double distanceCost = instance.getDistances()[customer.getNumber()][customerToAdd.getNumber()];
					// check feasible
					if ((load + customer.getLoad()) <= vehicle.getCapacity()
							&& (time + distanceCost) < (customer.getEndTw() - customer
									.getServiceDuration())) {
						if ((time + distanceCost) < customer.getStartTw()) { // I have to wait
							customersCost.add(customer.getStartTw() - time);
						} else {
							customersCost.add(distanceCost);
						}
					} else {
						customersCost.add(Double.POSITIVE_INFINITY);
					}
				}
				// if there isn't any feasible customer than I exit
				if (!feasibleCustomers(customersCost)) {
					break;
				}
				index = minimumCost(customersCost);
				customerToAdd = customers.get(index);
				// add the customerToAdd
				routeCustomers.add(customerToAdd);
				load += customerToAdd.getLoad();
				customers.remove(index); // the list is automatically reordered

				// increment the time
				time += customersCost.get(index) + customerToAdd.getServiceDuration();

				if (load >= vehicle.getCapacity()) {
					full = Boolean.TRUE;
				}
			}

			/*
			 * Generate the new route
			 */
			Route route = new Route();
			route.setIndex(vehicleNumber - 1);
			route.setCustomers(routeCustomers);
			route.setAssignedVehicle(vehicle);
			route.setDepot(depot);

			routes[vehicleNumber - 1] = route;
			vehicleNumber++;

			// if there aren't other customers than exit
			if (customers.size() == 0) {
				stop = Boolean.TRUE;
			}
		}

	} // end function

	/**
	 * check if there are one or more feasibleCustomers
	 */
	private boolean feasibleCustomers(List<Double> customersCost) {
		// no elements in the list
		if (customersCost.size() == 0) {
			return false;
		}
		boolean feasible = false;
		for (Double cost : customersCost) {
			if (cost < Double.POSITIVE_INFINITY) {
				feasible = true;
				break;
			}
		}
		return feasible;
	}

	/**
	 * Get the index of the element with the minimum cost
	 * 
	 * @param customersCost
	 *            The list of cost to analyze
	 * @return The index of the minimum cost element
	 */
	private int minimumCost(List<Double> customersCost) {
		double result = Double.POSITIVE_INFINITY;
		for (double cost : customersCost) {
			if (cost < result) {
				result = cost;
			}
		}
		return customersCost.indexOf(result);
	}

	/*
	 *   Sorts the customers by starting TW and return an integer
	 * array with the indexes in order
	 */
	private int[] sortByTw(List<Customer> customers) {
		int[] indexes = new int[customersNumber];
		int[] startTws = new int[customersNumber];
		int temp;
		
		// store in the matrix the relative data for sorting
		for(int i = 0; i < customersNumber; i++)
		{
			Customer customer = customers.get(i);
			indexes[i] = customer.getNumber();
			startTws[i] = customer.getEndTw();
		}
		
		// A simple bubble sort can be replaced with some quicker sorting algorithm
		
		for(int i = 0; i < customersNumber; i++)
		{
			for(int j = 1; j < customersNumber - i; j++)
			{
				if(startTws[j - 1] > startTws[j])
				{
					temp = startTws[j - 1];
					startTws[j - 1] = startTws[j];
					startTws[j] = temp;
					temp = indexes[j - 1];
					indexes[j - 1] = indexes[j];
					indexes[j] = temp;
				}
			}
		}
		
		return indexes;
	}


	/**
	 * This function is used to clone a Solution.
	 * 
	 * @return a MySolution object
	 */
	public Object clone() {
		MySolution clonedSolution = (MySolution) super.clone();

		clonedSolution.instance = instance;
		clonedSolution.maxVehicleNumber = instance.getVehiclesNr();
		clonedSolution.maxVehicleCapacity = instance.getVehicleCapacity();
		clonedSolution.customersNumber = instance.getCustomersNr();
		clonedSolution.distances = instance.getDistances();
		clonedSolution.depot = instance.getDepot();
		clonedSolution.routes = new Route[this.maxVehicleNumber];
		clonedSolution.cost = new Cost();

		List<Customer> customers;
		int i = 0;

		clonedSolution.setCost(new Cost(this.cost));
		clonedSolution.alpha = this.alpha;
		clonedSolution.beta = this.beta;
		clonedSolution.gamma = this.gamma;
		clonedSolution.routeLimitFactor = this.routeLimitFactor;

		for (Route route : this.getRoutes()) {
			customers = route.getCustomers();

			clonedSolution.routes[i] = new Route();
			// clone route information
			clonedSolution.routes[i].setIndex(route.getIndex());
			clonedSolution.routes[i].setDepot(clonedSolution.depot);
			clonedSolution.routes[i].setAssignedVehicle(route.getAssignedVehicle());
			clonedSolution.routes[i].setCost(new Cost(route.getCost()));

			for (Customer customer : customers) {
				// clone customers
				clonedSolution.routes[i].addCustomer(new Customer(customer), -1);
			}
			i++;
		}

		return clonedSolution;
	}

	public void print() {
		Route[] routes = this.getRoutes();
		String solution = "\n";
		for (Route route : routes) {
			if (route == null) {
				break;
			}
			solution += "R#" + route.getIndex() + " V#" + route.getAssignedVehicle().getVehicleNr()
					+ " ";
			List<Customer> customers = route.getCustomers();
			for (Customer cust : customers) {
				solution += "C#" + cust.getNumber() + " ";
			}
			Cost cost = route.getCost();
			solution += "Cost=" + cost.getTotal() + " ";
			if (cost.checkFeasible()) {
				solution += "isFeasible ";
			} else {
				solution += "isNotFeasible ";
			}
			solution += "\n";
		}
		solution += "The total cost is: " + this.cost.getTotal();
		System.out.println(solution);
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
				continue;
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
		this.routes[index] = new Route(route);
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

	public boolean isFeasible() {
		for(Route route : this.getRoutes())
		{
			if(!route.getCost().checkFeasible())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public void calculateCost() {
		Route[] routes = this.getRoutes();
		Cost solCost = this.cost;
		solCost.reset();
		for (Route route : routes) {
			Cost cost = route.getCost();
			solCost.add(cost);
		}
		solCost.calculateTotal(instance.getAlpha(), instance.getBeta(), instance.getGamma());
	}
	
	public void removeRoute(int index) {
		this.routes[index] = null;
	}
	
	public Route[] removeRoutes(Route[] routes, List<Route> toDelete) {
		List<Route> list = new ArrayList<>();
		for (Route route : routes) {
			int size = 0;
			for (Route tmp : toDelete) {
				if (route != null && route.getIndex() != tmp.getIndex()) {
					size++;
				} else if (route.getIndex() == tmp.getIndex()) {
					size--;
				}
				if (size == toDelete.size()) {
					list.add(route);
				}
			}
		}
		return list.toArray(new Route[list.size()]);
	}

}
