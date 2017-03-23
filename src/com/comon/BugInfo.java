package com.comon;

/*
 * <BugInstance type="EQ_DOESNT_OVERRIDE_EQUALS" priority="2" rank="17" abbrev="Eq" category="STYLE" first="1">
 */
public class BugInfo {
	String type;
	Integer priority;
	Integer rank;
	String category;
	
	public BugInfo ( String type, Integer priority, Integer rank, String category ){
		this.type = type;
		this.priority = priority;
		this.rank = rank;
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
