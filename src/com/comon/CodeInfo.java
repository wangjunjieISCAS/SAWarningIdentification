package com.comon;

import org.eclipse.jdt.core.dom.ASTNode;

//在SourceCodeSlicer中调用
public class CodeInfo {
	Integer codeLine;
	ASTNode codeContent;
	
	public CodeInfo(Integer codeLine, ASTNode codeContent ){
		this.codeLine = codeLine;
		this.codeContent = codeContent;
	}
	
	public Integer getCodeLine() {
		return codeLine;
	}
	public void setCodeLine(Integer codeLine) {
		this.codeLine = codeLine;
	}
	public ASTNode getCodeContent() {
		return codeContent;
	}
	public void setCodeContent(ASTNode codeContent) {
		this.codeContent = codeContent;
	}
	

	@Override
	public boolean equals ( Object obj ){
		if ( obj == null ){
			return false;
		}
		
		if ( this.getClass() == obj.getClass() ){
			CodeInfo codeInfo = (CodeInfo) obj;
			if ( this.getCodeLine().equals(codeInfo.getCodeLine() ) && this.getCodeContent().equals( codeInfo.getCodeContent()  )){
				return true;
			}
			return false;
		}
		return false;
	}
	
}
