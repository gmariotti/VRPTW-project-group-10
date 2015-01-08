/**
 * 
 */
package com.tabusearch;

import java.util.ArrayList;
import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.MoveManager;
import org.coinor.opents.Solution;

import com.vrptw.*;

/**
 * Implementation of the interface MoveManager for managing all moves for the Tabu Search
 */
@SuppressWarnings("serial")
public class MyMoveManager implements MoveManager {
	private static Instance	instance;
	private MovesType		movesType;

	public MyMoveManager(Instance instance) {
		MyMoveManager.setInstance(instance);
	}

	/**
	 * Return all the moves based on the move type of the solution
	 */
	@Override
	public Move[] getAllMoves(Solution solution) {
		MySolution mySol = (MySolution) solution;

		switch (this.getMovesType()) {
		case TWO_EXCHANGE:
			return getTwoExchangeMoves(mySol);
		default:
			return getTwoExchangeMoves(mySol);
		}
	}

	/**
	 * 
	 * @param solution
	 * @return
	 */
	private Move[] getTwoExchangeMoves(MySolution solution) {
		Route[] routes = solution.getRoutes();
		List<Move> moves = new ArrayList<Move>();

		// iterate routes
		for (int i = 0; i < routes.length; i++) {
			// iterate for each customer in the route
			List<Customer> customers = routes[i].getCustomers();
			for (int j = 0; j < customers.size(); j++) {
				Customer customer = customers.get(j);
				// generate moves to all other routes
				// avoiding reconsider previous route
				for (int k = 0; k < routes.length; k++) {
					// to avoid the same route
					if (i != k) {
						// scan all customers of route k
						List<Customer> otherCustomers = routes[k].getCustomers();
						for (int l = 0; l < otherCustomers.size(); l++) {
							// if Granular Attribute and distance with
							// customer.calculateDistance(otherCustomer)
							Customer otherCustomer = otherCustomers.get(l);
							if (customer.getDistance(otherCustomer.getXCoordinate(),
									otherCustomer.getYCoordinate()) < Granular
									.getGranularityThreshold()) 
							{
								Move move = new MyTwoExchangeMove(MyMoveManager.getInstance(),
										customer, otherCustomer, i, k);
								moves.add(move);
							}
						}
					}
				}
			}
		}
		if (moves.size() == 0)
			System.out.println("No new moves have been generated");

		Move[] temp = moves.toArray(new Move[moves.size()]);
		return temp;
	}

	/**
	 * @return the instance
	 */
	public static Instance getInstance() {
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public static void setInstance(Instance instance) {
		MyMoveManager.instance = instance;
	}

	/**
	 * @return the movesType
	 */
	public MovesType getMovesType() {
		return movesType;
	}

	/**
	 * @param movesType
	 *            the movesType to set
	 */
	public void setMovesType(MovesType movesType) {
		this.movesType = movesType;
	}

}
