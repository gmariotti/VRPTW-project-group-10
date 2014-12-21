package com.tabusearch;

import java.util.ArrayList;
import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import com.vrptw.*;

@SuppressWarnings("serial")
public class MyObjectiveFunction implements ObjectiveFunction {
	private static Instance	instance;
	private MySolution		currentSolution;
	private MyTwoExchangeMove currentMove;
	private double			penalizationFactor;

	public MyObjectiveFunction(Instance instance) {
		MyObjectiveFunction.instance = instance;
		penalizationFactor = 0.5 * Math.sqrt(instance.getVehiclesNr() * instance.getCustomersNr());
	}

	/**
	 * This function evaluates the impact that a move has on a solution. If a null Move is passed
	 * then a full evaluation of the cost of the solution is performed, otherwise the method
	 * computes, incrementally, the impact the move has on the solution.
	 * 
	 * @return
	 */
	public double[] evaluate(Solution soln, Move move) {
		currentSolution = (MySolution) soln;

		/*
		 * If the move is not null I clone the solution perform the move and evaluate the cost
		 * variation, by checking the cost of the modified routes, i.e. of the solution with the
		 * move, and then I subtract the cost of the routes before the move.
		 */
		if (move != null) {
			currentMove = (MyTwoExchangeMove) move;
			Cost totalVarCost;
			Cost newTotalCost = new Cost(currentSolution.getCost());
			double penalty = 0.0;
			
			totalVarCost = calculateTotalCostVariation ();
			
			newTotalCost.add(totalVarCost);
			newTotalCost.addLoadViol(totalVarCost.getLoadViol());
			newTotalCost.calculateTotal(currentSolution.getAlpha(), currentSolution.getBeta(), currentSolution.getGamma());
			
			if(currentSolution.getObjectiveValue()[0] < newTotalCost.getTotal())
				penalty = penalizationFactor * totalVarCost.getTotal();
			
			return new double[]{newTotalCost.getTotal() + penalty, 
								newTotalCost.getTotal(), 
								newTotalCost.getTravelTime(), 
								newTotalCost.getLoadViol(), 
								newTotalCost.getDurationViol(), 
								newTotalCost.getTwViol()};		
			} else {
			evaluateFullSolutionCost();

			return new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
					currentSolution.getCost().getTravelTime(),
					currentSolution.getCost().getLoadViol(),
					currentSolution.getCost().getDurationViol(),
					currentSolution.getCost().getTwViol() };
		}
	}

	/*
	 * Evaluates the cost of a solution from scratch. It is called from the evaluate method of this
	 * class whenever a null move is supplied. Actually this method determines the cost of the Cost
	 * object inside the MySolution object
	 */
	private void evaluateFullSolutionCost() {
		Route[] routes = currentSolution.getRoutes();
		Depot depot = instance.getDepot();
		List<Customer> customers;
		Customer previousCustomer;
		Customer currentCustomer;
		Cost totalSolutionCost = new Cost();
		Cost cost;

		for (Route route : routes) {
			if(route == null) { break; }
			route.getCost().reset(); // reset the cost of the route for the calculation
			customers = route.getCustomers();

			currentCustomer = customers.get(0);

			cost = route.getCost(); // also reset the cost variable for the next loop iteration
			cost.add(calculateEdgeCost(depot, currentCustomer));

			for (int i = 1; i < customers.size(); i++) {
				previousCustomer = currentCustomer;
				currentCustomer = customers.get(i);
				cost.add(calculateEdgeCost(previousCustomer, currentCustomer));
			}

			cost.add(calculateEdgeCost(currentCustomer, depot), true);
			
			// calculate the load violation externally as it's not part of a single edge, but of the whole route
			cost.setLoadViol(Math.max(0, cost.getLoad() - instance.getCapacity(0)));
			// calculate the total
			cost.calculateTotal(currentSolution.getAlpha(), currentSolution.getBeta(), currentSolution.getGamma());
			
			route.setCost(new Cost(cost)); // finally set the total cost of the route

			totalSolutionCost.add(cost); // add the cost mentioned above to the total cost of the solution
			totalSolutionCost.addLoadViol(cost.getLoadViol());
		}

		
		totalSolutionCost.calculateTotal(currentSolution.getAlpha(), currentSolution.getBeta(), currentSolution.getGamma());
		currentSolution.setCost(new Cost(totalSolutionCost));
	}

	/*
	 * Evaluates the cost of and edge that starts with a depot and ends with a customer. In the end
	 * this method returns the Cost of this edge. The boolean modifyCustomer should be set to true
	 * when the values of the fields of the customer object should not be modified. This is also
	 * used to increase the speed of the program when recalculating the cost of part of the route.
	 */

	private Cost calculateEdgeCost(Depot depot, Customer customer) {
		Cost cost = new Cost();

		cost.setTravelTime(instance.getTravelTime(instance.getCustomersNr(), customer.getNumber()));
		customer.setArriveTime(depot.getStartTw() + cost.getTravelTime());

		cost.setLoad(customer.getLoad());

		cost.setWaitingTime(Math.max(0, customer.getStartTw() - customer.getArriveTime()));
		customer.setWaitingTime(cost.getWaitingTime());

		cost.setTwViol(Math.max(0, customer.getArriveTime() - customer.getEndTw()));
		customer.setTwViol(cost.getTwViol());

		cost.setServiceTime(customer.getServiceDuration());

		return cost;
	}

	/*
	 * Evaluates the cost of and edge that starts with a customer and ends with a customer. In the
	 * end this method returns the Cost of this edge. The method is also supplied the current load
	 * of the route so as to be able to detect any load violations.
	 */

	private Cost calculateEdgeCost(Customer first, Customer second) {
		Cost cost = new Cost();

		cost.setTravelTime(instance.getTravelTime(first.getNumber(), second.getNumber()));
		second.setArriveTime(first.getDepartureTime() + cost.getTravelTime());

		cost.setLoad(second.getLoad());

		cost.setServiceTime(second.getServiceDuration());

		cost.setWaitingTime(Math.max(0, second.getStartTw() - second.getArriveTime()));
		second.setWaitingTime(cost.getWaitingTime());

		cost.setTwViol(Math.max(0, second.getArriveTime() - second.getEndTw()));
		second.setTwViol(cost.getTwViol());

		return cost;
	}

	/*
	 * Evaluates the cost of and edge that starts with a customer and ends in a depot. In the end
	 * this method returns the Cost of this edge.
	 */

	private Cost calculateEdgeCost(Customer customer, Depot depot) {
		Cost cost = new Cost();

		cost.setTravelTime(instance.getTravelTime(customer.getNumber(), instance.getCustomersNr()));

		// this is the time in which the vehicle reaches the depot
		cost.setReturnToDepotTime(customer.getDepartureTime() + cost.getTravelTime()); 

		cost.setDepotTwViol(Math.max(0, cost.getReturnToDepotTime() - depot.getEndTw()));
		// note that I've mentioned before that the depot time window violation is included also in
		// the total time window violation
		cost.setTwViol(cost.getDepotTwViol());

		return cost;
	}

	private Cost calculateTotalCostVariation ()
	{
		Route firstRoute = currentSolution.getRoutes(currentMove.getFirstRouteIndex());
		Route secondRoute = currentSolution.getRoutes(currentMove.getSecondRouteIndex());
		int firstCustomerIndex = firstRoute.indexOfCustomer(currentMove.getFirstCustomer());
		int secondCustomerIndex = secondRoute.indexOfCustomer(currentMove.getSecondCustomer());
		List<Customer> customers = new ArrayList<>();
		Cost firstRouteCostVariation = new Cost();
		Cost secondRouteCostVariation = new Cost();
		Cost cost;
		Route newFirstRoute = new Route();
		Route newSecondRoute = new Route();
		
		firstRouteCostVariation = evaluateSegmentCost(firstRoute, firstCustomerIndex);
		secondRouteCostVariation = evaluateSegmentCost(secondRoute, secondCustomerIndex);
		
		customers.add(currentMove.getFirstCustomer());
		
		for(int i = secondCustomerIndex + 1; i < secondRoute.getCustomers().size(); i++)
		{
			customers.add(new Customer(secondRoute.getCustomers(i)));
		}
		
		newFirstRoute.setCustomers(customers);
		
		customers.clear();
		
		customers.add(currentMove.getSecondCustomer());
		
		for(int i = firstCustomerIndex + 1; i < firstRoute.getCustomers().size(); i++)
		{
			customers.add(new Customer(firstRoute.getCustomers(i)));
		}
		
		newSecondRoute.setCustomers(customers);
		
		// get the full cost of the new first and second routes
		firstRouteCostVariation.add(evaluateRouteCost(firstRoute), true);
		secondRouteCostVariation.add(evaluateRouteCost(secondRoute), true);
		
		// get the cost variation for the first and second routes respectively
		firstRouteCostVariation.subtract(firstRoute.getCost());
		secondRouteCostVariation.subtract(secondRoute.getCost());
		
		// create the cost object that carries the total variation of the current two exchange move
		cost = new Cost(firstRouteCostVariation);
		cost.add(secondRouteCostVariation);
		
		// add the contribution in load violation from the second route as well
		cost.addLoadViol(secondRouteCostVariation.getLoadViol());
		
		// finally calculate the total field of the cost object
		cost.calculateTotal(currentSolution.getAlpha(), currentSolution.getBeta(), currentSolution.getGamma());
		
		return cost;
	}
	
	/*
	 * Method that quickly retrieves the cost of the unchanged part of the segment.
	 * The route parameter is the route whose cost we're retrieving. The stopping
	 * index is used as stopping point of the index. Note that this method implicitly
	 * assumes that we start in the depot.
	 */
	
	private Cost evaluateSegmentCost(Route route, int stoppingIndex)
	{
		if(stoppingIndex < 0 || stoppingIndex >= route.getCustomers().size())
		{
			throw new IndexOutOfBoundsException("Evaluation of the segment cost is not possible");
		}
		
		List<Customer> customers = route.getCustomers();
		Customer previousCustomer;
		Customer currentCustomer;
		Cost totalSegmentCost = new Cost();
		
		currentCustomer = customers.get(0);
		totalSegmentCost.setTravelTime(instance.getTravelTime(instance.getCustomersNr(), currentCustomer.getNumber()));
		totalSegmentCost.setLoad(currentCustomer.getLoad());
		totalSegmentCost.setServiceTime(currentCustomer.getServiceDuration());
		totalSegmentCost.setWaitingTime(currentCustomer.getWaitingTime());
		totalSegmentCost.addTWViol(currentCustomer.getTwViol());
		
		for(int i = 1; i <= stoppingIndex; i++)
		{
			previousCustomer = currentCustomer;
			currentCustomer = customers.get(i);
			totalSegmentCost.setTravelTime(instance.getTravelTime(previousCustomer.getNumber(), currentCustomer.getNumber()));
			totalSegmentCost.setLoad(totalSegmentCost.getLoad() + currentCustomer.getLoad());
			totalSegmentCost.setServiceTime(totalSegmentCost.getServiceTime() + currentCustomer.getServiceDuration());
			totalSegmentCost.setWaitingTime(totalSegmentCost.getWaitingTime() +currentCustomer.getWaitingTime());
			totalSegmentCost.addTWViol(currentCustomer.getTwViol());
		}
		
		return totalSegmentCost;
	}
	
	/*
	 * Evaluates the route of a cost and returns it. This cost is used just
	 * to calculate the cost introduced from a path added in a route modified
	 * by the two exchange move.
	 */
	private Cost evaluateRouteCost(Route route) {
		List<Customer> customers;
		Customer previousCustomer;
		Customer currentCustomer;
		Depot depot = instance.getDepot();
		Cost cost = new Cost();

		customers = route.getCustomers();

		currentCustomer = customers.get(0);
		for (int i = 1; i < customers.size(); i++) {
			previousCustomer = currentCustomer;
			currentCustomer = customers.get(i);
			cost.add(calculateEdgeCost(previousCustomer, currentCustomer));
		}

		cost.add(calculateEdgeCost(currentCustomer, depot), true);

		return cost;
	}
}
