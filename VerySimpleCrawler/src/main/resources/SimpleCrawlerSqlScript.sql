CREATE DATABASE SimpleCrawler;

USE SimpleCrawler;

CREATE TABLE Crawler (
  CrawlRunID int NOT NULL AUTO_INCREMENT,
  CrawlURL varchar(50) NOT NULL,  
  CrawlDate DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (CrawlRunID)
);

CREATE TABLE WordCount (
  CrawlRunID int NOT NULL,
  Word varchar(100) NOT NULL,  
  Count int NOT NULL,
  PRIMARY KEY (CrawlRunID, Word),
  FOREIGN KEY (CrawlRunID) REFERENCES Crawler(CrawlRunID)
);