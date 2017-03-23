package com.featureExtractionRefined;

import java.util.HashMap;
import java.util.Map;

import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.FileFeatureExtraction;

public class FileRelatedExtraction extends BasicFeatureExtraction{

	public FileRelatedExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}
		
	
	@Override
	public void featureExtractionPrecondition() {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();
	}


	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index) {
		// TODO Auto-generated method stub
		super.extractFeatures(warning, index);
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		
		FileFeatureExtraction featureExtraction = new FileFeatureExtraction();
		Map<String, Object> F25_F72 = featureExtraction.extractFileAge_F25_F72(fileName);
		System.out.println ( "F25_F72: " + F25_F72.toString() );
		
		boolean F26 = featureExtraction.extractFileExtension_F26(fileName);
		System.out.println ( "isJavaFile: " + F26 );
		
		HashMap<String, String> F53_to_F55 = featureExtraction.extractFilePackageProjectName_F53_to_F55(projectInfo, fileName);
		System.out.println ( "F53_to_F55: " + F53_to_F55.toString() );
		
		HashMap<String, Object> featureValue = new HashMap<String, Object>();
		featureValue.put( "F26", F26 );
		featureValue.putAll( F25_F72 );
		featureValue.putAll( F53_to_F55 );
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
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "fileCharacteris.csv";
		return fileName;
	}


	public static void main ( String args[] ){
		FileRelatedExtraction featureExtraction = new FileRelatedExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		featureExtraction.generateFeatures();
	}
}
