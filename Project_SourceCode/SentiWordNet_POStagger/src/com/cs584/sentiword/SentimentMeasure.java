package com.cs584.sentiword;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SentimentMeasure {
	public void statisticsGenerator() throws Exception{
		
		ResourceBundle bundle = ResourceBundle.getBundle("com.cs584.sentiword.properties.ApplicationProperties");
		SentimentClassifier sc=new SentimentClassifier();
		
		Map<String,List<String>> positiveReviews=sc.classifyDataset(bundle.getString("pos_dir_path"));
		long numTP=positiveReviews.get(bundle.getString("pos_class")).size();
		long numFN=positiveReviews.get(bundle.getString("neg_class")).size();	
		Map<String,List<String>> negativeReviews=sc.classifyDataset(bundle.getString("neg_dir_path"));
		long numTN=negativeReviews.get(bundle.getString("neg_class")).size();
		long numFP=negativeReviews.get(bundle.getString("pos_class")).size();
		
		double f1Measure=calcF1(numTP,numTN,numFP,numFN);
		double accuracy=calcAccuracy(numTP,numTN,numFP,numFN);
		
		try{
		File resultFile=new File(bundle.getString("out_file"));
		BufferedWriter bw=new BufferedWriter(new FileWriter(resultFile));
		
		bw.write("Number of True Positives: "+numTP+"\n");		
		bw.write("Number of True Negatives: "+numTN+"\n");		
		bw.write("Number of False Positives: "+numFP+"\n");		
		bw.write("Number of False Negatives: "+numFN+"\n");		
		bw.write("Accuracy: "+(accuracy*100)+"%\n");
		bw.write("F1-Measure: "+(f1Measure*100)+"%\n");

		bw.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
			System.out.println("File write error in SentimentMeasure");
		}
	}

	private double calcAccuracy(long TP,long TN, long FP,long FN ) {
		return ((double)(TP+TN)/(TP+TN+FP+FN));
	}

	private double calcF1(long TP,long TN, long FP,long FN) {
		double precision=(double)(TP/(TP+FP));
		double recall=(double)((TP/(TP+FN)));
		return (double)(2*precision*recall)/(precision+recall);
	}

	public static void main(String[] args){
		SentimentMeasure me=new SentimentMeasure();
		try{
		me.statisticsGenerator();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error while calculating measure");
		}
	}
}
