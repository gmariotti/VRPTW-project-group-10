package com.vrptw;

import com.tabusearch.MovesType;

public class Parameters {
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
		// TODO
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

	public Object getMovesType() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
