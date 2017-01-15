package com.cs584.sentiword;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SentimentMeasure {
	public void generateStatistics() throws IOException{
		SentimentClassifier sc=new SentimentClassifier();
		ResourceBundle bundle = ResourceBundle.getBundle("com.cs584.sentiword.properties.ApplicationProperties");
		
		Map<String,List<String>> positiveReview=sc.classifyDataset(bundle.getString("pos_dir_path"));
		long numTP=positiveReview.get(bundle.getString("pos_class")).size();
		long numFN=positiveReview.get(bundle.getString("neg_class")).size();
		Map<String,List<String>> negativeReview=sc.classifyDataset(bundle.getString("neg_dir_path"));
		long numTN=negativeReview.get(bundle.getString("neg_class")).size();
		long numFP=negativeReview.get(bundle.getString("pos_class")).size();
		
		
		double f1Measure=calcF1(numTP,numTN,numFP,numFN);
		double accuracy=calcAccuracy(numTP,numTN,numFP,numFN);
		try{
		File resultFile=new File(bundle.getString("out_file"));
		BufferedWriter bw=new BufferedWriter(new FileWriter(resultFile));
		bw.write("Number of True Positives: "+numTP+"\n");
//		bufferedWriter.write(((Long)numTP).toString()+"\n");
		
		bw.write("Number of True Negatives: "+numTN+"\n");		
		bw.write("Number of False Positives: "+numFP+"\n");
		bw.write("Number of False Negatives: "+numFN+"\n");
		bw.write("Accuracy: "+(accuracy*100)+"\n");
		System.out.println((accuracy*100));
		bw.write("F1-Measure: "+(f1Measure*100)+"\n");
		System.out.println((f1Measure*100));
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
		double precision= (double)TP/(TP+FP);
		double recall= (double)TP/(TP+FN);
		return (double)(2*precision*recall)/(precision+recall);
	}

	public static void main(String[] args){
		SentimentMeasure me=new SentimentMeasure();
		try{
		me.generateStatistics();
		}
		catch(IOException ex){
			ex.printStackTrace();
			System.out.println("Error while calculating measure");
		}
	}
}