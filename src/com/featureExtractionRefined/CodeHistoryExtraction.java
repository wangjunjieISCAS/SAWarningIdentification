package com.featureExtractionRefined;

import java.util.ArrayList;
import java.util.HashMap;

import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.CodeHistoryFeatureExtraction;
import com.featureExtractionInitial.WarningHistoryFeatureExtraction;

public class CodeHistoryExtraction extends BasicFeatureExtraction{
	
	HashMap<Integer, Integer> openRevisionNumberList = new HashMap<Integer, Integer>();
	HashMap<String, HashMap<String, Integer>> fileStalenessList = new HashMap<String, HashMap<String, Integer>>();
	HashMap<String, HashMap<String, Integer>> packageStalenessList = new HashMap<String, HashMap<String, Integer>>();
	HashMap<String, Object> projectStalenessList = new HashMap<String, Object>();
	
	public CodeHistoryExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void featureExtractionPrecondition() {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();
		
		WarningHistoryFeatureExtraction featureExtractionHis = new WarningHistoryFeatureExtraction();
		openRevisionNumberList = featureExtractionHis.obtainAlertOpenRevisionNumberForAllFiles(warningList);
		
		CodeHistoryFeatureExtraction featureExtractionCodeHis = new CodeHistoryFeatureExtraction();
		fileStalenessList = featureExtractionCodeHis.obtainFileStalenessForAllFiles (warningList);
		packageStalenessList = featureExtractionCodeHis.obtainPackageStalenessForAllPackages(projectInfo );
		projectStalenessList = featureExtractionCodeHis.obtainProjectStalenessForProject( );
	}
	
	
	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index ) {
		// TODO Auto-generated method stub
		super.extractFeatures(warning, index);
		
		CodeHistoryFeatureExtraction featureExtraction = new CodeHistoryFeatureExtraction();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		
		int openRevisionNumber = openRevisionNumberList.get( index );
		int currentRevisionNumber = Constants.CURRENT_REVISION_NUMBER;
		
		ArrayList<String> F71 = featureExtraction.extractDeveloper_F71(openRevisionNumber, currentRevisionNumber, fileName);
		int F73 = featureExtraction.extractFileDeletionRevison_F73(fileName);
		
		HashMap<String, Integer>  F83_F74 = featureExtraction.extractFileStaleness_F83_F74(fileName, fileStalenessList );
		
		
		String packageName = projectInfo.getFilePackageNameMap().get( fileName );
		HashMap<String, Integer>  F84_F146 = featureExtraction.extractPackageStaleness_F84_F146(packageName, packageStalenessList );
		
		HashMap<String, Object>  F85_F147 = featureExtraction.extractProjectStaleness_F85_F147(projectStalenessList);
		
		
		String F71_combined = "";
		for ( int i =0; i < F71.size(); i++){
			if ( i == 0 ){
				F71_combined += F71.get( i );
			}else{
				F71_combined += "+==+" + F71.get( i );
			}				
		}
			
		System.out.println( "F71: " + F71_combined );
		System.out.println( "F73: " + F73 );
		System.out.println( "F83_F74: " + F83_F74 );
		System.out.println( "F84_F146: " + F84_F146 );
		System.out.println( "F85_F147: " + F85_F147 );
		
		HashMap<String, Object> featureValue = new HashMap<String, Object>();
		featureValue.put( "F71", F71_combined );
		featureValue.put("F73",  F73 );
		featureValue.putAll( F83_F74 );
		featureValue.putAll( F84_F146 );
		featureValue.putAll( F85_F147 );
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
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "codeHistory.csv";
		return fileName;
	}

	public static void main ( String args[] ){
		CodeHistoryExtraction featureExtraction = new CodeHistoryExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		featureExtraction.generateFeatures();
	}
}
