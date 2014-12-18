/**
 * 
 */
package com.vrptw;

import java.io.FileWriter;
import java.io.PrintStream;
import java.util.List;

import org.coinor.opents.SimpleTabuList;
import org.coinor.opents.TabuList;

import com.tabusearch.MyMoveManager;
import com.tabusearch.MyObjectiveFunction;
import com.tabusearch.MySearchProgram;
import com.tabusearch.MySolution;

/**
 * @author Guido Pio
 * 
 */
public class VRPTW {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MySearchProgram searchProgram;
		MySolution initialSol;
		MyObjectiveFunction objFunc;
		MyMoveManager moveManager;
		SimpleTabuList tabuList;
		int tenure = 7;
		Parameters parameters = new Parameters();
		Instance instance;
		Duration duration = new Duration(); // to see how much time does the program require
		PrintStream outPrintStream = null;

		try {
			// Initialize our objects
			parameters.updateParameters(args);

			duration.start();

			instance = new Instance(parameters);
			instance.populateFromHombergFile(parameters.getInputFileName());

			// Init memory for Tabu Search
			initialSol = new MySolution(instance);
			objFunc = new MyObjectiveFunction(instance);
			initialSol.generateInitialSolution();
			
			// temporary just to see if initialSol works
			initialSol.print();
			
			moveManager = new MyMoveManager(instance);
			moveManager.setMovesType(parameters.getMovesType());

			// Tabu list
			tabuList = new SimpleTabuList(tenure);

			// Create Tabu Search object
			searchProgram = new MySearchProgram(instance, initialSol, moveManager, objFunc,
					tabuList, false, outPrintStream);

			// Start solving
			searchProgram.tabuSearch.setIterationsToGo(parameters.getIterations());
			searchProgram.tabuSearch.startSolving();

			duration.stop();

			// Show all routes, routes' vehicle and routes' customers
			MySolution sol = searchProgram.getSolution();
			Route[] routes = sol.getRoutes();
			String solution = "";
			for (Route route : routes) {
				solution += "R#" + route.getIndex() + " V#"
						+ route.getAssignedVehicle().getVehicleNr() + " ";
				List<Customer> customers = route.getCustomers();
				for (Customer cust : customers) {
					solution += "C#" + cust.getNumber() + " ";
				}
				solution += "\n";
			}
			System.out.println(solution);
			FileWriter fw = new FileWriter(System.getProperty("user.dir")
					+ "/output/routeOfSolution.csv", true);
			fw.write(solution);
			fw.close();

			// Show solution on solution.csv

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
