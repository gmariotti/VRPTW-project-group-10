package com.vrptw;

/**
 * Depot class stores information about one depot which implements the Vertex
 * interface. It stores the number of the depot, it's capacity, coordinates,
 * it's working time(time windows)
 * 
 */
public class Depot {
	private int number;
	private double xCoordinate;
	private double yCoordinate;
	private int startTw; 		// beginning of time window (earliest time for start of service), if any
	private int endTw; 			// end of time window (latest time for start of service), if any


	public Depot() {
		this.startTw = 0;
		this.endTw = 0;
	}

	/**
	 * Return the formated string of the depot
	 */
	public String toString() {
		String str = "Depot in coordinates (" + xCoordinate + ";" + yCoordinate + ")\n"
				   + "that opens at " + startTw + " and closes at " + endTw;
		
		return str;
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

	/**
	 * @return The opening time of the depot.
	 */
	public int getStartTw() {
		return startTw;
	}

	/**
	 * @param startTw - The opening time of the depot.
	 */
	public void setStartTw(int startTw) {
		this.startTw = startTw;
	}

	/**
	 * @return The closing time of the depot.
	 */
	public int getEndTw() {
		return endTw;
	}

	/**
	 * @param endTw - The closing time of the depot.
	 */
	public void setEndTw(int endTw) {
		this.endTw = endTw;
	}

}
