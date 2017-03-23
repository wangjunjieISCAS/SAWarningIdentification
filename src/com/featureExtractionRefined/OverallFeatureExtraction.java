package com.featureExtractionRefined;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.WarningHistoryFeatureExtraction;

public class OverallFeatureExtraction {
	
	public void extractAllFeatures ( String fileName, String folderName ){	
		CodeChurnExtraction codeChurn = new CodeChurnExtraction( fileName, folderName );
		codeChurn.generateFeatures();
		
		CodeHistoryExtraction codeHistory = new CodeHistoryExtraction (fileName, folderName );
		codeHistory.generateFeatures();
		
		FileRelatedExtraction fileCharacter = new FileRelatedExtraction(fileName, folderName );
		fileCharacter.generateFeatures();
		
		SourceCodeExtraction sourceCode = new SourceCodeExtraction( fileName, folderName );
		sourceCode.generateFeatures();
		
		SourceCodeSlicerExtraction codeSlicer = new SourceCodeSlicerExtraction ( fileName, folderName );
		codeSlicer.generateFeatures();
		
		WarningCharacteristicsExtraction warningCharacter = new WarningCharacteristicsExtraction ( fileName, folderName );
		warningCharacter.generateFeatures();
		
		WarningCombinedExtraction warningCombine = new WarningCombinedExtraction ( fileName, folderName );
		warningCombine.generateFeatures();
		
		WarningHistoryExtraction warningHistory = new WarningHistoryExtraction( fileName, folderName );
		warningHistory.generateFeatures();
	}
	
	public void obtainWarningGroundtruth ( String fileName, String folderName ){
		WarningHistoryFeatureExtraction featureExtraction = new WarningHistoryFeatureExtraction();
		BasicFeatureExtraction basicExtraction = new BasicFeatureExtraction ( fileName, folderName );
		
		ArrayList<StaticWarning> warningList = basicExtraction.getWarningList();
		
		ArrayList<String> statusBug = featureExtraction.obtainWarningStatus(warningList, "bug fix");
		//ArrayList<String> staticNoBug = featureExtraction.obtainWarningStatus(warningList, "non bug fix");
		ArrayList<String> statusAll =  featureExtraction.obtainWarningStatus(warningList, "all");
		//System.out.println( statusAll.size() + "=======================================================");
		this.writeToFile( statusBug, Constants.FEATURE_VALUE_OUTPUT_FOLDER + "labelBug.csv", "bug");
		this.writeToFile( statusAll, Constants.FEATURE_VALUE_OUTPUT_FOLDER + "labelAll.csv", "all");
	}
	
	
	public void writeToFile ( ArrayList<String> status, String fileName, String type){
		try {	
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( fileName )) , "GB2312"), 1024);
			
			for ( int i =0; i < status.size(); i++ ){
				String temp = status.get(i);
				output.write( temp + "," );
				/*
				if ( type.contains( "bug")){
					output.write( temp + ",");
				}else{
					output.write( "," + temp + ",");
				}
				*/
				output.newLine();
			}
			output.flush();
			output.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main ( String args[] ){
		OverallFeatureExtraction featureExtraction = new OverallFeatureExtraction (  );
		featureExtraction.extractAllFeatures(Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME);
		//featureExtraction.obtainWarningGroundtruth( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
	}
}
