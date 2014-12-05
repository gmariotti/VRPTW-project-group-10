/**
 * 
 */
package com.tabusearch;

import org.coinor.opents.SolutionAdapter;

import com.vrptw.*;

/**
 * @author Guido Pio
 *
 */
public class MySolution extends SolutionAdapter {
	Route[] routes;
	
	public MySolution(Instance instance) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the routes
	 */
	public Route[] getRoutes() {
		return routes;
	}
	
	/**
	 * 
	 * @param index the position of the route
	 * @return the route searched
	 */
	public Route getRoutes(int index) {
		return routes[index];
	}
	
	/**
	 * @param routes the routes to set
	 */
	public void setRoutes(Route[] routes) {
		this.routes = routes;
	}


	/**
	 * 
	 * @param route the route to set
	 * @param index the position in which as to be set
	 */
	public void setRoutes(Route route, int index) {
		this.routes[index] = route;
	}
}
