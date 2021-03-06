package com.cs584.sentiword.util;

import java.io.*;
import java.util.*;

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
			System.out.println("Error reading file: " + fileName);
		}
		return text;
	}

	public static void writeData(String fileName, String data) {
		try {
			File statFile = new File(fileName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(statFile));
			bw.write(data);
			bw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error writing to file: " + fileName);
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

	public static List<String> stringTokenizer(String str, String regEx) {
		List<String> tokenList = new ArrayList<String>();
		String[] lineTokens = str.split(regEx);
		for (String token : lineTokens) {
			tokenList.add(token);
		}
		return tokenList;
	}

	public static Map<String, Double> getAdjectiveMap(Map<String, Double> sentiwordnetDictionary) {
		Map<String, Double> adjWords = null;
		adjWords = new HashMap<String, Double>();
		for (String key : sentiwordnetDictionary.keySet()) {
			if (key.contains("#a")) {
				String adjkey = key.split("#a")[0].trim();
				adjWords.put(adjkey, sentiwordnetDictionary.get(key));
			}
		}
		return adjWords;
	}

	// Removes irrelevant tokens and whitespaces
	public static List<String> listSanitizer(List<String> tokenList) {
		List<String> list = new ArrayList<String>();
		for (String token : tokenList) {
			if (!token.trim().equals(""))
				list.add(token.trim());
		}
		return list;
	}
}
