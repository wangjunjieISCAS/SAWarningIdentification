package com.featureExtractionInitial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.comon.BugInfo;
import com.comon.BugLocation;
import com.comon.Constants;
import com.comon.StaticWarning;
import com.comon.StaticWarningInfo;
import com.comon.Constants.BUG_LOCATION_REGION_TYPE;

/*
 * 从fingBugs生成的xml文件中，识别出来bug对应的class 和 codeLine
 */
public class WarningParser {
	
	public ArrayList<StaticWarning> parseFingbugsWarnings (String fileName  ){
		ArrayList<StaticWarning> warningInfoList = new ArrayList<StaticWarning>();
				
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build( fileName );
			Element rootElement = doc.getRootElement();
			List warningList = rootElement.getChildren( "BugInstance");
			
			System.out.println( warningList.size() );
			for ( int i = 0;  i < warningList.size(); i++ ){
				Element element = (Element) warningList.get( i );
				
				String category = element.getAttribute( "category").getValue();
				//只保留某些种类的warning
				/*
				int p = 0;
				for ( ; p < Constants.WARN_CATEGORY_UNDER_INVESTIGATION.length; p++ ){
					if ( category.equals( Constants.WARN_CATEGORY_UNDER_INVESTIGATION[p]))
						break;
				}
				if ( p == Constants.WARN_CATEGORY_UNDER_INVESTIGATION.length )
					continue;
				*/
				
				String type = element.getAttribute( "type").getValue();
				Integer priority = Integer.parseInt( element.getAttribute( "priority").getValue() );
				Integer rank = Integer.parseInt( element.getAttribute( "rank").getValue() );
				
				BugInfo bugInfo = new BugInfo ( type, priority, rank ,category );			
						
				ArrayList<BugLocation> bugLocationList = new ArrayList<BugLocation>();
				//不是从单独的sourceLine中得到的信息，先都解析出来，最后再确定是否需要最为最终的
				ArrayList<BugLocation> secBugLocationList = new ArrayList<BugLocation>();     
				
				//如果有单独的sourceLine，就用单独的sourceLine的数据	
				List codeList = element.getChildren( "SourceLine");
				if ( codeList != null && codeList.size() != 0 ){
					for ( int j = 0; j< codeList.size(); j++ ){
						Element detailElement = (Element) codeList.get( j );
						if ( detailElement.getAttribute( "sourcepath") == null || detailElement.getAttribute( "start") == null)
							continue;
						
						String className = detailElement.getAttribute( "sourcepath").getValue();
						Attribute attributeStart = detailElement.getAttribute("start");
						Integer startCodeLine = Integer.parseInt( attributeStart.getValue() );
						Attribute attributeEnd = detailElement.getAttribute( "end");
						Integer endCodeLine = Integer.parseInt( attributeEnd.getValue() );
						
						BugLocation bugLocation = new BugLocation ( className, startCodeLine, endCodeLine, BUG_LOCATION_REGION_TYPE.DEFAULT, "" );
						
						//有时会出现连续多个sourceLine，排除掉相等的情况。
						boolean tag = true;
						for ( int k = 0; k < bugLocationList.size() && tag ; k++ ) {
							if ( bugLocationList.get(k).getStartLine().equals( startCodeLine ) && bugLocationList.get(k).getEndLine().equals( endCodeLine )
									&& bugLocationList.get(k).getClassName().equals(className) )
								tag = false;
						}
						if ( (bugLocation.getStartLine() != 1 ) && ( bugLocationList.size() == 0  || tag == true ) ){
							bugLocationList.add( bugLocation );
						}						
					}
				}
				
				String[] regionStr = {"Class", "Field", "Method", "Type" };
				BUG_LOCATION_REGION_TYPE[] region = {BUG_LOCATION_REGION_TYPE.CLASS, BUG_LOCATION_REGION_TYPE.FIELD, 
						BUG_LOCATION_REGION_TYPE.METHOD, BUG_LOCATION_REGION_TYPE.TYPE };
				for ( int j = 0; j< regionStr.length ; j++ ){
					ArrayList<BugLocation> bugLocationListPerRegion = this.parseSpecificSite(element, regionStr[j], region[j] );
					secBugLocationList.addAll( bugLocationListPerRegion );
				}	 
				
				
				//只考虑bugLocationList中的，不需要考虑secBugLocationList中的；但需要用secBugLocationList中的信息对bugLocationList中的信息进行修改
				if ( bugLocationList.size() != 0 ){
					String location = bugLocationList.get(0).getClassName();
					int j = 1;
					//判断是否所有的fileName是同一个，如果不是，则不要该warning
					for ( ; j < bugLocationList.size(); j++ ){
						String newLocation = bugLocationList.get( j ).getClassName();
						if ( !location.equals( newLocation ) ) 
							break;
					}
					if ( j < bugLocationList.size() ){
						System.out.println ( "Multiple class name! Do not store the warning!");
					}
					else{
						String methodName = "";
						for ( j =0;  j < secBugLocationList.size(); j++ ){
							BugLocation bugLoc = secBugLocationList.get( j );
							if ( bugLoc.getRegion() == Constants.BUG_LOCATION_REGION_TYPE.METHOD 
									&& bugLoc.getClassName().equals( bugLocationList.get(0).getClassName() ) ) {
								methodName = bugLoc.getRelatedMethodName();
							}
						}
						
						if ( !methodName.equals( "")){
							for ( j = 0; j < bugLocationList.size(); j++ ){
								bugLocationList.get(j).setRelatedMethodName( methodName );
							}
						}
						
						StaticWarning warning = new StaticWarning ( bugInfo, bugLocationList );
						warningInfoList.add( warning );
						System.out.println( warning.toString() );
					}			
				}
				//处理没有单独的SourceLine的情况，直接把所有的都加上
				else if ( secBugLocationList.size() != 0 ){
					StaticWarning warning = new StaticWarning ( bugInfo, secBugLocationList );
					warningInfoList.add( warning );
					System.out.println( warning.toString() );
				}				
				else{
					System.out.println ( "Wrong parse! Do not have the information!" + bugInfo.getType() );
				}
			}
			System.out.println( warningInfoList.size() );
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//warningInfoList = this.obtainCodeInfo(warningInfoList, folderName);
		return warningInfoList;
	}
	
	public ArrayList<BugLocation> parseSpecificSite( Element element, String regionStr, BUG_LOCATION_REGION_TYPE region ){
		ArrayList<BugLocation> bugLocationList = new ArrayList<BugLocation>();
		
		List typeList = element.getChildren( regionStr );
		if ( typeList != null && typeList.size() != 0  ){
			for ( int j = 0; j < typeList.size(); j++ ){
				Element detailElement = (Element) typeList.get( j );
				
				String methodName = "";
				if ( region == Constants.BUG_LOCATION_REGION_TYPE.METHOD ){
					Attribute attributeName = detailElement.getAttribute( "name");
					if ( attributeName != null )
						methodName = attributeName.getValue();
				}
				
				BugLocation bugLocation = this.generateBugLocation(detailElement,  region);
				if ( bugLocation != null ){
					bugLocation.setRelatedMethodName( methodName );
					bugLocationList.add( bugLocation );
				}					
			}
		}
		
		return bugLocationList;
	}
	
	public BugLocation generateBugLocation ( Element element, BUG_LOCATION_REGION_TYPE region ){
		String className = "";
		Integer startCodeLine = 0;
		Integer endCodeLine = 0;
		
		List detailList = element.getChildren( "SourceLine");
		//默认只有一个
		if ( detailList.size() == 0 )
			return null;
		
		Element detailElement = (Element) detailList.get( 0 );
		
		Attribute attribute = detailElement.getAttribute( "sourcepath");
		if ( attribute != null )
			className = attribute.getValue();
		Attribute attributeStart = detailElement.getAttribute("start");
		if ( attributeStart != null )
			startCodeLine = Integer.parseInt( attributeStart.getValue() );
		Attribute attributeEnd = detailElement.getAttribute( "end");
		if ( attributeEnd != null )
			endCodeLine = Integer.parseInt( attributeEnd.getValue() );
		
		if ( className.equals( "") || startCodeLine == 0 || endCodeLine == 0 )
			return null;
		
		BugLocation bugLocation = new BugLocation ( className, startCodeLine, endCodeLine, region, "" );
		return bugLocation;
	}
	
	
	public ArrayList<String> obtainAllFiles ( String filePath, String relativePath ){
		ArrayList<String> fileList = new ArrayList<String>();
		
		File root = new File ( filePath );
		File[] files = root.listFiles();
		for ( File file: files ){
			if ( file.isDirectory() ){
				ArrayList<String> fileListFolder = obtainAllFiles ( file.getAbsolutePath(), relativePath );
				fileList.addAll( fileListFolder );
			}
			else{
				String absolutePath = file.getAbsolutePath();
				String newRelativePath = relativePath.replace( "//" , "\\");
				String path = absolutePath.substring( newRelativePath.length() );
				fileList.add( path );
				//System.out.println ( path );
			}
		}
		return fileList;
	}
	
	/*
	 * 运行该函数，通过codeLine和 源代码信息，得到codeInfo
	 */
	public ArrayList<StaticWarning> obtainCodeInfo( ArrayList<StaticWarning> warningInfoList, String folderName  ){
		
		ArrayList<String> fileList = this.obtainAllFiles( folderName, folderName );
		
		ArrayList<String> shortFileList = new ArrayList<String>();
		/*
		 * 初始的fileName是这种形式 lucene\analysis\kuromoji\src\java\org\apache\lucene\analysis\ja\tokenattributes\ReadingAttribute.java
		 * warning.xml里面的package是这种形式 org\apache\lucene\analysis\ja\tokenattributes\ReadingAttribute.java
		 * 都是从org开始的
		 */
		for ( int i = 0; i < fileList.size(); i++ ){
			String fileName = fileList.get( i );
			
			int orgIndex = fileName.indexOf( "org");
			if ( orgIndex < 0 ){
				shortFileList.add( fileName );
			}
			else{
				fileName = fileName.substring( orgIndex );
				shortFileList.add( fileName );
			}
		}
		
		//System.out.println( fileList.get( 1000 ) + " " + fileList.size() );
		
		for ( int i = 0; i < warningInfoList.size(); i++ ){
			StaticWarning warning = warningInfoList.get( i );
			ArrayList<BugLocation> bugLocationList = warning.getBugLocationList();
			
			for ( int j = 0; j < bugLocationList.size(); j++ ){
				BugLocation location = bugLocationList.get(j);
				String className = location.getClassName();
				
				className = className.replace( "/", "\\");
				Integer startCodeLine = location.getStartLine();
				Integer endCodeLine = location.getEndLine();
				BUG_LOCATION_REGION_TYPE region = location.getRegion();
				
				ArrayList<String> codeInfoList = new ArrayList<String>();
				
				//专门针对tomcat项目，外面还有一层，但是在warning文件中没有包含
				//String refinedClassName = "java\\" + className;
				//针对cass项目
				String refinedClassName = "src\\java\\" + className;
				boolean isFind = false;
				if ( shortFileList.contains( className ) ){
					isFind = true;
				}
				else{
					if ( shortFileList.contains( refinedClassName ) ){
						isFind = true;
						className = refinedClassName;
					}
				}
					
				
				if ( isFind  ) {
					int index = shortFileList.indexOf( className );
					String completeClassName = fileList.get( index );
					
					BufferedReader br;
					try {
						completeClassName = completeClassName.replace( "\\", "//");
						br = new BufferedReader(new FileReader( new File ( folderName + completeClassName )));
						//System.out.println ( "------------------------------------------------- " + folderName + completeClassName);
						
						String line = "";
						int codeIndex = 0;
						while ( ( line = br.readLine() ) != null ) {
							codeIndex += 1;
							if ( codeIndex >= startCodeLine && codeIndex <= endCodeLine ){
								//这里不能用trim，因为需要获得空格
								codeInfoList.add( line );
							}
						}	
						location.setCodeInfoList( codeInfoList );
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					System.out.println ( "Do not find the file: " + folderName + className );
				}
			}
		}	
		return warningInfoList;
	}
	
	//将warningInfoList中没有代码信息的去掉
	public ArrayList<StaticWarning> refineWarningWithoutCode ( ArrayList<StaticWarning> warningInfoList ){
		ArrayList<StaticWarning> newWarningInfoList = new ArrayList<StaticWarning> ();
		
		for ( int i =0; i < warningInfoList.size(); i++ ){
			StaticWarning warning = warningInfoList.get(i);
			boolean hasCode = false;
			for ( int j =0; j < warning.getBugLocationList().size(); j++ ){
				ArrayList<String> codeInfoList = warning.getBugLocationList().get(j).getCodeInfoList();
				if ( codeInfoList.size() != 0 && !codeInfoList.get(0).trim().equals( "")){
					hasCode = true;
				}
			}
			if ( hasCode ){
				newWarningInfoList.add( warning );
			}
		}
		return newWarningInfoList;
	}
	
	//需要先运行该程序，将不用的warning Category删除掉
	//也能涉及到多种类型的refine，例如某个category下面的某个type，
	//在单类型研究中也会用到
	//针对cass项目，将UUF_UNUSED_FIELD type的warning去除掉
	public ArrayList<StaticWarning> refineWarningInfoListStyle (  ArrayList<StaticWarning> warningInfoList ){
		//得到所有的warn类型
		ArrayList<StaticWarning> refinedWarningList = new ArrayList<StaticWarning>();
		for ( int i =0; i < warningInfoList.size(); i++ ){
			String type = warningInfoList.get( i ).getBugInfo().getType();
			if ( !type.trim().equals( "UUF_UNUSED_FIELD")){
				refinedWarningList.add( warningInfoList.get( i ));
			}
		}
		
		System.out.println ( "refined warning list size: " + refinedWarningList.size() ); 
		return refinedWarningList;
	}
	
	
	//运行一次得到所有method的，在WarningCharacteristics中调用
	public HashMap<String, Integer> obtainWarningNumberForMethod ( ArrayList<StaticWarning> warningInfoList ){
		HashMap<String, Integer> warningNumberForMethod = new HashMap<String, Integer>();
		for ( int i = 0; i < warningInfoList.size(); i++ ){
			StaticWarning warning = warningInfoList.get( i );
			ArrayList<BugLocation> bugLocationList = warning.getBugLocationList();
			
			//因为在bugLocationList里面会出现一个method里面的多处位置，应该算作一个warning。这里需要找到不同的method
			Set<String> methodSet = new HashSet<String>();
			for ( int j = 0; j < bugLocationList.size(); j++ ){
				BugLocation bugLoc = bugLocationList.get( j );
				String method = bugLoc.getRelatedMethodName();
				if ( method.equals( ""))
					continue;
				
				String name = bugLoc.getClassName();
				name = name +"-" + method;
				
				methodSet.add( name );
			}
			for ( String methodName: methodSet){
				int num = 1;
				if ( warningNumberForMethod.containsKey( methodName )){
					num += warningNumberForMethod.get( methodName );
				}
				warningNumberForMethod.put( methodName, num );
			}
		}		
		return warningNumberForMethod;
	}
	
	//运行一次得到所有warningType的
	public HashMap<String, Integer> obtainWarningNumberForWarnType ( ArrayList<StaticWarning> warningInfoList ){
		HashMap<String, Integer> warningNumberForType = new HashMap<String, Integer>();
		for ( int i = 0; i < warningInfoList.size(); i++ ){
			StaticWarning warning = warningInfoList.get( i );
			BugInfo bugInfo = warning.getBugInfo();
			String warnType = bugInfo.getType();
			
			int num = 1;
			if ( warningNumberForType.containsKey( warnType )){
				num += warningNumberForType.get( warnType );
			}
			warningNumberForType.put( warnType, num );
		}
		
		int total = 0;
		for ( String key: warningNumberForType.keySet() ){
			int value = warningNumberForType.get( key );
			total += value;
		}
		
		warningNumberForType.put( "total", total);
		
		return warningNumberForType;
	}	
	
	/*
	 * 生成StaticWarningInfo
	 */
	public StaticWarningInfo obtainWarningTypeCategoryInfo ( ArrayList<StaticWarning> warning ){
		StaticWarningInfo warningInfo = new StaticWarningInfo();
		for ( int i = 0; i< warning.size(); i++ ){
			StaticWarning warningDetail = warning.get( i );
			String category = warningDetail.getBugInfo().getCategory();
			String type = warningDetail.getBugInfo().getType();
			
			warningInfo.getCategoryList().add( category );
			if ( !warningInfo.getTypeToCateogoryMap().containsKey( type ) ){
				warningInfo.getTypeToCateogoryMap().put( type, category );
				
				if ( warningInfo.getTypeInCategoryList().containsKey( category ))
					warningInfo.getTypeInCategoryList().get(category).add( type );
				else{
					HashSet<String> typeList = new HashSet<String>();
					typeList.add( type );
					warningInfo.getTypeInCategoryList().put( category, typeList );
				}
			}			
		}
		return warningInfo;
	}
	
	
	public static void main ( String args[] ){
		WarningParser parser = new WarningParser();
		String folderName = "D://java-workstation//lucene2.9.2//src//";
		ArrayList<StaticWarning> warningInfoList  = parser.parseFingbugsWarnings( "data/warning.xml" );
		//parser.obtainWarningNumberForMethod(warningInfoList);
		//parser.obtainWarningNumberForWarnType(warningInfoList);
		//parser.refineWarningInfoList(warningInfoList);
		//parser.obtainCodeInfo( "D://java-workstation//lucene2.9.2//src//");
	}
}
