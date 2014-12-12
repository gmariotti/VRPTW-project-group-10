package com.vrptw;

import com.tabusearch.MovesType;

public class Parameters {
	@SuppressWarnings("unused")
	private MovesType movesType;
	private String inputFileName;
	private String outputFileName;
	private double precision;
	private int iterations;
	private int startClient;
	private int randomSeed;
	private int tabuTenure;
	private boolean variableTenure;
	private String currDir;
	
	public Parameters() {
		currDir 			= System.getProperty("user.dir");
		outputFileName    	= currDir + "/output/solutions.csv";
//		movesType         	= MovesType.SWAP;
		precision         	= 1E-2;
		iterations        	= 1000;
		startClient       	= -1;
		tabuTenure        	= -1;
		randomSeed		  	= -1;
		variableTenure    	= false;
	}
	
	public void updateParameters(String[] args) throws Exception
	{
		if(args.length % 2 == 0){
			for(int i = 0; i < args.length; i += 2){
				switch (args[i]) {
//					case "-mT":
//						movesType = MovesType.SWAP;
//						break;
					case "-if":
						inputFileName = args[i+1];
						break;
					case "-of":
						outputFileName = args[i+1];
						break;
					case "-p":
						precision = Double.parseDouble(args[i+1]);
						break;
					case "-it":
						iterations = Integer.parseInt(args[i+1]);
						break;
					case "-sc":
						startClient = Integer.parseInt(args[i+1]);
						break;
					case "-rs":
						randomSeed = Integer.parseInt(args[i+1]);
						break;
					case "-t":
						tabuTenure = Integer.parseInt(args[i+1]);
						break;
					case "-vt":
						if(args[i+1].equalsIgnoreCase("true")){
							variableTenure = true;
						}else if(args[i+1].equalsIgnoreCase("false")){
							variableTenure = false;
						}else {
							System.out.println("Variable tenure argument must be true of false. Set to default false!");
							throw new Exception();
						}
						break;
					default: {
						System.out.println("Unknown type of argument: " + args[i]);
						throw new Exception();
					}
					break;
				case "-g":
					if (args[i + 1].equalsIgnoreCase("on")) {
						setGraphics(true);
					} else if (args[i + 1].equalsIgnoreCase("off")) {
						setGraphics(false);
					} else {
						System.out
								.println("Graphics argument must be on of off. Set to default off!");
					}
					break;
				default: {
					System.out.println("Unknown type of argument: " + args[i]);
					throw new Exception();
				}
				}
			}
		} else {
			System.out.println("Parameters are not in correct format");
			throw new Exception();
		}

	}

	/**
	 * @return the movesType
	 */
	public MovesType getMovesType() {
		return movesType;
	}

	/**
	 * @param movesType
	 *            the movesType to set
	 */
	public void setMovesType(MovesType movesType) {
		this.movesType = movesType;
	}


	public int getRandomSeed() {
		return randomSeed;
	}

	public String getCurrDir() {
		return currDir;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	/**
	 * @param inputFileName
	 *            the inputFileName to set
	 */
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	/**
	 * @return the outputFileName
	 */

	public String getOutputFileName() {
		return outputFileName;
	}


	/**
	 * @param outputFileName
	 *            the outputFileName to set
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}


	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * @param precision
	 *            the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * @return the iterations
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @param iterations
	 *            the iterations to set
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	/**
	 * @return the startClient
	 */
	public int getStartClient() {
		return startClient;
	}

	/**
	 * @param startClient
	 *            the startClient to set
	 */
	public void setStartClient(int startClient) {
		this.startClient = startClient;
	}

	/**
	 * @return the randomSeed
	 */
	public int getRandomSeed() {
		return randomSeed;
	}

	/**
	 * @param randomSeed
	 *            the randomSeed to set
	 */
	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	/**
	 * @return the tabuTenure
	 */
	public int getTabuTenure() {
		return tabuTenure;
	}

	/**
	 * @param tabuTenure
	 *            the tabuTenure to set
	 */
	public void setTabuTenure(int tabuTenure) {
		this.tabuTenure = tabuTenure;
	}

	/**
	 * @return the variableTenure
	 */
	public boolean isVariableTenure() {
		return variableTenure;
	}

	/**
	 * @param variableTenure
	 *            the variableTenure to set
	 */
	public void setVariableTenure(boolean variableTenure) {
		this.variableTenure = variableTenure;
	}

	/**
	 * @return the currDir
	 */
	public String getCurrDir() {
		return currDir;
	}

	/**
	 * @param currDir
	 *            the currDir to set
	 */
	public void setCurrDir(String currDir) {
		this.currDir = currDir;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	public void setCurrDir(String currDir) {
		this.currDir = currDir;
	}
}
