package com.featureExtractionInitial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.comon.BugInfo;
import com.comon.BugLocation;
import com.comon.ProjectInfo;
import com.comon.StaticWarning;
import com.comon.StaticWarningInfo;

/*
 * warning相关的这几个featureExtraction仅仅是为了使每个类的内容变少些
 */
public class WarningCombinedFeatureExtraction {
	
	/*
	 * 需要先运行WarningStatus中的obtainWarningStatus，得到warningStatusList
	 */
	public HashMap<String, Double>  obtainClosedSuppressedRatio ( ArrayList<StaticWarning> warningList, ArrayList<String> warningStatusList ){
		HashMap<String, Integer> warningTypeCloseMap = new HashMap<String, Integer>();
		HashMap<String, Integer> warningTypeOpenMap = new HashMap<String, Integer>();
		HashMap<String, Integer> warningTypeMap = new HashMap<String, Integer>();
		
		//目前delete的情况没有考虑 
		for ( int i = 0;  i< warningList.size(); i++ ){
			BugInfo bugInfo = warningList.get(i).getBugInfo();
			String type = bugInfo.getType();
			int temp = 1;
			if ( warningTypeMap.containsKey( type )){
				temp += warningTypeMap.get( type );
			}
			warningTypeMap.put( type, temp );
			
			String status = warningStatusList.get( i );
			if ( status.equals( "close")){
				int count = 1;
				if ( warningTypeCloseMap.containsKey( type )){
					count += warningTypeCloseMap.get( type );
				}
				warningTypeCloseMap.put( type, count );
			}
			if ( status.equals( "open")){
				int count = 1;
				if ( warningTypeOpenMap.containsKey( type )){
					count += warningTypeOpenMap.get( type );
				}
				warningTypeOpenMap.put( type, count );
			}
		}
		
		HashMap<String, Double> statusPercentMap = new HashMap<String, Double>();
		for ( String key: warningTypeMap.keySet() ){
			int totalCount = warningTypeMap.get( key );
			int openCount = 0;
			if ( warningTypeOpenMap.containsKey( key ))
				openCount = warningTypeOpenMap.get( key);
			int closeCount = 0;
			if ( warningTypeCloseMap.containsKey( key ))
				closeCount = warningTypeCloseMap.get( key);
			
			if ( openCount + closeCount != totalCount ){
				System.out.println( "Wrong in obtainClosedSuppressedRatio!" + " " + openCount + " " + closeCount + " " + totalCount );
			}
			double percent = (1.0* ( closeCount - openCount ) ) / (1.0*totalCount);
			statusPercentMap.put( key , percent );
		}
		return statusPercentMap;
	}
	
	//需要运行一次obtainClosedSuppressedRatio 得到statusPercentMap
	public Double extractClosedSuppressedRatioType_F110 ( String type, HashMap<String, Double> statusPercentMap ){
		double ratio = 0.0;
		if ( statusPercentMap.containsKey( type )){
			ratio = statusPercentMap.get( type );
		}
		return ratio;
	}
	
	//需要先运行obtainClosedSuppressedRatioMetFPro，得到总的ratio
	public Double extractClosedSuppressedRatioPackage_F114 ( String packageName, HashMap<String, Double> warningRatioMap ){
		double ratio = 0.0;
		if ( warningRatioMap.containsKey( packageName ))
			ratio = warningRatioMap.get( packageName );
		
		return ratio;
	}
	public Double extractClosedSuppressedRatioFile_F115 ( String fileName, HashMap<String, Double> warningRatioMap ){
		double ratio = 0.0;
		if ( warningRatioMap.containsKey( fileName ))
			ratio = warningRatioMap.get( fileName );
		
		return ratio;
	}
	public Double extractClosedSuppressedRatioMethod_F116 ( String methodName, HashMap<String, Double> warningRatioMap ){
		double ratio = 0.0;
		if ( warningRatioMap.containsKey( methodName ))
			ratio = warningRatioMap.get( methodName );
		
		return ratio;
	}
	
	
	public HashMap<String, HashMap<String, Double>> obtainDefectLikelihoodType ( HashMap<String, Integer> closeTypeCountMap, HashMap<String, Integer> warningTypeCountMap, StaticWarningInfo warningInfo ){
		HashMap<String, Double> defectLikelihoodType = new HashMap<String, Double>();
		HashMap<String, Double> defectLikelihoodPercentType = new HashMap<String, Double>();
		
		for ( String category: warningInfo.getCategoryList() ){
			for ( String type: warningInfo.getTypeInCategoryList().get( category )){
				int totalCount = 0;
				if ( warningTypeCountMap.containsKey( type ) )
					totalCount = warningTypeCountMap.get( type );
				int closeCount  = 0;
				if ( closeTypeCountMap.containsKey( type ))
					closeCount = closeTypeCountMap.get( type );
				
				double percent = 0.0, variance = 0.0;
				int numTypeInCategory = warningInfo.getTypeInCategoryList().get( category ).size();
				
				if ( totalCount !=0 ){
					percent = (1.0*closeCount) / (1.0*totalCount);
					variance =( percent * (1-percent) ) / numTypeInCategory;
				}
				
				defectLikelihoodType.put( type , percent );
				defectLikelihoodPercentType.put( type, variance );
			}
		}
		
		HashMap<String, HashMap<String, Double>> result = new HashMap<String, HashMap<String, Double>>();
		result.put( "likelihood", defectLikelihoodType );
		result.put( "variance", defectLikelihoodPercentType );
		
		return result;
	}
	
	//对于F123
	public HashMap<String,Double> obtainLifetimeCategory ( StaticWarningInfo warningInfo, ArrayList<StaticWarning> warningList ){
		WarningHistoryFeatureExtraction featureExtraction = new WarningHistoryFeatureExtraction();
		HashMap<String, HashMap<Integer, Object>> openRevisionFilesList = featureExtraction.obtainAlertOpenRevisionForAllFiles ( warningList );
		
		WarningHistoryFeatureExtraction historyFeatureExtraction = new WarningHistoryFeatureExtraction();		
		HashMap<String, Double> lifetimeCategory = new HashMap<String, Double>();
		HashMap<String, Integer> categoryNumber = new HashMap<String, Integer>();
		
		for ( int i =0; i < warningList.size(); i++ ){
			StaticWarning warning = warningList.get( i );
			String category = warning.getBugInfo().getCategory();
			
			int number = 1;
			if ( categoryNumber.containsKey( category))
				number += categoryNumber.get( category );
			categoryNumber.put( category, number );
			
			ArrayList<String> codeInfo = new ArrayList<String>();
			for ( int j = 0; j < warning.getBugLocationList().size(); j++ ){
				ArrayList<String> temp = warning.getBugLocationList().get(j).getCodeInfoList();
				codeInfo.addAll( temp );
			}
			String fileName = warning.getBugLocationList().get(0).getClassName();
			
			double time = historyFeatureExtraction.extractAlertLifeTime_F88( i, openRevisionFilesList ) ;
			if ( lifetimeCategory.containsKey( category )){
				time += lifetimeCategory.get( category );
			}
			lifetimeCategory.put( category, time );
		}
		for ( String category: lifetimeCategory.keySet() ){
			lifetimeCategory.put( category,  lifetimeCategory.get(category) / categoryNumber.get( category ));
		}
		
		return lifetimeCategory;
	}
	
	//首先运行obtainLifetimeCategory，得到lifetimeCategory 
	public double extractLifetimeCategory_F123 (HashMap<String, Double> lifetimeCategory, String category ){
		double lifeTime = 0.0;
		if ( lifetimeCategory.containsKey( category ))
			lifeTime = lifetimeCategory.get( category );
		
		return lifeTime;
	}
	
	//首先运行obtainDefectLikelihoodType，得到defectLikelihoodType和defectLikelihoodPercentType
	public double extractDefectLikelihoodType_F117 ( String type, HashMap<String, Double> defectLikelihoodType ){
		double percent = 0.0;
		if ( defectLikelihoodType.containsKey( type ))
			percent = defectLikelihoodType.get( type );
		
		return percent;
	}
	public double extractDefectLikelihoodVarianceType_F118 ( String type, HashMap<String, Double> defectLikelihoodPercentType ){
		double percent = 0.0;
		if ( defectLikelihoodPercentType.containsKey( type ))
			percent = defectLikelihoodPercentType.get( type );
		
		return percent;
	}
	
	//首先运行obtainDefectLikelihoodType，得到defectLikelihoodType
	public HashMap<String, HashMap<String, Double>> obtainDefectLikelihoodCategory ( StaticWarningInfo warningInfo, HashMap<String, Double> defectLikelihoodType, HashMap<String, Integer> warningTypeCountMap ){
		HashMap<String, Double> defectLikelihoodCategory = new HashMap<String, Double> ();
		HashMap<String, Double> defectLikelihoodDiscretCategory = new HashMap<String, Double> ();
		
		for ( String category: warningInfo.getCategoryList() ){
			int totalDefect = 0;
			double totalDefectLikihood = 0.0;
			
			for ( String type: warningInfo.getTypeInCategoryList().get(category)){
				double percent = defectLikelihoodType.get( type );
				int count = warningTypeCountMap.get( type );
				
				totalDefect += count;
				totalDefectLikihood += (1.0*count) * percent;				
			}
			defectLikelihoodCategory.put( category, totalDefectLikihood / totalDefect );
		}
		
		for ( String category: warningInfo.getCategoryList() ){
			double totalDefectLikihood = 0.0;
			double defectLikehoodCategory = defectLikelihoodCategory.get( category );
					
			for ( String type: warningInfo.getTypeInCategoryList().get(category)){
				double percent = defectLikelihoodType.get( type );
				
				totalDefectLikihood += (percent - defectLikehoodCategory)*(percent - defectLikehoodCategory);	
			}
			int size = warningInfo.getTypeInCategoryList().size() - 1;
			totalDefectLikihood = totalDefectLikihood / ( 1.0*size );
			defectLikelihoodDiscretCategory.put( category,  totalDefectLikihood);
		}
		
		HashMap<String, HashMap<String, Double>> result = new HashMap<String, HashMap<String, Double>> ();
		result.put( "likelihood", defectLikelihoodCategory );
		result.put( "variance", defectLikelihoodDiscretCategory);
		
		return result;
	}
	
	//首先运行obtainDefectLikelihoodCategory，得到defectLikelihoodCategory和defectLikelihoodDiscretCategory
	public double extractDefectLikelihoodCategory_F119 ( String category, HashMap<String, Double> defectLikelihoodCategory ){
		double percent = 0.0;
		if ( defectLikelihoodCategory.containsKey( category ))
			percent = defectLikelihoodCategory.get( category );
		
		return percent;
	}
	public double extractDefectLikelihoodVarianceCategory_F120 ( String category, HashMap<String, Double> defectLikelihoodDiscretCategory ){
		double percent = 0.0;
		if ( defectLikelihoodDiscretCategory.containsKey( category ))
			percent = defectLikelihoodDiscretCategory.get( category );
		
		return percent;
	}

	
	/*
	 * 需要首先运行WarningParser.obtainWarningNumberForMethod(), 得到warningNumberForMethod
	 * 需要先运行WarningStatus中的obtainWarningStatus，得到warningStatusList
	 */	
	public HashMap<String, HashMap<String, Double>> obtainClosedSuppressedRatioMetFPro ( ArrayList<StaticWarning> warningInfoList, ArrayList<String> warningStatusList, ProjectInfo projectInfo, HashMap<String, Integer> warningNumberForMethod ){
		HashMap<String, Integer> closeNumForPackage = new HashMap<String, Integer>();
		HashMap<String, Integer> closeNumForFile = new HashMap<String, Integer>();
		HashMap<String, Integer> closeNumForMethod = new HashMap<String, Integer>();
		
		for ( int i = 0; i < warningInfoList.size(); i++ ){
			StaticWarning warning = warningInfoList.get( i );
			ArrayList<BugLocation> bugLocationList = warning.getBugLocationList();
			
			String status = warningStatusList.get( i );
			boolean isClose = false;
			if ( status.equals( "close")){
				isClose = true;
			}
			
			if ( isClose == false )
				continue;
			
			//得到对应的package，file，method的name
			String fileName = bugLocationList.get( 0).getClassName();
			int numF = 1;
			if ( closeNumForFile.containsKey( fileName )){
				numF += closeNumForFile.get( fileName );
			}
			closeNumForFile.put( fileName, numF );
			
			String packageName = projectInfo.getFilePackageNameMap().get( fileName );
			int numP = 1;
			if ( closeNumForPackage.containsKey( packageName )){
				numP += closeNumForPackage.get( packageName );
			}
			closeNumForPackage.put( packageName, numP );
			
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
				if ( closeNumForMethod.containsKey( methodName )){
					num += closeNumForMethod.get( methodName );
				}
				closeNumForMethod.put( methodName, num );
			}
		}	
		
		HashMap<String, Double> closeRatioForPackage = this.obtainRatioValueMap( projectInfo.getWarningNumForPackage(), closeNumForPackage);
		HashMap<String, Double> closeRatioForFile =  this.obtainRatioValueMap( projectInfo.getWarningNumForFile(), closeNumForFile );
		HashMap<String, Double> closeRatioForMethod =  this.obtainRatioValueMap( warningNumberForMethod , closeNumForMethod );
		
		HashMap<String, HashMap<String, Double>> result = new HashMap<String, HashMap<String, Double>> ();
		result.put( "package", closeRatioForPackage );
		result.put("file" , closeRatioForFile);
		result.put( "method", closeRatioForMethod);
		
		return result;
	}
	
	public HashMap<String, Double> obtainRatioValueMap (HashMap<String, Integer> warningNumberMap, HashMap<String, Integer> closeNumberMap){
		HashMap<String, Double> closeRatio = new HashMap<String, Double>();
		
		for ( String packageName: warningNumberMap.keySet() ){
			int warningNum = warningNumberMap.get( packageName );
			int closeNum = 0;
			if ( closeNumberMap.containsKey( packageName ))
				closeNum = closeNumberMap.get( packageName );
			int openNum = warningNum - closeNum;
			
			double ratio = 0.0;
			if ( warningNum != 0 ){
				ratio = (1.0*(closeNum-openNum) ) / (1.0*warningNum);
			}
			closeRatio.put( packageName, ratio);
		}
		return closeRatio;
	}
}
