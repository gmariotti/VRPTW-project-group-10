/**
 * 
 */
package com.tabusearch;

import java.util.*;

import org.coinor.opents.Solution;
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

	private double		alpha				= 1;
	private double		beta				= 1;
	private double		gamma				= 0.1;

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

	public void calculateCost() {
		Route[] routes = this.getRoutes();
		Cost solCost = this.cost;
		solCost.reset();
		for (Route route : routes) {
			Cost cost = route.getCost();
			solCost.add(cost);
		}
		solCost.calculateTotal(this.getAlpha(), this.getBeta(), this.getGamma());
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
		for (Customer customer : this.instance.getCustomers()) {
			customers.add(customer.clone());
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
				double distanceCost = customer.getDistance(depot.getXCoordinate(),
						depot.getYCoordinate());
				if (distanceCost < customer.getStartTw()) { // I have to wait the opening time
					customersCost.add((double) customer.getStartTw());
				} else if (distanceCost < customer.getEndTw()) {
					// I need more time to arrive compared to the opening time but I can arrive
					// before the ending time
					customersCost.add(distanceCost);
				} else {
					customersCost.add(Double.POSITIVE_INFINITY);
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
					double distanceCost = customer.getDistance(customerToAdd.getXCoordinate(),
							customerToAdd.getYCoordinate());
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
			route.calculateCost(route.getAssignedVehicle().getCapacity());

			routes[vehicleNumber - 1] = route;
			vehicleNumber++;

			// if there aren't other customers than exit
			if (customers.size() == 0) {
				stop = Boolean.TRUE;
			}
		}

		/*
		 * I delete all the routes that have customers less than a param, given by the number of
		 * routes created
		 */
		Route[] routes = this.getRoutes();
		int param = (int) (this.getRoutes().length / 10);
		List<Customer> toAssign = new ArrayList<>();
		List<Route> toDelete = new ArrayList<>();
		int tot = 0;
		for (int i = 0; i < routes.length; i++) {
			Route route = routes[i];
			customers = route.getCustomers();
			if (customers.size() <= param) {
				for (Customer customer : customers) {
					toAssign.add(customer);
				}
				toDelete.add(route);
				tot++;
			}
			if (tot == param) {
				break;
			}
		}

		if (toDelete.size() > 0) {
			routes = removeRoutes(routes, toDelete);

			/*
			 * All the customers in the list as to be reassigned to the other routes
			 */
			for (int i = 0; i < routes.length; i++) {
				customers = routes[i].getCustomers();
				Cost cost = routes[i].getCost();
				Vehicle vehicle = routes[i].getAssignedVehicle();
				if (cost.getLoad() < vehicle.getCapacity()) {
					boolean cycle = true;
					int index = 0;
					while (cycle && index < toAssign.size()) {
						if (cost.getLoad() + toAssign.get(index).getLoad() < vehicle.getCapacity()) {
							customers.add(toAssign.get(index));
							cost.setLoad(cost.getLoad() + toAssign.get(index).getLoad());
							cycle = false;
							toAssign.remove(index);
						} else {
							index++;
						}
					}
				}
			}
		}
		this.setRoutes(routes);
	} // end function

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

	/**
	 * This function is used to clone a Solution.
	 * 
	 * @return a MySolution object
	 */
	public Object clone() {
		Solution solution = (Solution) super.clone();
		MySolution clonedSolution = (MySolution) solution;

		clonedSolution.instance = instance;
		clonedSolution.maxVehicleNumber = instance.getVehiclesNr();
		clonedSolution.maxVehicleCapacity = instance.getVehicleCapacity();
		clonedSolution.customersNumber = instance.getCustomersNr();
		clonedSolution.distances = instance.getDistances();
		clonedSolution.depot = instance.getDepot();
		clonedSolution.routes = new Route[this.maxVehicleNumber];
		clonedSolution.cost = new Cost();

		int i = 0;

		clonedSolution.setCost(new Cost(this.cost));
		clonedSolution.alpha = this.alpha;
		clonedSolution.beta = this.beta;
		clonedSolution.gamma = this.gamma;

		for (Route route : this.getRoutes()) {
			clonedSolution.routes[i] = new Route();
			// clone route information
			clonedSolution.routes[i].setIndex(route.getIndex());
			clonedSolution.routes[i].setDepot(clonedSolution.depot);
			clonedSolution.routes[i].setAssignedVehicle(route.getAssignedVehicle());
			clonedSolution.routes[i].setCost(new Cost(route.getCost()));

			List<Customer> customers = route.getCustomers();
			for (Customer customer : customers) {
				clonedSolution.routes[i].addCustomer(customer.clone(), -1);
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
		List<Route> list = new ArrayList<>();
		for (Route route : routes) {
			if (route == null) {
				continue;
			}
			list.add(route);
		}
		this.routes = list.toArray(new Route[list.size()]);
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

}
