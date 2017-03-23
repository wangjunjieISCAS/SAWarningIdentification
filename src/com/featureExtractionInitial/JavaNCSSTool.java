package com.featureExtractionInitial;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.git.CodeSearcher;

import javancss.Javancss;

public class JavaNCSSTool {
	
	public void obtainSourceMeasure ( String fileName ){
		File javaFile = new File ( fileName );
		
        Javancss javaNcss = new Javancss (  javaFile );
        //int fileLength = javaNcss.getLOC();
        System.out.println ( javaNcss.getSl() );
        
        System.out.println( javaNcss.getFunctionMetrics());
        
        List<String> function = javaNcss.getFunctions();
        System.out.println( function.size() );
        
        for ( int i = 0; i < function.size(); i++ )
        	System.out.println( function.get( i)  );
        
        
        //System.out.println( javaNcss.getJdcl() );
        //System.out.println( javaNcss.getLOC() );
        //System.out.println( javaNcss.getJdcl() );
        
        //System.out.println( javaNcss.printFunctionNcss() );
        //System.out.println ( javaNcss.printJavaNcss() );
        //System.out.println( javaNcss.printObjectNcss() );
        //System.out.println ( javaNcss.printPackageNcss() );
        
	}
	
	public static void main ( String args[] ){
		JavaNCSSTool javaNcssTool = new JavaNCSSTool();
		//File javaFile = new File ( "D://javancss-32.53//bin//test//Test1.java");
		//String javaFile = "D://java-workstation//lucene2.9.2//src//org//apache//lucene//analysis//NumericTokenStream.java";
		//String javaFile = "D://java-workstation//experimentalProject//lucene-solr-releases-lucene-solr-5.1.0//lucene//benchmark//src//java//org//apache//lucene/benchmark//byTask//feeds//DirContentSource.java";
		String javaFile = "data/Test3.java";
		
		javaNcssTool.obtainSourceMeasure(javaFile);
	}
}
