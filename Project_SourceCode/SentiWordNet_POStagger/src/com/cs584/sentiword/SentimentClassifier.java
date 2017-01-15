package com.cs584.sentiword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.cs584.sentiword.util.UtilClasses;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class SentimentClassifier {
	private Map<String,Double> sentiWordDictionary;
	MaxentTagger tagger;
	ResourceBundle bundle = ResourceBundle.getBundle("com.cs584.sentiword.properties.ApplicationProperties");
	
	public SentimentClassifier() throws Exception{
		
			//initialize sentiword dictionary
//			System.out.println(bundle.getString("sentiword_dictionary_path"));
			SentiWordNetDemoCode swndc=new SentiWordNetDemoCode(bundle.getString("sentiword_dictionary_path"));
			sentiWordDictionary=swndc.getDictionary();
			
			//create postagger object
			tagger = new MaxentTagger(bundle.getString("tagger_path"));
		
	}
	
	//Classifies all the dataset files in pos folder as positive and those in neg folder as negative	 
	public Map<String,List<String>> classifyDataset(String directoryPath) throws IOException{
		String filePath;
		String classification;
		Map<String,List<String>> classMap=new HashMap<String,List<String>>();
		List<String> posFiles=new ArrayList<String>();
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
		new ArrayList<String>();
		String taggedText="";
		try{	
			// Feed the text to tagger which tokenizes the files and tags each word with its POS
		   
			List<List<HasWord>> sentences = tagger.tokenizeText(new BufferedReader(new FileReader(filePath)));
			for (List<HasWord> sentence : sentences) {
				ArrayList<TaggedWord> taggedSentence = (ArrayList<TaggedWord>) tagger.tagSentence(sentence);
				taggedText = taggedText+" "+Sentence.listToString(taggedSentence, false);
			}
			
			taggedText = taggedText.replace("/", "_");
			UtilClasses.writeData(bundle.getString("tagged_file"),taggedText);
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error tagging file at "+filePath);
		}
		
		List<String> tokenList=UtilClasses.tokenize(bundle.getString("tagged_file"), bundle.getString("delimeters"));
		tokenList=UtilClasses.listSanitizer(tokenList);

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
	
	private String getPOS(String token) {
		if(token.contains("_JJ") || token.contains("_JJR")||token.contains("_JJS")){
			return "adj";
		}
		if(token.contains("_NN") || token.contains("_NNP")||token.contains("_NNPS")||token.contains("_NNS")){
			return "noun";
		}
		if(token.contains("_RB") || token.contains("_RBR")||token.contains("_RBS")||token.contains("_WRB")){
			return "adv";
		}
		if(token.contains("_VB") || token.contains("_VBD")||token.contains("_VBG")||token.contains("_VBN")||token.contains("_VBP")||token.contains("_VBZ")){
			return "verb";
		}
		return "none";
	}
	
	private Double getSentimentScore(List<String> tokenList) {
		Double score=0D;
		new HashMap<String,String>();
		for(String token : tokenList){
			String newToken;
			//Get the POS for the supplied token
			String posVal=getPOS(token);
			if(token.indexOf("_")!=-1 && token.indexOf("_")!=0){
				newToken=token.substring(0,token.indexOf("_"));
				newToken=newToken.trim();

				switch(posVal)
				{
				case "adj":
					if(sentiWordDictionary.get(newToken+"#a")!=null){
						score+=sentiWordDictionary.get(newToken+"#a");
					}break;
				case "noun":
					if(sentiWordDictionary.get(newToken+"#n")!=null){
						score+=sentiWordDictionary.get(newToken+"#n");
					}break;
				case "adv":
					if(sentiWordDictionary.get(newToken+"#r")!=null){
						score+=sentiWordDictionary.get(newToken+"#r");
					}break;
				case "verb":
					if(sentiWordDictionary.get(newToken+"#v")!=null){
						score+=sentiWordDictionary.get(newToken+"#v");
					}break;
				default:
					continue;
				}
			}
			else
				continue;
		}
		return score;
	}

//	public static void main(String[] args){
//
//		String fileName="E:\\CS584 project\\data\\poscv000_29590.txt";
//		try{
//			SentimentClassifier sc=new SentimentClassifier();
//			System.out.println("Sentiment is :"+sc.getClassification(fileName));
//		}
//		catch(Exception ex){
//			ex.printStackTrace();
//			System.out.println("Error classifying file: "+fileName);
//		}
//		
//	}
 
}
