The VerySimpleCrawler accepts a URL as input, generates a count of all the non-HTML words on 
the page referenced by the URL, and persists the word count to a MySQL database (and records the 
time when the page was loaded). It is called VerySimpleCrawler since it is designed to 
process only one level of URL (input URL).

The project was created on Eclipse using Maven.

The following jars are added as dependencies in the pom.xml
i) jsoup-1.7.2.jar
ii) mysql-connector-java-5.1.36.jar

The folder 'src/main/resources' includes two files 
i) SimpleCrawlerSqlScript.sql - the database schema 
ii) dbconn.properties - a properties file with config parameters for db connectivity

The SimpleCrawler project has two packages:
i) com.enterprise: This package has two classes (i) SimpleCrawler and (ii) DocumentProcessor
ii) com.enterprise.dao: This package has one class (i) SimpleCrawlerDAO

Steps to run the application:
i) Create the database schema in MySQL by running the SimpleCrawlerSqlScript.sql
ii) Import the project jar file into Eclipse
iii) Set the database url, username, and password in the dbconn.properties file and place this 
file in the project folder of the Eclipse workspace
iv) Run the application from the SimpleCrawler class which has the main method

Testing:
The application was tested successfully with several URLs including https://www.yahoo.com, 
https://www.google.com, and https://www.bing.com

Testing TODO:
Create a webpage on localhost with predetermined number of words as non-HTML content to 
validate the application using JUnit





