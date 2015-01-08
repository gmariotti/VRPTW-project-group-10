package com.vrptw;

/**
 * Depot class stores information about one depot which implements the Vertex interface. It stores
 * the number of the depot, it's capacity, coordinates, it's working time(time windows)
 * 
 */
public class Depot {
	private int		number;
	private double	xCoordinate;
	private double	yCoordinate;
	private int		startTw;		// beginning of time window (earliest time for start of
									// service), if any
	private int		endTw;			// end of time window (latest time for start of service), if any

	public Depot() {
		this.startTw = 0;
		this.endTw = 0;
	}

	/**
	 * Return the formated string of the depot
	 */
	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("--- Depot ---" + "\n");
		print.append("x=" + xCoordinate + " y=" + yCoordinate + "\n");
		print.append("StartTW " + startTw + " EndTW " + endTw + "\n");
		print.append("------" + "\n");
		return print.toString();
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

}
