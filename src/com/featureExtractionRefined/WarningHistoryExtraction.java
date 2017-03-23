package com.featureExtractionRefined;

import java.util.ArrayList;
import java.util.HashMap;

import com.comon.BugLocation;
import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.WarningCharacFeatureExtraction;
import com.featureExtractionInitial.WarningHistoryFeatureExtraction;
import com.featureExtractionInitial.WarningParser;

public class WarningHistoryExtraction extends BasicFeatureExtraction{
	
	HashMap<String, HashMap<Integer, Object>> openRevisionFilesList = new HashMap<String, HashMap<Integer, Object>> ();
	
	public WarningHistoryExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void featureExtractionPrecondition() {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();	
		
		WarningHistoryFeatureExtraction featureExtraction = new WarningHistoryFeatureExtraction();
		openRevisionFilesList = featureExtraction.obtainAlertOpenRevisionForAllFiles ( warningList );
	}

	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index ) {
		// TODO Auto-generated method stub
		super.extractFeatures(warning, index);
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		
		WarningHistoryFeatureExtraction featureExtractionHis = new WarningHistoryFeatureExtraction();
		int F61 = featureExtractionHis.extractAlertModification_F61(warning, index, openRevisionFilesList );
		
		ArrayList<String> codeInfo = new ArrayList<String>();
		for ( int i = 0; i < warning.getBugLocationList().size(); i++ ){
			ArrayList<String> temp = warning.getBugLocationList().get(i).getCodeInfoList();
			codeInfo.addAll( temp );
		}		
		
		int F70 = featureExtractionHis.extractAlertOpenRevisionTime_F70(index, openRevisionFilesList );
		
		int F77 = featureExtractionHis.extractAlertLifeRevision_F77( index, openRevisionFilesList );
		
		int F88 = featureExtractionHis.extractAlertLifeTime_F88( index, openRevisionFilesList );
		
		System.out.println( "F61: " + F61);
		System.out.println( "F70: " + F70 );
		System.out.println( "F77: " + F77);
		System.out.println( "F88: " + F88);
		
		HashMap<String, Object> featureValue = new HashMap<String, Object>();	
		featureValue.put( "F61", F61);
		featureValue.put( "F70", F70);
		featureValue.put( "F77", F77);
		featureValue.put( "F88", F88);
		
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
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "warningHistory.csv";
		return fileName;
	}

	public static void main ( String args[] ){
		WarningHistoryExtraction featureExtraction = new WarningHistoryExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		featureExtraction.generateFeatures();
	}
}
