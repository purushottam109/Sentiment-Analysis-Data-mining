package com.cs584.sentiword;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.cs584.sentiword.util.UtilClasses;

public class SentimentClassifier {
	private Map<String,Double> sentiWordDictionary;	
	ResourceBundle bundle = ResourceBundle.getBundle("com.cs584.sentiword.properties.ApplicationProperties");
	
	public SentimentClassifier() throws IOException{
			//initialize sentiword dictionary
			SentiWordNetDemoCode swndc=new SentiWordNetDemoCode(bundle.getString("sentiword_dictionary_path"));
			sentiWordDictionary=swndc.getDictionary();
	}
	
	/*
	 * Classifies all the dataset files in pos folder as positive and those in neg folder as negative
	 */
	public Map<String,List<String>> classifyDataset(String directoryPath) throws IOException{
		
		String classification;
		String filePath;		
		List<String> posFiles=new ArrayList<String>();
		Map<String,List<String>> classMap=new HashMap<String,List<String>>();
		List<String> negFiles=new ArrayList<String>();
		
		classMap.put(bundle.getString("pos_class"),posFiles);
		classMap.put(bundle.getString("neg_class"),negFiles);
		
		File folder = new File(directoryPath);
		File[] fileList = folder.listFiles();
		
		for(int i=0; i<fileList.length; i++){
			if (fileList[i].isFile()) {
		        filePath=directoryPath+"\\"+fileList[i].getName();
		        classification=getClassification(filePath);
		        classMap.get(classification).add(fileList[i].getName());
		      }
		}
		return classMap;
	}	
		
	public String getClassification(String filePath) throws IOException {
		List<String> tokenList=UtilClasses.tokenize(filePath, bundle.getString("delimeters"));
		tokenList=UtilClasses.sanitizeList(tokenList);
		String ngram = bundle.getString("ngram_mode");
//		System.out.println("Ngram value is "+ngram);
		
		//when ngram=tri we add both bigram and trigrams in list
		if(ngram.equals("uni-tri")){
			List<String> bigrams=addBigrams(tokenList);
//			for(int i=0;i<5;i++){
//				System.out.println(bigrams.get(i));
//			}
			List<String> trigrams=addTrigrams(tokenList);
			for(String token: bigrams){
				tokenList.add(token);
			}
			for(String token: trigrams){
				tokenList.add(token);
			}
			List<String> unigrams=addTrigrams(tokenList);
			for(String token: unigrams){
				tokenList.add(token);
			}
		}else if(ngram.equals("tri")){
			List<String> trigrams=addTrigrams(tokenList);
			for(String token: trigrams){
				tokenList.add(token);
			}
		}else if(ngram.equals("uni")){
			List<String> unigrams=addTrigrams(tokenList);
			for(String token: unigrams){
				tokenList.add(token);
			}
		}
		
		Double score = getSentimentScore(tokenList);
		/*
		 * Since sentiword is biased towards positive words courtesy number of FPs we are classifying
		 * score of zero as negative to balance out final result
		*/
		Double thVal = Double.parseDouble(bundle.getString("threshold"));
		if(score<=thVal){
			return bundle.getString("neg_class");
		}
		else
			return bundle.getString("pos_class");
	}

	private List<String> addTrigrams(List<String> tokenList) {
		int len=tokenList.size();
		List<String> triGramsList=new ArrayList<String>();
		for(int i=0;i<len-2;i++){
			String trigram=""+tokenList.get(i)+"_"+tokenList.get(i+1)+"_"+tokenList.get(i+2);
			triGramsList.add(trigram);
		}
		return triGramsList;
	}

	private List<String> addBigrams(List<String> tokenList) {
		int len=tokenList.size();
		List<String> biGramsList=new ArrayList<String>();
		for(int i=0;i<len-1;i++){
			String bigram=""+tokenList.get(i)+"_"+tokenList.get(i+1);
//			System.out.println("Bigram value is "+bigram);
			biGramsList.add(bigram);
		}
		return biGramsList;
	}
	
	private List<String> addUnigrams(List<String> tokenList) {
		int len=tokenList.size();
		List<String> uniGramsList=new ArrayList<String>();
		for(int i=0;i<len;i++){
			String unigram=""+tokenList.get(i);
			uniGramsList.add(unigram);
		}
		return uniGramsList;
	}
	
	private Double getSentimentScore(List<String> tokenList) {
		Double score=0D;

		for(String token:tokenList){
			double tokenScore=0D;
			int count=0;
			if(sentiWordDictionary.get(token+"#a")!=null){
				tokenScore+=sentiWordDictionary.get(token+"#a");
				count++;
			}
			String posMode = bundle.getString("pos_mode");
			if(posMode.equals("all")){				
				if(sentiWordDictionary.get(token+"#n")!=null){
					tokenScore+=sentiWordDictionary.get(token+"#n");
					count++;
				}
				if(sentiWordDictionary.get(token+"#r")!=null){
					tokenScore+=sentiWordDictionary.get(token+"#r");
					count++;
				}
				if(sentiWordDictionary.get(token+"#v")!=null){
					tokenScore+=sentiWordDictionary.get(token+"#v");
					count++;
				}
			}
			
			//Take the average of a word's sentiment score across all POS 
			if(count!=0)
				score+=tokenScore/count;

		}
		return score;
	}
	
	
 
}