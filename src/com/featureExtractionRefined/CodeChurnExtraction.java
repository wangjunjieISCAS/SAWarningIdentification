package com.featureExtractionRefined;

import java.util.HashMap;

import com.comon.Constants;
import com.comon.StaticWarning;
import com.featureExtractionInitial.CodeChurnFeatureExtraction;

public class CodeChurnExtraction extends BasicFeatureExtraction{
	HashMap<String, HashMap<String, Object>> codeChurnFileTime;
	HashMap<String, HashMap<String, Object>> codeChurnFileRevision;
	HashMap<String, HashMap<String, Object>> codeChurnPackageTime;
	HashMap<String, HashMap<String, Object>> codeChurnPackageRevision;
	HashMap<String, Object> codeChurnProjectTime;
	HashMap<String, Object> codeChurnProjectRevision;
	
	public CodeChurnExtraction(String fileName, String folderName) {
		super(fileName, folderName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void featureExtractionPrecondition() {
		// TODO Auto-generated method stub
		super.featureExtractionPrecondition();
		
		CodeChurnFeatureExtraction featureExtraction = new CodeChurnFeatureExtraction();
		codeChurnFileTime = featureExtraction.obtainCodeChurnForAllFiles(projectInfo, "time");
		codeChurnFileRevision = featureExtraction.obtainCodeChurnForAllFiles(projectInfo, "revision");
		
		codeChurnPackageTime = featureExtraction.obtainCodeChurnForAllPackages(projectInfo, codeChurnFileTime );
		codeChurnPackageRevision = featureExtraction.obtainCodeChurnForAllPackages(projectInfo, codeChurnFileRevision );
		
		codeChurnProjectTime = featureExtraction.obtainCodeChurnForThisProject(projectInfo, codeChurnPackageTime);
		codeChurnProjectRevision = featureExtraction.obtainCodeChurnForThisProject(projectInfo, codeChurnPackageRevision );
	}
		
	@Override
	public HashMap<String, Object> extractFeatures(StaticWarning warning, int index ) {
		// TODO Auto-generated method stub
		super.extractFeatures(warning, index);
		
		CodeChurnFeatureExtraction featureExtraction = new CodeChurnFeatureExtraction();
		
		String fileName = warning.getBugLocationList().get(0).getClassName();
		String packageName = projectInfo.getFilePackageNameMap().get( fileName );
		
		HashMap<String, Object> F35_to_F40 = featureExtraction.extractCodeChurnInFile_F35_to_F40(fileName, codeChurnFileTime );		
		HashMap<String, Object> F126_to_F131 = featureExtraction.extractCodeChurnInFile_F126_to_F131(fileName, codeChurnFileRevision );		
		
		HashMap<String, Object> F41_to_F46 = featureExtraction.extractCodeChurnInPackage_F41_to_F46(packageName, codeChurnPackageTime);
		HashMap<String, Object> F132_to_F137 = featureExtraction.extractCodeChurnInPackage_F132_to_F137(packageName, codeChurnPackageRevision);
		
		HashMap<String, Object> F47_to_F52 = featureExtraction.extractCodeChurnInProject_F47_to_F52(codeChurnProjectTime);
		HashMap<String, Object> F138_to_F143 = featureExtraction.extractCodeChurnInProject_F138_to_F143(codeChurnProjectRevision);
		
		System.out.println( "F35_to_F40: " + F35_to_F40 );
		System.out.println( "F41_to_F46: " + F41_to_F46 );
		System.out.println( "F47_to_F52: " + F47_to_F52 );
		System.out.println( "F126_to_F131: " + F126_to_F131 );
		System.out.println( "F132_to_F137: " + F132_to_F137 );
		System.out.println( "F138_to_F143: " + F138_to_F143 );
		
		HashMap<String, Object> featureValue = new HashMap<String, Object>();
		
		F35_to_F40 = this.refineCodeChurn( F35_to_F40, 35);
		F41_to_F46 = this.refineCodeChurn( F41_to_F46, 41);
		F47_to_F52 = this.refineCodeChurn( F47_to_F52, 47);
		
		F126_to_F131 = this.refineCodeChurn( F126_to_F131, 126 );
		F132_to_F137 = this.refineCodeChurn( F132_to_F137, 132 );
		F138_to_F143 = this.refineCodeChurn( F138_to_F143, 138 );
		
		featureValue.putAll( F35_to_F40 );
		featureValue.putAll( F41_to_F46 );
		featureValue.putAll( F47_to_F52 );
		featureValue.putAll( F126_to_F131 );
		featureValue.putAll( F132_to_F137 );
		featureValue.putAll( F138_to_F143 );
		
		return featureValue;
	}

	public HashMap<String, Object> refineCodeChurn ( HashMap<String, Object> codeChurnInfo, int beginIndex  ){
		String[] featureName = {"add", "delete", "change", "churn", "growth", "percentChurn"};
		HashMap<String, Object> refinedCodeChurnInfo = new HashMap<String, Object>();
		
		for ( int i =0; i < featureName.length; i++ ){
			Object churnInfo = codeChurnInfo.get( featureName[i]);
			refinedCodeChurnInfo.put( "F" + beginIndex, churnInfo);
			beginIndex++;
		}

		return refinedCodeChurnInfo;
	}
	
	@Override
	public void generateFeatures() {
		// TODO Auto-generated method stub
		super.generateFeatures();
	}
	
	
	@Override
	public String obtainOutputFileName() {
		// TODO Auto-generated method stub
		String fileName = Constants.FEATURE_VALUE_OUTPUT_FOLDER + "codeChurn.csv";
		return fileName;
	}

	public static void main ( String args[] ){
		CodeChurnExtraction featureExtraction = new CodeChurnExtraction ( Constants.WARNING_FILE_NAME, Constants.FOLDER_NAME );
		featureExtraction.generateFeatures();
	}
	
}
