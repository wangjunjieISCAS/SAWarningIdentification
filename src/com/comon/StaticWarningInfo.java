package com.comon;

import java.util.HashMap;
import java.util.HashSet;

public class StaticWarningInfo {
	HashSet<String> categoryList;
	HashMap<String, String> typeToCateogoryMap;
	HashMap<String, HashSet<String>> typeInCategoryList;
	
	public StaticWarningInfo ( ){
		categoryList = new HashSet<String>();
		typeToCateogoryMap = new HashMap<String, String>();
		typeInCategoryList = new HashMap<String, HashSet<String>>();
	}

	public HashSet<String> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(HashSet<String> categoryList) {
		this.categoryList = categoryList;
	}

	public HashMap<String, String> getTypeToCateogoryMap() {
		return typeToCateogoryMap;
	}

	public void setTypeToCateogoryMap(HashMap<String, String> typeToCateogoryMap) {
		this.typeToCateogoryMap = typeToCateogoryMap;
	}

	public HashMap<String, HashSet<String>> getTypeInCategoryList() {
		return typeInCategoryList;
	}

	public void setTypeInCategoryList(HashMap<String, HashSet<String>> typeInCategoryList) {
		this.typeInCategoryList = typeInCategoryList;
	}
	
	
}
