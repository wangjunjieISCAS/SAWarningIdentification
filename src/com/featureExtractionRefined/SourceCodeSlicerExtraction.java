package com.featureExtractionRefined;

import java.util.HashMap;

import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.SourceCodeFeatureExtraction;

public class SourceCodeSlicerExtraction extends BasicFeatureExtraction {

	public SourceCodeSlicerExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void featureExtractionPrecondition() {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();
	}

	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index ) {
		// TODO Auto-generated method stub		
		SourceCodeFeatureExtraction featureExtraction = new SourceCodeFeatureExtraction();
		
		HashMap<String, Object> F1_to_F19 = featureExtraction.extractCodeAnalysisFeature_F1_to_F19(warning);

		HashMap<String, Object> featureValue = new HashMap<String, Object>();
		featureValue.putAll( F1_to_F19  );
		
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
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "sourceCodeSlicer.csv";
		return fileName;
	}
	
	public static void main ( String args[] ){
		SourceCodeSlicerExtraction featureExtraction = new SourceCodeSlicerExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		
		featureExtraction.generateFeatures();
	}
}
