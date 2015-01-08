/**
 * 
 */
package com.vrptw;

import java.io.FileWriter;
import java.io.PrintStream;

import org.coinor.opents.SimpleTabuList;

import com.tabusearch.Granular;
import com.tabusearch.MyMoveManager;
import com.tabusearch.MyObjectiveFunction;
import com.tabusearch.MySearchProgram;
import com.tabusearch.MySolution;

/**
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

			initialSol.setObjectiveValue(objFunc.evaluate(initialSol, null));

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
			Granular.setGranularity(initialSol);
			searchProgram.tabuSearch.setIterationsToGo(parameters.getIterations());
			searchProgram.tabuSearch.startSolving();

			duration.stop();

			// Show all routes, routes' vehicle and routes' customers
			MySolution sol = searchProgram.getBestSolution();
			sol.print();

			// Show solution on solution.csv
			int routesNr = 0;
			for (int i = 0; i < searchProgram.getFeasibleRoutes().length; ++i)
				if (searchProgram.getFeasibleRoutes()[i].getCustomers().size() > 0)
					routesNr++;
			// Print results
			String outSol = String.format("%s; %5.2f; %d; %4d\r\n", instance.getParameters()
					.getInputFileName(), searchProgram.getFeasibleCost().getTotal(), duration
					.getSeconds(), routesNr);
			System.out.println(outSol);
			FileWriter fw = new FileWriter(parameters.getOutputFileName(), true);
			fw.write(outSol);
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
