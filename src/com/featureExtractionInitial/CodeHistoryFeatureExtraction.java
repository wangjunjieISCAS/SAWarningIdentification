package com.featureExtractionInitial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.comon.Constants;
import com.comon.ProjectInfo;
import com.comon.StaticWarning;
import com.database.DBOperation;

public class CodeHistoryFeatureExtraction {
	private DBOperation dbOperation;
	
	public CodeHistoryFeatureExtraction ( ){
		dbOperation = new DBOperation();
	}
	
	public String obtainRevisionTime ( int revisionNumber ){
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE + " where commitAutoId = " + revisionNumber ;
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		String time = "";
		try {
			if ( rs.next() ){
				time = rs.getString( "commitTime");
			}
			rs.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return time;
	}
	
	
	/*
	 * 这里将priorRevisionNumber全部改成currentRevisionNumber
	 * 因为如果alertOpenRevision是currentRevision，那么如果用priorRevisionNumber，就会出现负的情况
	 */
	public ArrayList<String> extractDeveloper_F71 ( int openRevisionNumber, int currentRevisionNumber, String fileName ){
		String openTime = this.obtainRevisionTime(openRevisionNumber);
		String priorTime = this.obtainRevisionTime(currentRevisionNumber);
		
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE + " where commitTime >= '" + openTime + "' and commitTime <= '" +
				priorTime + "' and commitAutoId in (select distinct commitId from " + Constants.COMMIT_CONTENT_TABLE + 
				" where className like '%" + fileName + "' )" ;
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		ArrayList<String> developerList = new ArrayList<String>();
		
		try {
			while ( rs.next() ){
				String developer = rs.getString( "developerEmail");
				developerList.add( developer );
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println ( developerList.toString() );
		return developerList;
	}
	
	
	public ArrayList<String> obtainFileListFromPackage ( String packageName, ProjectInfo projectInfo ){
		ArrayList<String> fileList = projectInfo.getFileListForPackage().get( packageName );
		
		return fileList;
	}
	
	/*
	 * 先运行obtainProjectStalenessForProject，得到该项目的；否则需要重复进行数据库操作
	 */
	public HashMap<String, Object>  extractProjectStaleness_F85_F147 ( HashMap<String, Object> projectStaleness ){
		HashMap<String, Object> resultInProject = new HashMap<String, Object>();
		
		resultInProject.put( "F147", projectStaleness.get( "revision" ) );
		resultInProject.put( "F85", projectStaleness.get("staleness"));
		
		return resultInProject;
	}
	
	//项目上一个版本的修改就是上一个版本
	public HashMap<String, Object> obtainProjectStalenessForProject (  ){
		HashMap<String, Object> resultInProject = new HashMap<String, Object>();
		
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE + " where commitTime < '" + Constants.CURRENT_COMMIT_TIME + 
				"' order by commitTime desc";
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		int lastRevisionNumber = 0;
		try {
			if ( rs.next() ){
				lastRevisionNumber = rs.getInt( "commitAutoId");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resultInProject.put( "revision", lastRevisionNumber );
		resultInProject.put( "staleness", 1);
		
		return resultInProject;
	}
	
	/*
	 * 首先运行obtainPackageStalenessForAllPackage， 得到packageStalenessList
	 */
	public HashMap<String, Integer> extractPackageStaleness_F84_F146 ( String packageName, HashMap<String, HashMap<String, Integer>> packageStalenessList){	
		HashMap<String, Integer> resultInPackage = new HashMap<String, Integer>();
		
		int revision = -1;
		if ( packageStalenessList.get( "revision").containsKey( packageName ) ) 
			revision = packageStalenessList.get( "revision").get( packageName );
		int staleness = -1;
		if ( packageStalenessList.get( "staleness").containsKey( packageName ) ) 
			staleness = packageStalenessList.get( "staleness").get( packageName );
		
		resultInPackage.put( "F146", revision );
		resultInPackage.put( "F84", staleness );
		
		return resultInPackage;
	}
	
	public HashMap<String, HashMap<String, Integer>> obtainPackageStalenessForAllPackages ( ProjectInfo projectInfo ){
		HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> revisionResult = new HashMap<String, Integer>();
		HashMap<String, Integer> stalenessResult = new HashMap<String, Integer>();
		
		for ( int i =0; i < projectInfo.getPackageList().size(); i++ ){
			String packageName = projectInfo.getPackageList().get( i );
			ArrayList<String> fileList = projectInfo.getFileListForPackage().get(packageName);
			
			HashMap<String, Integer> resultInFiles = this.obtainFileListStaleness(fileList);
			
			revisionResult.put( packageName, resultInFiles.get("revision" ));
			stalenessResult.put( packageName , resultInFiles.get( "staleness"));
		}
		result.put( "revision", revisionResult );
		result.put( "staleness", stalenessResult );
		
		return result;
	}
	/*
	 * 先运行obtainFileStalenessForAllFiles，得到fileStalenessList
	 */
	public HashMap<String, Integer>  extractFileStaleness_F83_F74 (String fileName, HashMap<String, HashMap<String, Integer>> fileStalenessList ){
		HashMap<String, Integer> resultInFile = new HashMap<String, Integer>();
		
		int revision = -1;
		if ( fileStalenessList.get( "revision").containsKey( fileName ) ) 
			revision = fileStalenessList.get( "revision").get( fileName );
		int staleness = -1;
		if ( fileStalenessList.get( "staleness").containsKey( fileName ) ) 
			staleness = fileStalenessList.get( "staleness").get( fileName );
		
		resultInFile.put( "F74", revision );
		resultInFile.put( "F83", staleness );
		
		return resultInFile;
	}
	
	public HashMap<String, HashMap<String, Integer>> obtainFileStalenessForAllFiles ( ArrayList<StaticWarning> warningList){
		HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> revisionResult = new HashMap<String, Integer>();
		HashMap<String, Integer> stalenessResult = new HashMap<String, Integer>();
		
		for ( int i =0; i < warningList.size(); i++ ){
			StaticWarning warning = warningList.get( i );
			String fileName = warning.getBugLocationList().get(0).getClassName();
			
			ArrayList<String> fileList = new ArrayList<String>();
			fileList.add( fileName );
			HashMap<String, Integer> resultInFile = this.obtainFileListStaleness(fileList);
			
			revisionResult.put( fileName, resultInFile.get("revision" ));
			stalenessResult.put( fileName , resultInFile.get( "staleness"));
		}
		result.put( "revision", revisionResult );
		result.put( "staleness", stalenessResult );
		
		return result;
	}
	
	public HashMap<String, Integer> obtainFileListStaleness ( ArrayList<String> fileList ){
		ArrayList<String> timeList = new ArrayList<String>();
		ArrayList<Integer> revisionNumberList = new ArrayList<Integer>();
		
		int lastRevisionNumber = 0;
		for ( int i = 0; i < fileList.size(); i++ ){
			String fileName = fileList.get( i );
			
			String sql = "SELECT * from " + Constants.COMMIT_CONTENT_TABLE + " where className like '%" + fileName 
					+ "'  and commitTime < '" + Constants.CURRENT_COMMIT_TIME + "' order by commitTime DESC";
			System.out.println( sql );
			ResultSet rs = dbOperation.DBSelect(sql);
			
			String lastChangeTime = Constants.GIVEN_EARLIEST_TIME;
			try {
				if ( rs.next() ){
					lastChangeTime = rs.getString( "commitTime");
					lastRevisionNumber = rs.getInt( "commitId");
				}
				
				timeList.add( lastChangeTime );
				revisionNumberList.add( lastRevisionNumber );
				
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int index = this.obtainLatestRevision(revisionNumberList);
		String lastChangeTimeAmongList = timeList.get( index );
		int lastRevisionNumberTotal = revisionNumberList.get( index );
				
		int fileStalenessRevisonNumber = 0;
		//之所以需要这么做，是因为revision的序号并不是按照时间来排列的；看两个时间间隔之间有多少个revision
		String sql = "SELECT count(*) from " + Constants.COMMIT_INFO_TABLE + " where commitTime >= '" + lastChangeTimeAmongList + "' and commitTime <= '" +
				Constants.CURRENT_COMMIT_TIME + "'";
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		try {
			if ( rs.next() ){
				fileStalenessRevisonNumber = Integer.parseInt( rs.getString( 1 )  );
			}
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put( "revision", lastRevisionNumberTotal );
		result.put( "staleness", fileStalenessRevisonNumber );
		
		System.out.println( lastRevisionNumber + " " + fileStalenessRevisonNumber );
		return result;
	}
	

	public String obtainLatestTime ( ArrayList<String> timeList ){
		if ( timeList.size() == 1 )
			return timeList.get( 0);
		
		List<Date> timeDateList = new ArrayList<Date>();
		SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
		for ( int i = 0; i < timeList.size(); i++ ){
			Date tempDate;
			try {
				tempDate = dateFormat.parse( timeList.get(i) );
				timeDateList.add( tempDate );
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Collections.sort( timeDateList, new Comparator<Date>(){
            public int compare(Date date1, Date date2) {        
               if ( date1.getTime() >= date2.getTime() )
            	   return -1;
               return 1;
            }
        });
		
		String latestTime = timeDateList.get(0).toString();
		return latestTime;
	}
	
	public int obtainLatestRevision ( ArrayList<Integer> revisionNumList ){
		int index = -1;
		//选取最小的revisonNumber，即为最近的
		int minRevisionNum = Constants.MAX_REVISION_NUMBER;
		for ( int i =0; i < revisionNumList.size(); i++ ){
			if ( minRevisionNum > revisionNumList.get(i )){
				minRevisionNum = revisionNumList.get(i);
				index = i;
			}				
		}
		
		return index;
	}
	
	public Integer extractFileDeletionRevison_F73 ( String fileName ){
		String sql = "SELECT * from " + Constants.COMMIT_CONTENT_TABLE + " where className like '%" + fileName 
				+ "' and commitTime >= '" + Constants.CURRENT_COMMIT_TIME + "' and commitType = 'D' order by commitTime";
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		int revisionNumber = Constants.MAX_REVISION_NUMBER ;
		try {
			if ( rs.next() ){
				revisionNumber = rs.getInt( "commitId");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println ( revisionNumber );
		return revisionNumber;
	}
	
	public static void main ( String args[] ){
		CodeHistoryFeatureExtraction extraction = new CodeHistoryFeatureExtraction();
		ArrayList<String> codeInfo = new ArrayList<String>();
		codeInfo.add( " return new IntersectTermsEnum(compiled, startTerm);");
		//extraction.extractAlertOpenRevision_F70( "lucene/codecs/src/java/org/apache/lucene/codecs/memory/FSTOrdTermsReader.java", codeInfo);
		String fileName = "lucene/codecs/src/java/org/apache/lucene/codecs/memory/FSTOrdTermsReader.java";
		
		extraction.extractFileDeletionRevison_F73(fileName);
	}
	
}
