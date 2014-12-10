/**
 * 
 */
package com.tabusearch;

import org.coinor.opents.*;

/**
 * @author Guido Pio
 *
 */
public class MySearchProgram implements TabuSearchListener {

	public TabuSearch tabuSearch;
	
	/**
	 * Considered other parameters that can be used
	 */
	public MySearchProgram() {
		tabuSearch = new SingleThreadedTabuSearch(); // parameters as to be set
	}
	
	/* (non-Javadoc)
	 * @see org.coinor.opents.TabuSearchListener#tabuSearchStarted(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void tabuSearchStarted(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.TabuSearchListener#tabuSearchStopped(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void tabuSearchStopped(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.TabuSearchListener#newBestSolutionFound(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void newBestSolutionFound(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.TabuSearchListener#newCurrentSolutionFound(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void newCurrentSolutionFound(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.TabuSearchListener#unimprovingMoveMade(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void unimprovingMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.TabuSearchListener#improvingMoveMade(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void improvingMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.coinor.opents.TabuSearchListener#noChangeInValueMoveMade(org.coinor.opents.TabuSearchEvent)
	 */
	@Override
	public void noChangeInValueMoveMade(TabuSearchEvent e) {
		// TODO Auto-generated method stub

	}

}
