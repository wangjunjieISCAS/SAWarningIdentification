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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class FeatureCategoryEvaluation {
	
	//一个类型的feature set的evaluation
	public void featureSetEvaluation( String featureCategory ){
		ArrayList<String> selectedFeatureList = new ArrayList<String>();
		//得到的是类似F3这种类型的，实际在totalFeature中是F3-**，需要将具体的类型找到
		BufferedReader brCategory;
		try {
			brCategory = new BufferedReader(new FileReader( new File ( "data/feature_category.csv" ) ));
			String line = "";
			while ( (line = brCategory.readLine()) != null ){
				line = line.trim();
				if ( line.equals( ""))
					continue;
				
				String[] temp = line.split( ",");
				if ( temp.length != 2 )
					continue;
				
				int featureId = Integer.parseInt( temp[0] );
				String category =  temp[1].trim();
				if ( featureCategory.equals( category )){
					selectedFeatureList.add( "F" + featureId );
				}
			}
			brCategory.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/" + featureCategory + "-featureCategoryEvaluation.csv" )) , "GB2312"), 1024);

			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				String folderName = folder + "/" + warningInfoList[i];
				String fileTrain = this.generateFeatureValueBasedSelectedFeatures( folderName, selectedFeatureList );
				
				Double accuracy = this.conductPrediction(fileTrain);
				System.out.println( "The prediction accuracy of category " + featureCategory + " in " + folderName + " is : " + accuracy );
				
				output.write( folderName + "," + accuracy + ",");
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
	
	////基于totalFeatures里面所有的features，以及selectedFeatureName里面需要选择的featureName，生成新的可以用于weka分类的文件
	public String generateFeatureValueBasedSelectedFeatures ( String folderName, ArrayList<String> selectedFeatureName ){
		try {
			/*
			 * 存储的是该版本中的fullFeatureList，selectedFeatureName总的是F3，refinedSelectedFeatureName中存储的是该项目中具体的F3-**,
			 * 需要在各个项目中单独统计，因为每个项目可能会不一样
			 */
			
			ArrayList<String> refinedSelectedFeatureName = new ArrayList<String>();
			
			HashMap<String, Integer> featureNameMap = new HashMap<String, Integer>();
			HashMap<Integer, ArrayList<String>> featureValueMap = new LinkedHashMap<Integer, ArrayList<String>>();
			
			BufferedReader br = new BufferedReader(new FileReader( new File ( folderName + "/" + "totalFeatures.csv" )));
			String line = "";
			boolean featureName = false;
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
				if ( temp.length <= 0 )
					continue;
				if ( featureName == false){
					featureName = true;
					for ( int i =0; i < temp.length; i++ ){
						String featureFullName = temp[i].trim();
						String featureShortName = featureFullName;
						int index = featureFullName.indexOf( "-");
						if ( index > 0 ){
							featureShortName = featureFullName.substring( 0, index);
						}
						if ( selectedFeatureName.contains( featureShortName) ) {
							refinedSelectedFeatureName.add( featureFullName );
						}
						
						ArrayList<String> featureValue = new ArrayList<String>();
						featureValueMap.put( i, featureValue );
						featureNameMap.put( featureFullName, i );
					}
				}
				else{
					for ( int i =0; i < temp.length; i++ ){
						ArrayList<String> featureValue = featureValueMap.get( i );
						featureValue.add( temp[i]);
						featureValueMap.put( i , featureValue );
					}
				}
			}
			br.close();
			
			String fileTrain = folderName + "/newCategoryFeatures.csv";
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( fileTrain )) , "GB2312"), 1024);
			for ( int j =0; j < refinedSelectedFeatureName.size(); j++ ){
				output.write( refinedSelectedFeatureName.get( j ) + ",");
			}
			output.write( "category");
			output.newLine();
			//k表示instance的数目，需要生成数据的行数
			for ( int k =0; k < featureValueMap.entrySet().iterator().next().getValue().size(); k++ ){
				for ( int j =0; j < refinedSelectedFeatureName.size(); j++ ){	
					if ( featureNameMap.containsKey( refinedSelectedFeatureName.get( j) ) ){
						int index = featureNameMap.get( refinedSelectedFeatureName.get( j ));
						ArrayList<String> featureValue = featureValueMap.get( index );
						output.write( featureValue.get( k) + ",");
					}else{
						output.write( "0" + ",");
					}
				}
				int index = featureNameMap.get( "category");
				ArrayList<String> featureValue = featureValueMap.get( index );
				output.write( featureValue.get( k) );
				
				output.newLine();
			}
			output.flush();
			output.close();
			
			return fileTrain;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Double conductPrediction ( String fileTrain ){
		try {
		    Instances data = DataSource.read( fileTrain );
		    //默认最末行是category
		    data.setClassIndex( data.numAttributes() - 1 );
		    
		    Evaluation evaluation  = new Evaluation( data );
		    
		    NaiveBayes classify = new NaiveBayes() ;   
		    String[] options = {};
		    classify.setOptions(options);
		 
		    evaluation.crossValidateModel( classify, data, 10, new Random(1));
		    
		    System.out.println ( evaluation.toSummaryString("/nResults/n======/n", true) );
			//System.out.println ( evaluation.toClassDetailsString() );
			//System.out.println ( evaluation.toMatrixString(  ) );
			
		    //Double accuracy = evaluation.weightedFMeasure();
		    Double accuracy = (1.0* (evaluation.truePositiveRate( 0) + evaluation.trueNegativeRate( 0) ) ) / (evaluation.truePositiveRate( 0) + 
		    		evaluation.trueNegativeRate( 0) + evaluation.falsePositiveRate(0) + evaluation.falseNegativeRate(0));
		    //Double accuracy = (1.0* evaluation.truePositiveRate( 0) ) / (evaluation.truePositiveRate( 0) +  evaluation.falsePositiveRate(0) ); precision
		    //Double accuracy = evaluation.fMeasure(1);
		    return accuracy;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	
	public static void main ( String args[] ){
		FeatureCategoryEvaluation evaluation = new FeatureCategoryEvaluation();
		String[] category = { "fChr", "fHst", "cChr", "cHst", "cAnl", "wChr", "wHst", "wCmb"};
		//evaluation.featureSetEvaluation(  "fChr");
		for ( int i =0; i < category.length; i++ ){
			evaluation.featureSetEvaluation(  category[i] );
		}
	}
}
