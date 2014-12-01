package com.vrptw;

/**
 * Customer class stores information about one customer which implements the
 * Vertex interface. Stores the number of the customer, coordinates, service
 * duration, capacity,
 */
public class Customer {
    private int number;
    private double xCoordinate;
    private double yCoordinate;
    private double serviceDuration; // duration that takes to dispatch the
				    // delivery
    private double load; // capacity of the pack that is expecting
    private int startTw; // beginning of time window (earliest time for start of
			 // service),if any
    private int endTw; // end of time window (latest time for start of service),
		       // if any
    private int patternUsed; // the combination of
    private double arriveTime; // time at which the car arrives to the customer
    private double waitingTime; // time to wait until arriveTime equal start
				// time window
    private double twViol; // value of time window violation, 0 if none

    /**
     * Not sure if we need them
     */
    private int frequency; // frequency of visit
    private int combinationsVisitsNr; // number of possible visits combinations
    private int[][] combinationsList; // combinationslist[i][j] where i = visit
				      // combinations nr and j = frequency

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
    public String print() {
	// TODO --> probably can be substituted with toString()
	return null;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return the xCoordinate
     */
    public double getXCoordinate() {
        return xCoordinate;
    }

    /**
     * @param xCoordinate the xCoordinate to set
     */
    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    /**
     * @return the yCoordinate
     */
    public double getYCoordinate() {
        return yCoordinate;
    }

    /**
     * @param yCoordinate the yCoordinate to set
     */
    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    /**
     * @return the serviceDuration
     */
    public double getServiceDuration() {
        return serviceDuration;
    }

    /**
     * @param serviceDuration the serviceDuration to set
     */
    public void setServiceDuration(double serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    /**
     * @return the load
     */
    public double getLoad() {
        return load;
    }

    /**
     * @param load the load to set
     */
    public void setLoad(double load) {
        this.load = load;
    }

    /**
     * @return the startTw
     */
    public int getStartTw() {
        return startTw;
    }

    /**
     * @param startTw the startTw to set
     */
    public void setStartTw(int startTw) {
        this.startTw = startTw;
    }

    /**
     * @return the endTw
     */
    public int getEndTw() {
        return endTw;
    }

    /**
     * @param endTw the endTw to set
     */
    public void setEndTw(int endTw) {
        this.endTw = endTw;
    }

    /**
     * @return the patternUsed
     */
    public int getPatternUsed() {
        return patternUsed;
    }

    /**
     * @param patternUsed the patternUsed to set
     */
    public void setPatternUsed(int patternUsed) {
        this.patternUsed = patternUsed;
    }

    /**
     * @return the arriveTime
     */
    public double getArriveTime() {
        return arriveTime;
    }

    /**
     * @param arriveTime the arriveTime to set
     */
    public void setArriveTime(double arriveTime) {
        this.arriveTime = arriveTime;
    }

    /**
     * @return the waitingTime
     */
    public double getWaitingTime() {
        return waitingTime;
    }

    /**
     * @param waitingTime the waitingTime to set
     */
    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    /**
     * @return the twViol
     */
    public double getTwViol() {
        return twViol;
    }

    /**
     * @param twViol the twViol to set
     */
    public void setTwViol(double twViol) {
        this.twViol = twViol;
    }

    /**
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the combinationsVisitsNr
     */
    public int getCombinationsVisitsNr() {
        return combinationsVisitsNr;
    }

    /**
     * @param combinationsVisitsNr the combinationsVisitsNr to set
     */
    public void setCombinationsVisitsNr(int combinationsVisitsNr) {
        this.combinationsVisitsNr = combinationsVisitsNr;
    }

    /**
     * @return the combinationsList
     */
    public int[][] getCombinationsList() {
        return combinationsList;
    }

    /**
     * @param combinationsList the combinationsList to set
     */
    public void setCombinationsList(int[][] combinationsList) {
        this.combinationsList = combinationsList;
    }

}
