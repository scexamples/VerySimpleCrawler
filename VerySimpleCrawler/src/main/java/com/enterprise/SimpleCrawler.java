package com.enterprise;

import java.util.Map;
import java.util.Scanner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.enterprise.dao.SimpleCrawlerDAO;

/** 
 * The SimpleCrawler class uses jsoup to parse HTML from a URL.
 * 
 * The main method prompts the user for a URL. The static method crawl parses the HTML file 
 * referenced by the URL and returns a HTML document.
 * 
 * The HTML document is passed to an instance of the DocumentProcessor class.
 * 
 * The getWordCount method of the DocumentProcessor then calculates the frequency of each
 * word in the document.
 * 
 * An instance of the SimpleCrawlerDAO object is used to persist the results of the 
 * getWordCount method to the db
 * 
 * @see <a href="http://jsoup.org">http://jsoup.org</a> for jsoup documentation
 * 
 * @author sc
 *
 */

public class SimpleCrawler {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) "
			+ "AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private static final String CONFIG_FILENAME = "dbconn.properties";
	
	public static void main(String[] args) throws Exception {

		SimpleCrawlerDAO scDAO = null;
		Scanner console = null;
		
		try {
			console = new Scanner(System.in);

			/* 
			 * If error loading the config file, uncomment the following line of code to
			 * find out where to put the config file 
			 */

			//System.out.println(new File(".").getAbsolutePath());
			
			scDAO = new SimpleCrawlerDAO(CONFIG_FILENAME);			
			
			scDAO.openConnection(); System.out.println("opened conn");
			scDAO.prepareStatements();
			String URL = "";
			
			// prompt user for a URL
			System.out.println("Enter a URL or press enter to exit:");
			
			while(!(URL = console.nextLine()).equals("")){		
				System.out.println("You entered: " + URL);
				
				Document htmlDocument = SimpleCrawler.crawl(URL); // get HTML doc from URL
				
				DocumentProcessor docProc = new DocumentProcessor(htmlDocument); // pass HTML doc to processor
				Map<String, Integer> wordCount = docProc.getWordCount();
				
			    //System.out.println("wordCount size: " + wordCount.size()); 
				
				scDAO.persistToDB(URL, wordCount); // pass URL and docProc results to db
				
				System.out.println("WordCount saved to the database");
				
				System.out.print("Enter a URL or press enter to exit:"); // prompt for next input URL
			}
			
			scDAO.closeConnection(); System.out.println("closed conn");
			
			console.close();
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			
		}
		finally {
			
			if (scDAO != null)
				scDAO.closeConnection();
			
			if (console != null)
				console.close();
			
		}
		
		System.out.println("\n\n ***** Exiting *****");
	}
	
/**
 * @param URL
 * @return Document - the parsed HTML Document
 * @throws Exception 
 */
	public static Document crawl(String URL) throws Exception {
		
		Document htmlDocument = null;
		
		try {
			Connection connection = Jsoup.connect(URL).userAgent(USER_AGENT);
			htmlDocument = connection.get();
			
			// check status code is OK and retrieved document is html
			if (connection.response().statusCode() != 200){
				System.out.println("received error code: " + connection.response().statusCode());
			}				

			if (!connection.response().contentType().contains("text/html")) {
				System.out.println("retrieved content is not HTML");
			}
			
			
		} catch (Exception ex) {
			
			//ex.printStackTrace();
			throw ex;
		}

		return htmlDocument;
	}
}

