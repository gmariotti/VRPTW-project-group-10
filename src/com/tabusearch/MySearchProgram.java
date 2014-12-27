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
	private static int	iterationsDone;
	public TabuSearch	tabuSearch;
	private MySolution	solution;
	private MySolution	bestSolution;
	private Instance	instance;
	private Route[]		feasibleRoutes; // stores the routes of the feasible solution
	private Cost		feasibleCost;	// stores the total cost of the feasible solution
	private Route[]		bestRoutes;	// stores the routes of the best solution
	private Cost		bestCost;		// stores the total cost of the best solution
	private Route[]		currentRoutes;	// stores the routes of the current solution
	private Cost		currentCost;	// stores the total cost of the current solution

	public int			count	= 0;

	/**
	 * Considered other parameters that can be used
	 */
	public MySearchProgram(Instance instance, Solution initialSol, MoveManager moveManager,
			ObjectiveFunction objFunc, TabuList tabuList, boolean minmax, PrintStream outPrintStream) {
		tabuSearch = new SingleThreadedTabuSearch(initialSol, moveManager, objFunc, tabuList,
				new BestEverAspirationCriteria(), minmax);
		/*
		 * feasibleIndex = -1; bestIndex = 0;
		 */
		this.instance = instance;
		MySearchProgram.setIterationsDone(0);
		tabuSearch.addTabuSearchListener(this);
		solution = (MySolution) initialSol;
		bestSolution = new MySolution(instance);
		bestSolution.getCost().setTotal(Double.POSITIVE_INFINITY);
		// tabuSearch.addTabuSearchListener((MyTabuList)tabuList);
	}

	public void correction() {
		solution = (MySolution) tabuSearch.getCurrentSolution();
		Route[] routes = solution.getRoutes();
		int param = (int) routes.length / 10;

		/*
		 * I delete all the routes that have customers less than a param, given by the number of
		 * routes created
		 */
		List<Customer> toAssign = new ArrayList<>();
		List<Route> toDelete = new ArrayList<>();
		for (int i = 0; i < routes.length; i++) {
			Route route = routes[i];
			List<Customer> customers = route.getCustomers();
			if (customers.size() <= param) {
				for (Customer customer : customers) {
					toAssign.add(customer);
				}
				toDelete.add(route);
			}
		}

		if (toDelete.size() > 0) {

			routes = solution.removeRoutes(routes, toDelete);

			/*
			 * All the customers in the list as to be reassigned to the other routes
			 */
			for (int i = 0; i < routes.length; i++) {
				List<Customer> customers = routes[i].getCustomers();
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

		/*
		 * TEST
		 */
		int totCust = 0;
		for (Route route : routes) {
			totCust += route.getCustomers().size();
		}
		System.out.println("Tot customers after correction " + totCust);

		solution.setRoutes(routes);
		solution.calculateCost();
	}

	/*
	 * When Tabu Search starts initialize best cost - best routes and feasible cost - feasible
	 * routes
	 */
	@Override
	public void tabuSearchStarted(TabuSearchEvent e) {
		System.out.println("Iteration done: " + iterationsDone);
//		iterationsDone++;
		
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
		
	}

	/*
	 * When a new best solution is found, we have to save it
	 */
	@Override
	public void newBestSolutionFound(TabuSearchEvent e) {
		
		
		// this way we store the actual best solution
		if(solution.isFeasible() && solution.getCost().getTotal() < bestSolution.getCost().getTotal())
		{
			bestSolution = (MySolution) solution.clone();
			bestSolution.print();
		}

//		solution = (MySolution) tabuSearch.getBestSolution();
//		this.setBestRoutes(solution.getRoutes());
//		double[] objectiveValue = solution.getObjectiveValue();
//		if (objectiveValue == null) {
//			System.err.println("ObjectiveValue equals to null into newBestSolutionFound");
//			System.exit(0);
//		}
//		this.setBestCost(getCostFromObjective(objectiveValue));
//		solution = (MySolution) tabuSearch.getCurrentSolution();
	}

	/**
	 * When a new current solution is triggered we need to see if a new better feasible solution is
	 * found
	 */
	@Override
	public void newCurrentSolutionFound(TabuSearchEvent event) {
		iterationsDone++;
		
		solution = ((MySolution) tabuSearch.getCurrentSolution());
		double[] objectiveValue = solution.getObjectiveValue();
		if (objectiveValue == null) {
			System.err.println("ObjectiveValue equals to null into newCurrentSolutionFound");
			System.exit(0);
		}
		currentCost = getCostFromObjective(objectiveValue);

		// Check to see if a new feasible solution is found
		// Checking with the current solution admits new feasible solution
		// that are worst than the best solution
		if (currentCost.checkFeasible() && currentCost.getTotal() < feasibleCost.getTotal()) {
			feasibleCost = currentCost;
			feasibleRoutes = cloneRoutes(solution.getRoutes());
			// set the new best to the current one
			tabuSearch.setBestSolution(solution);
			System.out.println("It " + tabuSearch.getIterationsCompleted() + " - New solution "
					+ solution.getCost().getTotal());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#unimprovingMoveMade(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void unimprovingMoveMade(TabuSearchEvent e) {
		System.out.println("Unimproving Move made in iteration " + iterationsDone);
		/*
		 * this.count++; if (this.count == 20) { MoveManager moveManager =
		 * this.tabuSearch.getMoveManager(); solution = (MySolution)
		 * this.tabuSearch.getCurrentSolution(); int routeLength = solution.getRoutes().length; for
		 * (int i = 0; i < routeLength; i++) { Move[] moves = moveManager.getAllMoves(solution); int
		 * random = new Random().nextInt(moves.length); moves[random].operateOn(solution); }
		 * this.count = 0; }
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#improvingMoveMade(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void improvingMoveMade(TabuSearchEvent e) {
		System.out.println("Improving Move made in iteration " + iterationsDone);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.coinor.opents.TabuSearchListener#noChangeInValueMoveMade(org.coinor.opents.TabuSearchEvent
	 * )
	 */
	@Override
	public void noChangeInValueMoveMade(TabuSearchEvent e) {
		System.out.println("No change in the overall value made in iteration " + iterationsDone);
		solution = (MySolution) this.tabuSearch.getCurrentSolution().clone();
		solution.print();
		
		
		
//		count++;
//		switch (count) {
//		case 20:
//			Granular.setGranularity((MySolution) this.tabuSearch.getBestSolution());
//			MySolution sol = (MySolution) this.tabuSearch.getCurrentSolution();
//			instance.setGamma(0);
//			count++;
//			break;
//		case 40:
//			MySolution solution = (MySolution) this.tabuSearch.getCurrentSolution();
//			instance.setGamma(0.1);
//			this.tabuSearch.setBestSolution(bestSolution);
//			this.tabuSearch.getObjectiveFunction().evaluate(solution, null);
//			break;
//		}
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
