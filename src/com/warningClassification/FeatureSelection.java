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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.RankedOutputSearch;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

public class FeatureSelection {
	
	public void wrapperSearchGreedyStepwiseFeatureSelection ( String fileName ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/featureRank.csv" )) , "GB2312"), 1024);
			
			Instances data = DataSource.read(  fileName );	
			//DataSink.write( "data/feature/featuresOut.arff", data);
			
			System.out.println ( data.numAttributes() );
			data.setClassIndex( data.numAttributes() -1  );
			
			AttributeSelection selection = new AttributeSelection();  
			WrapperSubsetEval evaluator = new WrapperSubsetEval();
			evaluator.setClassifier( new NaiveBayes() );
			GreedyStepwise search = new GreedyStepwise();
			search.setSearchBackwards(true);
			selection.setEvaluator( evaluator );
			selection.setSearch( search );
			selection.SelectAttributes( data );
			selection.setRanking( true );
			selection.setXval( true );
			
			evaluator.buildEvaluator( data );
			int[] attrIndex = selection.selectedAttributes();
			
			System.out.println ( "numberAttributesSelected: " + selection.numberAttributesSelected() );
			for ( int i =0; i < attrIndex.length; i++){
				int index = attrIndex[i];
				String featureName = data.attribute( index).name();
				
				output.write( index + "," +"," + featureName + ",");
				output.newLine();
				System.out.println( i + " " + index + " " + " " + featureName );
			}
			
			output.flush();
			output.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void informationGainFeatureSelection ( String fileName ){
		HashMap<String, Double> featureValue = new HashMap<String, Double>();
		
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/featureRank.csv" )) , "GB2312"), 1024);
			BufferedWriter outputMerged = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/featureRankMerged.csv" )) , "GB2312"), 1024);
			
			BufferedReader br = new BufferedReader(new FileReader( new File ( "data/feature/totalFeatures.csv" )));
			String line = "";
			int index2 = 0;
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
			}
			
			Instances data = DataSource.read(  fileName );	
			//DataSink.write( "data/feature/featuresOut.arff", data);
			
			System.out.println ( data.numAttributes() );
			data.setClassIndex( data.numAttributes() -1  );
			
			
			Ranker rank = new Ranker();
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			
			eval.buildEvaluator( data );
			int[] attrIndex = rank.search( eval, data);
			
			for ( int i =0; i < attrIndex.length; i++){
				int index = attrIndex[i];
				Double value = eval.evaluateAttribute( index);
				String featureName = data.attribute( index).name();
				
				output.write( index + "," + value + "," + featureName + ",");
				output.newLine();
				System.out.println( i + " " + index + " " + value + " " + featureName );
				
				int nameIndex = featureName.indexOf( "-");
				String shortName = featureName.substring( 0, nameIndex);
				
				double priorValue = 0.0;
				if ( featureValue.containsKey( shortName )){
					priorValue = featureValue.get( shortName );
				}
				
				priorValue += value;
				
				featureValue.put( shortName, priorValue );
			}
			
			output.flush();
			output.close();
			
			System.out.println( featureValue.size() );
			ArrayList<Map.Entry<String, Double>> featureValueList =  new ArrayList<Map.Entry<String, Double>>(featureValue.entrySet());
			Collections.sort( featureValueList, new Comparator <Map.Entry<String, Double>>() {
				@Override
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					return o2.getValue().compareTo( o1.getValue() );
				}
			});
			
			for ( int i =0; i < featureValueList.size(); i++ ){
				outputMerged.write( featureValueList.get(i).getKey() + "," + featureValueList.get(i).getValue() );
				outputMerged.newLine();
			}
			outputMerged.flush();
			outputMerged.close();
			
			/*
			AttributeSelection selection = new AttributeSelection();
			InfoGainAttributeEval infoGainEval = new InfoGainAttributeEval();
			Ranker rankSearch = new Ranker();
			rankSearch.setNumToSelect( data.numAttributes() - 1);
			
			selection.setEvaluator( infoGainEval );
			selection.setSearch(  rankSearch );
			selection.SelectAttributes ( data );
			
			int[] attributes = selection.selectedAttributes();
			for ( int i =0; i < attributes.length; i++ ){
				System.out.println( data.attribute( attributes[i]).name() );
			}
			*/
			
			/*
			AttributeSelection attsel = new AttributeSelection();  
			CfsSubsetEval eval = new CfsSubsetEval();
			GreedyStepwise search = new GreedyStepwise();
			search.setSearchBackwards(true);
			attsel.setEvaluator(eval);
			attsel.setSearch(search);
			attsel.SelectAttributes( data );
			// obtain the attribute indices that were selected
			int[] indices = attsel.selectedAttributes();
			System.out.println( Utils.arrayToString(indices));
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main ( String args[] ){
		FeatureSelection selection = new FeatureSelection();
		//selection.informationGainFeatureSelection( "data/feature/totalFeatures.csv");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		String time = dateFormat.format( new Date() ); 
		System.out.println( "current time : " + time ); 
		
		selection.wrapperSearchGreedyStepwiseFeatureSelection( "data/feature/totalFeatures.csv" );
		
		time = dateFormat.format( new Date() ); 
		System.out.println( "current time : " + time ); 
	}
}	
