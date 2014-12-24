/**
 * 
 */
package com.tabusearch;

/**
 * This class is used to manage the Granularity Threshold in the idea of reducing the number of
 * moves generated every time, with the idea of avoiding long arcs
 */
public class Granular {
	private static double	granularityThreshold;
	private static double	beta	= 2;			// we have to change it for modify the
													// granularityThreshold

	/**
	 * set the granularity threshold using a formula given by the document The Granular Tabu Search
	 * and Its Application to the Vehicle-Routing Problem Paolo Toth, Daniele Vigo
	 * 
	 * @param sol
	 */
	public static void setGranularity(MySolution sol) {
		// granularityThreshold = beta*initialSolutionCost/(numVehicles + numCustomers)
		granularityThreshold = (beta * sol.getCost().getTotal())
				/ (sol.getMaxVehicleNumber() + sol.getCustomersNumber());
	}

	/**
	 * @return the granularityThreshold
	 */
	public static double getGranularityThreshold() {
		return granularityThreshold;
	}

}
