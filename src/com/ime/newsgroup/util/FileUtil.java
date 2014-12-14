package com.ime.newsgroup.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author rayssak
 * @reason Class responsible for reading system properties.
 *
 */
public class FileUtil {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String LUCENE_INDEX_PATH;
	public static final String NEWSGROUP_PATH;
	public static final String HADOOP_PATH;
	public static final String ARFF_FILE;
	public static final String PROJECT_PATH;
	public static final String WEKA_TRAIN;
	public static final String WEKA_TEST;
	
	private static final Properties properties = new Properties();
	
	private NewsgroupPostTO postTO = new NewsgroupPostTO();
	
	static {
	
		InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream("config.properties");
		
		try {
			properties.load(inputStream);
		} catch (IOException ex) {
			Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
  
		LUCENE_INDEX_PATH = properties.getProperty("LUCENE_INDEX_PATH");
		NEWSGROUP_PATH = properties.getProperty("NEWSGROUP_PATH");
		HADOOP_PATH = properties.getProperty("HADOOP_PATH");
		ARFF_FILE = properties.getProperty("ARFF_FILE");
		PROJECT_PATH = properties.getProperty("PROJECT_PATH");
		WEKA_TRAIN = properties.getProperty("WEKA_TRAIN");
		WEKA_TEST = properties.getProperty("WEKA_TEST");
	      
	}
	
	public LinkedHashMap<String, String> getPostFiles() {
		
		LinkedHashMap<String, String> postFiles = new LinkedHashMap<String, String>();
		
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(FileSystems.getDefault().getPath(NEWSGROUP_PATH))) {
			
			for (Path path : ds) {
			
				if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
					DirectoryStream<Path> subDs = Files.newDirectoryStream(FileSystems.getDefault().getPath(path.toString()));
					for(Path subPath : subDs) {
						readPostFile(subPath);
						postFiles.put(postTO.getMessage(), postTO.getCategory());
					}
						
				}
				
			}

		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		}
		
		return postFiles;
		
	}
	
	private void readPostFile(Path path) throws IOException {
		
		String post = "";
		
		List<String> tmp = new LinkedList<String>();
		tmp = Files.readAllLines(path, Charset.defaultCharset());
		int postStart = tmp.indexOf("");
		
		for(int i=postStart+3; i<tmp.size(); i++) 
			post += tmp.get(i) + LINE_SEPARATOR;

		postTO.setCategory(path.toString().split("/")[path.toString().split("/").length-2]);
		postTO.setMessage(post.replaceAll("'", ""));
		
	}

}
