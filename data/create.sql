use staticwarning;
CREATE TABLE `jmeter_commit_content` (
  `contentId` int(11) NOT NULL,
  `className` varchar(500) DEFAULT NULL,
  `commitType` varchar(10) DEFAULT NULL,
  `commitId` int(11) DEFAULT NULL,
  `commitTime` datetime DEFAULT NULL,
  `issueType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`contentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `jmeter_commit_info` (
  `commitAutoId` int(11) NOT NULL,
  `commitHashId` varchar(255) DEFAULT NULL,
  `commitTime` datetime(6) DEFAULT NULL,
  `issueId` varchar(50) DEFAULT NULL,
  `issueName` varchar(1000) DEFAULT NULL,
  `issueType` varchar(20) DEFAULT NULL,
  `developerEmail` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`commitAutoId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `jmeter_issue_info` (
  `issueAutoId` int(11) NOT NULL,
  `issueId` varchar(50) DEFAULT NULL,
  `issueType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`issueAutoId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



