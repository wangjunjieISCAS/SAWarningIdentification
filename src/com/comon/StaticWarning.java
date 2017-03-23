package com.comon;

import java.util.ArrayList;

public class StaticWarning {
	BugInfo bugInfo;
	//默认所有bugLocationList均属于同一fileName，否则对于file相关的指标无法计算
	ArrayList<BugLocation> bugLocationList;
	
	public StaticWarning ( BugInfo bugInfo, ArrayList<BugLocation> bugLocationList ){
		this.bugInfo = bugInfo;
		this.bugLocationList = bugLocationList;
	}

	public BugInfo getBugInfo() {
		return bugInfo;
	}

	public void setBugInfo(BugInfo bugInfo) {
		this.bugInfo = bugInfo;
	}

	public ArrayList<BugLocation> getBugLocationList() {
		return bugLocationList;
	}

	public void setBugLocationList(ArrayList<BugLocation> bugLocationList) {
		this.bugLocationList = bugLocationList;
	}

	public String toString ( ){
		String bugLocationString = "";
		for ( int i = 0; i < bugLocationList.size(); i++ ){
			bugLocationString += bugLocationList.get( i).getClassName() + " " + bugLocationList.get(i).getStartLine() + " " + bugLocationList.get(i).getEndLine() 
					+ " " + bugLocationList.get(i).getRegion() + " " + bugLocationList.get(i).getRelatedMethodName() + "\n"  ;
		}
		return bugInfo.getType() + "\n" + bugLocationString;
	}
}
