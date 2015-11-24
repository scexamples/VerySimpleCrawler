package com.enterprise.dao;


import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

/**
 * Run sql statements against a back-end database
 */
public class SimpleCrawlerDAO {
	
	public SimpleCrawlerDAO() {
		
	}

	// Constants
	
	// SQL Statement Constants
	private static final String COMMIT_SQL = "COMMIT;";
	private static final String BEGIN_TRANSACTION_SQL = 
			"START TRANSACTION;";		
	private static final String ROLLBACK_SQL = "ROLLBACK;";
	
	private static final String CRAWLER_INSERT__SQL = 
			"insert into Crawler (CrawlURL) values (?)";
	private static final String MAX_CRAWL_RUN_ID__SQL = 
			"select Max(CrawlRunID) from Crawler";	
	private static final String WORDCOUNT_INSERT__SQL = 
			"insert into WordCount values (?, ?, ?)";

	// Fields
	
	// DB Connection Properties
	private String configFilename;
	private Properties configProps = new Properties();

	private String jSQLDriver;
	private String jSQLUrl;
	private String jSQLUser;
	private String jSQLPassword;
	
	// DB Connection
	private Connection conn;
	
	// Prepared Statements
	private PreparedStatement crawlerInsertStatement;
	private PreparedStatement maxCrawlRunIdStatement;
	private PreparedStatement wordCountInsertStatement;
	private PreparedStatement beginTransactionStatement;
	private PreparedStatement commitTransactionStatement;
	private PreparedStatement rollbackTransactionStatement;

	// Constructor
	public SimpleCrawlerDAO(String configFilename) {
		this.configFilename = configFilename;
	}

	/* 
	 * Open Connection
	 */
	public void openConnection() throws Exception {
		configProps.load(new FileInputStream(configFilename));
		
		jSQLDriver = configProps.getProperty("jSQLDriver");
		jSQLUrl = configProps.getProperty("SimpleCrawler.jdbc_url");
		jSQLUser = configProps.getProperty("jSQL_username");
		jSQLPassword = configProps.getProperty("jSQL_password");
		
		// Load JDBC driver
		Class.forName(jSQLDriver).newInstance();

		// Open connection to the database
		conn = DriverManager.getConnection(jSQLUrl, jSQLUser, jSQLPassword);
		conn.setAutoCommit(true);
	}

	/*
	 * Close Connection
	 */
	public void closeConnection() throws Exception {
		if (conn != null)
		conn.close();
	}

	/*
	 * Begin Transaction
	 */
	public void beginTransaction() throws Exception {
		conn.setAutoCommit(false);
		beginTransactionStatement.executeUpdate();
	}

	/*
	 * Commit Transaction
	 */
	public void commitTransaction() throws Exception {
		commitTransactionStatement.executeUpdate();
		conn.setAutoCommit(true);
	}
	
	/*
	 * Rollback Transaction
	 */
	public void rollbackTransaction() throws Exception {
		rollbackTransactionStatement.executeUpdate();
		conn.setAutoCommit(true);
	}
	
	/* 
	 * Prepare Statements
	 */
	public void prepareStatements() throws Exception {
		crawlerInsertStatement = conn.prepareStatement(CRAWLER_INSERT__SQL);
		maxCrawlRunIdStatement = conn.prepareStatement(MAX_CRAWL_RUN_ID__SQL);
		wordCountInsertStatement = conn.prepareStatement(WORDCOUNT_INSERT__SQL);
		beginTransactionStatement = conn.prepareStatement(BEGIN_TRANSACTION_SQL);
		commitTransactionStatement = conn.prepareStatement(COMMIT_SQL);
		rollbackTransactionStatement = conn.prepareStatement(ROLLBACK_SQL);
	}

	/*
	 * Persist to DB
	 */
	public void persistToDB(String url, Map<String, Integer> counts) throws Exception{
		
		// Begin Transaction
		beginTransaction();

		// Set prepared statement parameters and populate Crawler Table 
		crawlerInsertStatement.setString(1, url);
		crawlerInsertStatement.executeUpdate();
		
		// Get max run id
		int CrawlRunID = 0;		
		ResultSet rs = maxCrawlRunIdStatement.executeQuery();
		
		if(rs.next()) {
			CrawlRunID = rs.getInt(1); // use as FK in WordCount to reference Crawler
			rs.close();
		} else {
			rollbackTransaction();
			System.out.println("ResultSet has no records");
		}
		
		// populate WordCount Table
		for(String word : counts.keySet()){
			
			int count = counts.get(word);
			
			wordCountInsertStatement.setInt(1, CrawlRunID);
			wordCountInsertStatement.setString(2, word);
			wordCountInsertStatement.setInt(3, count);
			
			//System.out.println("CrawlRunID : " + CrawlRunID + ", word: " + word + ", count: " + count );
			
			wordCountInsertStatement.executeUpdate();
		}
		
		// Commit Transaction
		commitTransaction();
		
	}

}

