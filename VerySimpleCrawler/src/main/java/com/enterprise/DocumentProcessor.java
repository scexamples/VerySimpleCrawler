package com.enterprise;


import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

/** 
 * The DocumentProcessor class has one field that stores a html Document
 * It has one method, wordCount that returns a map of each word and its count (frequency)in the document
 * The wordCount method processes the words in a case-insensitive manner
 *
 */

public class DocumentProcessor {

	private Document htmlDocument;

	public DocumentProcessor(Document htmlDocument) {
		this.htmlDocument = htmlDocument;
	}

/**
 * 	
 * @return - a Map of each word and its frequency in the document
 */
	
	public Map<String, Integer> getWordCount() {

		String bodyText = this.htmlDocument.body().text();
		System.out.println("DocumentProcessor is getting WordCount...");
		
		// initialize a Map to store words,frequencies
		Map<String, Integer> wordCount = new HashMap<String, Integer>();

		// split the retrieved text on whitespace to get words
		String[] words = bodyText.split("\\s+"); 

		for (String word : words) {			
			// processing will be case-insensitive
			word = word.toLowerCase(); 
			
			// Remove punctuation
			word = word.replaceAll("[^A-Za-z0-9]", ""); 
			
			if (word.matches("[a-zA-Z0-9]+")) // Consider it a word only if letters/numbers/alphanumeric												
			{
				if (wordCount.containsKey(word)) {
					wordCount.put(word, (wordCount.get(word) + 1));
				} else {
					wordCount.put(word, 1);
				}
			}
		}
		
		return wordCount;
	}

}
