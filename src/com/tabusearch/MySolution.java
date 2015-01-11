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
	private double		routeLimitFactor	= 1.5;	// used to limit the number of Customers per
													// route

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
		// TODO
	}

	/**
	 * Check if the solution is feasible or not
	 * 
	 * @return
	 */
	public boolean isFeasible() {
		for (Route route : this.getRoutes()) {
			if (!route.getCost().checkFeasible()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Calculate the total cost of the solution, based from the cost of each route in it
	 */
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
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha
	 *            the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the beta
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * @param beta
	 *            the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	/**
	 * @return the gamma
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * @param gamma
	 *            the gamma to set
	 */
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

}
