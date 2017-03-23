package com.comon;

public class StringTool {
	public static String obtainClassNameShort ( String className ){
		String classNameShort = "";
		
		int index = className.lastIndexOf( ".");      //Ô­À´ÊÇclassName.indexOf(".java");
		if ( index < 0 )
			return classNameShort;
		classNameShort = className.substring( 0, index );
		
		index = classNameShort.lastIndexOf( "/");
		if ( index < 0)
			return classNameShort;
		classNameShort = classNameShort.substring( index+1 );
		
		return classNameShort;
	}
	
}
