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
				}
			}
		}else {
			System.out.println("Parameters are not in correct format");
			throw new Exception();
		}
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

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int getStartClient() {
		return startClient;
	}

	public void setStartClient(int startClient) {
		this.startClient = startClient;
	}

	public int getTabuTenure() {
		return tabuTenure;
	}

	public void setTabuTenure(int tabuTenure) {
		this.tabuTenure = tabuTenure;
	}

	public boolean isVariableTenure() {
		return variableTenure;
	}

	public void setVariableTenure(boolean variableTenure) {
		this.variableTenure = variableTenure;
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
