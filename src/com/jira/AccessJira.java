package com.jira;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comon.Constants;
import com.database.DBOperation;

import sun.misc.BASE64Encoder;

public class AccessJira {

	private String url_pre = "https://issues.apache.org/jira/rest/";
	
	private DBOperation dbOperation;
	
	public AccessJira ( ){
		dbOperation = new DBOperation();
	}
	

	public void connectJira( String projectName ) {

		BASE64Encoder base64 = new BASE64Encoder();
		// String encoding =Base64.
		String id = "itechswang:19870130";
		String encoding = base64.encode(id.getBytes());
		int batch = 10;
		int issueAutoId = 0;
		
		int totalnub = 1285;
		int loopnub = totalnub / batch;
		for (int nub = 0; nub <= loopnub; nub++) {
			int tempstart = nub * batch;
			String url_string = url_pre
					+ "api/2/search?jql=project='" + projectName 
					+ "'&startAt=" + tempstart
					+ "&maxResults=" + batch + "&fields=issuetype&fields=summary&fields=key";
			System.out.println( url_string );
			// 发送rest请求
			URL url;
			InputStream in = null;
			char[] buffer = new char[50000];
			try {
				url = new URL(url_string);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic "
						+ encoding);
				
				in = (InputStream) connection.getInputStream();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			//对输入流进行格式处理
			//StringBulider builder = new StringBuilder();
			int bytesRead = 0;
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
				while ((bytesRead = reader.read(buffer)) != -1) {
					String temp = new String(buffer,0, bytesRead);
					System.out.println( temp );
					
					JSONObject json = new JSONObject ( temp );
					JSONArray jsonArray = json.getJSONArray( "issues" );
					for ( int j = 0; j < jsonArray.length(); j++ ){
						String key = jsonArray.getJSONObject(j).getString( "key");
						
						String type = jsonArray.getJSONObject(j).getJSONObject( "fields").getJSONObject("issuetype").getString( "name");
						//String type = jsonArrayType.getJSONObject(0).getString( "name");
						System.out.println( key + " " + type );
						
						this.storeToCommitContent( issueAutoId , key, type);
						issueAutoId++;
					}	
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		dbOperation.DBClose();
	}
	
	
	public void storeToCommitContent ( int issueAutoId, String issueId, String issueType ){
		String sql = "insert into " + Constants.ISSUE_TABLE + " values ( " + issueAutoId + ", '" +
				issueId + "', '" + issueType + "' ) "; 
				
		System.out.println ( sql );
		dbOperation.DBUpdate(sql);	
	}
	
	public static void main(String[] args) {
		AccessJira tl = new AccessJira();
		String projectName = "LANG";
		tl.connectJira( projectName );
		System.out.println("Success!");
	}

}