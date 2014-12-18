package com.tabusearch;

import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import com.vrptw.*;

@SuppressWarnings("serial")
public class MyObjectiveFunction implements ObjectiveFunction {
	private static Instance instance;
	
	public MyObjectiveFunction(Instance instance) {
		this.instance = instance;
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
		MySolution solution = (MySolution) soln;
		
		if(move != null)
		{
			// TODO -> case when move != null
		}
		else
		{
			evaluateFullSolutionCost(solution);
			
			return new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, solution.getCost().getTravelTime(), 
					            solution.getCost().getLoadViol(), solution.getCost().getDurationViol(), solution.getCost().getTwViol()};
		}
		
		
		return null;
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
		Customer currentCustomer;
		Customer nextCustomer;
		Cost totalSolutionCost = new Cost();
		
		// TODO - main loop
		
		for(Route route : routes)
		{
			customers = route.getCustomers();
			nextCustomer = customers.get(0);
			for(int i = 1; i < customers.size(); i++)
			{
				currentCustomer = nextCustomer;
				nextCustomer = customers.get(i);
			}
		}
		
	}
	
	
	// TODO
	private Cost calculateEdgeCost(Depot depot, Customer customer)
	{
		Cost cost = new Cost();
		double temp;
		
		cost.setTravelTime(instance.getTravelTime(instance.getCustomersNr(), customer.getNumber()));
		cost.setLoad(customer.getLoad());
		
		if((temp = cost.getLoad() - instance.getCapacity(0)) > 0)
			cost.setLoadViol(temp);
		
		customer.setArriveTime(depot.getStartTw() + cost.getTravelTime());
		
		if((temp = customer.getStartTw() - customer.getArriveTime()) > 0) {
			customer.setWaitingTime(temp);
			cost.setWaitingTime(cost.getWaitingTime() + temp);
		}
		
		if((temp = customer.getArriveTime() - customer.getEndTw()) > 0) {
			customer.setTwViol(temp);
			cost.setTwViol(cost.getTwViol() + temp);
		}
		
		cost.setServiceTime(customer.getServiceDuration());
		
		return cost;
	}
	
	// TODO
	private Cost calculateEdgeCost(Customer first, Customer second)
	{
		Cost cost = new Cost();
		
		cost.setTravelTime(instance.getTravelTime(first.getNumber(), second.getNumber()));
		cost.setLoad(second.getLoad());
		cost.setServiceTime(second.getServiceDuration());
		
		
		return null;
	}
	
	// TODO
	private Cost calculateEdgeCost(Customer customer, Depot depot)
	{
		
		return null;
	}

}
