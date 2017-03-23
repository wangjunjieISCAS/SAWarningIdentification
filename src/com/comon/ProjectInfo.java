package com.comon;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * 该类目前实现了两个功能，一个是得到了某个项目相关的package和class信息，
 * 二是得到了每个package和class中的static warning的数目
 * 
 * 有些时候这两者不是唯一对应的，一个file中可能含有多个class
	/*
	 * <ClassStats class="org.apache.lucene.benchmark.byTask.feeds.DocMaker" sourceFile="DocMaker.java" interface="false" size="188" bugs="1" priority_2="1"/>
      <ClassStats class="org.apache.lucene.benchmark.byTask.feeds.DocMaker$1" sourceFile="DocMaker.java" interface="false" size="4" bugs="0"/>
      <ClassStats class="org.apache.lucene.benchmark.byTask.feeds.DocMaker$DateUtil" sourceFile="DocMaker.java" interface="false" size="11" bugs="0"/>
      <ClassStats class="org.apache.lucene.benchmark.byTask.feeds.DocMaker$DocState" sourceFile="DocMaker.java" interface="false" size="51" bugs="0"/>
      <ClassStats class="org.apache.lucene.benchmark.byTask.feeds.DocMaker$LeftOver" sourceFile="DocMaker.java" interface="false" size="10" bugs="0"/>
      	类里面包含内部类，
      	
      	$1， $2这种标记是这种形式的类
      	protected SpatialStrategy makeSpatialStrategy(final Config config) {
    //A Map view of Config that prefixes keys with "spatial."
    Map<String, String> configMap = new AbstractMap<String, String>() {
      @Override
      public Set<Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException();
      }

      @Override
      public String get(Object key) {
        return config.get("spatial." + key, null);
      }
    };
      	
      	iterface也被作为class
      	也就是说如果要统计一个file的static warning数目，需要进行合并
	 */

public class ProjectInfo {
	//这种存储方式相当于重复存储了信息
	ArrayList<String> packageList;
	HashMap<String, Integer> warningNumForPackage;
	
	HashMap<String, ArrayList<String>> fileListForPackage;
	HashMap<String, Integer> warningNumForFile;
	HashMap<String, Integer> classNumForFile;          //一个文件中类的数目，包含内部类
	HashMap<String, String> filePackageNameMap;      //已知fileName得到packageName
	
	Integer totalWarningCount;
	 
	public ProjectInfo ( ){	
		packageList = new ArrayList<String>();
		warningNumForPackage = new HashMap<String, Integer>();
		
		fileListForPackage = new HashMap<String, ArrayList<String>>();
		warningNumForFile = new HashMap<String, Integer>();
		classNumForFile = new HashMap<String, Integer>() ;
		
		filePackageNameMap = new HashMap<String, String>();
		
		totalWarningCount = 0;
	}

	
	public ArrayList<String> getPackageList() {
		return packageList;
	}

	public void setPackageList(ArrayList<String> packageList) {
		this.packageList = packageList;
	}

	public HashMap<String, Integer> getWarningNumForPackage() {
		return warningNumForPackage;
	}

	public void setWarningNumForPackage(HashMap<String, Integer> warningNumForPackage) {
		this.warningNumForPackage = warningNumForPackage;
	}

	public HashMap<String, ArrayList<String>> getFileListForPackage() {
		return fileListForPackage;
	}

	public void setFileListForPackage(HashMap<String, ArrayList<String>> fileListForPackage) {
		this.fileListForPackage = fileListForPackage;
	}

	public HashMap<String, Integer> getWarningNumForFile() {
		return warningNumForFile;
	}

	public void setWarningNumForFile(HashMap<String, Integer> warningNumForFile) {
		this.warningNumForFile = warningNumForFile;
	}

	public HashMap<String, Integer> getClassNumForFile() {
		return classNumForFile;
	}

	public void setClassNumForFile(HashMap<String, Integer> classNumForFile) {
		this.classNumForFile = classNumForFile;
	}


	public HashMap<String, String> getFilePackageNameMap() {
		return filePackageNameMap;
	}


	public void setFilePackageNameMap(HashMap<String, String> filePackageNameMap) {
		this.filePackageNameMap = filePackageNameMap;
	}


	public Integer getTotalWarningCount() {
		return totalWarningCount;
	}


	public void setTotalWarningCount(Integer totalWarningCount) {
		this.totalWarningCount = totalWarningCount;
	}
	
	
}
