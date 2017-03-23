package com.comon;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodBodyLocation {
	MethodDeclaration method;
	int startLine;
	
	public MethodBodyLocation ( MethodDeclaration method, int startLine){
		this.method = method;
		this.startLine = startLine;
	}

	public MethodDeclaration getMethod() {
		return method;
	}

	public void setMethod(MethodDeclaration method) {
		this.method = method;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	
}
