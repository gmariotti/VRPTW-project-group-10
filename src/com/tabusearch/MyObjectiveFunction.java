package com.tabusearch;

import java.util.ArrayList;
import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import com.vrptw.*;

@SuppressWarnings("serial")
public class MyObjectiveFunction implements ObjectiveFunction {
	private static Instance instance;
	private MySolution currentSolution;
	private double penalizationFactor;
	
	public MyObjectiveFunction(Instance instance) {
		MyObjectiveFunction.instance = instance;
		penalizationFactor = 0.5 * Math.sqrt(instance.getVehiclesNr() * instance.getCustomersNr());
	}

	/**
	 * This function evaluates the impact that a move has on a solution.
	 * If a null Move is passed then a full evaluation of the cost of the
	 * solution is performed, otherwise the method computes, incrementally,
	 * the impact the move has on the solution.
	 * 
	 * @return
	 */
	public double[] evaluate(Solution soln, Move move) {
		currentSolution = (MySolution) soln;
		
		/*
		 * If the move is not null I clone the solution perform the move
		 * and evaluate the cost variation, by checking the cost of the
		 * modified routes, i.e. of the solution with the move, and then
		 * I subtract the cost of the routes before the move.
		 */
		if(move != null)
		{
			MySolution sol = (MySolution)currentSolution.clone();
			MyTwoExchangeMove twoExchangeMove = (MyTwoExchangeMove) move;
			Route firstRoute = sol.getRoutes(twoExchangeMove.getFirstRouteIndex());
			Route secondRoute = sol.getRoutes(twoExchangeMove.getSecondRouteIndex());
			Customer firstCustomer = twoExchangeMove.getFirstCustomer();
			Customer secondCustomer = twoExchangeMove.getSecondCustomer();
			Cost newSolutionCost = sol.getCost();
			Cost costVariationFirstRoute = new Cost();
			Cost costVariationSecondRoute = new Cost();
			double penalty = 0;
			
			// modify the routes in the clone object
			evaluateNewRoutes(firstRoute, secondRoute, firstCustomer, secondCustomer);
			
			// add contribution from the first modified route
			costVariationFirstRoute.add(evaluateRouteCost(firstRoute));
			costVariationFirstRoute.subtract(currentSolution.getRoutes(twoExchangeMove.getFirstRouteIndex()).getCost());
			
			// add contribution from the second modified route
			costVariationSecondRoute.add(evaluateRouteCost(secondRoute));
			costVariationSecondRoute.subtract(currentSolution.getRoutes(twoExchangeMove.getSecondRouteIndex()).getCost());
			
			newSolutionCost.add(costVariationFirstRoute);
			newSolutionCost.add(costVariationSecondRoute);
			
			if(currentSolution.getObjectiveValue()[0] < newSolutionCost.total)
				penalty = penalizationFactor * newSolutionCost.total;
			
			return new double[] {
					newSolutionCost.total + penalty,
					newSolutionCost.total,
					newSolutionCost.travelTime, 
					newSolutionCost.loadViol, 
					newSolutionCost.durationViol, 
					newSolutionCost.twViol
					};
		}
		else
		{
			evaluateFullSolutionCost(currentSolution);
			
			return new double[] {
					Double.POSITIVE_INFINITY,
					Double.POSITIVE_INFINITY,
					currentSolution.getCost().travelTime, 
					currentSolution.getCost().loadViol, 
					currentSolution.getCost().durationViol, 
					currentSolution.getCost().twViol
					};
		}
	}
	
	
	
	/*
	 * Evaluates the cost of a solution from scratch. It is called from the evaluate
	 * method of this class whenever a null move is supplied. Actually this method
	 * determines the cost of the Cost object inside the MySolution object
	 */
	private void evaluateFullSolutionCost(MySolution solution) {
		Route[] routes = solution.getRoutes();
		Depot depot = instance.getDepot();
		List<Customer> customers;
		Customer previousCustomer;
		Customer currentCustomer;
		Cost totalSolutionCost = new Cost();
		Cost cost;
		
		for(Route route : routes)
		{
			route.setCost(new Cost());								// reset the cost of the route for the calculation
			customers = route.getCustomers();
			
			currentCustomer = customers.get(0);
			
			cost = route.getCost();									// also reset the cost variable for the next loop iteration
			cost.add(calculateEdgeCost(depot, currentCustomer));
			
			for(int i = 1; i < customers.size(); i++)
			{
				previousCustomer = currentCustomer;
				currentCustomer = customers.get(i);
				cost.add(calculateEdgeCost(previousCustomer, currentCustomer, route.getCost().load));
			}
			
			cost.add(calculateEdgeCost(currentCustomer, depot), true);
			route.setCost(cost);									// finally set the total cost of the route
			
			totalSolutionCost.add(cost);						// add the cost mentioned above to the total cost of the solution
		}
		
		solution.setCost(totalSolutionCost);
	}
	
	/*
	 * Evaluates the cost of and edge that starts with a depot and ends
	 * with a customer. In the end this method returns the Cost of this
	 * edge. The boolean modifyCustomer should be set to true when
	 * the values of the fields of the customer object should not be
	 * modified. This is also used to increase the speed of the program
	 * when recalculating the cost of part of the route.
	 */
	
	private Cost calculateEdgeCost(Depot depot, Customer customer)
	{
		Cost cost = new Cost();
		
		cost.travelTime = instance.getTravelTime(instance.getCustomersNr(), customer.getNumber());
		customer.setArriveTime(depot.getStartTw() + cost.travelTime);
		
		cost.load = customer.getLoad();
		
		cost.waitingTime = Math.max(0, customer.getStartTw() - customer.getArriveTime());
		customer.setWaitingTime(cost.waitingTime);
		
		cost.twViol = Math.max(0, customer.getArriveTime() - customer.getEndTw());
		customer.setTwViol(cost.twViol);
		
		cost.serviceTime = customer.getServiceDuration();
		
		cost.calculateTotal(currentSolution.getAlpha(), currentSolution.getBeta(), currentSolution.getGamma());
		
		return cost;
	}
	
	/*
	 * Evaluates the cost of and edge that starts with a customer and ends
	 * with a customer. In the end this method returns the Cost of this
	 * edge. The method is also supplied the current load of the route
	 * so as to be able to detect any load violations.
	 */
	
	private Cost calculateEdgeCost(Customer first, Customer second, double currentRouteLoad)
	{
		Cost cost = new Cost();
		
		cost.travelTime = instance.getTravelTime(first.getNumber(), second.getNumber());
		second.setArriveTime(first.getDepartureTime() + cost.travelTime);
		
		cost.load = second.getLoad();
		
		cost.serviceTime = second.getServiceDuration();
		
		cost.waitingTime = Math.max(0, second.getStartTw() - second.getArriveTime());
		second.setWaitingTime(cost.waitingTime);
		
		cost.twViol = Math.max(0, second.getArriveTime() - second.getEndTw());
		second.setTwViol(cost.twViol);
		
		cost.calculateTotal(currentSolution.getAlpha(), currentSolution.getBeta(), currentSolution.getGamma());
		
		return cost;
	}
	
	/*
	 * Evaluates the cost of and edge that starts with a customer and 
	 * ends in a depot. In the end this method returns the Cost of 
	 * this edge. 
	 */
	
	private Cost calculateEdgeCost(Customer customer, Depot depot)
	{
		Cost cost = new Cost();
		
		cost.travelTime = instance.getTravelTime(customer.getNumber(), instance.getCustomersNr());
		
		cost.returnToDepotTime = customer.getDepartureTime() + cost.travelTime;		// this is the time in which the vehicle reaches the depot
		
		cost.depotTwViol = Math.max(0, cost.returnToDepotTime - depot.getEndTw());
		// note that I've mentioned before that the depot time window violation is included also in the total time window violation
		cost.twViol = cost.depotTwViol;
		
		cost.calculateTotal(currentSolution.getAlpha(), currentSolution.getBeta(), currentSolution.getGamma());
		
		return cost;
	}
	
	/*
	 * This is a copy of the method used in the MyTwoExchangeMove class, with
	 * a different objective function evaluation.
	 */

	private void evaluateNewRoutes(Route routeFirst, Route routeSecond, Customer firstCustomer, Customer secondCustomer) {
		List<Customer> newCustomersFirst = new ArrayList<>();
		List<Customer> newCustomersSecond = new ArrayList<>();
		List<Customer> oldCustomersFirst = routeFirst.getCustomers();
		List<Customer> oldCustomersSecond = routeSecond.getCustomers();
	
		// scan the first route customers before the customer of the move
		for (int i = 0; i < oldCustomersFirst.indexOf(firstCustomer); i++) {
			Customer tmp = oldCustomersFirst.get(i);
			newCustomersFirst.add(tmp);
		}
		newCustomersFirst.add(firstCustomer);
		// scan the second route customers after the customer of the move
		for (int i = (oldCustomersSecond.indexOf(secondCustomer) + 1); i < oldCustomersSecond.size(); i++) {
			Customer tmp = oldCustomersSecond.get(i);
			newCustomersFirst.add(tmp);
		}
	
		// scan the second route customers before the customer of the move
		for (int i = 0; i < oldCustomersSecond.indexOf(secondCustomer); i++) {
			Customer tmp = oldCustomersSecond.get(i);
			newCustomersSecond.add(tmp);
		}
		newCustomersSecond.add(secondCustomer);
		// scan the first route customers after the customer of the move
		for (int i = (oldCustomersFirst.indexOf(firstCustomer) + 1); i < oldCustomersFirst.size(); i++) {
			Customer tmp = oldCustomersFirst.get(i);
			newCustomersSecond.add(tmp);
		}
	
		// set the new customers list for each route and evaluate the new cost
		routeFirst.setCustomers(newCustomersFirst);
		routeSecond.setCustomers(newCustomersSecond);
	}

	
	private Cost evaluateRouteCost(Route route) {
		List<Customer> customers;
		Customer previousCustomer;
		Customer currentCustomer;
		Depot depot = instance.getDepot();
		Cost cost;
		
		route.setCost(new Cost());								
		customers = route.getCustomers();
		
		currentCustomer = customers.get(0);
		
		cost = route.getCost();									
		cost.add(calculateEdgeCost(depot, currentCustomer));
		
		for(int i = 1; i < customers.size(); i++)
		{
			previousCustomer = currentCustomer;
			currentCustomer = customers.get(i);
			cost.add(calculateEdgeCost(previousCustomer, currentCustomer, route.getCost().load));
		}
		
		cost.add(calculateEdgeCost(currentCustomer, depot), true);
		route.setCost(cost);									
		
		return cost;
	}
}
