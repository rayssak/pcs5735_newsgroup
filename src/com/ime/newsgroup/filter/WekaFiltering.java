package com.ime.newsgroup.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import com.ime.newsgroup.util.FileUtil;

/**
 * 
 * @author rayssak
 * @reason Filters the generated .arff Weka file using the StringToWordVector 
 * 		   (this is required to use as input of the Machine Learning algorithms).
 * 
 * 		   Obs.: if this filtering is done by the Weka interface, it will not be 
 * 		   possible to use both the train and the test files in any algorithm 
 * 		   since the interface will generate vectorized files with different 
 * 		   attributes, making them incompatible. 
 * 
 *         Please use this class for this case!
 *
 */
public class WekaFiltering {
	
	public static void main(String[] args) {
		
		PrintWriter outTrain = null;
		PrintWriter outTest = null;

		try {
			
			outTrain = new PrintWriter(new File("/Users/rayssak/Documents/rayssak/workspace/git/pcs5735_newsgroup/src/20_newsgroups-vector.arff"));
			outTest = new PrintWriter(new File("/Users/rayssak/Documents/rayssak/workspace/git/pcs5735_newsgroup/src/mini_newsgroups-vector.arff"));
			
			BufferedReader readTrain = new BufferedReader(new FileReader(FileUtil.WEKA_TRAIN));
			BufferedReader readTest = new BufferedReader(new FileReader(FileUtil.WEKA_TEST));
			Instances train = new Instances(readTrain);
			Instances test = new Instances(readTest);
			StringToWordVector filter = new StringToWordVector();
			
			// Initializing the filter once with training set..
			filter.setInputFormat(train);
			// Configures the Filter based on train instances and returns filtered instances...
			Instances newTrain = Filter.useFilter(train, filter);
			// Create new test set
			Instances newTest = Filter.useFilter(test, filter);

			outTrain.println(newTrain);
			outTest.println(newTest);
			
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println(fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			outTrain.close();
			outTest.close();
		}
		

	}

}