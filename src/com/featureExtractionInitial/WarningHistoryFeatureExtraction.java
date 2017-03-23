package com.featureExtractionInitial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.comon.Constants;
import com.comon.DateTimeTool;
import com.comon.StaticWarning;
import com.database.DBOperation;

public class WarningHistoryFeatureExtraction {
	private DBOperation dbOperation;
	
	public WarningHistoryFeatureExtraction ( ){
		dbOperation = new DBOperation();
	}
	
	
	/*
	 * 和openRevision类似， 如果codeInfo中有多行代码，则查找其50%以上代码都删除的第一个revisionNumber
	 * 
	 * 首先找到该文件在currentRevision之后哪些commit中发生了变更，然后在commit log中进行匹配
	 * deletion比open逻辑更复杂的地方在于:
	 * 有时候 - XXXX, 但后来还有 + XXXX.
	 * 也就是说某语句，虽然删除，但又添加上一模一样的语句。这种情况需要排除
	 * 
	 * type 分为 "bug fix", "non bug fix", "all"
	 * 分别表示只在bug fix change里面找，只在non bug fix里面找，以及在所有的里面找
	 * 
	 * 注！！！！
	 * 该函数同时返回了某warning被修复前整个文件被删除的情况，应该先调用该函数，得到哪些warning在修复前文件被删除了，将该种情况去掉
	 * 
	 * 如果revisionNumber = -1，则表示还没有close
	 */
	public HashMap<String, Object> obtainAlertCloseTimeRevision ( int index, StaticWarning warning, String type, String closeEndTime ){
		String fileName = warning.getBugLocationList().get(0).getClassName();
		ArrayList<String> codeInfo = new ArrayList<String>();
		
		for ( int j = 0;  j < warning.getBugLocationList().size(); j ++){
			codeInfo.addAll( warning.getBugLocationList().get(j).getCodeInfoList()  );
		}
		
		String typeSql = "";
		if ( type.equals( "bug fix"))
			typeSql = "and issueType = 'BUG'";
		if ( type.equals( "non bug fix"))
			typeSql = "and issueType != 'BUG'";
			
		if ( closeEndTime.equals( ""))
			closeEndTime = Constants.CURRENT_TIME;
		
		String sql = "SELECT * from " + Constants.COMMIT_CONTENT_TABLE + " where className like '%" + fileName 
				+ "'  and commitTime > '" + Constants.CURRENT_COMMIT_TIME + "' and commitTime <= '" + closeEndTime + "' "
				+ typeSql + " order by commitTime ";
		
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
	
		int revisionNumber = -1;
		String commitTime = "";
		boolean isDeletion = false;
		try {
			while ( rs.next() ){
				String commitId = rs.getString( "commitId");
				commitTime = rs.getString( "commitTime");
				
				//可能存在删除该file的情况，这种情况单独标记出来，可能会从训练集中去除
				String commitType = rs.getString( "commitType");
				if ( commitType.equals( "D")){
					revisionNumber = Integer.parseInt( commitId );
					isDeletion = true;
					break;
				}
				
				BufferedReader br = new BufferedReader(new FileReader( new File ( Constants.LOG_CODE_FOLDER_OUT + commitId + ".txt" ) ));
				String line = "";
				boolean isRelatedFile = false;
				ArrayList<String> addCodeList = new ArrayList<String>();
				ArrayList<String> deleteCodeList = new ArrayList<String>();
				
				ArrayList<String> writeCodeList = new ArrayList<String>();
				
				while ( ( line = br.readLine() ) != null ) {
					//fileName相关的文件已经遍历完了
					if ( line.startsWith( "diff") && isRelatedFile == true )
						break;
					//接下来的都是与fileName文件相关的
					if ( isRelatedFile == false && line.contains( fileName ))
						isRelatedFile = true;
					//如果是不相关的，继续
					if ( isRelatedFile == false )
						continue;
					
					writeCodeList.add( line );
					if ( line.startsWith( "+ ")  ){
						line = line.substring( 2);
						line = line.trim();
						if ( line.equals( ""))
							continue;
						addCodeList.add( line );
					}
					if ( line.startsWith( "- ")){
						line = line.substring( 2);
						line = line.trim();
						if ( line.equals( ""))
							continue;
						deleteCodeList.add( line );
					}
				}
				
				//将addCodeList和deleteCodeList中的文件进行对比，将同样的去掉
				ArrayList<String> refinedDeleteCodeList = new ArrayList<String>();
				for ( int i =0; i < deleteCodeList.size(); i++  ){
					String deleteCode = deleteCodeList.get( i );
					if ( addCodeList.contains( deleteCode ))
						continue;
					refinedDeleteCodeList.add( deleteCode );
				}
				
				int equalTimes = 0;
				for ( int i = 0; i < codeInfo.size(); i++ ){
					String codeQuery = codeInfo.get(i).trim();
					if ( refinedDeleteCodeList.contains( codeQuery )){
						equalTimes ++;
					}
				}
				
				if ( equalTimes >= 1 ){
					BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( Constants.GROUND_TRUTH_FOLDER + index +".txt" )) , "GB2312"), 1024);
					output.write( "category: " + warning.getBugInfo().getCategory() + "\r\n");
					output.write( "type: " + warning.getBugInfo().getType() + "\r\n");
					output.write( "class name: " + warning.getBugLocationList().get(0).getClassName() + "\r\n");
					for ( int i =0; i < warning.getBugLocationList().size(); i++ ){
						output.write( i + " method name: " + warning.getBugLocationList().get(i).getRelatedMethodName() + "\r\n");
						output.write( "start line: " + warning.getBugLocationList().get(i).getStartLine() + "\r\n" );
						output.write( "end line: " + warning.getBugLocationList().get(i).getEndLine() + "\r\n" );
						for ( int j =0; j < warning.getBugLocationList().get(i).getCodeInfoList().size(); j++ )
							output.write( "code: " + warning.getBugLocationList().get(i).getCodeInfoList().get(j) + "\r\n" );
						output.newLine();
					}
					
					output.write( "=======================================================================");
					output.newLine();
					
					if ( isDeletion == true ){
						output.write( "is delete ?  yes!" );
					}else{
						output.write( "is delete ?  no!");
					}
					output.newLine();
					output.write( "=======================================================================");
					output.newLine();
					
					for ( int i =0; i < writeCodeList.size(); i++ ){
						output.write( writeCodeList.get( i ));
						output.newLine();
					}
					output.flush();
					output.close();
				}
				
				//1行代码，1行匹配就退出；2行代码，1行匹配就退出
				if ( equalTimes >= (1+ codeInfo.size()) / 2 ){
				//if ( equalTimes >= 1 ){
					revisionNumber = Integer.parseInt( commitId );
					break;
				}							
			
				if ( revisionNumber != -1 )
					break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put( "revision", revisionNumber );
		result.put( "time", commitTime );
		result.put( "isDeletion", isDeletion );
		
		System.out.println( revisionNumber + " " + commitTime + " " + isDeletion );
		return result;
	}
	
	/*
	 * 找打该codeInfo代码行第一次出现的revisionNumber
	 * 如果codeInfo中有多行代码，则查找其50%以上代码都出现的第一个revisionNumber
	 * 
	 * 首先找到该文件在哪些commit中发生了变更，然后在commit log中进行匹配
	 */
	public HashMap<String, Object>  obtainAlertOpenRevision( String fileName, ArrayList<String> codeInfo ){
		String sql = "SELECT * from " + Constants.COMMIT_CONTENT_TABLE + " where className like '%" + fileName 
				+ "'  and commitTime <= '" + Constants.CURRENT_COMMIT_TIME + "' order by commitTime ";
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		int revisionNumber = -1;
		int equalTimes = 0;
		String commitTime = "";
		try {
			while ( rs.next() ){
				String commitId = rs.getString( "commitId");
				commitTime = rs.getString( "commitTime");
				
				BufferedReader br = new BufferedReader(new FileReader( new File ( Constants.LOG_CODE_FOLDER_OUT + commitId + ".txt" ) ));
				String line = "";
				boolean isRelatedFile = false;
				while ( ( line = br.readLine() ) != null ) {
					// 一次commit会含有多个文件，找和fileName相同的文件的
					if ( line.contains( fileName ))
						isRelatedFile = true;
					if ( isRelatedFile == false )
						continue;
					
					if ( line.startsWith( "+ ") ){
						line = line.substring( 2);
						line = line.trim();
						if ( line.equals( ""))
							continue;
						
						for ( int i = 0; i < codeInfo.size(); i++ ){
							if ( line.equals( codeInfo.get(i).trim() )){
								equalTimes ++;
							}
						}
						//1行代码，1行匹配就退出；2行代码，1行匹配就退出
						if ( equalTimes >= (1+ codeInfo.size()) / 2 ){
							revisionNumber = Integer.parseInt( commitId );
							break;
						}							
					}
				}
				if ( revisionNumber != -1 )
					break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( revisionNumber == -1 ){
			System.out.println( "could not find the alert open revision!");
			revisionNumber = Constants.GIVEN_EARLIEST_REVISION_NUMBER;
			commitTime = Constants.GIVEN_EARLIEST_TIME;
		}
			
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put( "revision", revisionNumber );
		result.put( "time", commitTime);
		
		System.out.println ( revisionNumber  + " " + commitTime );
		return result;
	}
	
	//先运行obtainAlertOpenRevisionForAllFiles，得到所有warning的openRevisionTimeList
	public Integer extractAlertOpenRevisionTime_F70 ( int index, HashMap<String, HashMap<Integer, Object>> openRevisionTimeList ){
		int revision = -1;
		if ( openRevisionTimeList.get("revision").containsKey( index ))
			revision = (int) openRevisionTimeList.get("revision").get( index );
		
		return revision;
	}
	
	public HashMap<String, HashMap<Integer, Object>> obtainAlertOpenRevisionForAllFiles ( ArrayList<StaticWarning> warningList ){
		HashMap<String, HashMap<Integer, Object>> result = new HashMap<String, HashMap<Integer, Object>>();
		//key 为 warning的 index
		HashMap<Integer, Object> revisionResult = new HashMap<Integer, Object>();
		HashMap<Integer, Object> timeResult = new HashMap<Integer, Object>();
		
		for ( int i =0; i < warningList.size(); i++ ){
			StaticWarning warning = warningList.get( i );
			String fileName = warning.getBugLocationList().get(0).getClassName();
			
			ArrayList<String> codeInfo = new ArrayList<String>();
			for ( int j = 0; j < warning.getBugLocationList().size(); j++ ){
				ArrayList<String> temp = warning.getBugLocationList().get(j).getCodeInfoList();
				codeInfo.addAll( temp );
			}		
				
			HashMap<String, Object> resultInFile = this.obtainAlertOpenRevision( fileName, codeInfo);
			
			revisionResult.put( i, resultInFile.get( "revision"));
			timeResult.put( i, resultInFile.get( "time" ));
		}
		result.put( "revision", revisionResult );
		result.put( "time", timeResult );
		
		return result;
	}
	
	//在CodeHistoryExtraction中调用，只调用一次，得到所有file的 openRevisionNumber
	public HashMap<Integer, Integer> obtainAlertOpenRevisionNumberForAllFiles ( ArrayList<StaticWarning> warningList ){
		HashMap<Integer, Integer> openRevisionNumber = new HashMap<Integer, Integer>();
		for ( int i =0; i < warningList.size(); i++ ){
			StaticWarning warning = warningList.get( i );
			String fileName = warning.getBugLocationList().get(0).getClassName();
			
			ArrayList<String> codeInfo = new ArrayList<String>();
			for ( int j = 0; j < warning.getBugLocationList().size(); j++ ){
				ArrayList<String> temp = warning.getBugLocationList().get(j).getCodeInfoList();
				codeInfo.addAll( temp );
			}		
				
			HashMap<String, Object> result = this.obtainAlertOpenRevision(fileName, codeInfo);
			int revision = (int) result.get( "revision");
			openRevisionNumber.put( i , revision );
		}
		return openRevisionNumber;
	}
	
	
	public Integer extractAlertModification_F61 ( StaticWarning warning, int index, HashMap<String, HashMap<Integer, Object>> openRevisionTimeList ){
		int modifyNum = 0;
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		String openTime = Constants.CURRENT_COMMIT_TIME;
		if ( openRevisionTimeList.get( "time" ).containsKey( index ) ) 
			openTime = (String) openRevisionTimeList.get( "time" ).get( index );
		
		String sql = "SELECT count(*) from " + Constants.COMMIT_CONTENT_TABLE + " where className like '%" + fileName 
				+ "'  and commitTime <= '" + Constants.CURRENT_COMMIT_TIME + "' and commitTime > '" + openTime + "'";
		System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		try {
			if ( rs.next() ){
				modifyNum = Integer.parseInt( rs.getString( 1 ));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return modifyNum;
	}
	
	public Integer extractAlertLifeRevision_F77 ( int index , HashMap<String, HashMap<Integer, Object>> openRevisionTimeList ){
		Integer openRevision = -1;
		if ( openRevisionTimeList.get( "revision" ).containsKey( index ) ) 
			openRevision = (Integer) openRevisionTimeList.get( "revision" ).get( index );
		
		Integer currentRevisin = Constants.CURRENT_REVISION_NUMBER;
		int lifeRevision = openRevision - currentRevisin ;
		
		return lifeRevision;
	}
	
	public Integer extractAlertLifeTime_F88 ( int index,  HashMap<String, HashMap<Integer, Object>> openRevisionTimeList ){
		String openTime = Constants.CURRENT_COMMIT_TIME;
		if ( openRevisionTimeList.get( "time" ).containsKey( index ) ) 
			openTime = (String) openRevisionTimeList.get( "time" ).get( index );
		 
		String currentTime = Constants.CURRENT_COMMIT_TIME;	
		int dayGap = DateTimeTool.obtainDayGap( openTime, currentTime );
		
		return dayGap;
	}
	
	
	//对于该revision所有的warning，统计其是否closed（fixed），也就是说是否为真正的bug
	public ArrayList<String> obtainWarningStatus ( ArrayList<StaticWarning> warningList, String type ){
		ArrayList<String> warningStatusList = new ArrayList<String>();
		
		for ( int i = 0;  i < warningList.size(); i++ ){
			StaticWarning warning = warningList.get( i );
			String fileName = warning.getBugLocationList().get(0).getClassName();
			ArrayList<String> codeInfo = new ArrayList<String>();
			
			for ( int j = 0;  j < warning.getBugLocationList().size(); j ++){
				codeInfo.addAll( warning.getBugLocationList().get(j).getCodeInfoList()  );
			}
			
			HashMap<String, Object> status = this.obtainAlertCloseTimeRevision(i,  warning, type, "");
			if ( (int)status.get("revision") == -1 ){
				warningStatusList.add( "open");
			}else if ( (boolean)status.get("isDeletion") == true ){
				System.out.println ( "the file is deleted!");
				warningStatusList.add( "deleted");
			}else{
				warningStatusList.add( "close");
			}
		}
		
		return warningStatusList;
	}	
		
	public static void main ( String args[] ){
		WarningHistoryFeatureExtraction extraction = new WarningHistoryFeatureExtraction();
		
		ArrayList<String> codeInfo = new ArrayList<String>();
		codeInfo.add( "private static final boolean VERBOSE = false;");
		codeInfo.add( "private final static class TermsWriter extends TermsConsumer {");
		codeInfo.add( "if (VERBOSE) System.out.println(\"    startDoc docID=\" + docID + \" freq=\" + termDocFreq);");
		codeInfo.add( "assert docID == 0 || delta > 0;");
		codeInfo.add( "lastPos = pos;");
		
		String fileName = "lucene/core/src/java/org/apache/lucene/codecs/memory/MemoryPostingsFormat.java";
		
		//extraction.obtainAlertCloseTimeRevision( fileName, codeInfo, "all", "");
		
	}
	
}
