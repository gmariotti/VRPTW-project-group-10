package com.tabusearch;

import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import com.vrptw.*;

@SuppressWarnings("serial")
public class MyObjectiveFunction implements ObjectiveFunction {
	private static Instance		instance;
	private MySolution			currentSolution;
	private MyTwoExchangeMove	currentMove;
	private double				penalizationFactor;

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
		this.currentSolution = (MySolution) soln;

		if (move != null) {
			this.currentMove = (MyTwoExchangeMove) move;
			Cost newTotalCost;
			double penalty = 0.0;

			newTotalCost = calculateTotalCost();

			if (currentSolution.getObjectiveValue()[0] < newTotalCost.getTotal())
				penalty = penalizationFactor * newTotalCost.getTotal();

			return new double[] { newTotalCost.getTotal() + penalty, newTotalCost.getTotal(),
					newTotalCost.getTravelTime(), newTotalCost.getLoadViol(),
					newTotalCost.getDurationViol(), newTotalCost.getTwViol() };
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
		Cost totalSolutionCost = new Cost();

		for (Route route : routes) {
			route.getCost().reset(); // reset the cost of the route for the calculation

			route.calculateCost(route.getAssignedVehicle().getCapacity(), instance.getAlpha(), instance.getBeta(), instance.getGamma());

			addCostToTotal(totalSolutionCost, route.getCost());
		}

		currentSolution.setCost(new Cost(totalSolutionCost));
	}

	private void addCostToTotal(Cost totalCost, Cost cost) {
		totalCost.setTravelTime(totalCost.getTravelTime() + cost.getTravelTime());

		totalCost.setLoad(totalCost.getLoad() + cost.getLoad());
		totalCost.addLoadViol(cost.getLoadViol());

		totalCost.setServiceTime(totalCost.getServiceTime() + cost.getServiceTime());

		totalCost.setWaitingTime(totalCost.getWaitingTime() + cost.getWaitingTime());

		totalCost.addTwViol(cost.getTwViol());
		totalCost.addDepotTwViol(cost.getDepotTwViol());

		totalCost.calculateTotal(instance.getAlpha(), instance.getBeta(), instance.getGamma());
	}

	/*
	 * Calculate the impact of the currentMove cost-wise with respect to the currentSolution. The
	 * currentSolution is cloned first the move is applied and then the cost is reevaluated so as
	 * not to modify the currentSolution as this isn't the problem
	 */
	private Cost calculateTotalCost() {
		MySolution solution = (MySolution) currentSolution.clone();
		Cost totalCost = new Cost(solution.getCost());
		Cost variation;
		Route firstRoute = new Route(solution.getRoutes(currentMove.getFirstRouteIndex()));
		Route secondRoute = new Route(solution.getRoutes(currentMove.getSecondRouteIndex()));

		currentMove.operateOn(solution);

		Route newFirstRoute = solution.getRoutes(currentMove.getFirstRouteIndex());
		Route newSecondRoute = solution.getRoutes(currentMove.getSecondRouteIndex());

		variation = calculateCostVariation(firstRoute, newFirstRoute);

		addCostToTotal(totalCost, variation);

		variation = calculateCostVariation(secondRoute, newSecondRoute);

		addCostToTotal(totalCost, variation);

		return totalCost;
	}

	/*
	 * Used to calculate the variation in cost between two routes. This is used to evaluate
	 * simultaneously the impact of removing route and adding new route.
	 */
	private Cost calculateCostVariation(Route route, Route newRoute) {
		Cost variation = new Cost(newRoute.getCost());
		Cost cost = new Cost(route.getCost());

		variation.setTravelTime(variation.getTravelTime() - cost.getTravelTime());

		variation.setLoad(variation.getLoad() - cost.getLoad());
		variation.addLoadViol((-1) * cost.getLoadViol());

		variation.setServiceTime(variation.getServiceTime() - cost.getServiceTime());

		variation.setWaitingTime(variation.getWaitingTime() - cost.getWaitingTime());

		variation.addTwViol((-1) * cost.getTwViol());
		variation.addDepotTwViol((-1) * cost.getDepotTwViol());

		return variation;
	}
}
