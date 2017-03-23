package com.warningClassification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FeatureRefinementWarnType {

	/*
	 * 根据warning的类型对totalFeature.csv中的feature进行选择，将某种类型的warning对应的instance取出来，放在另外的文件中
	 * F21对应的就是warning type
	 */
	public void generateRefinedFeaturesWarnType ( String warnType){
		try {
			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				String folderName = folder + "/" + warningInfoList[i];
				BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( folderName + "/totalFeaturesType.csv" )) , "GB2312"), 1024);
				
				int index = -1;
				BufferedReader br = new BufferedReader(new FileReader( new File ( folderName + "/totalFeatures.csv" )));
				String line = "";
				while ( ( line = br.readLine() ) != null ) {
					String[] temp = line.split( ",");
					if ( index == -1 ){
						for ( int j =0; j < temp.length; j++ ){
							String feature = temp[j].trim();
							if ( feature.equals( "F21")){
								index = j;
							}	
							output.write( feature  );
							if ( j < temp.length -1 )
								output.write( "," );
						}
						output.newLine();
					}
					else{
						if ( temp.length < index )
							continue;
						String featureValue = temp[index].trim();
						if ( featureValue.equals( warnType )){
							for ( int j =0; j < temp.length; j++ ){
								output.write( temp[j]);
								if ( j < temp.length -1 )
									output.write( "," );
							}
							output.newLine();
						}
						
					}
				}
				br.close();
				output.flush();
				output.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main ( String args[] ){
		FeatureRefinementWarnType refine = new FeatureRefinementWarnType();
		refine.generateRefinedFeaturesWarnType( "MALICIOUS_CODE");
	}
}
