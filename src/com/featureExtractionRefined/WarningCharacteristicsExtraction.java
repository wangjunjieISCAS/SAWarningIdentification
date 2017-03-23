package com.featureExtractionRefined;

import java.util.ArrayList;
import java.util.HashMap;

import com.comon.BugLocation;
import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.WarningCharacFeatureExtraction;
import com.featureExtractionInitial.WarningHistoryFeatureExtraction;
import com.featureExtractionInitial.WarningParser;

public class WarningCharacteristicsExtraction extends BasicFeatureExtraction{
	HashMap<String, Integer> warningNumberForMethod;
	HashMap<String, Double> warningTypePercentMap;
	
	public WarningCharacteristicsExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void featureExtractionPrecondition() {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();
		
		WarningHistoryFeatureExtraction featureExtraction = new WarningHistoryFeatureExtraction();
		ArrayList<String> warningStatusList = featureExtraction.obtainWarningStatus(warningList, "all");
		
		WarningCharacFeatureExtraction featureExtractionChr = new WarningCharacFeatureExtraction();
		HashMap<String, HashMap<String, Integer>> typeCountMap = featureExtractionChr.obtainWarningAccordingType(warningList, warningStatusList);
		HashMap<String, Integer> warningTypeCountMap =typeCountMap.get( "number");
		HashMap<String, Integer> closeTypeCountMap = typeCountMap.get ( "percent");
		
		warningTypePercentMap = featureExtractionChr.obtainWarningPercentAccordingType(warningTypeCountMap, warningList );
	
		WarningParser warnParser = new WarningParser();
		warningNumberForMethod = warnParser.obtainWarningNumberForMethod(warningList);
	}

	
	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index ) {
		// TODO Auto-generated method stub
		super.extractFeatures(warning, index);
		
		WarningCharacFeatureExtraction featureExtraction = new WarningCharacFeatureExtraction();
		HashMap<String, Object> F20_to_F23 = featureExtraction.extractWarningInfo_F20_to_F23(warning);
		
		//Integer F82 = featureExtraction.extractWarningCountProject_F82(projectInfo);
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		HashMap<String, Object> F95_F112 = featureExtraction.extractWarningCountFile_F95_F112(fileName, projectInfo);
		
		String packageName = projectInfo.getFilePackageNameMap().get( fileName );
		HashMap<String, Object> F94_F111 = null;
		if ( featureOfPackage.containsKey( packageName ) && featureOfPackage.get( packageName).containsKey( "F94")){
			String[] keyArray = { "F94", "F111"};			
			F94_F111 = this.putSeveralEntrySets( featureOfPackage.get( packageName) , keyArray);
		}
		else{
			F94_F111 = featureExtraction.extractWarningCountPackage_F94_F111(packageName, projectInfo);
			this.refineFeatureOfPackage(F94_F111, packageName);
		}		
		
		HashMap<String, Object> F79_F113 = new HashMap<String, Object>();		
		int totalF79 = 0;
		double totalF113 = 0.0;
		for ( int i = 0; i < warning.getBugLocationList().size(); i++ ){
			BugLocation bugLoc = warning.getBugLocationList().get( i );
			String methodName  = bugLoc.getRelatedMethodName();
			HashMap<String, Object> result = featureExtraction.extractWarningPercentMethod_F79_F113(projectInfo, fileName, methodName, warningNumberForMethod);
			
			totalF79 += (Integer)result.get( "F79");
			totalF113 += (Double) result.get( "F113");
			System.out.println( "totalF113 " + totalF113);
		}
		totalF79 = totalF79 / warning.getBugLocationList().size();
		totalF113 = totalF113 / warning.getBugLocationList().size();
		F79_F113.put( "F79", totalF79);
		F79_F113.put( "F113", totalF113);
		
		String type = warning.getBugInfo().getType();
		double F109 = featureExtraction.extractWarningPercentType_F109(type, warningTypePercentMap);
		
		System.out.println("F20_to_F23: "+ F20_to_F23 );
		//System.out.println("F82: "+ F82 );
		System.out.println("F95_F112: "+ F95_F112 );
		System.out.println("F94_F111: "+ F94_F111 );
		System.out.println("F113: "+ F79_F113 );
		System.out.println("F109: "+ F109 );
		
		HashMap<String, Object> featureValue = new HashMap<String, Object>();
		featureValue.putAll( F20_to_F23 );
		featureValue.putAll( F95_F112 );
		featureValue.putAll( F94_F111 );
		featureValue.putAll( F79_F113 );
		featureValue.put( "F109", F109);
		
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
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "warningCharacteris.csv";
		return fileName;
	}


	public static void main ( String args[] ){
		WarningCharacteristicsExtraction featureExtraction = new WarningCharacteristicsExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		featureExtraction.generateFeatures();
	}
}
