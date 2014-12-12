/**
 * 
 */
package com.vrptw;

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
		MySolution initialSol = new MySolution();
		MyObjectiveFunction objFunc;
		MyMoveManager moveManager;
		TabuList tabuList;
		Parameters parameters = new Parameters(); // holds all the parameters
													// passed from the input
													// line
		Instance instance;

		try {
			// Initialize our objects
			parameters.updateParameters(args);
			instance = new Instance(parameters);
			instance.populateFromHombergFile(parameters.getInputFileName());
			initialSol 		= new MySolution();
			objFunc 		= new MyObjectiveFunction(instance);
	        moveManager 	= new MyMoveManager(instance);

			initialSol.generateInitialSolution(instance);	// modified here (Emmanuel)
			objFunc = new MyObjectiveFunction(instance);
	        moveManager = new MyMoveManager(instance);
	        moveManager.setMovesType(parameters.getMovesType());

			// Create Tabu Search object
			

			// Start solving
			// ...

			// Show solution
			// ...
	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
