/**
 * 
 */
package com.tabusearch;

import java.io.PrintStream;

import org.coinor.opents.*;

import com.tabusearch.MySearchProgram;
import com.vrptw.Cost;
import com.vrptw.Instance;
import com.vrptw.Route;

/**
 * This class implements the TabuSearchListener for the execution of the Tabu Search implementation
 */
@SuppressWarnings("serial")
public class MySearchProgram implements TabuSearchListener {
	private static int	iterationsDone;
	public TabuSearch	tabuSearch;
	private MySolution	solution;
	private Instance	instance;
	private Route[]		feasibleRoutes; // stores the routes of the feasible solution
	private Cost		feasibleCost;	// stores the total cost of the feasible solution
	private Route[]		bestRoutes;	// stores the routes of the best solution
	private Cost		bestCost;		// stores the total cost of the best solution
	private Route[]		currentRoutes;	// stores the routes of the current solution
	private Cost		currentCost;	// stores the total cost of the current solution

	/**
	 * Considered other parameters that can be used
	 */
	public MySearchProgram(Instance instance, Solution initialSol, MoveManager moveManager, ObjectiveFunction objFunc, TabuList tabuList, boolean minmax, PrintStream outPrintStream) {
		tabuSearch = new SingleThreadedTabuSearch(initialSol, moveManager, objFunc, tabuList, new BestEverAspirationCriteria(), minmax );
		/*feasibleIndex = -1;
		bestIndex = 0;*/
		this.instance = instance;
		MySearchProgram.setIterationsDone(0);
		tabuSearch.addTabuSearchListener(this);
		//tabuSearch.addTabuSearchListener((MyTabuList)tabuList);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#tabuSearchStarted(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void tabuSearchStarted(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#tabuSearchStopped(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void tabuSearchStopped(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#newBestSolutionFound(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void newBestSolutionFound(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#newCurrentSolutionFound(org.coinor.opents.TabuSearchEvent
	 * )
	 */
	@Override
	public void newCurrentSolutionFound(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#unimprovingMoveMade(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void unimprovingMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#improvingMoveMade(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void improvingMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#noChangeInValueMoveMade(org.coinor.opents.TabuSearchEvent
	 * )
	 */
	@Override
	public void noChangeInValueMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the iterationsDone
	 */
	public static int getIterationsDone() {
		return iterationsDone;
	}

	/**
	 * @param iterationsDone
	 *            the iterationsDone to set
	 */
	public static void setIterationsDone(int iterationsDone) {
		MySearchProgram.iterationsDone = iterationsDone;
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

}
