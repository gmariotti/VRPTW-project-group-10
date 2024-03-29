package com.vrptw;

/**
 * This class stores information about cost of a route or a group of routes. It has total which is
 * the sum of travel, capacityViol, durationViol, twViol.
 */
public class Cost {
	private double	total;				// sum of all the costs
	private double	travelTime;		// sum of all travel times
	private double	load;				// sum of all quantities
	private double	serviceTime;		// sum of all service time
	private double	waitingTime;		// sum of all waiting times
	private double	loadViol;			// violation of the load
	private double	durationViol;		// violation of the duration waiting time + service time
	private double	twViol;			// violation of the time window
	private double	returnToDepotTime;	// stores time to return to the depot
	private double	depotTwViol;		// stores the time window violation of the depot

	public Cost() {
		total = 0;
		travelTime = 0;
		load = 0;
		serviceTime = 0;
		waitingTime = 0;

		loadViol = 0;
		durationViol = 0;
		twViol = 0;

		returnToDepotTime = 0;
		depotTwViol = 0;
	}

	public Cost(Cost cost) {
		this.total = new Double(cost.total);
		this.travelTime = new Double(cost.travelTime);
		this.load = new Double(cost.load);
		this.serviceTime = new Double(cost.serviceTime);
		this.waitingTime = new Double(cost.waitingTime);

		this.loadViol = new Double(cost.loadViol);
		this.durationViol = new Double(cost.durationViol);
		this.twViol = new Double(cost.twViol);

		this.returnToDepotTime = new Double(cost.returnToDepotTime);
		this.depotTwViol = new Double(cost.depotTwViol);
	}

	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("--- Cost -------------------------------------");
		print.append("\n" + "| TotalTravelCost=" + travelTime + " TotalCostViol=" + total);
		print.append("\n" + "| LoadViol=" + loadViol + " DurationViol=" + durationViol + " TWViol="
				+ twViol);
		print.append("\n" + "--------------------------------------------------" + "\n");
		return print.toString();
	}

	/**
	 * This method allows the addition of two costs and stores the corresponding values in the
	 * calling object. The method consists in adding all costs and time window violations to the
	 * costs and violations in the original object. Used to add a full route cost to a total.
	 * 
	 * @param cost
	 *            - The cost to add to the calling object
	 */

	public void add(Cost cost) {
		this.travelTime += cost.travelTime;
		this.load += cost.load;
		this.serviceTime += cost.serviceTime;
		this.waitingTime += cost.waitingTime;

		this.loadViol += cost.loadViol;
		this.durationViol += cost.durationViol;
		this.twViol += cost.twViol;
		this.depotTwViol += cost.depotTwViol;
	}

	/**
	 * This method is similar to the add method of this class, but it also allows you to set the
	 * time in which you return to the depot.
	 * 
	 * @param cost
	 *            - The cost to add to the calling object
	 * @param setReturnToDepotTime
	 *            - set to true to also set the return time to the depot with this cost
	 */

	public void add(Cost cost, boolean setReturnToDepotTime) {
		this.travelTime += cost.travelTime;
		this.load += cost.load;
		this.serviceTime += cost.serviceTime;
		this.waitingTime += cost.waitingTime;

		this.loadViol += cost.loadViol;
		this.durationViol += cost.durationViol;
		this.twViol += cost.twViol;
		this.depotTwViol += cost.depotTwViol;

		this.returnToDepotTime = (setReturnToDepotTime ? cost.returnToDepotTime : 0);
	}

	/**
	 * This method allows the subtraction between two costs and stores the corresponding values in
	 * the calling object. The method consists in subtracting all costs and violations from the
	 * costs and violations in the original object. Used to subtract a full route cost from a total.
	 * 
	 * @param cost
	 *            - The cost to subtract to the calling object
	 */
	public void subtract(Cost cost) {
		this.travelTime -= cost.travelTime;
		this.load -= cost.load;
		this.serviceTime -= cost.serviceTime;
		this.waitingTime -= cost.waitingTime;
		this.durationViol -= cost.durationViol;
		this.loadViol -= cost.loadViol;
		this.durationViol -= cost.durationViol;
		this.twViol -= cost.twViol;
		this.depotTwViol -= cost.depotTwViol;
	}

	/**
	 * Set the total cost based on alpha, beta, gamma. These parameters determine the importance of
	 * the different parameters and can be tuned in a later stage. A bigger value of the parameter
	 * results in a bigger total cost.
	 * 
	 * @param alpha
	 *            - This parameter determines the importance of the load violation
	 * @param beta
	 *            - This parameter determines the importance of the duration violation
	 * @param gamma
	 *            - This parameter determines the importance of the time window violation
	 */
	public void calculateTotal(double alpha, double beta, double gamma) {
		total = travelTime + alpha * loadViol + beta * durationViol + gamma * twViol;
	}

	public void addLoadViol(double capacityviol) {
		this.loadViol += capacityviol;
	}

	public void addDurationViol(double durationviol) {
		this.durationViol += durationviol;
	}

	public void addTwViol(double TWviol) {
		this.twViol += TWviol;
	}

	public void addDepotTwViol(double depotTwViol) {
		this.depotTwViol += depotTwViol;
	}

	public void addTravel(double cost) {
		travelTime += cost;
	}

	/**
	 * Check if a route is feasible.
	 * 
	 * @return true if there is no violation, false otherwise
	 */
	public boolean checkFeasible() {

		// notice that (loadViol + durationViol + twViol) is always bigger than or equal to 0

		return (loadViol + durationViol + twViol) == 0;
	}

	/**
	 * Returns the total cost.
	 */
	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}

	public double getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(double serviceTime) {
		this.serviceTime = serviceTime;
	}

	public double getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}

	public double getLoadViol() {
		return loadViol;
	}

	public void setLoadViol(double loadViol) {
		this.loadViol = loadViol;
	}

	public double getDurationViol() {
		return durationViol;
	}

	public void setDurationViol(double durationViol) {
		this.durationViol = durationViol;
	}

	public double getTwViol() {
		return twViol;
	}

	public void setTwViol(double twViol) {
		this.twViol = twViol;
	}

	/**
	 * @return The time in which the vehicle returns to the depot.
	 */
	public double getReturnToDepotTime() {
		return returnToDepotTime;
	}

	/**
	 * Sets the time in which the vehicle arrives to the depot to the desired value.
	 */
	public void setReturnToDepotTime(double returnToDepotTime) {
		this.returnToDepotTime = returnToDepotTime;
	}

	/**
	 * @return The time by which the vehicle is late to the depot. Note that this value is also
	 *         included in the total time window violation returned by getTwViol().
	 */
	public double getDepotTwViol() {
		return depotTwViol;
	}

	/**
	 * Sets the time by which the vehicle is late to the depot to the desired value.
	 */
	public void setDepotTwViol(double depotTwViol) {
		this.depotTwViol = depotTwViol;
	}

	public void reset() {
		total = 0;
		travelTime = 0;
		load = 0;
		serviceTime = 0;
		waitingTime = 0;

		loadViol = 0;
		durationViol = 0;
		twViol = 0;

		returnToDepotTime = 0;
		depotTwViol = 0;
	}

}
