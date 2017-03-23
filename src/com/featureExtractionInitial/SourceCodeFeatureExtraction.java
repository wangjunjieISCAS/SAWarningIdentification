package com.featureExtractionInitial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.comon.BugInfo;
import com.comon.BugLocation;
import com.comon.CodeInfo;
import com.comon.Constants;
import com.comon.StaticWarning;
import com.comon.StringTool;
import com.comon.Constants.BUG_LOCATION_REGION_TYPE;
import com.comon.MethodBodyLocation;
import com.comon.ProjectInfo;

import javancss.Javancss;


public class SourceCodeFeatureExtraction {
	ArrayList<String> fullFileNameList;
	
	public SourceCodeFeatureExtraction ( ){
		WarningParser parser = new WarningParser();
		fullFileNameList = parser.obtainAllFiles( Constants.FOLDER_NAME, Constants.FOLDER_NAME);
	}
	
	public HashMap<String, Object> extractCodeAnalysisFeature_F1_to_F19 ( StaticWarning warning){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		//fileName = this.obtainFullFileName(fileName);
		
		SourceCodeSlicer slicer = new SourceCodeSlicer();
		
		String fullFileName = this.obtainFullFileName(fileName);
		ArrayList<CodeInfo> slicesCodeList = slicer.obtainWarningStatementSlices(warning, fullFileName );
		
		SourceCodeParser codeParser = new SourceCodeParser();
		if ( slicesCodeList != null ){
			System.out.println( "========================================== code to be parsed: ");
			for ( int i = 0; i < slicesCodeList.size(); i++ ){
				if ( slicesCodeList.get(i).getCodeContent() != null)		
					System.out.println( slicesCodeList.get(i).getCodeContent() );
				else 
					System.out.println(  slicesCodeList.get(i).getCodeLine() );
			}
		}
		
		HashMap<Integer, HashMap<String, String>> codeFeature = codeParser.obtainMethodDetails( fullFileName, slicesCodeList);
		HashMap<String, String> F18_F19 = this.obtainClassType(warning);
		codeFeature.put( 0, F18_F19 );
		
		Iterator iter = codeFeature.entrySet().iterator();
		for ( int i = 0; i < Constants.MAX_SLICES; i++ ){
			HashMap<String, String> featureValue = null;
			
			if ( iter.hasNext() ){
				Map.Entry entry = (Map.Entry) iter.next();
				featureValue = (HashMap<String, String>) entry.getValue();
			}
			
			for ( int j = 0; j < 20; j++ ){
				String newKey = "F" + j ;
				String transferKey = newKey + "-" + i;
				String value = "";
				if ( featureValue != null && featureValue.containsKey( newKey )){
					value = featureValue.get( newKey );
				}
				if ( value.trim().equals( ""))
					value = "NA";
				value = value.replace( ",", " ");     //写csv时，如果有， 会分成两个格
				result.put( transferKey, value);
			}			
		}
		return result;
	}
	
	public HashMap<String, String> obtainClassType ( StaticWarning warning ){
		SourceCodeSlicer slicer = new SourceCodeSlicer();
		
		HashMap<String, String> result = new HashMap<String, String>();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		String fullFileName = this.obtainFullFileName(fileName);
		System.out.println("fullFileName:  " +  fullFileName );
		
		String fileContent = slicer.readJavaFile(fullFileName );
		String[] fileContentDetail = fileContent.split( "\n");
		
		for ( int i =0; i < warning.getBugLocationList().size(); i++ ){
			BugLocation bugLoc = warning.getBugLocationList().get( i );
			if ( bugLoc.getRelatedMethodName().equals( "")){
				for ( int j = bugLoc.getStartLine(); j <= bugLoc.getEndLine(); j++ ){
					if ( fileContentDetail.length > j ){
						String detail = fileContentDetail[j-1];
						if ( detail.contains( "class ") || detail.contains( "interface ")){
							for ( int k =0; k < Constants.CLASS_TYPE.length; k++ ){
								if ( detail.contains( Constants.CLASS_TYPE[k] + " ")){
									result.put( "F19", Constants.CLASS_TYPE[k]);
								}
							}
							for ( int k =0; k < Constants.METHOD_FIELD_VISIBILITY.length; k++ ){
								if ( detail.contains( Constants.METHOD_FIELD_VISIBILITY[k] + " ")){
									result.put( "F18", Constants.METHOD_FIELD_VISIBILITY[k]);
								}
							}
							break;
						}
					}
				}				
			}
		}
		return result;		
	}
	
	//需要首先执行obtainMethodCallerCallee，得到HashMap<String, HashMap<String, Integer>>
	public HashMap<String, Object> extractMethodCall_F107_F108 ( StaticWarning warning , HashMap<String, HashMap<String, Integer>> callInfo){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		HashMap<String, Integer> methodCallerMap = callInfo.get( "caller");
		HashMap<String, Integer> methodCalleeMap = callInfo.get( "callee");
		
		String className = warning.getBugLocationList().get(0).getClassName();
		
		int callerNum =0, calleeNum = 1, methodNum =0 ;
		for ( int i =0; i < warning.getBugLocationList().size(); i ++ ){
			BugLocation bugLoc = warning.getBugLocationList().get( i );
			if ( !bugLoc.getRelatedMethodName().equals( "")){
				methodNum ++;
				String methodName = bugLoc.getRelatedMethodName();
				methodName = className + "-" + methodName;
				
				if ( methodCallerMap.containsKey( methodName )){
					callerNum += methodCallerMap.get( methodName );
				}
				if ( methodCalleeMap.containsKey( methodName )){
					calleeNum += methodCalleeMap.get( methodName );
				}
			}
		}
		
		if ( methodNum != 0 ){
			callerNum = callerNum / methodNum;
			calleeNum = calleeNum / methodNum;
		}
		
		result.put( "F107", callerNum );
		result.put( "F108", calleeNum );
		
		return result;
	}
	
	public HashMap<String, HashMap<String, Integer>> obtainMethodCallerCallee ( ArrayList<StaticWarning> warningList, ProjectInfo projectInfo, String folderName ){
		SourceCodeSlicer slicer = new SourceCodeSlicer();
		
		//得到所有的methodName，用className-methodName的形式保持
		HashMap<String, Set<String>> methodInClass = new HashMap<String, Set<String>>();
		
		for ( int i = 0; i < warningList.size(); i++ ){
			StaticWarning warning = warningList.get( i );
			ArrayList<BugLocation> bugLocationList = warning.getBugLocationList();
			
			//因为在bugLocationList里面会出现一个method里面的多处位置，应该算作一个warning。这里需要找到不同的method
			Set<String> methodSet = new HashSet<String>();
			for ( int j = 0; j < bugLocationList.size(); j++ ){
				BugLocation bugLoc = bugLocationList.get( j );
				String method = bugLoc.getRelatedMethodName();
				if ( method.equals( ""))
					continue;
				
				methodSet.add( method );
			}
			methodInClass.put( bugLocationList.get(0).getClassName(), methodSet);
			//methodInClass是这种类型的，org/tartarus/snowball/ext/SpanishStemmer.java
		}			
		
		HashMap<String, Integer> methodCallerMap = new HashMap<String, Integer>();
		HashMap<String, Integer> methodCalleeMap = new HashMap<String, Integer>();
		
		//ArrayList<String> fileList = warnParser.obtainAllFiles( folderName, folderName );
		for ( int i = 0; i < fullFileNameList.size(); i++ ){
			String fileName = fullFileNameList.get(i);
			System.out.println( i + " " + fileName );
			if ( !fileName.endsWith( ".java")){
				continue;
			}
			int orgIndex = fileName.indexOf( "org");
			if ( orgIndex < 0 )
				continue;	
			
			//该文件的名字
			String className = fileName.substring( orgIndex );
			//className格式为org\apache\lucene\search\suggest\TestTermFreqIterator.java
			String classNameTransfer = className.replace( "\\", "/");
			//System.out.println( className );
			
			String fileContent = slicer.readJavaFile( folderName + fileName );
			String fullFileName = this.obtainFullFileName( className );    
			
			//在其中寻找某method里面调用的其他method的数目
			if ( methodInClass.containsKey( classNameTransfer )){
				String classNameShort = StringTool.obtainClassNameShort(classNameTransfer);
				
				HashMap<String, MethodBodyLocation> methodDetails = slicer.obtainMethodInfo( fullFileName );
				for ( String methodName: methodInClass.get( classNameTransfer )){
					if ( !methodDetails.containsKey( methodName ))
						continue;
					//需要找这个method里面调用其他method的数目
					String methodBody = methodDetails.get( methodName ).getMethod().toString();
					int count = 0;
					
					Pattern pattern = Pattern.compile( "\\.\\w+" );
					Matcher matcher = pattern.matcher( methodBody );
					while ( matcher.find() ){
						count ++;
					}
					
					methodCallerMap.put( classNameTransfer + "-" + methodName, count / methodInClass.get( classNameTransfer).size() );
					//System.out.println ( "methodCallerMap:     " + className + "-" + methodName + " "+ count );
				 }
			}
			
			//得到该类里面调用其他函数的情况		
			for ( String classNameLong : methodInClass.keySet() ){
				String classNameShort = StringTool.obtainClassNameShort(classNameLong);
				//System.out.println ( "classNameShort: " + classNameShort  );
				
				for ( String methodName: methodInClass.get( classNameLong) ){
					boolean isReferred = this.isMethodReferred(classNameShort, methodName, fileContent);
					if ( isReferred == false )
						continue;
					
					String methodTotalName = classNameLong + "-" + methodName;
					int count = 1;
					if ( methodCalleeMap.containsKey( methodTotalName )){
						count += methodCalleeMap.get( methodTotalName );
					}
					
					methodCalleeMap.put( methodTotalName, count );
					System.out.println( "methodCalleeMap: " + methodTotalName  + " "  + count  );
				}				
			}		
		}	
		
		HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();
		result.put( "caller", methodCallerMap );
		result.put( "callee", methodCalleeMap );
		
		return result;
	}
	
	
	public boolean isMethodReferred ( String className, String methodName, String fileContent ){
		boolean isReferred = false;
		Pattern patternStatic = Pattern.compile( "\\s*" +  className + "\\." + methodName + "\\s+"  );
		Matcher matcherStatic = patternStatic.matcher( fileContent );
		
		//目前一个文件中只能统计出某方法的一次调用，多次调用统计不出来
		Pattern patternClass= Pattern.compile( className  + "([\\s\\w]*)=.*new.*" + className );
		Matcher matcherClass = patternClass.matcher( fileContent);
		String classNewName = "";
		if ( matcherClass.find()  ){
			classNewName = matcherClass.group(1);
		}
		//得到一个syn[]的情况，需要去除掉
		classNewName = classNewName.replaceAll( "[\\pP\\p{Punct}]", "");
		classNewName = classNewName.trim();
		
		Pattern patternNew = Pattern.compile( classNewName + "\\." + methodName );
		Matcher matcherNew = patternNew.matcher( fileContent);
		
		Pattern patternSameClass = Pattern.compile( methodName );
		if ( matcherStatic.find() || ( !classNewName.equals( "") && matcherNew.find() )  ){
			isReferred = true;
		}					
		
		return isReferred;
	}
	
	//这个用JNcss的不太对，重新自己写一下
	public HashMap<String, Object> extractCodeStatisticsJNcss_F101_to_F104 ( StaticWarning warning  ){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		//System.out.println( fileName );
		fileName = this.obtainFullFileName( fileName );
		//System.out.println( fileName );
		
		File javaFile = new File ( fileName );
		
		Javancss javaNcss = new Javancss (  javaFile );
        int fileLength = javaNcss.getLOC();
        //result.put( "F102", fileLength );
        
        int codeLineLength = javaNcss.getNcss();
        result.put( "F102", codeLineLength );
        
        int commentLength = fileLength - codeLineLength;
        result.put( "F103", commentLength );
        
        double commentCodeRatio = (1.0*commentLength) / (1.0*codeLineLength);
        result.put( "F104", commentCodeRatio );
        
        int num = 0;
        int totalIndex = 0;
        for ( int i = 0; i < warning.getBugLocationList().size(); i++ ){
        	totalIndex += warning.getBugLocationList().get(i).getStartLine();
        	num++;
        }
        double fileDepth = (1.0*totalIndex) / (1.0*num);
        fileDepth = fileDepth / (1.0*fileLength);
        result.put( "F101", fileDepth );
        
        System.out.println( result.toString() );
        
        return result;
	}
	
	public HashMap<String, Object> extractCodeStatistics_F101_to_F104 ( StaticWarning warning  ){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		//System.out.println( fileName );
		fileName = this.obtainFullFileName( fileName );
		//System.out.println( fileName );
		
		String line = "";
		BufferedReader br;
		int codeLength = 0;
		int commentLength = 0;
		int nullLength = 0;
		try {
			br = new BufferedReader(new FileReader( new File ( fileName )));
			while ( ( line = br.readLine() ) != null ) {
				line = line.trim();
				if ( line.equals( "")){
					nullLength++;
				}					
				else if ( line.startsWith( "/") || line.startsWith( "*")){
					commentLength++;
				}
				else{
					codeLength++;
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        result.put( "F102", codeLength );       
        result.put( "F103", commentLength );
        
        double commentCodeRatio = (1.0*commentLength) / (1.0*codeLength);
        result.put( "F104", commentCodeRatio );
        
        int num = 0;
        int totalIndex = 0;
        for ( int i = 0; i < warning.getBugLocationList().size(); i++ ){
        	totalIndex += warning.getBugLocationList().get(i).getStartLine();
        	num++;
        }
        double fileDepth = (1.0*totalIndex) / (1.0*num);
        fileDepth = fileDepth / (1.0* (codeLength + commentLength + nullLength ));
        result.put( "F101", fileDepth );
        
        System.out.println( result.toString() );
        
        return result;
	}
	
	//这个方法和extractCodeStatistics_F101_to_F104其中的部分是一样的
	public int obtainCodeLengthFile ( String fileName ){
		fileName = this.obtainFullFileName( fileName );
		File javaFile = new File ( fileName );
		
		Javancss javaNcss = new Javancss (  javaFile );
        int codeLineLength = javaNcss.getNcss();
        
        return codeLineLength;
	}
	
	public HashMap<String, Object> extractPackageStatistics_F64_F66_F68 ( StaticWarning warning , ProjectInfo projectInfo){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		String packageName = projectInfo.getFilePackageNameMap().get( fileName );
		ArrayList<String> fileList  = projectInfo.getFileListForPackage().get(packageName);
		
		int packageSize = 0;
		int methodNumInPackage = 0;
		int classNumInPackage = 0;
		for ( int i =0; i < fileList.size() ; i++ ){
			String file = fileList.get(i );
			packageSize += this.obtainCodeLengthFile( file );
			
			methodNumInPackage += this.obtainMethodNumberFile(file);
			classNumInPackage += this.extractClassNumberFile_F67( file, projectInfo);
		}
		
		result.put( "F64", packageSize);
		result.put( "F66", methodNumInPackage );
		result.put( "F68", classNumInPackage );
		return result;
	}
	
	public int extractClassNumberFile_F67 ( String fileName , ProjectInfo projectInfo ){
		int classNum = projectInfo.getClassNumForFile().get( fileName );
		
		return classNum;
	}
	
	//这个方法和extractMethodStatistics_F105_F106_F34_F62_F65其中的部分是一样的
	public int obtainMethodNumberFile ( String fileName ){
		int methodNumInFile = 0;
	
		fileName = this.obtainFullFileName( fileName );
		
		SourceCodeSlicer slicer = new SourceCodeSlicer();
		HashMap<String, MethodBodyLocation> methodInfo = slicer.obtainMethodInfo( fileName );
		
		//目前只能统计主类中的method数目，内部类里面的统计不到
		methodNumInFile = methodInfo.size();
		return methodNumInFile;
	}
	
	
	public HashMap<String, Object> extractMethodStatistics_F105_F106_F34_F62_F65_F69 ( StaticWarning warning ){ 
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		fileName = this.obtainFullFileName( fileName );
		//System.out.println( fileName );
		String className = StringTool.obtainClassNameShort( fileName );
		
		SourceCodeSlicer slicer = new SourceCodeSlicer();
		HashMap<String, MethodBodyLocation> methodInfo = slicer.obtainMethodInfo( fileName );
		
		//目前只能统计主类中的method数目，内部类里面的统计不到
		int methodNumberInFile = methodInfo.size();
				
		int numNoMethod = 0, numMethod = 0;
		int totalIndex = 0;
		int totalSpace = 0;
		int methodLength = 0;
		double methodDepth = 0.0;
		int codeMethodLength = 0;
		int complexity = 1;     //判定节点数+1
		for ( int i = 0; i < warning.getBugLocationList().size(); i++ ){
			BugLocation bugLoc = warning.getBugLocationList().get( i );
			if ( bugLoc.getRelatedMethodName().equals( "")){
				for ( int j =0; j < bugLoc.getCodeInfoList().size(); j++ ){
					totalSpace += this.obtainFrontSpace( bugLoc.getCodeInfoList().get( j ));
					numNoMethod ++;
				}				
			}else{
				String methodName = bugLoc.getRelatedMethodName();
				if ( methodName.equals( "<init>"))
					methodName = className;
				
				//System.out.println( "+++++++++++++++++++++++++++++++++++" + methodName );
				//System.out.println ( methodInfo.keySet().toString() );
				
				MethodDeclaration methodCode = slicer.obtainMethodDeclaration( methodInfo, methodName, bugLoc.getStartLine() );
				//内部类的问题仍然没有解决
				if ( methodCode == null )
					continue;
				Block methodBody = methodCode.getBody();
				if ( methodBody == null )
					continue;
				
				String[] methodDetail = methodBody.toString().split( "\n");
				methodLength += methodDetail.length;
				
				for ( int j =0; j < methodDetail.length; j++ ){
					String code = methodDetail[j];
					if ( code.trim().length() != 0 && !code.trim().startsWith( "*") && !code.trim().startsWith( "/"))
						codeMethodLength ++;
						
					for ( int k =0; k < bugLoc.getCodeInfoList().size(); k++ ){
						String str1 = bugLoc.getCodeInfoList().get(k).replaceAll( " ", "");
						String str2 = code.replace( " ", "");
						
						if ( str1.equals( str2 )){
							//可能会有多个method的情况，相加，求均值
							totalIndex += (j+1);
							totalSpace += this.obtainFrontSpace( bugLoc.getCodeInfoList().get(k) );
							numMethod ++;
						}
					}		
					
					Pattern pattern = Pattern.compile(  "[^\\w]for|while|else|if|case[^\\w]");
					Matcher matcher = pattern.matcher( code );
					if ( matcher.find() ){
						complexity ++;
					}					
				}	
			}
		}
		
		if ( numMethod == 0 ){
			methodLength = 1;
			methodDepth = 1.0;
		}else{
			if ( methodLength == 0 )
				methodDepth = 1.0;
			else{
				methodDepth = (1.0*totalIndex) / (1.0* numMethod );
				methodDepth = methodDepth / (1.0*methodLength);
			}				
		}
		
		if ( numMethod ==0 && numNoMethod == 0 )
			totalSpace = 0;
		else
			totalSpace = totalSpace / (numMethod + numNoMethod );
		
		result.put( "F105", methodDepth );
		//result.put( "F106", methodLength );
		result.put( "F34", totalSpace );
		result.put( "F62", codeMethodLength );
		result.put("F65", methodNumberInFile );
		result.put ( "F69", complexity );
		
		System.out.println( result.toString() );
		
		return result;
	}
	
	
	public int obtainFrontSpace ( String str ){
		int space = 0;
		for ( int i =0; i < str.length(); i++ ){
			if ( str.charAt( i)  == ' ' ){
				space ++;
			}
			else
				break;
		}
		return space;
	}
	
	/*参数的fileName为  org\apache\lucene\analysis\ar\ArabicStemmer.java 这种形式，
	 *需要借助fullFileNameList找到完整的文件名
	 */
	public String obtainFullFileName ( String fileName ){
		String fullFileName = "";
		//System.out.println( "fileName: " + fileName );
		String newFileName = fileName.replace( "/", "\\" );
		//System.out.println( "newFileName: " + newFileName );
		for ( int i = 0;  i < fullFileNameList.size(); i++ ) {
			fullFileName = fullFileNameList.get( i );
			if (  fullFileName.contains( newFileName ) ){
				break;
			}
		}
		
		if ( fullFileName.equals( ""))
			return fileName;
		
		fullFileName = fullFileName.replace( "\\", "//");
		return Constants.FOLDER_NAME + fullFileName;
	}
	
	
	public static void main ( String args[] ){
		/*
		String fileContent = " HttpSolrServer.clone\n else WordBreakSpellChecker; this.clone (int i = 0; i < n; i++); HttpSolrServer server = new HttpSolrServer  ( true );"
				+ "server.clone";
		String className = "HttpSolrServer";
		String methodName = "clone";
		Pattern pattern2 = Pattern.compile( "[^\\w]for|while|else[^\\w]" );
		Matcher matcher2 = pattern2.matcher( fileContent);
		if ( matcher2.find() ){
			//System.out.println( "found!");
		}
		
		Pattern patternStatic = Pattern.compile( "\\s+" +  className + "\\." + methodName + "\\s+"  );
		Matcher matcherStatic = patternStatic.matcher( fileContent );
		
		//目前一个文件中只能统计出某方法的一次调用，多次调用统计不出来
		Pattern patternClass= Pattern.compile( className  + "([\\s\\w]*)=.*new.*" + className );
		Matcher matcherClass = patternClass.matcher( fileContent);
		String classNewName = "";
		if ( matcherClass.find()  ){
			classNewName = matcherClass.group(1);
		}
		//得到一个syn[]的情况，需要去除掉
		classNewName = classNewName.replaceAll( "[\\pP\\p{Punct}]", "");
		classNewName = classNewName.trim();
		
		Pattern patternNew = Pattern.compile( classNewName + "\\." + methodName );
		Matcher matcherNew = patternNew.matcher( fileContent);
		
		Pattern patternSame = Pattern.compile( "\\.\\w+" );
		Matcher matcherSame = patternSame.matcher( fileContent);
		
		if ( matcherStatic.find() ){
			System.out.println( "static found!");
		}
		if ( !classNewName.equals( "") && matcherNew.find()  ){
			System.out.println( "new found!");
		}	
		int count = 0;
		while ( matcherSame.find() ){
			count++;
		}
		System.out.println( "sameclass found! " + count );
	
		//pattern[0] = Pattern.compile(  "\\s+" +  classNameShort + "\\." + methodName + "\\s+" );
		//pattern[1] = Pattern.compile( classNameShort  + "([\\s\\w]*)=.*new.*" + classNameShort );
		*/
		SourceCodeFeatureExtraction extraction = new SourceCodeFeatureExtraction();
		
		BugInfo bugInfo = new BugInfo ( "REC_CATCH_EXCEPTION", 3, 20, "STYLE" );
		ArrayList<BugLocation> bugLocList = new ArrayList<BugLocation>();
		/*
		BugLocation bugLoc = new BugLocation ( "data/test3.java", 219, 219, Constants.BUG_LOCATION_REGION_TYPE.METHOD, "rewrite");
		ArrayList<String> codeInfoList = new ArrayList<String>();
		codeInfoList.add( "        clone.disjuncts.set(i, rewrite);");
		bugLoc.setCodeInfoList(codeInfoList);
		bugLocList.add( bugLoc );
		*/
		//BugLocation bugLoc2 = new BugLocation ( "data/test3.java", 216, 216, Constants.BUG_LOCATION_REGION_TYPE.METHOD, "rewrite");
		BugLocation bugLoc2 = new BugLocation ( "data/test3.java", 44, 60, Constants.BUG_LOCATION_REGION_TYPE.METHOD, "");
		ArrayList<String> codeInfoList2 = new ArrayList<String>();
		//codeInfoList2.add( "StringBuilder buffer = new StringBuilder();");
		//codeInfoList2.add( "buffer.append(tieBreakerMultiplier);");
		codeInfoList2.add( "Query rewrite = clause.rewrite(reader);");
		//codeInfoList2.add( "StringBuilder buffer = new StringBuilder();");
		
		bugLoc2.setCodeInfoList(codeInfoList2);
		bugLocList.add( bugLoc2 );
		
        StaticWarning warning = new StaticWarning( bugInfo, bugLocList );
        extraction.extractCodeAnalysisFeature_F1_to_F19(  warning );
        //extraction.extractMethodStatistics_F105_F106_F34( warning );
        //extraction.extractCodeAnalysisFeature_F1_to_F10(warning);
        /*
		SourceCodeFeatureExtraction extraction = new SourceCodeFeatureExtraction();
		
		ArrayList<String> codeInfo = new ArrayList<String>();
		codeInfo.add( "int termLength = termAtt.length();");
		codeInfo.add( "private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);");
		//codeInfo.add( "if (VERBOSE) System.out.println(\"    startDoc docID=\" + docID + \" freq=\" + termDocFreq);");
		
		String fileName = "D://java-workstation//lucene4.0//lucene//analysis//common//src//java//org//apache//lucene//analysis//util//ElisionFilter.java";
		
		extraction.extractFileDepth_F101( fileName, codeInfo);
		*/
	}
	
}
