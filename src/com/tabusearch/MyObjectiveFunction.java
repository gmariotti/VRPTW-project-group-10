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
			
			return new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, solution.getCost().travelTime, 
					            solution.getCost().loadViol, solution.getCost().durationViol, solution.getCost().twViol};
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
		
		cost.travelTime = instance.getTravelTime(instance.getCustomersNr(), customer.getNumber());
		cost.load = customer.getLoad();
		
		if((temp = cost.load - instance.getCapacity(0)) > 0)
			cost.loadViol = temp;
		
		customer.setArriveTime(depot.getStartTw() + cost.travelTime);
		
		if((temp = customer.getStartTw() - customer.getArriveTime()) > 0) {
			customer.setWaitingTime(temp);
			cost.waitingTime += temp;
		}
		
		if((temp = customer.getArriveTime() - customer.getEndTw()) > 0) {
			customer.setTwViol(temp);
			cost.twViol += temp;
		}
		
		cost.serviceTime = customer.getServiceDuration();
		
		return cost;
	}
	
	// TODO
	private Cost calculateEdgeCost(Customer first, Customer second)
	{
		Cost cost = new Cost();
		
		cost.travelTime = instance.getTravelTime(first.getNumber(), second.getNumber());
		cost.load = second.getLoad();
		cost.serviceTime = second.getServiceDuration();
		
		
		return null;
	}
	
	// TODO
	private Cost calculateEdgeCost(Customer customer, Depot depot)
	{
		
		return null;
	}

}
