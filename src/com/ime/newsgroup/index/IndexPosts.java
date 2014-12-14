package com.ime.newsgroup.index;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.document.Document;

import com.ime.newsgroup.util.FileUtil;
import com.ime.newsgroup.util.NewsgroupPostTO;

/**
 * 
 * @author rayssak
 * @reason Insert the post messages (only the message without header) into the
 * 		   Lucene index. The message is already inserted pre-processed 
 * 		   (normalized, tokenized, without stop words, stemming).
 *
 */
public class IndexPosts {
	
	private static LuceneIndex index = new LuceneIndex();
	
	public static void main(String[] args) {
		
		LinkedHashMap<String, String> postFiles = new LinkedHashMap<String, String>();
		
		FileUtil file = new FileUtil();
		postFiles = file.getPostFiles();
	
		for (Entry<String, String> entry : postFiles.entrySet())
			setPost(entry.getKey(), entry.getValue());
		
	}

	private static void setPost(String postText, String category) {
		
		NewsgroupPostTO post = new NewsgroupPostTO(postText, category);
		
		// Indexing post...
		index.insertNewsgroupPosts(post);
		
	}
	
	private static void getIndexedPosts() {
		Set<Document> luceneDocs = new HashSet<Document>();
		luceneDocs = index.searchIntoIndexBase();
	}

}