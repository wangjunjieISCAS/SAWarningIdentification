package com.git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.comon.Constants;
import com.database.DBOperation;

/*
 * 给出文件名和代码语句的名称
 * 首先根据文件名进行数据库搜索，得到该文件在哪些commit里面修改过；
 * 然后基于代码语句在这些commit里面搜索，找到包含该语句的commit。输出对应的commitId，以及该commit的内容
 */
public class CodeSearcher {
	private DBOperation dbOperation;
	
	public CodeSearcher ( ){
		dbOperation = new DBOperation();
	}
	
	//SELECT * FROM staticwarning.commit_info where commitTime > "2010-02-09 00:00:00" and 
	//commitAutoId in (select commitId from staticwarning.commit_content where className like "%/IndexHTML.java") order by commitTime;
	public ArrayList<String> obtainCommitIds ( String fileName ){
		String sql = "SELECT * FROM " + Constants.COMMIT_INFO_TABLE + " where commitTime > \"2010-02-09 00:00:00\" and "
				+ "commitAutoId in (select commitId from " + Constants.COMMIT_CONTENT_TABLE + " where className like \"%/" + fileName + ""
						+ ".java\") order by commitTime ";
		//System.out.println( sql );
		ResultSet rs = dbOperation.DBSelect(sql);
		
		ArrayList<String> commitIdList = new ArrayList<String>();
		try {
			while ( rs.next() ){
				commitIdList.add(  rs.getString( "commitAutoId"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return commitIdList;
	}
	
	public void searchCodeLine ( ArrayList<String> commitIdList , String codeLine, String warningFileName ){
		//System.out.println( "commitIdList: " +  commitIdList.size() );
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( Constants.WARNING_FILE_OUT ), true ) , "GB2312"), 1024);
			output.write( "warning file name: " + warningFileName );
			output.write( "  warning code line: " + codeLine );
			output.newLine();
			output.flush();
			
			for ( int i = 0; i < commitIdList.size(); i++ ){
				String fileName = Constants.LOG_CODE_FOLDER_OUT + commitIdList.get( i ) + ".txt";
				String line = "";
				ArrayList<String> codeLineList = new ArrayList<String>();
				boolean flag = false;
				
				try {
					BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
					while ( ( line = br.readLine() ) != null ) {
						codeLineList.add( line );
						
						if ( line.contains( codeLine ) && !flag ){
							System.out.println( "fileName : " + fileName + " commitId: " + commitIdList.get( i ));		
							output.write( " commitId: " + commitIdList.get( i ) );
							output.newLine();
							flag = true;
						}
					}
					
					if ( flag == true ){
						for ( int j = 0; j < codeLineList.size(); j++ ){
							output.write( codeLineList.get( j ));
							output.newLine();
						}
					}
					output.flush();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			System.out.println( "---------------------------------------------------");
			output.write( "=========================================================");
			output.newLine();
			output.write ("=========================================================");
			output.newLine();
			output.flush();
			
			output.close();
			
		} catch (UnsupportedEncodingException | FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ArrayList<ArrayList<String>> obtainFileNameCodeLine (  ){
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> fileNameList = new ArrayList<String>();
		ArrayList<String> codeLineList = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( Constants.WARNING_FILE_NAME )));
			String line = "";
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
				if ( temp.length < 2 )
					continue;
				fileNameList.add( temp[0].trim() );
				codeLineList.add( temp[1].trim() );
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.add( fileNameList );
		result.add( codeLineList );
		
		return result;
	}
	
	public static void main ( String args[] ){
		CodeSearcher searcher = new CodeSearcher();
		ArrayList<ArrayList<String>> result = searcher.obtainFileNameCodeLine() ;
		ArrayList<String> fileNameList = result.get( 0);
		ArrayList<String> codeLineList = result.get( 1);
		
		for ( int i = 0 ; i < fileNameList.size(); i++ ){
			String fileName = fileNameList.get( i );
			String codeLine = codeLineList.get( i );
			ArrayList commitIdList = searcher.obtainCommitIds(  fileName );
			searcher.searchCodeLine(commitIdList, codeLine, fileName  );
		}	
		
		searcher.dbOperation.DBClose();
	}
}
