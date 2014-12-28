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
	private MySolution 	previousSolution;
	private Instance	instance;
	private Route[]		feasibleRoutes; // stores the routes of the feasible solution
	private Cost		feasibleCost;	// stores the total cost of the feasible solution
	private Route[]		bestRoutes;	// stores the routes of the best solution
	private Cost		bestCost;		// stores the total cost of the best solution
	private Route[]		currentRoutes;	// stores the routes of the current solution
	private Cost		currentCost;	// stores the total cost of the current solution

	public int			count		= 0;
	private boolean 	skip 		= false;

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
	}

	public void correction() {
		solution = (MySolution) tabuSearch.getCurrentSolution();
		Route[] routes = solution.getRoutes();
		int param = (int) routes.length / 10 + 1;

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

		previousSolution = solution;
		
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
		/*
		 * this.count++; if (this.count == 20) { MoveManager moveManager =
		 * this.tabuSearch.getMoveManager(); solution = (MySolution)
		 * this.tabuSearch.getCurrentSolution(); int routeLength = solution.getRoutes().length; for
		 * (int i = 0; i < routeLength; i++) { Move[] moves = moveManager.getAllMoves(solution); int
		 * random = new Random().nextInt(moves.length); moves[random].operateOn(solution); }
		 * this.count = 0; }
		 */
		System.out.println("Unimproving Move made in iterations " + iterationsDone);
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
		count++;
		if (count == 1) {
			Granular.setGranularity((MySolution) this.tabuSearch.getBestSolution());
			correction();

//		System.out.println("No change in the overall value made in iteration " + iterationsDone);
//		solution = (MySolution) this.tabuSearch.getCurrentSolution().clone();
//		solution.print();
		
		MySolution solution = (MySolution) this.tabuSearch.getCurrentSolution();
		MySolution splitSolution = null;
		boolean noSplit = solution.getRoutes().length > instance.getVehiclesNr() - 1;
		MySolution combineSolution = null;
		boolean noCombine = solution.getRoutes().length <= 1;
		
		if(!noCombine)
		{
			combineSolution = performCombine();
		}
		
		if(!noSplit)
		{
			splitSolution = performSplit();
		}
		
		if(skip == false)
		{
			if(noCombine)		// the combine move cannot be implemented
			{
				this.tabuSearch.setCurrentSolution(splitSolution);
			}
			else if(noSplit)	// the split move cannot be implemented
			{
				this.tabuSearch.setCurrentSolution(combineSolution);
			}
			else
			{
				if(splitSolution.getCost().getTotal() < combineSolution.getCost().getTotal())
				{
					this.tabuSearch.setCurrentSolution(splitSolution);
				}
				else
				{
					this.tabuSearch.setCurrentSolution(combineSolution);
				}
			}
			
			skip = true;
		
			System.out.println("The current solution seen below:");
			solution.print();
			System.out.println("was changed to:");
			((MySolution) this.tabuSearch.getCurrentSolution()).print();
		}
		else
		{
			skip = false;
		}

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
	

	private MySolution performCombine() {
		MySolution sol = (MySolution) solution.clone();
		Route[] routes = sol.getRoutes();
		int size = routes.length;
		int[] indexes = new int[size];
		int[] routeCustomerNumber = new int[size];
		double[] routeTotalCost = new double[size];
		Route newRoute;
		List<Customer> customers;
		
		// initialization of data
		for(int i = 0; i < size; i++)
		{
			indexes[i] = routes[i].getIndex();
			routeCustomerNumber[i] = routes[i].getCustomers().size();
			routeTotalCost[i] = routes[i].getCost().getTotal();
		}
		
		/*
		 *  Use a bubble sort to sort first by the number of customers per route,
		 * in ascending order, and then, in case of ties, by the total cost of the route,
		 * in descending order. 
		 */
		for(int i = 0; i < size; i++)
		{
			for(int j = 1; j < size - i; j++)
			{
				boolean condition = false;
				
				if(routeCustomerNumber[j - 1] > routeCustomerNumber[j])
				{
					condition = true;
				}
				else if(routeCustomerNumber[j - 1] == routeCustomerNumber[j])
				{
					if(routeTotalCost[j - 1] < routeTotalCost[j])
					{
						condition = true;
					}
				}
				
				if(condition)
				{
					int tempInt = indexes[j - 1];
					indexes[j - 1] = indexes[j];
					indexes[j] = tempInt;
					tempInt = routeCustomerNumber[j - 1];
					routeCustomerNumber[j - 1] = routeCustomerNumber[j];
					routeCustomerNumber[j] = tempInt;
					double tempDouble = routeTotalCost[j - 1];
					routeTotalCost[j - 1] = routeTotalCost[j];
					routeTotalCost[j] = tempDouble;
				}
			}
		}
		
		// get the lowest index among the chosen routes
		int index = (indexes[0] < indexes[1] ? indexes[0] : indexes[1]);
		
		newRoute = routes[index].copyRouteInformation();
		customers = new ArrayList<>(routeCustomerNumber[0] + routeCustomerNumber[1]);
		
		customers.addAll(routes[indexes[0]].getCustomers());
		customers.addAll(routes[indexes[1]].getCustomers());
		orderCustomersByEndTw(customers);
		
		newRoute.setCustomers(customers);
		newRoute.calculateCost(instance.getVehicleCapacity(), instance.getAlpha(), instance.getBeta(), instance.getGamma());
		
		// cost change made here
		Cost varCost = new Cost(newRoute.getCost());
		varCost.subtract(routes[indexes[0]].getCost());
		varCost.subtract(routes[indexes[1]].getCost());
		
		sol.setRoutes(newRoute, index);
		Cost newCost = sol.getCost();
		newCost.add(varCost);
		newCost.calculateTotal(instance.getAlpha(), instance.getBeta(), instance.getGamma());
		sol.setCost(newCost);
		
		index = (indexes[0] < indexes[1] ? indexes[1] : indexes[0]);
		
		/*
		 *   Correct the routes in the solution by removing the route in the highest
		 *  index and substitute it with the following routes.
		 */
		for(int i = index; i < size - 1; i++)
		{
			Route route = sol.getRoutes(i + 1);
			Vehicle vehicle = route.getAssignedVehicle();
			vehicle.setVehicleNr(i + 1);
			route.setIndex(i);
			route.setAssignedVehicle(vehicle);
			sol.setRoutes(route, i);
		}
		
		// remove the last route as it is no longer needed
		sol.removeRoute(size - 1);
		
		return sol;
	}

	private void orderCustomersByEndTw(List<Customer> customers) {
		Customer temp;
		
		for(int i = 0; i < customers.size(); i++)
		{
			for(int j = 1; j < customers.size(); j++)
			{
				if(customers.get(j-1).getEndTw() < customers.get(j).getEndTw())
				{
					temp = customers.get(j - 1);
					customers.set(j-1, customers.get(j));
					customers.set(j, temp);
				}
			}
		}
		
	}

	private MySolution performSplit() {
		MySolution sol = (MySolution) solution.clone();
		Route[] routes = sol.getRoutes();
		int size = routes.length; 
		int[] indexes = new int[size];
		int maxIndex = -1;
		double[] costPerCustomer = new double[size];
		double maxCostPerCustomer = Double.NEGATIVE_INFINITY;
		
		/*
		 *   The factor is used to make it more likely that this method give higher
		 *  chance to longer routes to be picked. However, if the value is kept
		 *  in an appropriately small range even small route that are not efficient
		 *  in terms of cost will be picked.
		 */
		double factor = 0.1;
		
		// initialize the data and find the max total cost
		for(int i = 0; i < size; i++)
		{
			int customerNumber = routes[i].getCustomers().size();
			indexes[i] = i;
			// TODO -> This might be better if travelTime is taken into consideration
			
			if(customerNumber > 1) {
				costPerCustomer[i] = (routes[i].getCost().getTotal()/customerNumber) * (1 + factor * customerNumber);
			}
			else {
				// a route of a single customer can't be split
				costPerCustomer[i] = Double.NEGATIVE_INFINITY;
			}
			
			if(maxCostPerCustomer < costPerCustomer[i])
			{
				maxCostPerCustomer = costPerCustomer[i];
				maxIndex = i;
			}
		}
		
		Route routeToBeSplit = sol.getRoutes(maxIndex);
		List<Customer> customers = routeToBeSplit.getCustomers();
		
		// order customers by end time windo
		orderCustomersByEndTw(customers);
		
		/*
		 *   The first new route will simply replace the route to be split,
		 *  so we assign that route's unchanging information to the first
		 *  route to be added.
		 */
		Route newFirstRoute = routeToBeSplit.copyRouteInformation();
		List<Customer> firstRouteCustomers = new ArrayList<>();
		
		/*
		 *   The second new route will be a completely new route added at
		 *  the end of the routes of the solution so we're creating all
		 *  it's data excluding the cost and customers
		 */
		Route newSecondRoute = new Route();
		List<Customer> secondRouteCustomers = new ArrayList<>();
		newSecondRoute.setAssignedVehicle(new Vehicle(size + 1, instance.getVehicleCapacity(), instance.getDurations()[0]));
		newSecondRoute.setIndex(size);
		newSecondRoute.setDepot(instance.getDepot());
		
		/*
		 *  Approach #1
		 *  
		 *  	We add alternatively the customers first to the customers of the first
		 *  route and then to those of the second route. This will probably lead to less
		 *  or no Time Window constraint violation. However, some very good solutions, in
		 *  which compatible customers, might be avoided because of this.
		 *  	
		 *  Note: By compatible customers I mean two consecutive customers that don't
		 *  cause time window constraint violation.  
		 */
		
		for(int i = 0; i < customers.size(); i++)
		{
			if(i % 2 == 0){
				firstRouteCustomers.add(new Customer(customers.get(i)));
			}
			else {
				secondRouteCustomers.add(new Customer(customers.get(i)));
			}
		}
		
		/* 
		 *  Approach #2
		 * 
		 * 		In this case the index decides where the route will be split and the
		 * minimum cumulative cost (i.e the minimum cost coming from both the first and
		 * second new routes) is picked by remembering the index where it was found.
		 * This is a more complex method and has more or less the opposite qualities
		 * w.r.t. the Approach #1	
		 */
		
//		Cost minCost = new Cost();
//		minCost.setTotal(Double.POSITIVE_INFINITY);
//		int minIndex = -1;
//		
//		for(int i = 1; i < customers.size() - 1; i++)
//		{
//			for(int j = 0; j < i; j++)
//			{
//				firstRouteCustomers.add(new Customer(customers.get(j)));
//			}
//			for(int j = i; j < customers.size(); j++)
//			{
//				secondRouteCustomers.add(new Customer(customers.get(j)));
//			}
//			
//			Route firstRoute = routeToBeSplit.copyRouteInformation();
//			firstRoute.setCustomers(firstRouteCustomers);
//			firstRoute.calculateCost(instance.getVehicleCapacity(), instance.getAlpha(), instance.getBeta(), instance.getGamma());
//			Route secondRoute = routeToBeSplit.copyRouteInformation();
//			secondRoute.setCustomers(secondRouteCustomers);
//			secondRoute.calculateCost(instance.getVehicleCapacity(), instance.getAlpha(), instance.getBeta(), instance.getGamma());
//			
//			Cost cost = new Cost(firstRoute.getCost());
//			cost.add(secondRoute.getCost());
//			cost.calculateTotal(instance.getAlpha(), instance.getBeta(), instance.getGamma());
//			
//			if(minCost.getTotal() < cost.getTotal())
//			{
//				minCost = new Cost(cost);
//				minIndex = i;
//			}
//			
//			firstRouteCustomers.clear();
//			secondRouteCustomers.clear();
//		}
//		
//		// reconstruct the solution
//		
//		for(int i = 0; i < minIndex; i++)
//		{
//			firstRouteCustomers.add(new Customer(customers.get(i)));
//		}
//		for(int i = minIndex; i < customers.size(); i++)
//		{
//			secondRouteCustomers.add(new Customer(customers.get(i)));
//		}
		
		// the following is approach-independent
		
		newFirstRoute.setCustomers(firstRouteCustomers);
		newFirstRoute.calculateCost(instance.getVehicleCapacity(), instance.getAlpha(), instance.getBeta(), instance.getGamma());
		
		newSecondRoute.setCustomers(secondRouteCustomers);
		newSecondRoute.calculateCost(instance.getVehicleCapacity(), instance.getAlpha(), instance.getBeta(), instance.getGamma());
		
		// calculate incrementally the solution's cost
		Cost newCost = new Cost(sol.getCost());
		newCost.add(newFirstRoute.getCost());
		newCost.add(newSecondRoute.getCost());
		newCost.subtract(routeToBeSplit.getCost());
		newCost.calculateTotal(instance.getAlpha(), instance.getBeta(), instance.getGamma());
		sol.setCost(newCost);
		
		// change the route
		sol.setRoutes(newFirstRoute, maxIndex);
		sol.setRoutes(newSecondRoute, size);
		
		return sol;
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
