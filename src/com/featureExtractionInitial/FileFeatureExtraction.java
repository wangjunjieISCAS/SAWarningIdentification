package com.featureExtractionInitial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comon.Constants;
import com.comon.DateTimeTool;
import com.comon.ProjectInfo;
import com.database.DBOperation;

public class FileFeatureExtraction {
	private DBOperation dbOperation;
	
	public FileFeatureExtraction ( ){
		dbOperation = new DBOperation();
	}
	
	public Map<String, Object> extractFileAge_F25_F72 ( String fileName ){
		Map<String, Object> result = new HashMap<String, Object>();
		
		Integer age = 0;
		String revisionNumber = "";
		
		String sql = "SELECT * FROM " + Constants.COMMIT_CONTENT_TABLE + " where className like '%" + fileName + "'  and commitTime < '" + 
				Constants.CURRENT_COMMIT_TIME + "' and commitType = \"A\" ";
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		String createTime = "";
		try {
			if ( rs.next() ){
				createTime = rs.getString( "commitTime");
				revisionNumber = rs.getString( "commitId");
			}
			rs.close();
			
			if ( createTime.equals( "")){
				System.out.println( "cannot find the create time for file : " + fileName );
				sql = "SELECT * FROM " + Constants.COMMIT_CONTENT_TABLE + " where className like '%"  + fileName + "' and commitTime < '" + 
						Constants.CURRENT_COMMIT_TIME + "' order by commitTime ";
				System.out.println( sql );
				ResultSet rsTime = dbOperation.DBSelect(sql);
				if ( rsTime.next() ){
					createTime = rsTime.getString( "commitTime");
					revisionNumber = rsTime.getString( "commitId");
				}
				else{
					System.out.println ( "do not find the history of file: " + fileName );
					createTime = Constants.CURRENT_COMMIT_TIME;
					revisionNumber = new Integer ( Constants.CURRENT_REVISION_NUMBER ).toString() ;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		age = DateTimeTool.obtainDayGap(createTime , Constants.CURRENT_COMMIT_TIME );
		
		result.put( "F25", age);
		result.put( "F72", revisionNumber );
		System.out.println( "age: " + age  + " revisionNumber: " + revisionNumber  );
		return result;
	}
	
	public boolean extractFileExtension_F26 ( String fileName ){
		boolean isJavaFile = false;
		if ( fileName.endsWith( ".java")){
			isJavaFile = true;
		}
		//System.out.println( isJavaFile );
		return isJavaFile;
	}
	
	public HashMap<String, String> extractFilePackageProjectName_F53_to_F55 ( ProjectInfo projectInfo, String fileName  ){
		HashMap<String, String> fileInfo = new HashMap<String, String>();
		fileInfo.put( "F55", fileName );
		
		String packageName = projectInfo.getFilePackageNameMap().get( fileName );
		fileInfo.put( "F54", packageName );
		
		String projectName = Constants.PROJECT_NAME;
		fileInfo.put( "F53", projectName );
		
		return fileInfo;
	}
	
	
	 
	public static void main ( String args[] ){
		FileFeatureExtraction extraction = new FileFeatureExtraction();
		
		extraction.extractFileAge_F25_F72( "lucene/codecs/src/java/org/apache/lucene/codecs/memory/FSTTermsReader.java");		
	}
	
}
