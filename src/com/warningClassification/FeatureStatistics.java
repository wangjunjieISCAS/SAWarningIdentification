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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class FeatureStatistics {
	
	//得到各个版本的有效feature的情况，并且需要对同一个feature进行合并
	public void countFeatureStatistics ( ){
		try {
			HashMap<String, ArrayList<String>> refinedFeatureMap = new HashMap<String, ArrayList<String>>();
			HashMap<String, HashMap<String, Integer>> featureRankMap = new HashMap<String, HashMap<String, Integer>>();
			
			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				BufferedReader br = new BufferedReader(new FileReader( new File ( folder + "/" + warningInfoList[i] + "/" + "selectedFeatureRank.csv")));
				if ( br == null )
					continue;
				
				ArrayList<String> featureList = new ArrayList<String>();
				ArrayList<Double> featureValueList = new ArrayList<Double>();
				String line = "";
				while ( ( line = br.readLine() ) != null ) {					
					line = line .trim();
					if ( line.equals( ""))
						continue;
					
					String[] temp = line.split( ",");
					if ( temp.length != 2 )
						continue;
					
					String feature = temp[0];
					featureList.add( feature );
					featureValueList.add( Double.parseDouble( temp[1]) );
				}
				
				HashMap<String, Object> result = this.featureCombine(featureList, featureValueList );
				ArrayList<String> refinedFeatureList = (ArrayList<String>) result.get( "feature");
				HashMap<String, Integer> featureRankList = (HashMap<String, Integer>) result.get( "rank");
				
				refinedFeatureMap.put( warningInfoList[i] , refinedFeatureList );
				featureRankMap.put (warningInfoList[i] , featureRankList);
				br.close();
			}
			
			HashMap<String, ArrayList<String>> featureProjectMap = new HashMap<String, ArrayList<String>>();
		
			for ( String project : refinedFeatureMap.keySet() ){
				ArrayList<String> refinedFeatureList = refinedFeatureMap.get( project);
				for ( int i =0; i < refinedFeatureList.size(); i++ ){
					String feature = (String) refinedFeatureList.get( i );
					if ( !featureProjectMap.containsKey( feature )){
						ArrayList<String> projectList = new ArrayList<String>();
						featureProjectMap.put( feature, projectList );
					}
					ArrayList<String> projectList = featureProjectMap.get( feature);
					projectList.add( project );
					featureProjectMap.put( feature, projectList );
				}
			}
			
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/featureStatistics.csv" )) , "GB2312"), 1024);
			for ( String feature: featureProjectMap.keySet() ){
				output.write( feature +",");
				ArrayList<String> projects = featureProjectMap.get( feature );
				output.write( projects.size() + ",");
				for ( int i =0; i < projects.size(); i++ ){
					output.write( projects.get( i ) + ",");
				}
				output.newLine();
			}
			
			output.newLine();
			output.newLine();
			for ( String feature: featureProjectMap.keySet() ){
				output.write( feature +",");
				ArrayList<String> projects = featureProjectMap.get ( feature );
				output.write( projects.size() + ",");
				for ( int i =0; i < projects.size(); i++ ){
					HashMap<String, Integer> featureForProject = featureRankMap.get( projects.get(i));
					output.write ( featureForProject.get( feature) + ",");
				}
				output.newLine();
			}
			
			output.newLine();
			output.newLine();
			
			for ( String project: refinedFeatureMap.keySet() ){
				ArrayList<String> featureList = refinedFeatureMap.get( project );
				output.write( project + "," );
				output.write( String.valueOf( featureList.size() ) + "," );
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
	
	//和countFeatureStatistics区别在于，该函数从featureRank**中得到feature的统计情况，不需要事先生成selectedFeatureRank
	public void countFeatureStatisticsSimple ( String fileName ){
		try {
			HashMap<String, ArrayList<String>> refinedFeatureMap = new HashMap<String, ArrayList<String>>();
			
			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				File file = new File ( folder + "/" + warningInfoList[i] + "/" + fileName );
				if ( !file.exists() ){
					System.out.println ( "file: " + folder + "/" + warningInfoList[i] + "/" + fileName + " does not exist!");	
					continue;
				}
				BufferedReader br = new BufferedReader(new FileReader( file ));

				ArrayList<String> featureList = new ArrayList<String>();
				String line = "";
				while ( ( line = br.readLine() ) != null ) {					
					line = line .trim();
					if ( line.equals( ""))
						continue;
					
					String[] temp = line.split( ",");
					if ( temp.length != 3 )
						continue;
					
					String feature = temp[2];
					if ( feature.equals( "category"))
						continue;
					featureList.add( feature );
				}
				
				ArrayList<String> result = this.featureCombineSimple(featureList);
				refinedFeatureMap.put( warningInfoList[i] , result );
				br.close();
			}
			
			HashMap<String, ArrayList<String>> featureProjectMap = new HashMap<String, ArrayList<String>>();
		
			for ( String project : refinedFeatureMap.keySet() ){
				ArrayList<String> refinedFeatureList = refinedFeatureMap.get( project);
				for ( int i =0; i < refinedFeatureList.size(); i++ ){
					String feature = (String) refinedFeatureList.get( i );
					if ( !featureProjectMap.containsKey( feature )){
						ArrayList<String> projectList = new ArrayList<String>();
						featureProjectMap.put( feature, projectList );
					}
					ArrayList<String> projectList = featureProjectMap.get( feature);
					projectList.add( project );
					featureProjectMap.put( feature, projectList );
				}
			}
			
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/featureStatistics-" + fileName )) , "GB2312"), 1024);
			for ( String feature: featureProjectMap.keySet() ){
				output.write( feature +",");
				ArrayList<String> projects = featureProjectMap.get( feature );
				output.write( projects.size() + ",");
				for ( int i =0; i < projects.size(); i++ ){
					output.write( projects.get( i ) + ",");
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
	
	//和countFeatureStatisticsSimple逻辑类似，得到各个类型中的feature的重要程度
	public void countFeatureCategoryStatistics ( String fileName ){
		try {
			HashMap<Integer, String> featureCategoryMap = new HashMap<Integer, String>();
			HashMap<String, Integer> refinedFeatureMap = new HashMap<String, Integer>();
			
			BufferedReader brCategory = new BufferedReader(new FileReader( new File ( "data/feature_category.csv" ) ));
			String line = "";
			while ( (line = brCategory.readLine()) != null ){
				line = line.trim();
				if ( line.equals( ""))
					continue;
				
				String[] temp = line.split( ",");
				if ( temp.length != 2 )
					continue;
				
				int featureId = Integer.parseInt( temp[0] );
				featureCategoryMap.put( featureId, temp[1].trim() );
			}
			brCategory.close();
			
			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				File file = new File ( folder + "/" + warningInfoList[i] + "/" + fileName );
				if ( !file.exists() ){
					System.out.println ( "file: " + folder + "/" + warningInfoList[i] + "/" + fileName + " does not exist!");	
					continue;
				}
				BufferedReader br = new BufferedReader(new FileReader( file ));

				ArrayList<String> featureList = new ArrayList<String>();
				while ( ( line = br.readLine() ) != null ) {					
					line = line .trim();
					if ( line.equals( ""))
						continue;
					
					String[] temp = line.split( ",");
					if ( temp.length != 3 )
						continue;
					
					String feature = temp[2];
					if ( feature.equals( "category"))
						continue;
					featureList.add( feature );
				}
				
				ArrayList<String> result = this.featureCombineSimple(featureList);
				
				for ( int j =0; j < result.size(); j++ ){
					int count = 1;
					if ( refinedFeatureMap.containsKey( result.get( j )))
						count += refinedFeatureMap.get( result.get( j));
					refinedFeatureMap.put( result.get(j), count );
				}
				
				br.close();
			}
			
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/featureCategoryStatistics.csv"  )) , "GB2312"), 1024);
			for ( String feature: refinedFeatureMap.keySet() ){
				output.write( feature +",");
				output.write( refinedFeatureMap.get( feature) + ",");
				
				int featureId = Integer.parseInt( feature.replaceAll("F", "").trim() );
				if ( featureCategoryMap.containsKey( featureId ))
					output.write( featureCategoryMap.get( featureId ) +",");
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
	
	
	public ArrayList<String> featureCombineSimple ( ArrayList<String> featureList ){
		ArrayList<String> newFeatureList = new ArrayList<String>();
		for ( int i =0; i < featureList.size(); i++ ){
			String feature = featureList.get( i );
			String newFeature = feature;
			
			int index = feature.indexOf( "-");
			if ( index > 0 ){
				newFeature  = feature.substring( 0, index );
			}
			
			if ( !newFeatureList.contains( newFeature ))
				newFeatureList.add( newFeature );
		}
		return newFeatureList;
	}
	
	public HashMap<String, Object> featureCombine ( ArrayList<String> featureList, ArrayList<Double> featureValueList  ){
		ArrayList<String> refinedFeatureList = new ArrayList<String>();
		HashMap<String, Integer> featureIndexList = new HashMap<String, Integer>();
		
		HashMap<Integer,Double> featureValueMap = new HashMap<Integer, Double>();
		for ( int i =0; i < featureValueList.size(); i++ ){
			featureValueMap.put( i , featureValueList.get( i ));
		}
		
		List<HashMap.Entry<Integer, Double>> newFeatureValueMap = new ArrayList<HashMap.Entry<Integer, Double>>(featureValueMap.entrySet());

		Collections.sort( newFeatureValueMap, new Comparator<HashMap.Entry<Integer, Double>>() {   
			public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {      
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			}); 
		
		int count = 1;
		for ( int i =0; i < newFeatureValueMap.size(); i++ ){
			HashMap.Entry<Integer, Double> entry = newFeatureValueMap.get( i );
			Integer place = entry.getKey();
			
			String feature = featureList.get( place );
			String newFeature = feature;
			int index = feature.indexOf( "-");
			if ( index > 0 ){
				newFeature  = feature.substring( 0, index );
			}
			if ( !refinedFeatureList.contains( newFeature )){
				refinedFeatureList.add( newFeature);
				featureIndexList.put( newFeature, count );
				
				count++;
			}		
		}
		
		HashMap<String,Object> result = new HashMap<String, Object>();
		result.put( "feature", refinedFeatureList );
		result.put( "rank", featureIndexList );
		
		return result;
	}
	
	public void obtainTotalFeatures ( String fileName){
		try {
			HashSet<String> totalFeatureList = new HashSet<String>();
			
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
			String line = "";
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
				for ( int i =0; i < temp.length; i++ ){
					String feature = temp[i].trim();
					if ( feature.equals( "category"))
						continue;
					int index = feature.indexOf( "-");
					if ( index > 0 )
						feature = feature.substring( 0, index);
					
					totalFeatureList.add( feature );
				}
				break;
			}
			
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/totalFeatures.csv" )) , "GB2312"), 1024);
			Iterator iter = totalFeatureList.iterator();
			while ( iter.hasNext() ){
				String feature = (String) iter.next();
				output.write( feature );
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
	
	public static void main ( String args[] ){
		FeatureStatistics statis = new FeatureStatistics();
		statis.obtainTotalFeatures( "data/feature/ant-201101-done/totalFeatures.csv");
		//statis.countFeatureStatistics();
		
		//statis.countFeatureStatisticsSimple( "featureRankPerformance.csv");
		statis.countFeatureCategoryStatistics(  "featureRank.csv" );
	}
}
