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
 * @author Guido Pio
 * 
 */
@SuppressWarnings("serial")
public class MyMoveManager implements MoveManager {
	private static Instance instance;
	private MovesType movesType;

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
		case SWAP:
			return getSwapMoves(mySol);
		case TWO_EXCHANGE:
			return getTwoExchangeMoves(mySol);
		case THREE_EXCHANGE:
			return getThreeExchangeMoves(mySol);
		default:
			return null;
		}
	}

	private Move[] getSwapMoves(MySolution solution) {
		Route[] routes = solution.getRoutes();
		List<Move> moves = new ArrayList<Move>();

		// iterates routes
		for (int i = 0; i < routes.length; i++) {
			// iterate for each customer in the route
			List<Customer> customers = routes[i].getCustomers();
			for (int j = 0; j < customers.size(); j++) {
				// generate moves to all other routes
				for (int k = 0; k < routes.length; k++) {
					if (i != k) {
						Customer customer = customers.get(j);
						Move move = new MySwapMove(getInstance(), customer, i,
								j, k);
						moves.add(move);
					}
				}
			}
		}
		Move[] temp = moves.toArray(new Move[moves.size()]);
		return temp;
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
				// generate moves to all others routes {
				for (int k = 0; k < routes.length; k++) {
					if (i != k) {
						Customer customer = customers.get(j);
						Move move = new MyTwoExchangeMove(
								MyMoveManager.getInstance(), customer, i, k);
						moves.add(move);
					}
				}
			}
		}
		Move[] temp = moves.toArray(new Move[moves.size()]);
		return temp;
	}

	private Move[] getThreeExchangeMoves(Solution solution) {
		// TODO Auto-generated method stub
		return null;
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
