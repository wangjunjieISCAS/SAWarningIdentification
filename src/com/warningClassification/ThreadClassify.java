package com.warningClassification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ThreadClassify implements Runnable{
	String folderName;
	
	public ThreadClassify ( String folderName1 ){
		this.folderName = folderName1;
	}
	
	@Override
	public void run( ) {
		// TODO Auto-generated method stub
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		String time = dateFormat.format( new Date() ); 
		System.out.println( "current time : " + time + " " + folderName ); 
	
		try {
			String fileName = folderName + "totalFeaturesType.csv";
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
			
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( folderName + "featureRankPerformance.csv" )) , "GB2312"), 1024);
			System.out.println ( "numberAttributesSelected: " + selection.numberAttributesSelected() );
			
			for ( int i =0; i < attrIndex.length; i++){
				int index = attrIndex[i];
				String featureName = data.attribute( index).name();
				
				output.write( index + "," +"," + featureName + ",");
				output.newLine();
				System.out.println( i + " " + index + " " + " " + featureName );
			}
			
			output.write( "original feature size" + "," + data.numAttributes() );
			output.newLine();
			output.write( "refined feature size" + "," + selection.numberAttributesSelected() );
			output.newLine();
			output.flush();
			output.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		time = dateFormat.format( new Date() ); 
		System.out.println( "current time : " + time + " " + folderName ); 
	}
	
	public static void main(String[] args) {  
		File folder = new File ( "data/feature" );
		String[] warningInfoList = folder.list();
		for ( int i = 0; i < warningInfoList.length; i += 5 ){
			File projectFolder = new File ( folder + "/" + warningInfoList[i] );
			if ( !projectFolder.isDirectory() )
				continue;
			
			ThreadClassify thread1 = new ThreadClassify( folder + "/" + warningInfoList[i] +"/" );
			Thread t1 = new Thread ( thread1, "A");
			t1.start();  

			if ( i+1 < warningInfoList.length ){
				ThreadClassify thread2 = new ThreadClassify( folder + "/" + warningInfoList[i+1] +"/" );
				Thread t2 = new Thread ( thread2, "B");
				t2.start();
			}
			
			if ( i +2 <  warningInfoList.length ){
				ThreadClassify thread3 = new ThreadClassify( folder + "/" + warningInfoList[i+2] +"/"   );
				Thread t3 = new Thread ( thread3, "C");
				 t3.start();
			}
			
			if ( i+3 < warningInfoList.length ){
				ThreadClassify thread4 = new ThreadClassify( folder + "/" + warningInfoList[i+3] +"/" );
				Thread t4 = new Thread ( thread4, "B");
				t4.start();
			}
			
			if ( i+4 < warningInfoList.length ){
				ThreadClassify thread5 = new ThreadClassify( folder + "/" + warningInfoList[i+4] +"/" );
				Thread t5 = new Thread ( thread5, "B");
				t5.start();
			}
			
			//break;
			
			
			int time = 60*60*1000*6;
			//if ( i > 4 )
			//	time = 60*60*1000*8;
			try {
				Thread.sleep( time );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
   } 
}
