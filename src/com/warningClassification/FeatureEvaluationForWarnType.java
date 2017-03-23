package com.warningClassification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

public class FeatureEvaluationForWarnType {

	public void featureSetEvaluation ( String selectedFeatureFileName, String typeSpecificFeature ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/featureEvaluationForWarnType.csv" )) , "GB2312"), 1024);
			output.write( "folderName" + "," + "highFrequency" + "," + "medianHighFrequency" + "," + "typeSpecific" + "," + "projectSpecific" );
			output.newLine();
			
			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				String folderName = folder + "/" + warningInfoList[i];
				Double highFreqF1 = this.commonFeatureSetEvaluation( folderName, "data/feature/highFrequencyFeatures.csv", selectedFeatureFileName);
				
				Double medianFreqF1 = this.commonFeatureSetEvaluation( folderName, "data/feature/middleHighFrequencyFeatures.csv", selectedFeatureFileName);
				
				Double typeSpecificF1 = this.commonFeatureSetEvaluation( folderName, typeSpecificFeature, selectedFeatureFileName);
				
				Double projectSpecificF1 = this.commonFeatureSetEvaluation( folderName, folderName + "/" + selectedFeatureFileName, selectedFeatureFileName);
				
				output.write(  folderName + ",");
				output.write( highFreqF1 + "," + medianFreqF1 + "," + typeSpecificF1 + "," + projectSpecificF1 );
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
	
	public Double commonFeatureSetEvaluation( String folderName, String featureFileName, String selectedFeatureFileName ){
		//这里是得到总的feature
		ArrayList<String> selectedFeatureList = this.obtainSelectedFeatureList( featureFileName );
		ArrayList<String> selectedFeatureListTotal = this.obtainSelectedFeatureList( folderName + "/" + selectedFeatureFileName );
		/*
		 * selectedFeatureList 为F3这种各类型，而在generateFeatureValueBasedSelectedFeatures用到的是F3-String这种类型；
		 * 所以需要将后面的参数补上
		 */
		ArrayList<String> refinedSelectedFeatureList = new ArrayList<String>();
		for ( int i =0; i < selectedFeatureListTotal.size(); i++ ){
			String feature = selectedFeatureListTotal.get( i );
			if ( selectedFeatureList.contains( feature )){
				refinedSelectedFeatureList.add( feature );
			}
			else{
				int index = feature.indexOf( "-");
				String newFeature = "";
				if ( index > 0 ){
					newFeature = feature.substring( 0, index);
				}
				if ( selectedFeatureList.contains( newFeature)){
					refinedSelectedFeatureList.add( feature );
				}
			}			
		}
		
		String fileTrain = this.generateFeatureValueBasedSelectedFeatures(folderName, refinedSelectedFeatureList );
		
		Double accuracy = this.conductPrediction(fileTrain);
		System.out.println( "The prediction accuracy of commonFeatureSetEvaluation in " + folderName + " is : " + accuracy );
		
		return accuracy;
	}
	
	
	public ArrayList<String> obtainSelectedFeatureList ( String fileName ){
		ArrayList<String> selectedFeatureList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
			String line = "";
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
				if ( temp.length != 3 ){
					continue;
				}
				if ( !temp[2].trim().equals( "category"))
					selectedFeatureList.add( temp[2].trim() );
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return selectedFeatureList;
	}
	
	public String generateFeatureValueBasedSelectedFeatures ( String folderName, ArrayList<String> selectedFeatureName ){
		try {
			HashMap<String, Integer> featureNameMap = new HashMap<String, Integer>();
			HashMap<Integer, ArrayList<String>> featureValueMap = new LinkedHashMap<Integer, ArrayList<String>>();
			
			BufferedReader br = new BufferedReader(new FileReader( new File ( folderName + "/" + "totalFeaturesType.csv" )));
			String line = "";
			boolean featureName = false;
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
				if ( temp.length <= 0 )
					continue;
				if ( featureName == false){
					featureName = true;
					for ( int i =0; i < temp.length; i++ ){
						ArrayList<String> featureValue = new ArrayList<String>();
						featureValueMap.put( i, featureValue );
						featureNameMap.put( temp[i], i );
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
			
			String fileTrain = folderName + "/newTotalFeaturesType.csv";
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( fileTrain )) , "GB2312"), 1024);
			for ( int j =0; j < selectedFeatureName.size(); j++ ){
				output.write( selectedFeatureName.get( j ) + ",");
			}
			output.write( "category");
			output.newLine();
			//k表示instance的数目，需要生成数据的行数
			for ( int k =0; k < featureValueMap.entrySet().iterator().next().getValue().size(); k++ ){
				for ( int j =0; j < selectedFeatureName.size(); j++ ){	
					if ( featureNameMap.containsKey( selectedFeatureName.get( j) ) ){
						int index = featureNameMap.get( selectedFeatureName.get( j ));
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
		    
		    //DataSink.write( "data/feature/weka.arff", data );
		    
		    NaiveBayes classify = new NaiveBayes() ;   
		    String[] options = {};
		    classify.setOptions(options);
		 
		    evaluation.crossValidateModel( classify, data, 10, new Random(1));
		    
		    System.out.println ( evaluation.toSummaryString("/nResults/n======/n", true) );
			//System.out.println ( evaluation.toClassDetailsString() );
			//System.out.println ( evaluation.toMatrixString(  ) );
			
		    //Double accuracy = evaluation.weightedFMeasure();
		    //Double accuracy = (1.0* (evaluation.truePositiveRate( 0) + evaluation.trueNegativeRate( 0) ) ) / (evaluation.truePositiveRate( 0) + 
		    //		evaluation.trueNegativeRate( 0) + evaluation.falsePositiveRate(0) + evaluation.falseNegativeRate(0));
		    
		    Double accuracy = evaluation.fMeasure(1);
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
		FeatureEvaluationForWarnType evaluation = new FeatureEvaluationForWarnType();
		evaluation.featureSetEvaluation( "featureRankMalicious.csv", "data/feature/maliciousTypeSpecificFeatures.csv");
		
		/*
		 * 需要注意的是，featureRankCombine要转化成和单个项目的featureRank类似的形式，三列，第三列是feature name
		 */
	}
}
