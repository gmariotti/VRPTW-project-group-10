package com.vrptw;

/**
 * This class stores information about cost of a route or a group of routes. It
 * has total which is the sum of travel, capacityViol, durationViol, twViol.
 */
public class Cost {
    public double total; 				// sum of all the costs
    public double travelTime; 			// sum of all travel times
    public double load; 				// sum of all quantities
    public double serviceTime; 			// sum of all service time
    public double waitingTime; 			// sum of all waiting times
    public double loadViol; 			// violation of the load
    public double durationViol; 		// violation of the duration waiting time + service time
    public double twViol; 				// violation of the time window
    public double returnToDepotTime; 	// stores time to return to the depot
    public double depotTwViol; 			// stores the time window violation of the depot

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
		print.append("\n" + "| LoadViol=" + loadViol + " DurationViol=" + durationViol + " TWViol=" + twViol);
		print.append("\n" + "--------------------------------------------------" + "\n");
		return print.toString();
    }

    /**
     * This method allows the addition of two costs and stores the
     * corresponding values in the calling object. The method consists
     * in incrementing all costs and violations to the values of
     * the original object.
     */
    
    public void add(Cost cost)
    {
    	this.total += cost.total;
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
     * Set the total cost based on alpha, beta, gamma. These parameters
     * determine the importance of the different parameters and can be
     * tuned in a later stage. A bigger value of the parameter results
     * in a bigger total cost.
     * 
     * @param alpha - This parameter determines the importance of the load violation
     * @param beta - This parameter determines the importance of the duration violation
     * @param gamma - This parameter determines the importance of the time window violation
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

    public void addTWViol(double TWviol) {
    	this.twViol += TWviol;
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
     * Sets the time in which the vehicle arrives to the depot to
     * the desired value.
     */
    public void setReturnToDepotTime(double returnToDepotTime) {
    	this.returnToDepotTime = returnToDepotTime;
    }
    
    /**
     * @return The time by which the vehicle is late to the depot. 
     * 		   Note that this value is also included in the total 
     * 		   time window violation returned by getTwViol().
     */
    public double getDepotTwViol() {
    	return depotTwViol;
    }
    
    /**
     * Sets the time by which the vehicle is late to the depot to
     * the desired value.
     */
    public void setDepotTwViol(double depotTwViol) {
    	this.depotTwViol = depotTwViol;
    }

}
