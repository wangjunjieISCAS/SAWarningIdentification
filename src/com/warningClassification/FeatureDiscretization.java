package com.warningClassification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.comon.Constants;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/*
 * 为了进行,informationGainFeatureSelection,需要将全部的feature进行离散化
 * 如果是wrapperSearchGreedyStepwiseFeatureSelection,不需要进行离散化
 */
public class FeatureDiscretization {
	public HashMap<String, ArrayList<Boolean>> featureDiscretNumerical ( String featureName, ArrayList<Object> featureValue, int splitNum  ){
		double minorAdjust = 0.001;    //由于double里面有些时候小数点后面的后面会不一样
		
		HashSet<Double> newFeatureSet = new HashSet<Double>();
		for ( int i =0; i < featureValue.size(); i++ ){
			Double feature = Double.parseDouble( featureValue.get( i ).toString() );
			newFeatureSet.add( feature );
		}
		
		ArrayList<Double> newFeatureSetList = new ArrayList<Double>( newFeatureSet );
		Collections.sort( newFeatureSetList );
		
		if ( newFeatureSet.size() < splitNum )
			splitNum = newFeatureSet.size();
		
		Double[] splitValue = new Double[splitNum+1];
		int interval = newFeatureSet.size() / splitNum;
		//System.out.println( newFeatureSet.size() + " "+ interval );
		int index = 0;
		for ( int i = 0; i < splitNum+1; i ++ ){
			//完全整除的情况
			if ( index >= newFeatureSet.size() )
				index = newFeatureSet.size() -1;
			splitValue[i] = newFeatureSetList.get( index );
			index += interval;
		}
		
		splitValue[splitNum] =  newFeatureSetList.get( newFeatureSet.size() -1 ) + 1 ;
		//便于后面确定区间时统一
		
		HashMap<String, ArrayList<Boolean>> newFeatureValue = new HashMap<String, ArrayList<Boolean>>();    //表示的是一纵列
		for ( int i = 1; i < splitValue.length; i++ ){
			ArrayList<Boolean> newFeatureValueList = new ArrayList<Boolean>();
			for ( int j = 0; j < featureValue.size(); j++ ){
				Double value = Double.parseDouble( featureValue.get( j).toString() );
				if ( value >= splitValue[i-1] - minorAdjust && value < splitValue[i] - minorAdjust ){
					newFeatureValueList.add( true );
				}
				else{
					newFeatureValueList.add( false );
				}	
			}
			
			String newFeatureName = featureName + "-" + splitValue[i].toString();
			newFeatureValue.put( newFeatureName, newFeatureValueList );
		}
		return newFeatureValue;
	}
	
	
	public HashMap<String, ArrayList<Boolean>> featureDiscretCategorial ( String featureName, ArrayList<Object> featureValue ){
		HashSet<String> newFeatureSet = new HashSet<String>();
		for ( int i =0; i < featureValue.size(); i++ ){
			String feature = featureValue.get( i ).toString();
			newFeatureSet.add( feature );
		}
		
		System.out.println( "categorial feature size: " + newFeatureSet.size() );
		
		HashMap<String, ArrayList<Boolean>> newFeatureValue = new HashMap<String, ArrayList<Boolean>>();    //表示的是一纵列
		for ( String newFeatureName: newFeatureSet ){
			ArrayList<Boolean> newFeatureValueList = new ArrayList<Boolean>();
			for ( int i = 0; i < featureValue.size(); i++ ){
				if ( featureValue.get( i ).equals( newFeatureName )){
					newFeatureValueList.add( true );
				}else{
					newFeatureValueList.add( false );
				}
			}
			newFeatureValue.put( featureName + "-" + newFeatureName, newFeatureValueList );
		}
		
		return newFeatureValue;
	}
	
	//developer需要单独处理，目前是这样存储的hossman@apache.org+==+rmuir@apache.org+==+chrism@apache.org，
	//需要对其进行切分，然后
	public HashMap<String, ArrayList<Boolean>> featureDiscretMergedFeatures ( String featureName, ArrayList<Object> featureValue ){
		HashSet<String> newFeatureSet = new HashSet<String>();
		for ( int i =0; i < featureValue.size(); i++ ){
			//System.out.println( featureValue );
			String[] features = featureValue.get(i).toString().split( "\\+==\\+");
			for ( int j = 0; j < features.length; j++){
				if ( features[j] != null && !features[j].trim().equals( ""))
					newFeatureSet.add( features[j] );
			}				
		}
		
		System.out.println( "categorial feature size: " + newFeatureSet.size() );
		
		HashMap<String, ArrayList<Boolean>> newFeatureValue = new HashMap<String, ArrayList<Boolean>>();    //表示的是一纵列
		for ( String newFeatureName: newFeatureSet ){
			if ( newFeatureName.contains( "handyande@apache.org") && featureName.contains( "F71")){
				System.out.println ( "test");
			}
			ArrayList<Boolean> newFeatureValueList = new ArrayList<Boolean>();
			for ( int i = 0; i < featureValue.size(); i++ ){
				String[] features = featureValue.get(i).toString().split( "\\+==\\+");
				List featureList = Arrays.asList( features );
				if ( featureList.contains( newFeatureName )){
					newFeatureValueList.add( true );
				}else{
					newFeatureValueList.add( false );
				}
			}
			newFeatureValue.put( featureName + "-" + newFeatureName, newFeatureValueList );
		}
		
		return newFeatureValue;
	}
	
	//每个feature均为F1-0，F1-1，F1-2，F1-3，F1-4，需要将其进行合并
	public LinkedHashMap<String, ArrayList<Object>> mergeFeatures ( LinkedHashMap<String, ArrayList<Object>> inFeatureValue ){
		LinkedHashMap<String, ArrayList<Object>> mergedFeatureValue = new LinkedHashMap<String, ArrayList<Object>>();
		for( String featureName: inFeatureValue.keySet() ){		
			ArrayList<Object> inValues = new ArrayList<Object>();			
			int index = featureName.indexOf( "-");
			String newFeatureName = featureName;
			if ( index > 0 ){
				newFeatureName = featureName.substring( 0, index );
			}
			
			if ( !mergedFeatureValue.containsKey( newFeatureName ))
				mergedFeatureValue.put( newFeatureName, inValues );
		}
		
		int rowSize = inFeatureValue.entrySet().iterator().next().getValue().size();
		for ( int i =0; i < rowSize; i++ ){	
			for ( String mergedFeatureName: mergedFeatureValue.keySet() ){
				String mergedValue = "";
				for ( String featureName: inFeatureValue.keySet() ){
					String newFeatureName = featureName;
					int index = featureName.indexOf( "-");
					if ( index > 0 ){
						newFeatureName = featureName.substring( 0, index );
					}
					
					if ( !newFeatureName.equals( mergedFeatureName ))
						continue;
					
					String temp = "";
					if (inFeatureValue.get( featureName) != null &&  inFeatureValue.get( featureName).size() >  i )
						temp = inFeatureValue.get( featureName).get( i ).toString();
				
					if ( mergedValue.equals( ""))
						mergedValue = temp;
					else
						mergedValue = mergedValue + "+==+" + temp;
				}
				mergedFeatureValue.get( mergedFeatureName).add( mergedValue );
			}			
		}
		
		return mergedFeatureValue;		
	}
	
	public void discreteFeatureCSV ( String inFileName, String outFileName, int splitNum, boolean isNeedMerge ){
		
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
			
			if ( isNeedMerge == true ){
				inFeatureValue = this.mergeFeatures(inFeatureValue);
			}
			
			LinkedHashMap<String, ArrayList<Boolean>> newFeatureValue = new LinkedHashMap<String, ArrayList<Boolean>>();
			for ( String featureName: inFeatureValue.keySet() ){
				ArrayList<Object> values = inFeatureValue.get( featureName );
				
				HashMap<String, ArrayList<Boolean>> newValues = new HashMap<String, ArrayList<Boolean>>();
				if ( featureName.equals( "F71") || isNeedMerge == true ){
					System.out.println( "DEVELOPER and isSourceCode & featureName: " + featureName );
            		newValues = this.featureDiscretMergedFeatures( featureName, values);
				}
				else if ( com.comon.Constants.CATEGORIAL_FEATURES_LIST.contains( featureName )){
            		System.out.println( "CATEGORIAL_FEATURES & featureName: " + featureName );
            		newValues = this.featureDiscretCategorial(featureName, values);
            	}
            	else if ( com.comon.Constants.NUMERICAL_FEATURES_LIST.contains( featureName )){
            		System.out.println( "NUMERICAL_FEATURES & featureName: " + featureName );
            		newValues = this.featureDiscretNumerical(featureName, values, splitNum);
            	}
            	else{
            		System.out.println ( featureName + " CATEGORIAL_FEATURES_List or NUMERICAL_FEATURES_List, Wrong!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            	}
            	newFeatureValue.putAll( newValues );
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
					Boolean value = newFeatureValue.get( featureName).get( i );
					String wValue = "0";
					if ( value == true )
						wValue = "1";
					output.write(  wValue + ",");
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
	
	/*
	 * Excel格式省内存空间，读一列，然后处理，然后写入excel。目前excel存在的问题是只能存在256列。可能由于jxl.jar的版本太低
	 * CSV格式占用内存空间，需要将所有的列读完，然后才能处理
	 */
	public void discretFeaturesExcel ( String inFileName , String outFileName, int splitNum ){
		try {
			Workbook workbookRead = Workbook.getWorkbook( new FileInputStream( inFileName ) );
			Sheet sheetRead = workbookRead.getSheet(0);
			
			WritableWorkbook workbookWrite = Workbook.createWorkbook( new File ( outFileName ) );
            WritableSheet sheetWrite = workbookWrite.createSheet("sheet 1", 0);//创建sheet
            int writeColumnIndex = 0;
			
            for ( int i =0; i < sheetRead.getColumns(); i++ ){
            	String featureName = sheetRead.getCell( i, 0).getContents();
            	ArrayList<Object> featureValue = new ArrayList<Object>();
            	
            	for ( int j =1; j < sheetRead.getRows(); j++ ){
            		featureValue.add( sheetRead.getCell( i,j).getContents() );
            	}
            	
            	HashMap<String, ArrayList<Boolean>> newFeatureValue = new HashMap<String, ArrayList<Boolean>>();
            	
            	if ( com.comon.Constants.CATEGORIAL_FEATURES_LIST.contains( featureName )){
            		System.out.println( "CATEGORIAL_FEATURES & featureName: " + featureName );
            		newFeatureValue = this.featureDiscretCategorial(featureName, featureValue);
            	}
            	else if ( com.comon.Constants.NUMERICAL_FEATURES_LIST.contains( featureName )){
            		System.out.println( "NUMERICAL_FEATURES & featureName: " + featureName );
            		newFeatureValue = this.featureDiscretNumerical(featureName, featureValue, splitNum);
            	}
            	else{
            		System.out.println ( "CATEGORIAL_FEATURES_List or NUMERICAL_FEATURES_List, Wrong!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            	}
            	
            	for ( String featureKey: newFeatureValue.keySet() ){
            		ArrayList<Boolean> newFeatureList = newFeatureValue.get( featureKey );
            		
            		for ( int j = 0; j < newFeatureList.size(); j++ ){
            			String newValue = "0";
            			if ( newFeatureList.get(j) == true )
            				newValue = "1";
            			Label cell = new Label ( writeColumnIndex, j, newValue );
            			sheetWrite.addCell( cell );
            		}
	            	writeColumnIndex++;	
            	}
            	
            }
            
            workbookRead.close();
            workbookWrite.write();
            workbookWrite.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public void discreteAllFeatures ( ){
		String foldName = "data/feature/";
		String[] inFileNameList = { "fileCharacteris.csv", "sourceCode.csv", "sourceCodeSlicer.csv", "warningCharacteris.csv", "warningCombine.csv", "warningHistory.csv",
				"codeChurn.csv", "codeHistory.csv"};
		for ( int i = 0; i < inFileNameList.length; i++ ){
			String inFileName = foldName + inFileNameList[i];
			String outFileName = foldName + "out-" + inFileNameList[i];
			boolean isNeedMerge = false;
			System.out.println( inFileName );
			if ( inFileName.contains( "sourceCodeSlicer" ))
				isNeedMerge = true;
			this.discreteFeatureCSV( inFileName, outFileName, Constants.SPLIT_NUM, isNeedMerge );
		}
		
		//将代码文件合并为1个，并加上labal；整理成weka需要的格式
		LinkedHashMap<String, ArrayList<Boolean>> mergedFeatures = new LinkedHashMap<String, ArrayList<Boolean>>();
		HashMap<Integer, String> featureNameMap = new HashMap<Integer, String>();
		
		for ( int i =0; i < inFileNameList.length; i++ ){
			String fileName = foldName + "out-" + inFileNameList[i];
			
			try {
				BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
				String line = "";
				int row = 0;
				while ( ( line = br.readLine() ) != null ) {
					String[] temp = line.split( ",");
					if ( row ==0 ){
						for ( int j =0; j < temp.length; j++ ){
							ArrayList<Boolean> values = new ArrayList<Boolean>();
							mergedFeatures.put( temp[j], values );
							featureNameMap.put( i*1000+j, temp[j]);
						}						
					}
					else{
						for ( int j =0; j < temp.length; j++ ){
							String featureName = featureNameMap.get( i*1000+j );
							ArrayList<Boolean> values = mergedFeatures.get( featureName );
							if ( temp[j] != null && !temp[j].trim().equals( "")){
								if ( temp[j].trim().equals( "1"))
									values.add( true);
								else
									values.add( false );
							}
								
							mergedFeatures.put( featureName, values);
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
			for ( int i =0; i < rowSize; i++ ){
				output.newLine();
				for ( String featureName: mergedFeatures.keySet() ){
					Boolean value = mergedFeatures.get( featureName).get( i );
					String strValue = "0";
					if ( value == true)
						strValue = "1";
					output.write(  strValue + ",");
				}
				
				String value = categoryMap.get( i);
				value = value.replaceAll( ",", "");
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
		FeatureDiscretization discrete = new FeatureDiscretization();
		discrete.discreteAllFeatures();
		
		//discrete.discreteFeatureCSV( "data/feature/sourceCodeSlicer.csv", "data/feature/out-sourceCodeSlicer.csv", 5, true );
	}
}
