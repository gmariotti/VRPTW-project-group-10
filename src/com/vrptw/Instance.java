package com.vrptw;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Instance class holds all the information about the problem, customers,
 * depots, vehicles. It offers functions to grab all the data from a file print
 * it formated and all the function needed for the initial solution.
 */
public class Instance {
	private int vehiclesNr;
	private int customersNr;
	private int depotsNr;
	private int daysNr = 1;;
	private ArrayList<Customer> customers = new ArrayList<>(); // vector of
																// customers;
	private Depot depot;
	private double[][] durations;
	private double[][] capacities;
	private double[][] distances;
	private Route[][] routes;
	private Random random = new Random();
	private Parameters parameters;

	double maxX = Double.NEGATIVE_INFINITY;
	double maxY = Double.NEGATIVE_INFINITY;
	double minX = Double.POSITIVE_INFINITY;
	double minY = Double.POSITIVE_INFINITY;

	public Instance(Parameters parameters) {
		this.setParameters(parameters);
		// set the random seet if passed as parameter
		if (parameters.getRandomSeed() != -1)
			random.setSeed(parameters.getRandomSeed());
	}

	/**
	 * Returns the time necessary to travel from node 1 to node 2
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	public double getTravelTime(int v1, int v2) {
		return this.distances[v1][v2];
	}

	/**
	 * Read from file the problem data: D and Q, customers data and depots data.
	 * After the variables are populated calculates the distances, assign
	 * customers to depot and calculates angles
	 * 
	 * @param filename
	 */
	public void populateFromHombergFile(String filename) {
		try {
			// TODO --> check if it's correct

			Scanner in = new Scanner(new FileReader(parameters.getCurrDir()
					+ "/input/" + filename));
			depotsNr = 1;

			// skip unusefull lines
			in.nextLine(); // skip filename
			in.nextLine(); // skip empty line
			in.nextLine(); // skip vehicle line
			in.nextLine();
			vehiclesNr = in.nextInt();

			// read D and Q
			durations = new double[depotsNr][daysNr];
			capacities = new double[depotsNr][daysNr];
			durations[0][0] = Double.MAX_VALUE;
			capacities[0][0] = in.nextInt();

			// skip unusefull lines
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
			// this data is used for DrawPanel
			maxX = depot.getXCoordinate() > maxX ? depot.getXCoordinate()
					: maxX;
			maxY = depot.getYCoordinate() > maxY ? depot.getYCoordinate()
					: maxY;
			minX = depot.getXCoordinate() < minX ? depot.getXCoordinate()
					: minX;
			minY = depot.getYCoordinate() < minY ? depot.getYCoordinate()
					: minY;
			depots.add(depot);

			// read customers data
			customersNr = 0;
			while (in.hasNextInt()) {
				Customer customer = new Customer();
				customer.setNumber(in.nextInt() - 1);
				customer.setXCoordinate(in.nextDouble());
				customer.setYCoordinate(in.nextDouble());
				customer.setStartTw(in.nextInt());
				customer.setEndTw(in.nextInt());
				customer.setServiceDuration(in.nextDouble());

				// this data is used for DrawPanel
				maxX = customer.getXCoordinate() > maxX ? customer
						.getXCoordinate() : maxX;
				maxY = customer.getYCoordinate() > maxY ? customer
						.getYCoordinate() : maxY;
				minX = customer.getXCoordinate() < minX ? customer
						.getXCoordinate() : minX;
				minY = customer.getYCoordinate() < minY ? customer
						.getYCoordinate() : minY;

				// add customer to customers list
				customers.add(customer);
				customersNr++;
			}// end for customers
			in.close();

			depot.setNumber(customersNr);

			/*
			 * if(parameters.getTabuTenure() == -1)
			 * parameters.setTabuTenure((int)(Math.sqrt(getCustomersNr())));
			 */

			calculateDistances();
			assignCustomersToDepots();
		} catch (FileNotFoundException e) {
			// File not found
			System.out.println("File not found!");
			System.exit(-1);
		}
	}

	/**
	 * Assign to each customer the closed depot based on distances
	 */
	public void assignCustomersToDepots() {
		// TODO
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
		// TODO --> check if it's correct
		
		distances = new double[customersNr + depotsNr][customersNr + depotsNr];
		for (int i = 0; i < customersNr + depotsNr - 1; ++i)
			for (int j = i + 1; j < customersNr + depotsNr; ++j) {
				// case both customers
				if (i < customersNr && j < customersNr) {
					distances[i][j] = Math.sqrt(Math.pow(customers.get(i)
							.getXCoordinate()
							- customers.get(j).getXCoordinate(), 2)
							+ Math.pow(customers.get(i).getYCoordinate()
									- customers.get(j).getYCoordinate(), 2));
					distances[j][i] = distances[i][j];

					// case customer and depot
				} else if (i < customersNr && j >= customersNr) {
					int d = j - customersNr; // depot number in the instance
												// list
					distances[i][j] = Math.sqrt(Math.pow(customers.get(i)
							.getXCoordinate() - depots.get(d).getXCoordinate(),
							2)
							+ Math.pow(customers.get(i).getYCoordinate()
									- depots.get(d).getYCoordinate(), 2));
					distances[j][i] = distances[i][j];

					// case both depots
				} else if (i >= customersNr && j >= customersNr) {
					int d1 = i - customersNr; // first depot number in the
												// instance list
					int d2 = j - customersNr; // second depot number in the
												// instance list
					distances[i][j] = Math.sqrt(Math.pow(
							depots.get(d1).getXCoordinate()
									- depots.get(d2).getXCoordinate(), 2)
							+ Math.pow(depots.get(d1).getYCoordinate()
									- depots.get(d2).getYCoordinate(), 2));
					distances[j][i] = distances[i][j];
				}
			}
	}

}
