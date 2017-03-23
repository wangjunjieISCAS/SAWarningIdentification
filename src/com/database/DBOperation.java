package com.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBOperation {
	private static final String DBDRIVER = "com.mysql.jdbc.Driver";
	private static final String DBURL = "jdbc:mysql://localhost:3306/";
	private static final String DBNAME = "staticwarning";
	private static final String DBUSER = "root";
	private static final String DBPASSWORD = "1234";
	private static Connection conn;
	private static Statement stmt;
	
	static {
		try {
			Class.forName(DBDRIVER);
			conn = DriverManager.getConnection( DBURL+DBNAME, DBUSER, DBPASSWORD);
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ResultSet DBSelect(String sql){
        ResultSet rs = null;
        try {
			rs = stmt.executeQuery( sql );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return rs;
    }
	
	public boolean DBUpdate ( String sql ){
		try {
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void DBClose() {
		try {
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
