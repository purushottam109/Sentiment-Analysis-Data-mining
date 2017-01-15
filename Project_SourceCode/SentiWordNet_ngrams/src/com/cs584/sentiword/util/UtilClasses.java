package com.cs584.sentiword.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilClasses {
	
	public static String getText(String fileName) {
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				text = text + " " + line;
			}
			br.close();

		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Eror reading file: " + fileName);
		}
		return text;
	}

	public static void writeData(String fileName, String data) {
		try {
			File statistics = new File(fileName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(statistics));
			bw.write(data);
			bw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error writing to file " + fileName);
		}
	}
	
	public static List<String> tokenize(String filePath, String regEx) {
		List<String> tokenList = new ArrayList<String>();
		String text = getText(filePath);

		String[] lineTokens = text.split(regEx);
		for (String token : lineTokens) {
			tokenList.add(token);
		}

		return tokenList;

	}

	public static List<String> tokenizeString(String str, String regEx) {
		List<String> tokenList = new ArrayList<String>();
		String[] lineTokens = str.split(regEx);
		for (String token : lineTokens) {
			tokenList.add(token);
		}
		return tokenList;
	}

	public static Map<String, Double> getAdjectiveMap(Map<String, Double> sentiwordDictonary) {
		Map<String, Double> adjWords = null;
		adjWords = new HashMap<String, Double>();

		for (String key : sentiwordDictonary.keySet()) {
			if (key.contains("#a")) {
				String adjKey = key.split("#a")[0].trim();
				adjWords.put(adjKey, sentiwordDictonary.get(key));
			}
		}
		return adjWords;
	}

	//Removes irrelevant tokens and whitespaces
	
	public static List<String> sanitizeList(List<String> tokenList) {
		List<String> sanitizedList = new ArrayList<String>();
		for (String token : tokenList) {
			if (!token.trim().equals(""))
				sanitizedList.add(token.trim());
		}
		return sanitizedList;
	}	
}
