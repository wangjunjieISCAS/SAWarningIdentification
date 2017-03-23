package com.git;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.comon.Constants;
import com.database.DBOperation;


/*
 * 运行完LogParser后，数据表commit_info和commit_content已经有内容
 * 运行该程序，对其中的内容进行重新组织
 */

public class DatabaseReOrganization {
	
	private DBOperation dbOperation;
	
	public DatabaseReOrganization ( ){
		dbOperation = new DBOperation();
	}
	
	/*
	 * 不需要了，在logParser中已经实现了
	 */
	public void reOrganizationCommitTime ( ){
		String sql = "SELECT * FROM " + Constants.COMMIT_CONTENT_TABLE;
		//System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		ArrayList<String> commitIdList = new ArrayList<String>();
		ArrayList<String> contentIdList = new ArrayList<String>();
		try {
			while ( rs.next() ){
				String commitId = rs.getString( "commitId");
				commitIdList.add( commitId );
				
				String contentId = rs.getString( "contentId");
				contentIdList.add( contentId );
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//contentIdList 和  commitIdList 是同样大小的
		for ( int i = 0; i < contentIdList.size(); i++ ){
			String commitId = commitIdList.get( i );
			int commitIdInt = Integer.parseInt( commitId );
			
			String sqlCommitId = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE + " where commitAutoId = " + commitIdInt;
			ResultSet rsId = dbOperation.DBSelect(sqlCommitId);
			String time = "";
			try {
				if ( rsId.next() ){
					time = rsId.getString( "commitTime");
				}
				rsId.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String contentId = contentIdList.get( i );
			int contentIdInt = Integer.parseInt( contentId );
			String sqlTime = "update " + Constants.COMMIT_CONTENT_TABLE + " set commitTime = \"" + time + 
					"\" where contentId = " + contentIdInt;
			System.out.println( sqlTime );
			dbOperation.DBUpdate(sqlTime);
		}
	}
	
	//在LogParser时，没有将issueId提取出来。用正则表达式提取
	//对于Cass项目 
	public void retrieveIssueIdFromCommit ( ){
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE;
		//System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		ArrayList<String> commitIdList = new ArrayList<String>();
		ArrayList<String> issueIdList = new ArrayList<String>();
		
		try {
			while ( rs.next() ){
				String commitId = rs.getString( "commitAutoId");
				commitIdList.add( commitId );
				String issueName = rs.getString( "issueName");
				issueName = issueName.toLowerCase();
				
				String issueId = "0";
				Pattern pattern = Pattern.compile(  "CASSANDRA-\\d+");
				Matcher matcher = pattern.matcher( issueName );
				if ( matcher.find() ){
					issueId =  matcher.group(0);
				}	
				issueIdList.add( issueId );
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for ( int i = 0; i < commitIdList.size(); i++ ){
			String commitId = commitIdList.get( i );
			String issueId = issueIdList.get( i );
			
			String sqlTime = "update " + Constants.COMMIT_INFO_TABLE + " set issueId = \"" + issueId + 
					"\" where commitAutoId = " + commitId;
			System.out.println( sqlTime );
			dbOperation.DBUpdate(sqlTime);
		}		
	}
	/*
	 * 根据commit message中的关键字 确定issueType
	 * maven 用完jira后，也需要用这个
	 */
	public void reOrganizationIssueTypeFromCommitTomcat ( ){
		String[] notContainTerm = { "spelling", "typo", "javadoc", "typos", "docs", "indentation", "indent"};
		
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE;
		//System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		ArrayList<String> commitIdList = new ArrayList<String>();
		ArrayList<String> issueIdList = new ArrayList<String>();
		ArrayList<String> issueTypeList = new ArrayList<String>();
		ArrayList<String> commitMessageList = new ArrayList<String>();
		
		try {
			while ( rs.next() ){
				String commitId = rs.getString( "commitAutoId");
				commitIdList.add( commitId );
				
				String issueId = rs.getString( "issueId").trim();
				issueIdList.add( issueId );
				commitMessageList.add( rs.getString( "issueName"));
				
				issueTypeList.add( rs.getString( "issueType"));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//contentIdList 和  commitIdList 是同样大小的
		for ( int i = 0; i < commitIdList.size(); i++ ){
			String commitId = commitIdList.get( i );
			String issueId = issueIdList.get( i );
			
			String issueType = issueTypeList.get( i );
			String commitMessage = commitMessageList.get( i );
			commitMessage = commitMessage.toLowerCase();
			
			boolean isNotContanTerm = false;
			for ( int j = 0 ; j < notContainTerm.length; j++ ){
				String temp = notContainTerm[j].toLowerCase();
				if ( commitMessage.contains( temp )){
					isNotContanTerm = true;
				}
			}
			
			if ( commitMessage.contains( "fix") && !isNotContanTerm && issueId.length() <= 1 ){
				issueType = "BUG";
			}
			
			/*
			 * 对于ant项目,这种pattern， bug 50217, Bug-60628, Bugzilla-60349, /bugzilla/show_bug.cgi?id=60172
			 */
			/*
			Pattern pattern = Pattern.compile(  "bug \\d+");
			Matcher matcher = pattern.matcher( commitMessage );
			if ( matcher.find() ){
				issueType = "BUG";
			}	
			pattern = Pattern.compile(  "bugzilla\\w+\\d+");
			matcher = pattern.matcher( commitMessage );
			if ( matcher.find() ){
				issueType = "BUG";
			}	
			*/
			/*
			 * 对于aspectJ, bug id号，连续5位以上的数字 -------- 这种噪声太多,在bugzilla中也会存在improvement
			 * Fix 485055, Bug 467415
			 * 对于eclipse.jdt.core，也是这样的
			 */
			Pattern pattern = Pattern.compile(  "bug \\d+");
			Matcher matcher = pattern.matcher( commitMessage );
			if ( matcher.find() ){
				issueType = "BUG";
			}
			
			String sqlTime = "update " + Constants.COMMIT_INFO_TABLE + " set issueType = \"" + issueType + 
					"\" where commitAutoId = " + commitId;
			System.out.println( sqlTime );
			dbOperation.DBUpdate(sqlTime);
		}
	}
	
	
	public void reOrganizationIssueType ( ){
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE;
		//System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		ArrayList<String> commitIdList = new ArrayList<String>();
		ArrayList<String> issueIdList = new ArrayList<String>();
		try {
			while ( rs.next() ){
				String commitId = rs.getString( "commitAutoId");
				commitIdList.add( commitId );
				
				String issueId = rs.getString( "issueId").trim();
				issueIdList.add( issueId );
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//contentIdList 和  commitIdList 是同样大小的
		for ( int i = 0; i < commitIdList.size(); i++ ){
			String commitId = commitIdList.get( i );
			int commitIdInt = Integer.parseInt( commitId );
			String issueId = issueIdList.get( i );
			
			//如果没有这个编号，全部算作"Task"类别
			String issueType = "Task";
			if ( !issueId.equals( "")){
				String sqlCommitId = "SELECT * FROM " + Constants.ISSUE_TABLE + " where issueId = '" + issueId + "'";
				ResultSet rsId = dbOperation.DBSelect(sqlCommitId);
				try {
					if ( rsId.next() ){
						issueType = rsId.getString( "issueType");
					}
					rsId.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
			String sqlTime = "update " + Constants.COMMIT_INFO_TABLE + " set issueType = \"" + issueType + 
					"\" where commitAutoId = " + commitId;
			System.out.println( sqlTime );
			dbOperation.DBUpdate(sqlTime);
		}
	}
	
	
	public void reOrganizationIssueTypeContentTable ( ){
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE;
		//System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		ArrayList<String> commitIdList = new ArrayList<String>();
		ArrayList<String> issueTypeList = new ArrayList<String>();
		try {
			while ( rs.next() ){
				String commitId = rs.getString( "commitAutoId");
				commitIdList.add( commitId );
				
				String issueId = rs.getString( "issueType");
				issueTypeList.add( issueId );
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//contentIdList 和  commitIdList 是同样大小的
		for ( int i = 0; i < commitIdList.size(); i++ ){
			String commitId = commitIdList.get( i );
			int commitIdInt = Integer.parseInt( commitId );
			String issueType = issueTypeList.get( i );
			
			//如果没有这个编号，全部算作"Task"类别
			String sqlCommitId = "SELECT * FROM " + Constants.COMMIT_CONTENT_TABLE + " where commitId = " + commitIdInt;
			ResultSet rsId = dbOperation.DBSelect(sqlCommitId);
			ArrayList<Integer> contentIdList = new ArrayList<Integer>();
			try {
				while ( rsId.next() ){
					int contentId = Integer.parseInt( rsId.getString( "contentId") );
					contentIdList.add( contentId );
				}
				rsId.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for ( int j=0; j < contentIdList.size(); j++ ){
				String sqlTime = "update " + Constants.COMMIT_CONTENT_TABLE + " set issueType = \"" + issueType + 
						"\" where contentId = " + contentIdList.get( j );
				System.out.println( sqlTime );
				dbOperation.DBUpdate(sqlTime);
			}
		}
	}
	
	//都需要执行reOrganizationIssueTypeContentTable,
	//根据是否存在issueType表，决定执行reOrganizationIssueType(存在issueType表) 还是 reOrganizationIssueTypeFromCommit(不存在issueType表)
	//有些项目需要先用reOrganizationIssueType(存在issueType表)，再用 reOrganizationIssueTypeFromCommit，因为有些时候jira中记录的信息不全
	
	//对于Cass项目，先执行retrieveIssueIdFromCommit，后面的和其他的一样
	public static void main ( String args[] ){
		DatabaseReOrganization operation = new DatabaseReOrganization();
		//不需要了，在logParser中已经实现了
		//operation.reOrganizationCommitTime();
		
		//only for Cass 项目
		//operation.retrieveIssueIdFromCommit ();
		
		//operation.reOrganizationIssueType();
		//operation.reOrganizationIssueTypeFromCommitTomcat();
		
		operation.reOrganizationIssueTypeContentTable(); 
		operation.dbOperation.DBClose();
	}
}
