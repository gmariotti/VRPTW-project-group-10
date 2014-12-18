package com.vrptw;

/**
 * This class represents a generic vehicle entity
 * 
 */
public class Vehicle {
	private int		vehicleNr;
	private double	capacity;
	private double	duration;

	public Vehicle() {

	}

	public Vehicle(int vehicleNr, double capacity, double duration) {
		this.vehicleNr = vehicleNr;
		this.capacity = capacity;
		this.duration = duration;
	}

	/**
	 * @return the vehicleNr
	 */
	public int getVehicleNr() {
		return vehicleNr;
	}

	/**
	 * @param vehicleNr
	 *            the vehicleNr to set
	 */
	public void setVehicleNr(int vehicleNr) {
		this.vehicleNr = vehicleNr;
	}

	/**
	 * @return the capacity
	 */
	public double getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity
	 *            the capacity to set
	 */
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

}
