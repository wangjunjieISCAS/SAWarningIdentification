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
import java.util.Set;

import com.comon.Constants;
import com.comon.ProjectInfo;
import com.comon.StaticWarning;
import com.featureExtractionInitial.ProjectExplore;
import com.featureExtractionInitial.WarningParser;


/*
 * 基类含有抽取所有feature都可能会用到的信息，其他类只含有抽取特定feature用到的信息
 * 使需要读到内存中的信息变少
 */

public class BasicFeatureExtraction {
	ArrayList<StaticWarning> warningList;
	ProjectInfo projectInfo;	
	
	HashMap<String, HashMap<String, Object>> featureOfPackage;    
	//由于一个package包含多个file，如果不进行存储，对于每个file，都需要将package计算一遍
	//HashMap<PackageName, HashMap<FeatureName, value>>
	HashMap<String, Object> featureOfProject;  

	public BasicFeatureExtraction(String fileName, String folderName) {
		// TODO Auto-generated constructor stub
		warningList = new ArrayList<StaticWarning>();
		projectInfo = new ProjectInfo ();	
		
		WarningParser warningParser = new WarningParser();
		warningList = warningParser.parseFingbugsWarnings ( fileName );
		warningList = warningParser.refineWarningInfoListStyle( warningList );
		
		warningList = warningParser.obtainCodeInfo(warningList, folderName);
		this.writeToFileWarning(warningList, Constants.FEATURE_VALUE_OUTPUT_FOLDER + "warningInfoOriginal.csv" );
		
		warningList = warningParser.refineWarningWithoutCode(warningList);
		this.writeToFileWarning(warningList, Constants.FEATURE_VALUE_OUTPUT_FOLDER + "warningInfo.csv" );		
	
		ProjectExplore projectExplore = new ProjectExplore();
		projectInfo = projectExplore.obtainPackageClassInfo(fileName);
		
		featureOfPackage = new HashMap<String, HashMap<String, Object>>();
		featureOfProject = new HashMap<String, Object>();
	}
	
	public void featureExtractionPrecondition ( ){
		
	}
	
	public HashMap<String, Object> extractFeatures ( StaticWarning warning, int index ){
		
		return null;
	}
	
	public void generateFeatures ( ){
		this.featureExtractionPrecondition();
		
		//for ( int i =0; i < warningList.size() ; i++ ){
		System.out.println( "================================================= warningList size: " +  warningList.size() );
		for ( int i = 0; i < warningList.size() ; i++ ){
			StaticWarning warning = warningList.get( i );
			System.out.println( "----------------------------------" + i + " " + warning.getBugLocationList().get(0).getClassName() );
			
			ArrayList<String> methodNameList = new ArrayList<String>();
			for ( int j = 0; j < warning.getBugLocationList().size(); j++ ){
				methodNameList.add( warning.getBugLocationList().get(j).getRelatedMethodName() );
			}
			System.out.println( methodNameList.toString() );
			
			HashMap<String, Object> featureValue = this.extractFeatures( warning, i );
			this.writeToFile(featureValue, i);
		}
	}
	
	public HashMap<String, Object> putSeveralEntrySets ( HashMap<String, Object> sourceMap, String[] keyArray){
		HashMap<String, Object> targetMap = new HashMap<String, Object>();
		
		for ( int i =0; i < keyArray.length; i++ ){
			String key = keyArray[i];
			targetMap.put( key, sourceMap.get( key ));
		}
		
		return targetMap;
	}
	
	public void refineFeatureOfPackage ( HashMap<String, Object> newEntrySet, String packageName ){
		HashMap<String, Object> valuePackage = null;
		if ( featureOfPackage.containsKey( packageName ) ){
			valuePackage = featureOfPackage.get( packageName);
		}
		else{
			valuePackage = new HashMap<String, Object>();
		}
		valuePackage.putAll( newEntrySet );
		featureOfPackage.put( packageName , valuePackage );
	}
	
	public void writeToFile ( HashMap<String, Object> featureValue, int index ){
		String fileName = this.obtainOutputFileName();
		
		try {	
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( fileName ), true) , "GB2312"), 1024);
			
			if ( index == 0 ){
				for ( String key : featureValue.keySet() )
					output.write( key + ",");
				output.newLine();
			}
			
			for ( String key: featureValue.keySet() ){
				output.write( featureValue.get(key).toString() + "," );
			}
			output.newLine();
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
	
	public void writeToFileWarning ( ArrayList<StaticWarning> warningList, String fileName ){
		try {	
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( fileName )) , "GB2312"), 1024);
			for ( int i = 0; i < warningList.size() ; i++ ){
				StaticWarning warning = warningList.get( i );
			
				output.write( warning.getBugInfo().getCategory() + ",");
				output.write( warning.getBugInfo().getType() + ",");
				output.write( warning.getBugLocationList().get(0).getClassName() + ",");
				
				for ( int j =0; j < warning.getBugLocationList().size(); j++ ){
					output.write( warning.getBugLocationList().get(j).getRelatedMethodName() + ",");
					output.write( warning.getBugLocationList().get(j).getStartLine() + "-" + warning.getBugLocationList().get(j).getEndLine() +",");
					
					output.write( warning.getBugLocationList().get(j).getCodeInfoList().toString() + "," + ",");
				}
				output.newLine();
			}
			
			//output.write( warningList.size() + "," );
			//output.newLine();
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
	
	//不同的featureExtraction，输出到不同的文件中
	public String obtainOutputFileName ( ){
		return null;
	}

	public ArrayList<StaticWarning> getWarningList() {
		return warningList;
	}

	public void setWarningList(ArrayList<StaticWarning> warningList) {
		this.warningList = warningList;
	}
}
