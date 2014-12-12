package com.vrptw;

/**
 * Customer class stores information about one customer which implements the Vertex interface.
 * Stores the number of the customer, coordinates, service duration, capacity,
 */
public class Customer {

    private int number;
    private double xCoordinate;
    private double yCoordinate;
    private double serviceDuration; 		// duration that takes to dispatch the delivery
    private double load; 					// capacity of the pack that is expecting
    private int startTw; 					// beginning of time window (earliest time for start of service), if any
    private int endTw; 						// end of time window (latest time for start of service), if any
    private int patternUsed; 				// the combination of
    private double arriveTime; 				// time at which the car arrives to the customer
    private double waitingTime; 			// time to wait until arriveTime equal start time window
    private double twViol; 					// value of time window violation, 0 if none

    /**
     * Not sure if we need them
     */
    private int frequency; 					// frequency of visit
    private int combinationsVisitsNr; 		// number of possible visits combinations
    private int[][] combinationsList; 		// combinationslist[i][j] where i = visit combinations number and j = frequency

    public Customer() {
	xCoordinate = 0;
	yCoordinate = 0;
	serviceDuration = 0;
	load = 0;
	startTw = 0;
	endTw = 0;
	arriveTime = 0;
	waitingTime = 0;
	twViol = 0;

	frequency = 0;
	combinationsVisitsNr = 0;

    }

    public Customer(Customer customer) {
	this.number = customer.number;
	this.xCoordinate = customer.xCoordinate;
	this.yCoordinate = customer.yCoordinate;
	this.serviceDuration = customer.serviceDuration;
	this.load = customer.load;
	this.startTw = customer.startTw;
	this.endTw = customer.endTw;
	this.patternUsed = customer.patternUsed;
	this.arriveTime = new Double(customer.arriveTime);
	this.waitingTime = new Double(customer.waitingTime);
	this.twViol = new Double(customer.twViol);

	this.frequency = customer.frequency;
	this.combinationsVisitsNr = customer.combinationsVisitsNr;
	this.combinationsList = customer.combinationsList;
    }

    /**
     * This return a string with formated customer data
     * 
     * @return
     */
    public String toString() {
    	StringBuffer print = new StringBuffer();
		print.append("\n");
		print.append("\n" + "--- Customer " + number + " -----------------------------------");
		print.append("\n" + "| x=" + xCoordinate + " y=" + yCoordinate);
		print.append("\n" + "| ServiceDuration=" + serviceDuration + " Demand=" + load);
		print.append("\n" + "| frequency=" + frequency + " visitcombinationsnr=" + combinationsVisitsNr);
		print.append("\n" + "| StartTimeWindow=" + startTw + " EndTimeWindow=" + endTw);
		print.append("\n" + "| AnglesToDepots: ");
		print.append("\n" + "--------------------------------------------------");
		return print.toString();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

     public double getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(double serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public int getStartTw() {
        return startTw;
    }

    public void setStartTw(int startTw) {
        this.startTw = startTw;
    }

    public int getEndTw() {
        return endTw;
    }

    public void setEndTw(int endTw) {
        this.endTw = endTw;
    }

    public int getPatternUsed() {
        return patternUsed;
    }

    public void setPatternUsed(int patternUsed) {
        this.patternUsed = patternUsed;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(double arriveTime) {
        this.arriveTime = arriveTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getTwViol() {
        return twViol;
    }

    public void setTwViol(double twViol) {
        this.twViol = twViol;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getCombinationsVisitsNr() {
        return combinationsVisitsNr;
    }

    public void setCombinationsVisitsNr(int combinationsVisitsNr) {
        this.combinationsVisitsNr = combinationsVisitsNr;
    }

    public int[][] getCombinationsList() {
        return combinationsList;
    }

    public void setCombinationsList(int[][] combinationsList) {
        this.combinationsList = combinationsList;
    }

}
