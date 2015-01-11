/**
 * 
 */
package com.tabusearch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.coinor.opents.*;

import com.tabusearch.MySolution;
import com.tabusearch.MySearchProgram;
import com.vrptw.Cost;
import com.vrptw.Customer;
import com.vrptw.Instance;
import com.vrptw.Route;
import com.vrptw.Vehicle;

/**
 * This class implements the TabuSearchListener for the execution of the Tabu Search implementation
 */
@SuppressWarnings("serial")
public class MySearchProgram implements TabuSearchListener {
	public TabuSearch	tabuSearch;
	private MySolution	solution;
	private MySolution	bestSolution;

	private Instance	instance;
	private Route[]		feasibleRoutes;				// stores the routes of the feasible
														// solution
	private Cost		feasibleCost;					// stores the total cost of the feasible
														// solution
	private Route[]		bestRoutes;					// stores the routes of the best solution
	private Cost		bestCost;						// stores the total cost of the best
														// solution
	private Route[]		currentRoutes;					// stores the routes of the current solution
	private Cost		currentCost;					// stores the total cost of the current
														// solution

	/**
	 * Considered other parameters that can be used
	 */
	public MySearchProgram(Instance instance, Solution initialSol, MoveManager moveManager,
			ObjectiveFunction objFunc, TabuList tabuList, boolean minmax, PrintStream outPrintStream) {
		tabuSearch = new SingleThreadedTabuSearch(initialSol, moveManager, objFunc, tabuList,
				new BestEverAspirationCriteria(), minmax);

		this.instance = instance;
		tabuSearch.addTabuSearchListener(this);
		solution = (MySolution) initialSol;
		if (solution.isFeasible()) {
			bestSolution = (MySolution) solution.clone();
		} else {
			bestSolution = new MySolution(instance);
			bestSolution.getCost().setTotal(Double.POSITIVE_INFINITY);
		}
	}

	/*
	 * When Tabu Search starts initialize best cost - best routes and feasible cost - feasible
	 * routes
	 */
	@Override
	public void tabuSearchStarted(TabuSearchEvent e) {
		// TODO
		
		System.out.println("Iteration done: " + this.tabuSearch.getIterationsCompleted());

		solution = ((MySolution) tabuSearch.getCurrentSolution());
		// initialize the feasible and best cost with the initial solution objective value
		double[] objectiveValue = solution.getObjectiveValue();
		if (objectiveValue == null) {
			System.err.println("ObjectiveValue equals to null into tabuSearchStarted");
			System.exit(0);
		}
		bestCost = getCostFromObjective(objectiveValue);
		feasibleCost = bestCost;
		if (!feasibleCost.checkFeasible()) {
			feasibleCost.setTotal(Double.POSITIVE_INFINITY);
		}
		feasibleRoutes = cloneRoutes(solution.getRoutes());
		bestRoutes = feasibleRoutes;
	}

	/*
	 * When Tabu Search stops, set the solution equals to the best one found, and check if has a
	 * feasible cost or not
	 */
	@Override
	public void tabuSearchStopped(TabuSearchEvent e) {
		// TODO
		
		if (!bestSolution.isFeasible()) {
			feasibleCost.setTotal(Double.POSITIVE_INFINITY);
		} else {
			feasibleCost.setTotal(bestSolution.getCost().getTotal());
		}
		feasibleRoutes = cloneRoutes(bestSolution.getRoutes());
	}

	/*
	 * When a new best solution is found, we have to save it
	 */
	@Override
	public void newBestSolutionFound(TabuSearchEvent e) {
		// TODO
		
		// this way we store the actual best solution
		if (solution.isFeasible()
				&& solution.getCost().getTotal() < bestSolution.getCost().getTotal()) {
			bestSolution = (MySolution) solution.clone();
		}
	}

	/**
	 * When a new current solution is triggered we need to see if a new better feasible solution is
	 * found
	 */
	@Override
	public void newCurrentSolutionFound(TabuSearchEvent event) {
		// TODO
		
		solution = ((MySolution) tabuSearch.getCurrentSolution());
	}

	/*
	 * Called when the event UnimprovingMoveMade is fired
	 */
	@Override
	public void unimprovingMoveMade(TabuSearchEvent e) {
		// TODO
	}

	/*
	 * Called when the event ImprovingMoveMade is fired
	 */
	@Override
	public void improvingMoveMade(TabuSearchEvent e) {
		// TODO
	}

	/*
	 * Called when the event NoChangeInValueMoveMade is fired
	 */
	@Override
	public void noChangeInValueMoveMade(TabuSearchEvent e) {
		// TODO
	}

	/**
	 * Clone the routes passed as a parameter
	 * 
	 * @param routes
	 * @return
	 */
	private Route[] cloneRoutes(Route[] routes) {
		Route[] clones = new Route[routes.length];
		for (int i = 0; i < routes.length; i++) {
			Route clone = new Route(routes[i]);
			clones[i] = clone;
		}
		return clones;
	}

	/**
	 * I don't know why he prefer to use this method
	 * 
	 * @param objectiveValue
	 * @return
	 */
	private Cost getCostFromObjective(double[] objectiveValue) {
		Cost cost = new Cost();
		cost.setTotal(objectiveValue[1]);
		cost.setTravelTime(objectiveValue[2]);
		cost.setLoadViol(objectiveValue[3]);
		cost.setDurationViol(objectiveValue[4]);
		cost.setTwViol(objectiveValue[5]);

		return cost;
	}

	/**
	 * @return the iterationsDone
	 */
	public int getIterationsDone() {
		return this.tabuSearch.getIterationsCompleted();
	}

	/**
	 * @return the solution
	 */
	public MySolution getSolution() {
		return solution;
	}

	/**
	 * @param solution
	 *            the solution to set
	 */
	public void setSolution(MySolution solution) {
		this.solution = solution;
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
	 * @return the feasibleRoutes
	 */
	public Route[] getFeasibleRoutes() {
		return feasibleRoutes;
	}

	/**
	 * @param feasibleRoutes
	 *            the feasibleRoutes to set
	 */
	public void setFeasibleRoutes(Route[] feasibleRoutes) {
		this.feasibleRoutes = feasibleRoutes;
	}

	/**
	 * @return the feasibleCost
	 */
	public Cost getFeasibleCost() {
		return feasibleCost;
	}

	/**
	 * @param feasibleCost
	 *            the feasibleCost to set
	 */
	public void setFeasibleCost(Cost feasibleCost) {
		this.feasibleCost = feasibleCost;
	}

	/**
	 * @return the bestRoutes
	 */
	public Route[] getBestRoutes() {
		return bestRoutes;
	}

	/**
	 * @param bestRoutes
	 *            the bestRoutes to set
	 */
	public void setBestRoutes(Route[] bestRoutes) {
		this.bestRoutes = bestRoutes;
	}

	/**
	 * @return the bestCost
	 */
	public Cost getBestCost() {
		return bestCost;
	}

	/**
	 * @param bestCost
	 *            the bestCost to set
	 */
	public void setBestCost(Cost bestCost) {
		this.bestCost = bestCost;
	}

	/**
	 * @return the currentRoutes
	 */
	public Route[] getCurrentRoutes() {
		return currentRoutes;
	}

	/**
	 * @param currentRoutes
	 *            the currentRoutes to set
	 */
	public void setCurrentRoutes(Route[] currentRoutes) {
		this.currentRoutes = currentRoutes;
	}

	/**
	 * @return the currentCost
	 */
	public Cost getCurrentCost() {
		return currentCost;
	}

	/**
	 * @param currentCost
	 *            the currentCost to set
	 */
	public void setCurrentCost(Cost currentCost) {
		this.currentCost = currentCost;
	}

	/**
	 * @return the bestSolution
	 */
	public MySolution getBestSolution() {
		return bestSolution;
	}

	/**
	 * @param bestSolution
	 *            the bestSolution to set
	 */
	public void setBestSolution(MySolution bestSolution) {
		this.bestSolution = bestSolution;
	}

}
