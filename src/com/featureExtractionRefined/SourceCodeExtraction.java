package com.featureExtractionRefined;

import java.util.HashMap;

import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.SourceCodeFeatureExtraction;

public class SourceCodeExtraction extends BasicFeatureExtraction{
	HashMap<String, HashMap<String, Integer>> callInfo;
	
	public SourceCodeExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void featureExtractionPrecondition() {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();
		
		SourceCodeFeatureExtraction featureExtraction = new SourceCodeFeatureExtraction();
		callInfo = featureExtraction.obtainMethodCallerCallee( warningList, projectInfo, Constants.FOLDER_NAME);
	}

	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index ) {
		// TODO Auto-generated method stub
		super.extractFeatures(warning, index);
		
		SourceCodeFeatureExtraction featureExtraction = new SourceCodeFeatureExtraction();
		//featureExtraction.obtainMethodCallerCallee(warningList, projectInfo, Constants.FOLDER_NAME );
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		String packageName = projectInfo.getFilePackageNameMap().get( fileName );
		
		HashMap<String, Object> F64_F66_F68 = null;	
		if ( featureOfPackage.containsKey( packageName ) && featureOfPackage.get( packageName).containsKey( "F64")){
			String[] keyArray = { "F64", "F66", "F68"};
			F64_F66_F68 = this.putSeveralEntrySets( featureOfPackage.get( packageName), keyArray);
			//System.out.println( "-----------------------------------------------------------------------");
		}
		else{
			F64_F66_F68 = featureExtraction.extractPackageStatistics_F64_F66_F68(warning, projectInfo);
			this.refineFeatureOfPackage(F64_F66_F68, packageName);
		}
		System.out.println ( "F64_F66_F68: " + F64_F66_F68.toString() );
			
		int F67 = featureExtraction.extractClassNumberFile_F67(fileName, projectInfo);
		System.out.println ( "F67: " + F67 );
		
		HashMap<String, Object> F101_to_F104 = featureExtraction.extractCodeStatistics_F101_to_F104(warning);
		System.out.println ( "F101_to_F104: " + F101_to_F104.toString() );
		
		HashMap<String, Object> F105_F34_F62_F65_F69 = featureExtraction.extractMethodStatistics_F105_F106_F34_F62_F65_F69(warning);
		System.out.println ( "F105_F34_F62_F65_F69: " + F105_F34_F62_F65_F69.toString() );
		
		HashMap<String, Object> F107_F108 = featureExtraction.extractMethodCall_F107_F108( warning, callInfo);
		
		HashMap<String, Object> featureValue = new HashMap<String, Object>();
		featureValue.putAll( F64_F66_F68 );
		featureValue.put( "F67", F67 );
		featureValue.putAll( F101_to_F104 );
		featureValue.putAll( F105_F34_F62_F65_F69 );
		featureValue.putAll( F107_F108);
		
		return featureValue;
	}

	@Override
	public void generateFeatures() {
		// TODO Auto-generated method stub
		super.generateFeatures();
	}

	
	@Override
	public String obtainOutputFileName() {
		// TODO Auto-generated method stub
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "sourceCode.csv";
		return fileName;
	}

	public static void main ( String args[] ){
		SourceCodeExtraction featureExtraction = new SourceCodeExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		
		featureExtraction.generateFeatures();
	}
	
}
