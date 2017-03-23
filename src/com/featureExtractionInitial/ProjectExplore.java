package com.featureExtractionInitial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.comon.ProjectInfo;

public class ProjectExplore {
	
	/*
	 * 通过findbugs工具，得到project相关的package和class信息
	 * 顺便得到某些指标
	 */
	public ProjectInfo obtainPackageClassInfo ( String fileName ){
		ProjectInfo projectInfo = new ProjectInfo ( );
		
		SAXBuilder builder = new SAXBuilder();
		int totalCount =0;
		try {
			Document doc = builder.build( fileName );
			Element rootElement = doc.getRootElement();
			List bugSummaryList = rootElement.getChildren( "FindBugsSummary");
			//只有一个FindBugsSummary
			Element statsElement = (Element) bugSummaryList.get( 0 );
			
			List warningList = statsElement.getChildren( "PackageStats");
			
			System.out.println( warningList.size() );
			for ( int i = 0;  i < warningList.size(); i++ ){
				Element element = (Element) warningList.get( i );
				
				String packageName = element.getAttribute( "package").getValue();
				packageName = packageName.replace( ".", "/");
				Integer bugNumPackage =Integer.parseInt( element.getAttribute( "total_bugs").getValue() );	
				
				projectInfo.getPackageList().add( packageName );
				projectInfo.getWarningNumForPackage().put( packageName, bugNumPackage);
				projectInfo.getFileListForPackage().put( packageName, new ArrayList<String>());
				
				totalCount += bugNumPackage;
				
				List classList = element.getChildren( "ClassStats");
				if ( classList != null && classList.size() != 0 ){
					for ( int j = 0; j< classList.size(); j++ ){
						Element detailElement = (Element) classList.get( j );
						
						String className = detailElement.getAttribute( "class").getValue();
						className = className.replace( ".", "/");
						String relatedFileName = detailElement.getAttribute( "sourceFile").getValue();
						
						Attribute attributeBug = detailElement.getAttribute("bugs");
						Integer bugNumForClass = Integer.parseInt( attributeBug.getValue() );
						
						//得到完整的fileName
						int index = className.lastIndexOf( "/");
						String completeFileName = className.substring(0, index +1 ) + relatedFileName;
						
						projectInfo.getFilePackageNameMap().put( completeFileName, packageName );
						//System.out.println( completeFileName + " " + packageName );
						
						if ( !projectInfo.getFileListForPackage().get( packageName).contains( completeFileName )){
							projectInfo.getFileListForPackage().get( packageName).add( completeFileName );
						}
						if ( projectInfo.getWarningNumForFile().containsKey( completeFileName )){
							bugNumForClass +=  projectInfo.getWarningNumForFile().get( completeFileName );
						}
						projectInfo.getWarningNumForFile().put( completeFileName, bugNumForClass );
						
						int classNum = 1;
						if ( projectInfo.getClassNumForFile().containsKey( completeFileName )){
							classNum += projectInfo.getClassNumForFile().get( completeFileName );
						}
						projectInfo.getClassNumForFile().put( completeFileName, classNum );
					}
				}
			}
			projectInfo.setTotalWarningCount( totalCount );
			/*
			System.out.println ( projectInfo.getPackageList().size() );
			System.out.println( projectInfo.getWarningNumForPackage().get( "org.apache.lucene.analysis" ));
			
			System.out.println ( projectInfo.getFileListForPackage().get( "org.apache.lucene.analysis").size() );
			System.out.println ( projectInfo.getWarningNumForFile().get( "org.apache.lucene.analysis.LookaheadTokenFilter.java") );
			System.out.println ( projectInfo.getClassNumForFile().get( "org.apache.lucene.analysis.LookaheadTokenFilter.java") );
			*/
			
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return projectInfo;
	}
	
	public static void main ( String args[] ){
		ProjectExplore project = new ProjectExplore();
		String warningFile = "data/warning.xml";
		
		project.obtainPackageClassInfo( warningFile );
	}
}
