/**
 * 
 */
package com.tabusearch;

import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import com.vrptw.Instance;

/**
 * @author Guido Pio
 *
 */
public class MyObjectiveFunction implements ObjectiveFunction {

	public MyObjectiveFunction(Instance instance) {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.ObjectiveFunction#evaluate(org.coinor.opents.Solution, org.coinor.opents.Move)
	 */
	@Override
	public double[] evaluate(Solution soln, Move move) {
		// TODO Auto-generated method stub
		return null;
	}

}
