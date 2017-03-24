This folder contains the experimental results involved in the paper.

feature folder: All the feature values for every project revision after feature extraction process.

feature id mapping.csv: Because in the paper and in this website we have different naming order for the features, this file contains the mapping of feature id.


The details in feature folder:

There are 40 folder corresponding to the 40 project revisions, with the folder name being 'project name-time'.


The details in each project folder:

Type 1: warning details 

warningInfo.csv: contain the warnings of the project revision

Type 2: feature values

totalFeatures.csv: contain the values of all the features for the project revision, which is the summarization of the following files. The experiment is conducted based on this file. 

The following files contains the values in each category (note that, the category are not correspondings to the paper, but corresponding to the source code).
For example, codeChurn.csv: contain the features extracted using featureExtractionRefined/CodeChurnExtraction.java
codeHistory.csv 
fileCharacteris.csv 
sourceCode.csv  
sourceCodeSlicer.csv 
warningCharacteris.csv 
warningCombine.csv 
warningHistory.csv 


Type 3: the selected features after feature selection

featureRank.csv: the selected features for all the warning types

The following files are the selected features for the five separate warning types

featureRankBadPractice.csv 
featureRankCorrect.csv 
featureRankDodgy.csv 
featureRankMalicious.csv 
featureRankPerformance.csv 

Type 4: ground truth

labelAll.csv: the label of each warning to be actionable (close), unactionable (open), or delete
