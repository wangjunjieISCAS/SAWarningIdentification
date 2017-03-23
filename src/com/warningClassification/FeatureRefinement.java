package com.warningClassification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.comon.Constants;

/*
 * wrapperSearchGreedyStepwiseFeatureSelection 
 */
public class FeatureRefinement {

	public FeatureRefinement() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void refineFeatures ( String inFileName, String outFileName, boolean isNeedMerge ){		
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( inFileName )));
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( outFileName )) , "GB2312"), 1024);
			
			LinkedHashMap<String, ArrayList<Object>> inFeatureValue = new LinkedHashMap<String, ArrayList<Object>>();
			HashMap<Integer, String> featureNameMap = new HashMap<Integer, String>();
			
			String line = "";
			int row = 0;
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
				if ( row ==0 ){
					for ( int i =0; i < temp.length; i++ ){
						ArrayList<Object> values = new ArrayList<Object>();
						inFeatureValue.put( temp[i].trim(), values );
						featureNameMap.put( i , temp[i]);
					}
					
				}
				else{
					for ( int i =0; i < temp.length; i++ ){
						String featureName = featureNameMap.get( i);
						ArrayList<Object> values = inFeatureValue.get( featureName);
						if ( temp[i] != null && !temp[i].trim().equals( "")){
							//System.out.println( temp[i] + " " + featureName );
							values.add( temp[i]);
						}
						else{
							values.add( new String("") );
						}
						
						inFeatureValue.put( featureName, values);
					}
				}
				row++;
			}
			br.close();
			
			FeatureDiscretization featureDiscret = new FeatureDiscretization();
			if ( isNeedMerge == true ){
				inFeatureValue = featureDiscret.mergeFeatures(inFeatureValue);
			}
			
			LinkedHashMap<String, ArrayList<Object>> newFeatureValue = new LinkedHashMap<String, ArrayList<Object>>();
			for ( String featureName: inFeatureValue.keySet() ){
				ArrayList<Object> values = inFeatureValue.get( featureName );
				
				HashMap<String, ArrayList<Object>> newValues = new HashMap<String, ArrayList<Object>>();
				if ( featureName.equals( "F71") || isNeedMerge == true ){
					System.out.println( "DEVELOPER and isSourceCode & featureName: " + featureName );
            		newValues = this.reOrganizeMergedFeatures( featureName, values);
            		newFeatureValue.putAll( newValues );
				}
				else{
					newFeatureValue.put( featureName, values );
				}
			}
		
			for ( String featureName: newFeatureValue.keySet() ) {
				output.write( featureName + ",");
			}
			output.newLine();
			
			int rowSize = newFeatureValue.entrySet().iterator().next().getValue().size();
			System.out.println( rowSize );
			for ( int i =0; i < rowSize; i++ ){
				for ( String featureName: newFeatureValue.keySet() ){
					//System.out.println( featureName + " " + newFeatureValue.get( featureName).size());
					String value = (String) newFeatureValue.get( featureName).get( i );
					output.write( value + ",");
				}
				output.newLine();
			}
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	// 用这种分隔符进行分割 +==+
	public HashMap<String, ArrayList<Object>> reOrganizeMergedFeatures ( String featureName,  ArrayList<Object> featureValue ){
		HashMap<String, Integer> newFeatureMap = new HashMap<String, Integer>();
		for ( int i =0; i < featureValue.size(); i++ ){
			//System.out.println( featureValue );
			String[] features = featureValue.get(i).toString().split( "\\+==\\+");
			for ( int j = 0; j < features.length; j++){
				if ( features[j] != null && !features[j].trim().equals( "")){
					int count = 1;
					if ( newFeatureMap.containsKey( features[j]))
						count += newFeatureMap.get( features[j] );
					newFeatureMap.put( features[j], count );
				}
			}				
		}
		
		List<HashMap.Entry<String, Integer>> newFeatureMapList = new ArrayList<HashMap.Entry<String, Integer>>(newFeatureMap.entrySet());
		Collections.sort( newFeatureMapList, new Comparator<HashMap.Entry<String, Integer>>() {   
			public int compare(HashMap.Entry<String, Integer> o1, HashMap.Entry<String, Integer> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return o2.getValue().compareTo(o1.getValue() ) ;
		    }
			}); 
		
		HashMap<String, ArrayList<Object>> newFeatureValue = new HashMap<String, ArrayList<Object>>();    //表示的是一纵列
		
		for ( int i =0; i< newFeatureMapList.size() && i < Constants.FEATURE_SPLIT_SIZE; i++ ){
			HashMap.Entry<String, Integer> entry = newFeatureMapList.get( i );
			String newFeatureName = entry.getKey();
			//if ( newFeatureName.equals( "NA"))
			//	continue;
			
			ArrayList<Object> newFeatureValueList = new ArrayList<Object>();
			for ( int j =0; j < featureValue.size(); j++ ){
				String[] features = featureValue.get(j).toString().split( "\\+==\\+");
				List featureList = Arrays.asList( features );
				if ( featureList.contains( newFeatureName )){
					newFeatureValueList.add( "1" );
				}else{
					newFeatureValueList.add( "0" );
				}
			}
			newFeatureValue.put( featureName+ "-" + newFeatureName, newFeatureValueList );
		}
		return newFeatureValue;
	}
	
	public void generateRefinedFeatures ( ){
		String foldName = "data/feature/";
		String[] inFileNameList = { "fileCharacteris.csv", "sourceCode.csv", "sourceCodeSlicer.csv", "warningCharacteris.csv", "warningCombine.csv", "warningHistory.csv",
				"codeChurn.csv", "codeHistory.csv"};
		
		for ( int i = 0; i < inFileNameList.length; i++ ){
			String fileName = inFileNameList[i];
			if ( fileName.contains( "sourceCodeSlicer") || fileName.contains( "codeHistory" )) {
				boolean isNeedMerge = false;
				if ( fileName.contains( "sourceCodeSlicer" ))
					isNeedMerge = true;
				
				String inFileName = foldName + fileName;
				String outFileName = foldName + "out-" + fileName;
				this.refineFeatures(  inFileName, outFileName, isNeedMerge );
			}
		}
		
		//将代码文件合并为1个，并加上labal；整理成weka需要的格式
		LinkedHashMap<String, ArrayList<Object>> mergedFeatures = new LinkedHashMap<String, ArrayList<Object>>();
		HashMap<Integer, String> featureNameMap = new HashMap<Integer, String>();
		
		for ( int i =0; i < inFileNameList.length; i++ ){
			String fileName = inFileNameList[i];
			String inFileName = foldName + fileName ;
			if ( fileName.contains( "sourceCodeSlicer") || fileName.contains( "codeHistory" ))
				inFileName = foldName + "out-" + fileName;			
			
			try {
				BufferedReader br = new BufferedReader(new FileReader( new File ( inFileName )));
				String line = "";
				int row = 0;
				while ( ( line = br.readLine() ) != null ) {
					String[] temp = line.split( ",");
					if ( row == 0 ){
						for ( int j =0; j < temp.length; j++ ){
							ArrayList<Object> values = new ArrayList<Object>();
							if ( !com.comon.Constants.UNUSED_FEATURES_LIST.contains( temp[j] )){
								mergedFeatures.put( temp[j], values );
								featureNameMap.put( i*1000+j, temp[j]);
							}							
						}						
					}
					else{
						for ( int j =0; j < temp.length; j++ ){
							String featureName = featureNameMap.get( i*1000+j );
							//System.out.println ( featureName );
							if ( featureName != null ){
								//!com.comon.Constants.UNUSED_FEATURES_LIST.contains( featureName )
								ArrayList<Object> values = mergedFeatures.get( featureName );
								if ( temp[j] != null && !temp[j].trim().equals( "")){
									values.add( temp[j] );
								}
								else
									values.add (" ");
									
								mergedFeatures.put( featureName, values);
							}
						}
					}
					row++;
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		System.out.println( "final number of features: " + mergedFeatures.size() );
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/totalFeatures.csv" )) , "GB2312"), 1024);
			BufferedReader br = new BufferedReader(new FileReader( new File ( "data/feature/labelAll.csv" )));
			
			HashMap<Integer, String> categoryMap = new HashMap<Integer, String>();
			String line = "";
			int index = 0;
			while ( ( line = br.readLine() ) != null ) {
				String category = line.trim();
				categoryMap.put( index, category );
				index++;
			}
			
			for ( String featureName: mergedFeatures.keySet() ){
				featureName = featureName.replaceAll( ",", " ");
				featureName = featureName.replaceAll( "\"", " ");
				featureName = featureName.replaceAll( "'", " ");
				output.write( featureName + ",");
			}
			output.write( "category" );
			
			int rowSize = mergedFeatures.entrySet().iterator().next().getValue().size();
			for ( int i = 0; i < rowSize; i++ ){
				String value = categoryMap.get( i);
				if ( value == null || value.trim().equals( ""))
					continue;
				value = value.replaceAll( ",", "");
				value = value.trim();
				if ( value.equals( "deleted")){
					continue;
				}
				
				output.newLine();
				for ( String featureName: mergedFeatures.keySet() ){
					String featureValue = (String) mergedFeatures.get( featureName).get( i );
					output.write(  featureValue + ",");
				}
				
				//System.out.println( "|"+ value + "|" );
				output.write( value  );
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
	
	public static void main ( String args[] ){
		FeatureRefinement refine = new FeatureRefinement();
		refine.generateRefinedFeatures(); 
	}
}
