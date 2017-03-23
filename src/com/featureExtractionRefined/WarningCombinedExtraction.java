package com.featureExtractionRefined;

import java.util.ArrayList;
import java.util.HashMap;

import com.comon.Constants;
import com.comon.StaticWarning;
import com.comon.StaticWarningInfo;
import com.featureExtractionInitial.WarningCharacFeatureExtraction;
import com.featureExtractionInitial.WarningCombinedFeatureExtraction;
import com.featureExtractionInitial.WarningHistoryFeatureExtraction;
import com.featureExtractionInitial.WarningParser;

public class WarningCombinedExtraction extends BasicFeatureExtraction{
	ArrayList<String> warningStatusList;
	ArrayList<String> warningStatusListInBugFix;
	ArrayList<String> warningStatusListInNoBugFix;
	HashMap<String, Double> statusPercentMap ;
	HashMap<String, Integer> warningNumberForMethod;
	StaticWarningInfo warningInfo;
	
	HashMap<String, HashMap<String, Double>> closeSuppressRatio; 	
	HashMap<String, HashMap<String, Double>> categoryCountMap ;	
	HashMap<String, HashMap<String, Double>> defectLikelihoodMap;
	HashMap<String, HashMap<String, Double>> defectLikelihoodCat;
	
	HashMap<String, Double> lifetimeCategory;
	
	public WarningCombinedExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void featureExtractionPrecondition( ) {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();
		
		WarningHistoryFeatureExtraction featureExtraction = new WarningHistoryFeatureExtraction();
		warningStatusList = featureExtraction.obtainWarningStatus(warningList, "all");
		warningStatusListInBugFix = featureExtraction.obtainWarningStatus(warningList, "bug fix");		
		warningStatusListInNoBugFix = featureExtraction.obtainWarningStatus( warningList, "non bug fix");
		System.out.println( warningStatusListInBugFix.toString() );
	
		WarningCombinedFeatureExtraction featureExtractionComb = new WarningCombinedFeatureExtraction();
		statusPercentMap = featureExtractionComb.obtainClosedSuppressedRatio(warningList, warningStatusList);
		
		WarningParser warnParser = new WarningParser();
		warningNumberForMethod = warnParser.obtainWarningNumberForMethod(warningList);
		
		closeSuppressRatio = featureExtractionComb.obtainClosedSuppressedRatioMetFPro(warningList, warningStatusList, projectInfo, warningNumberForMethod);
				
		warningInfo = warnParser.obtainWarningTypeCategoryInfo(warningList);
		
		WarningCharacFeatureExtraction featureExtractionChr = new WarningCharacFeatureExtraction();
		
		categoryCountMap = featureExtractionChr.obtainWarningAccordingCategory(warningList, warningStatusListInBugFix, warningStatusListInNoBugFix);
		
		HashMap<String, HashMap<String, Integer>> typeCountMap = featureExtractionChr.obtainWarningAccordingType(warningList, warningStatusList);
		
		HashMap<String, Integer> warningTypeCountMap =typeCountMap.get( "number");
		HashMap<String, Integer> closeTypeCountMap = typeCountMap.get ( "percent");
		defectLikelihoodMap = featureExtractionComb.obtainDefectLikelihoodType(closeTypeCountMap, warningTypeCountMap, warningInfo);
		
		HashMap<String, Double> defectLikelihoodType = defectLikelihoodMap.get("likelihood" );
		defectLikelihoodCat = featureExtractionComb.obtainDefectLikelihoodCategory(warningInfo, defectLikelihoodType, warningTypeCountMap);
	
		lifetimeCategory = featureExtractionComb.obtainLifetimeCategory(warningInfo, warningList);
	}
	
	
	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index ) {
		// TODO Auto-generated method stub
		super.extractFeatures(warning, index);
		
		WarningCombinedFeatureExtraction featureExtraction = new WarningCombinedFeatureExtraction();
		
		String type = warning.getBugInfo().getType();
		double F110 = featureExtraction.extractClosedSuppressedRatioType_F110(type, statusPercentMap);
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		String packageName = projectInfo.getFilePackageNameMap().get( fileName );
			
		HashMap<String, Double> warningRatioMapPackage = closeSuppressRatio.get( "package" );
		HashMap<String, Double> warningRatioMapFile = closeSuppressRatio.get( "file" );
		HashMap<String, Double> warningRatioMapMethod = closeSuppressRatio.get( "method" );
		
		double F114 = featureExtraction.extractClosedSuppressedRatioPackage_F114(packageName, warningRatioMapPackage);
		double F115 = featureExtraction.extractClosedSuppressedRatioFile_F115(fileName, warningRatioMapFile);
		double F116 = 0.0;
		for ( int i = 0; i < warning.getBugLocationList().size(); i++ ){
			String methodName = fileName + "-" + warning.getBugLocationList().get(i).getRelatedMethodName();
			F116 += featureExtraction.extractClosedSuppressedRatioMethod_F116(methodName, warningRatioMapMethod);
		}
		F116 = F116 / warning.getBugLocationList().size();
		
		HashMap<String, Double> warningCategoryCountBugFix = categoryCountMap.get( "bugfix");
		HashMap<String, Double> warningCategoryCountNoBugFix = categoryCountMap.get( "nobugfix");
		System.out.println( "==========================================\n" + categoryCountMap.toString() );
		String category = warning.getBugInfo().getCategory();
		double F121 = 0.0;
		if ( warningCategoryCountBugFix.containsKey( category ))
			F121 = warningCategoryCountBugFix.get( category );
		double F122 = 0.0;
		if ( warningCategoryCountNoBugFix.containsKey( category ) )
			F122 = warningCategoryCountNoBugFix.get( category );
		
		HashMap<String, Double> defectLikelihoodType = defectLikelihoodMap.get("likelihood" );
		HashMap<String, Double> defectLikelihoodPercentType = defectLikelihoodMap.get("variance" );
		
		double F117 = featureExtraction.extractDefectLikelihoodType_F117(type, defectLikelihoodType);
		double F118 = featureExtraction.extractDefectLikelihoodVarianceType_F118(type, defectLikelihoodPercentType);
		
		HashMap<String, Double> defectLikelihoodCategory = defectLikelihoodCat.get( "likelihood");
		HashMap<String, Double> defectLikelihoodDiscretCategory = defectLikelihoodCat.get( "variance");
		
		double F119 = featureExtraction.extractDefectLikelihoodCategory_F119(category, defectLikelihoodCategory);
		double F120 = featureExtraction.extractDefectLikelihoodVarianceCategory_F120(category, defectLikelihoodDiscretCategory);
		
		double F123 = featureExtraction.extractLifetimeCategory_F123(defectLikelihoodDiscretCategory, category);
		
		System.out.println( "F110: " + F110);
		System.out.println( "F114: " + F114);
		System.out.println( "F115: " + F115);
		System.out.println( "F116: " + F116);
		System.out.println( "F117: " + F117);
		System.out.println( "F118: " + F118);
		System.out.println( "F119: " + F119);
		System.out.println( "F120: " + F120);
		System.out.println( "F121: " + F121);
		System.out.println( "F122: " + F122);
		System.out.println( "F123: " + F123);
		
		HashMap<String, Object> featureValue = new HashMap<String, Object>();	
		featureValue.put( "F110", F110);
		featureValue.put( "F114", F114);
		featureValue.put( "F115", F115);
		featureValue.put( "F116", F116);
		featureValue.put( "F117", F117);
		featureValue.put( "F118", F118);
		featureValue.put( "F119", F119);
		featureValue.put( "F120", F120);
		featureValue.put( "F121", F121);
		featureValue.put( "F122", F122);
		featureValue.put( "F123", F123);
		
		return featureValue;
	}

	@Override
	public void generateFeatures() {
		// TODO Auto-generated method stub
		super.generateFeatures( );
	}

	@Override
	public String obtainOutputFileName() {
		// TODO Auto-generated method stub
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "warningCombine.csv";
		return fileName;
	}

	public static void main ( String args[] ){
		WarningCombinedExtraction featureExtraction = new WarningCombinedExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		featureExtraction.generateFeatures();
	}
}
