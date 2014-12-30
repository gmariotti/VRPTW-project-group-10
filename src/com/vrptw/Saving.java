/**
 * 
 */
package com.vrptw;

/**
 * @author Guido Pio
 * 
 */
public class Saving {
	private Customer	customer1, customer2;
	private int			customerN1, customerN2;
	private double		saving;

	public Saving(Depot depot, Customer customer1, Customer customer2) {
		/*double saving = customer1.getDistance(depot.getXCoordinate(), depot.getYCoordinate())
				- customer2.getDistance(customer1.getXCoordinate(), customer1.getYCoordinate())
				+ customer2.getDistance(depot.getXCoordinate(), depot.getYCoordinate());*/
		double waitingTime0_1 = customer1.getStartTw()
				- customer1.getDistance(depot.getXCoordinate(), depot.getYCoordinate());
		waitingTime0_1 = waitingTime0_1 > 0 ? customer1.getStartTw() : customer1.getDistance(
				depot.getXCoordinate(), depot.getYCoordinate());
		double waitingTime1_2 = customer2.getStartTw() - waitingTime0_1
				- customer2.getDistance(customer1.getXCoordinate(), customer1.getYCoordinate());
		waitingTime1_2 = waitingTime1_2 > 0 ? customer2.getStartTw() : waitingTime0_1
				+ customer2.getDistance(customer1.getXCoordinate(), customer1.getYCoordinate());
		double waitingTime0_2 = customer2.getStartTw()
				- customer2.getDistance(depot.getXCoordinate(), depot.getYCoordinate());
		waitingTime0_2 = waitingTime0_2 > 0 ? customer2.getStartTw() : customer2.getDistance(
				depot.getXCoordinate(), depot.getYCoordinate());
		double waitingTime2_1 = customer1.getStartTw() - waitingTime0_2
				- customer2.getDistance(customer1.getXCoordinate(), customer1.getYCoordinate());
		waitingTime2_1 = waitingTime2_1 > 0 ? customer1.getStartTw() : waitingTime0_2
				+ customer2.getDistance(customer1.getXCoordinate(), customer1.getYCoordinate());
		double bestWaitingTime = waitingTime1_2 > waitingTime2_1 ? waitingTime2_1 : waitingTime1_2;
		double saving = waitingTime0_1 + waitingTime0_2 - bestWaitingTime;
		this.setSaving(saving);
		this.setCustomer1(customer1);
		this.setCustomerN1(customer1.getNumber());
		this.setCustomer2(customer2);
		this.setCustomerN2(customer2.getNumber());
	}

	/**
	 * @return the customer1
	 */
	public Customer getCustomer1() {
		return customer1;
	}

	/**
	 * @param customer1
	 *            the customer1 to set
	 */
	public void setCustomer1(Customer customer1) {
		this.customer1 = customer1;
	}

	/**
	 * @return the customer2
	 */
	public Customer getCustomer2() {
		return customer2;
	}

	/**
	 * @param customer2
	 *            the customer2 to set
	 */
	public void setCustomer2(Customer customer2) {
		this.customer2 = customer2;
	}

	/**
	 * @return the customerN1
	 */
	public int getCustomerN1() {
		return customerN1;
	}

	/**
	 * @param customerN1
	 *            the customerN1 to set
	 */
	public void setCustomerN1(int customerN1) {
		this.customerN1 = customerN1;
	}

	/**
	 * @return the customerN2
	 */
	public int getCustomerN2() {
		return customerN2;
	}

	/**
	 * @param customerN2
	 *            the customerN2 to set
	 */
	public void setCustomerN2(int customerN2) {
		this.customerN2 = customerN2;
	}

	/**
	 * @return the saving
	 */
	public double getSaving() {
		return saving;
	}

	/**
	 * @param saving
	 *            the saving to set
	 */
	public void setSaving(double saving) {
		this.saving = saving;
	}
}
