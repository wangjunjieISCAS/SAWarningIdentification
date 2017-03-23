package com.warningClassification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.comon.Constants;


public class WarningStatistics {	
	
	//得到每个版本的open,close和deleted的warning数目
	public void countWarningStatistics ( ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/warningStatistics.csv" )) , "GB2312"), 1024);
			output.write( "project" + "," + "open" + "," + "close" + "," + "delete" );
			output.newLine();
			
			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				BufferedReader br = new BufferedReader(new FileReader( new File ( folder + "/" + warningInfoList[i] + "/" + "labelAll.csv")));
				if ( br == null )
					continue;
				
				int openNum = 0, closeNum =0, deleteNum = 0;
				String line = "";
				while ( ( line = br.readLine() ) != null ) {
					line = line.replace( ",", "");
					line = line .trim();
					if ( line.trim().equals( ""))
						continue;
					
					if ( line.equals( "open"))
						openNum++;
					else if ( line.equals( "close"))
						closeNum++;
					else if ( line.equals( "deleted"))
						deleteNum++;
				}
				br.close();
				
				output.write(  warningInfoList[i] + ",");
				output.write( openNum + "," + closeNum + "," + deleteNum );
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
	
	//得到每种warning类型的数据
	public void countWarningTypeStatistics ( ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/feature/warningTypeStatistics.csv" )) , "GB2312"), 1024);
			output.write( "project" + ",");
			for ( int j =0; j < Constants.WARN_CATEGORY_UNDER_INVESTIGATION.length; j++ ){
				output.write( Constants.WARN_CATEGORY_UNDER_INVESTIGATION[j] + ",");
				output.write( "open" + ",");
				output.write( "close" + ",");
			}
			output.newLine();
			
			File folder = new File ( "data/feature" );
			String[] warningInfoList = folder.list();
			for ( int i =0; i < warningInfoList.length; i++ ){
				File projectFolder = new File ( folder + "/" + warningInfoList[i] );
				if ( !projectFolder.isDirectory() )
					continue;
				
				BufferedReader br = new BufferedReader(new FileReader( new File ( folder + "/" + warningInfoList[i] + "/" + "totalFeatures.csv")));
				if ( br == null )
					continue;
				
				Integer[] warnTypeNum = new Integer[Constants.WARN_CATEGORY_UNDER_INVESTIGATION.length];
				Integer[] openWarnTypeNum = new Integer[Constants.WARN_CATEGORY_UNDER_INVESTIGATION.length];
				Integer[] closeWarnTypeNum = new Integer[Constants.WARN_CATEGORY_UNDER_INVESTIGATION.length];
				for ( int j =0; j < warnTypeNum.length; j++ ){
					warnTypeNum[j] = 0;
					openWarnTypeNum[j] = 0;
					closeWarnTypeNum[j] = 0;
				}
				
				String line = "";
				int index = -1;
				while ( ( line = br.readLine() ) != null ) {
					String[] temp = line.split( ",");
					if ( index == -1 ){
						for ( int j =0; j < temp.length; j++ ){
							String feature = temp[j].trim();
							if ( feature.equals( "F21")){
								index = j;
							}	
						}
					}
					else{
						if ( temp.length < index )
							continue;
						String featureValue = temp[index].trim();
						String category = temp[temp.length-1].trim();
						for ( int j =0; j < Constants.WARN_CATEGORY_UNDER_INVESTIGATION.length; j++ ){
							if ( featureValue.equals( Constants.WARN_CATEGORY_UNDER_INVESTIGATION[j])){
								warnTypeNum[j]++;
								if ( category.equals( "open")){
									openWarnTypeNum[j]++;
								}
								if ( category.equals("close")){
									closeWarnTypeNum[j]++;
								}
									
							}
						}
					}
				}
				
				br.close();
				
				output.write(  warningInfoList[i] + ",");
				for ( int j =0; j < warnTypeNum.length; j++ ){
					output.write( warnTypeNum[j].toString() + "," );
					output.write( openWarnTypeNum[j].toString() + "," );
					output.write( closeWarnTypeNum[j].toString() + "," );
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
	
	public static void main ( String args[] ){
		WarningStatistics statis = new WarningStatistics();
		//statis.countWarningStatistics();
		statis.countWarningTypeStatistics();
	}

}
