package com.vrptw;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Instance class holds all the information about the problem, customers, depots, vehicles. It
 * offers functions to grab all the data from a file print it formated and all the function needed
 * for the initial solution.
 */
public class Instance {
	private int					vehiclesNr;
	private int					customersNr;
	private int					depotsNr	= 1;
	private int					daysNr		= 1;
	private ArrayList<Customer>	customers	= new ArrayList<>();	// vector of customers;
	private Depot				depot;
	private double[]			durations;
	private double[]			capacities;
	private double[][]			distances;
	private Route[]				routes;
	private Random				random		= new Random();
	private Parameters			parameters;
	
	private double		alpha				= 1;
	private double		beta				= 1;
	private double		gamma				= 1;

	public Instance(Parameters parameters) {
		this.setParameters(parameters);
		// set the random seed if passed as parameter
		if (parameters.getRandomSeed() != -1)
			random.setSeed(parameters.getRandomSeed());
	}

	/**
	 * @return The time necessary to travel from node 1 to node 2
	 */
	public double getTravelTime(int node1, int node2) {
		return this.distances[node1][node2];
	}

	/**
	 * Read from file the problem data: D and Q, customers data and depots data. After the variables
	 * are populated calculates the distances and assign customers to depot.
	 * 
	 * @param filename - The name in the input directory from which to read the data.
	 */
	public void populateFromHombergFile(String filename) {
		try {
			Scanner in = new Scanner(new FileReader(parameters.getCurrDir() + "/input/" + filename));

			in.nextLine(); // skip filename
			in.nextLine(); // skip empty line
			in.nextLine(); // skip vehicle line
			in.nextLine();
			vehiclesNr = in.nextInt();

			// read D and Q
			durations = new double[daysNr];
			capacities = new double[daysNr];
			durations[0] = Double.MAX_VALUE;
			capacities[0] = in.nextInt();

			in.nextLine();
			in.nextLine();
			in.nextLine();
			in.nextLine();
			in.nextLine();

			// read depots data
			Depot depot = new Depot();
			depot.setNumber(in.nextInt());
			depot.setXCoordinate(in.nextDouble());
			depot.setYCoordinate(in.nextDouble());
			in.nextDouble();
			depot.setStartTw(in.nextInt());
			depot.setEndTw(in.nextInt());
			in.nextDouble();

			this.depot = depot;

			// read customers data
			customersNr = 0;
			while (in.hasNextInt()) {
				Customer customer = new Customer();
				customer.setNumber(in.nextInt() - 1);
				customer.setXCoordinate(in.nextDouble());
				customer.setYCoordinate(in.nextDouble());
				customer.setLoad(in.nextDouble());
				customer.setStartTw(in.nextInt());
				customer.setEndTw(in.nextInt());
				customer.setServiceDuration(in.nextDouble());

				// add customer to customers list
				customers.add(customer);
				customersNr++;
			}// end for customers
			in.close();

			depot.setNumber(customersNr);

			if (parameters.getTabuTenure() == -1) {
				parameters.setTabuTenure((int) (Math.sqrt(customersNr)));
			}

			calculateDistances();
		} catch (FileNotFoundException e) {
			// File not found
			System.out.println("File not found!");
			System.exit(-1);
		}
	}

	/**
	 * 
	 */
	public void initializeRoutes() {
		// TODO
	}

	/**
	 * Calculate the symmetric euclidean matrix of costs
	 */
	public void calculateDistances() {

		distances = new double[customersNr + depotsNr][customersNr + depotsNr];

		for (int i = 0; i < customersNr + depotsNr - 1; ++i) {
			for (int j = i + 1; j < customersNr + depotsNr; ++j) {
				// distance between two customers
				if (i < customersNr && j < customersNr) {
					distances[i][j] = Math.sqrt(Math.pow(customers.get(i).getXCoordinate()
							- customers.get(j).getXCoordinate(), 2)
							+ Math.pow(customers.get(i).getYCoordinate()
									- customers.get(j).getYCoordinate(), 2));
				} // distance of a customer from the depot
				else if (i < customersNr && j >= customersNr) {
					distances[i][j] = Math.sqrt(Math.pow(
							customers.get(i).getXCoordinate() - depot.getXCoordinate(), 2)
							+ Math.pow(customers.get(i).getYCoordinate() - depot.getYCoordinate(),
									2));
				}

				distances[i][j] = Math.floor(distances[i][j] * 10) / 10;
				distances[j][i] = distances[i][j];
			}
		}
	}

	/**
	 * @return the vehiclesNr
	 */
	public int getVehiclesNr() {
		return vehiclesNr;
	}

	/**
	 * @param vehiclesNr
	 *            the vehiclesNr to set
	 */
	public void setVehiclesNr(int vehiclesNr) {
		this.vehiclesNr = vehiclesNr;
	}

	/**
	 * @return the customersNr
	 */
	public int getCustomersNr() {
		return customersNr;
	}

	/**
	 * @param customersNr
	 *            the customersNr to set
	 */
	public void setCustomersNr(int customersNr) {
		this.customersNr = customersNr;
	}

	/**
	 * @return the depotsNr
	 */

	public int getDepotsNr() {
		return depotsNr;
	}

	/**
	 * @param depotsNr
	 *            the depotsNr to set
	 */
	public void setDepotsNr(int depotsNr) {
		this.depotsNr = depotsNr;
	}

	/**
	 * @return the daysNr
	 */

	public int getDaysNr() {
		return daysNr;
	}

	/**
	 * @param daysNr
	 *            the daysNr to set
	 */
	public void setDaysNr(int daysNr) {
		this.daysNr = daysNr;
	}

	/**
	 * @return the customers
	 */
	public ArrayList<Customer> getCustomers() {
		return customers;
	}

	/**
	 * @param customers
	 *            the customers to set
	 */
	public void setCustomers(ArrayList<Customer> customers) {
		this.customers = customers;
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
	 * @return the durations
	 */
	public double[] getDurations() {
		return durations;
	}

	/**
	 * @param durations
	 *            the durations to set
	 */
	public void setDurations(double[] durations) {
		this.durations = durations;
	}

	/**
	 * @return the capacities
	 */
	public double[] getCapacities() {
		return capacities;
	}

	/**
	 * @param capacities
	 *            the capacities to set
	 */
	public void setCapacities(double[] capacities) {
		this.capacities = capacities;
	}

	public double getVehicleCapacity() {
		return this.capacities[0];
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
	 * @return the routes
	 */
	public Route[] getRoutes() {
		return routes;
	}

	/**
	 * @param routes
	 *            the routes to set
	 */
	public void setRoutes(Route[] routes) {
		this.routes = routes;
	}

	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random
	 *            the random to set
	 */
	public void setRandom(Random random) {
		this.random = random;
	}

	/**
	 * @return the parameters
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Get the capacity for the supplied day number.
	 * 
	 * @param index
	 *            - This is the day number.
	 * @return The maximum capacity a vehicle can carry for the specified day.
	 */
	public double getCapacity(int index) {
		return capacities[index];
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
}