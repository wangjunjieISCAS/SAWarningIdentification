package com.comon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.ece.qhanam.slicer.Slicer;

public class Constants {
	
	public final static String COMMIT_INFO_TABLE = "lucene_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "lucene_commit_content";
	public final static String ISSUE_TABLE = "lucene_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-lucene/";
	
	public final static String WARNING_FILE_NAME = "data/warning-lucene-201504.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//lucene-201504//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2015-04-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 3891;
	
	public final static String GIVEN_EARLIEST_TIME = "2001-09-11 21:44:36.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 25862;
	public final static Integer MAX_REVISION_NUMBER = 25862;
	
	/*
	public final static String COMMIT_INFO_TABLE = "tomcat_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "tomcat_commit_content";
	public final static String ISSUE_TABLE = "tomcat_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-tomcat/";
	
	public final static String WARNING_FILE_NAME = "data/warning-tomcat-201504.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//tomcat-201504//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2015-04-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 3354;
	
	public final static String GIVEN_EARLIEST_TIME = "2006-03-27 13:53:46";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 17995;
	public final static Integer MAX_REVISION_NUMBER = 17995;
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "mvn_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "mvn_commit_content";
	public final static String ISSUE_TABLE = "mvn_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-maven/";
	
	public final static String WARNING_FILE_NAME = "data/warning-mvn-201112.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//maven-201112//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2011-12-01 00:00:00 "; 
	public final static int CURRENT_REVISION_NUMBER = 1018;
	
	public final static String GIVEN_EARLIEST_TIME = "2003-09-01 16:05:50.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 10178;
	public final static Integer MAX_REVISION_NUMBER = 10178;
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "cass_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "cass_commit_content";
	public final static String ISSUE_TABLE = "cass_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-cass/";
	
	public final static String WARNING_FILE_NAME = "data/warning-cass-201510.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//cass-201510//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2015-10-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 1732;
	
	public final static String GIVEN_EARLIEST_TIME = "2009-03-02 07:57:22.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 13775;
	public final static Integer MAX_REVISION_NUMBER = 13775;
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "ant_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "ant_commit_content";
	public final static String ISSUE_TABLE = "ant_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-ant/";
	
	public final static String WARNING_FILE_NAME = "data/warning-ant-201101.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//ant-201101//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2011-01-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 1034;
	
	public final static String GIVEN_EARLIEST_TIME = "2000-01-13 10:15:43.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 13497;
	public final static Integer MAX_REVISION_NUMBER = 13497;
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "aspect_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "aspect_commit_content";
	public final static String ISSUE_TABLE = "aspect_issue_info";
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "eclipse_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "eclipse_commit_content";
	public final static String ISSUE_TABLE = "eclipse_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-eclipse/";
	
	public final static String WARNING_FILE_NAME = "data/warning-eclipse-201501.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//eclipse-201501//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2015-01-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 1006;
	
	public final static String GIVEN_EARLIEST_TIME = "2001-06-05 16:17:58.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 22395;
	public final static Integer MAX_REVISION_NUMBER = 22395;
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "commons_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "commons_commit_content";
	public final static String ISSUE_TABLE = "commons_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-commons/";
	
	public final static String WARNING_FILE_NAME = "data/warning-commons-201201.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//commons-201201//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2012-01-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 1541;
	
	public final static String GIVEN_EARLIEST_TIME = "2002-07-19 03:35:56.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 4805;
	public final static Integer MAX_REVISION_NUMBER = 4805;
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "derby_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "derby_commit_content";
	public final static String ISSUE_TABLE = "derby_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-derby/";
	
	public final static String WARNING_FILE_NAME = "data/warning-derby-201404.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//derby-201404//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2014-04-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 435;
	
	public final static String GIVEN_EARLIEST_TIME = "2004-08-11 21:07:37.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 8135;
	public final static Integer MAX_REVISION_NUMBER = 8135;
	*/
	/*
	public final static String COMMIT_INFO_TABLE = "jmeter_commit_info";
	public final static String COMMIT_CONTENT_TABLE = "jmeter_commit_content";
	public final static String ISSUE_TABLE = "jmeter_issue_info";
	public final static String LOG_CODE_FOLDER_OUT  = "data/logcode-jmeter/";
	
	public final static String WARNING_FILE_NAME = "data/warning-jmeter-201512.xml";
	public final static String FOLDER_NAME = "D://java-workstation//experimentalProject//jmeter-201512//";
	
	public final static String CURRENT_COMMIT_TIME 	= "2015-12-01 00:00:00"; 
	public final static int CURRENT_REVISION_NUMBER = 2655;
	
	public final static String GIVEN_EARLIEST_TIME = "1998-09-03 00:24:13.000000";   
	public final static Integer GIVEN_EARLIEST_REVISION_NUMBER = 14044;
	public final static Integer MAX_REVISION_NUMBER = 14044;
		*/
	public final static String LOG_FILE_IN = "data/log.txt";
	public final static String LOG_CODE_FILE_IN = "data/logCode.txt";
	
	public final static String GROUND_TRUTH_FOLDER = "data/groundtruth/";
	
	//public final static String WARNING_FILE_IN = "data/warning.csv";
	public final static String WARNING_FILE_OUT = "data/warning-result.txt";
	
	public final static String PROJECT_NAME = "apache";
	
	public static enum BUG_LOCATION_REGION_TYPE{
		CLASS, FIELD, METHOD, TYPE, DEFAULT;
	}
	
	public final static int FEATURE_SPLIT_SIZE = 20;
	
	public final static String CURRENT_TIME = "2017-02-20 17:58:25.000000";

	//public final static String FORMER_REVISION_TIME_UNDER_INVEST = "2013-07-01 00:00:01";
	//public final static String LATTER_REVISION_TIME_UNDER_INVEST = "2014-07-01 00:00:01";
	
	public final static String FEATURE_VALUE_OUTPUT_FOLDER = "data/feature/";
	
	public final static String[] WARN_CATEGORY_UNDER_INVESTIGATION = {"MALICIOUS_CODE", "CORRECTNESS", "PERFORMANCE", "SECURITY", 
			"MT_CORRECTNESS", "BAD_PRACTICE", "EXPERIMENTAL", "STYLE", "I18N"};
	//"BAD_PRACTICE", "EXPERIMENTAL",	"STYLE", "I18N" 
	//dodgy code没有，多了style，应该这两个是一样的
	
	// correctness, vulnerability, malicious code, security, multithreaded performance, bad practice
	
	public final static Slicer.Direction SLICER_DIRECTION = Slicer.Direction.BACKWARDS;
	public final static Slicer.Type SLICER_TYPE = Slicer.Type.DATA;
	public final static Slicer.Options[] SLICER_OPTIONS = new Slicer.Options[]{Slicer.Options.CONTROL_EXPRESSIONS_ONLY};
	public final static Integer MAX_SLICES = 5;
	
	public final static String JRE_LOCATION  = "C:\\Program Files\\Java\\jre1.8.0_91\\lib\\\rt.jar";
	
	//source code slicer/parser 相关的
	public final static String[] BINARY_OPERATION = { "*", "/", "%", "+", "-", "&", "^", "|", "+=", "-=", "*=", "/=", "&=", "^=", "|=", "++", "--"};
	public final static String[] METHOD_FIELD_VISIBILITY = { "public", "private", "protected"};
	public final static String[] METHOD_FIELD_TYPE = { "static", "final", "abstract" };
	public final static String[] CLASS_TYPE = { "abstract", "interface", "array" };
	
	public final static Integer SPLIT_NUM = 5;
	
	public final static String[] CATEGORIAL_FEATURES = { "F26", "F53", "F54", "F55",
			"F20", "F21", "F22", 
			"F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14", "F15", "F16", "F17", "F18", "F19",
			 "F71" };
	public final static List CATEGORIAL_FEATURES_LIST = Arrays.asList( com.comon.Constants.CATEGORIAL_FEATURES);
	
	public final static String[] NUMERICAL_FEATURES = { "F25",  "F72",
			"F23", "F61", "F94", "F95",
			"F34",  "F62", "F64", "F65", "F66", "F67", "F68", "F69", "F101", "F102", "F103", "F104", "F105", "F106", "F107", "F108",
			"F35", "F36", "F37", "F38", "F39", "F40", "F41", "F42", "F43", "F44", "F45", "F46", "F47", "F48", "F49", "F50", "F51", "F52",
			"F126", "F127", "F128", "F129", "F130", "F131", "F132", "F133", "F134", "F135", "F136", "F137", "F138", "F139", "F140", "F141", "F142", "F143",
			"F70", "F73", "F74", "F146", "F147", "F83", "F84", "F85",
			"F77", "F88", "F109", "F110", "F111", "F112", "F113", "F114", "F115", "F116", "F117", "F118", "F119", "F120", "F121", "F122", "F123"};
	public final static List NUMERICAL_FEATURES_LIST = Arrays.asList( com.comon.Constants.NUMERICAL_FEATURES);
	
	public final static String[] UNUSED_FEATURES = { "F47", "F48", "F49", "F50", "F51", "F52", "F138", "F139", "F140", "F141", "F142", "F143",
			 "F85", "F147", "F53", };
	public final static List UNUSED_FEATURES_LIST = Arrays.asList( com.comon.Constants.UNUSED_FEATURES);
}
