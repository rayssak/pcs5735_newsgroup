package com.ime.newsgroup.index;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import com.ime.newsgroup.util.FileUtil;
import com.ime.newsgroup.util.NewsgroupPostTO;

public class LuceneIndex {
	
	private String LUCENE_INDEX_PATH = FileUtil.LUCENE_INDEX_PATH;
	
	public void insertNewsgroupPosts(NewsgroupPostTO post) {
		
		IndexWriter indexWriter = null;
		File dir = new File(FileUtil.LUCENE_INDEX_PATH + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		if (!dir.exists())
			dir.mkdir();
		
		String directory = dir.toString();
		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_35); 

		try {
			
			Document doc = new Document();
			Directory indexDirectory = new SimpleFSDirectory(new File(directory));
			indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(Version.LUCENE_35, analyzer));
			
			post.setMessage(tokenizePost(analyzer, "post", post.getMessage()));
			
			doc.add(new Field("post", post.getMessage(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
			doc.add(new Field("category", post.getCategory(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
			indexWriter.addDocument(doc);
			
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		} finally {
			closeWriter(indexWriter);
		}

	}
	
	public static String tokenizePost(Analyzer analyzer, String field, String keywords) throws IOException {

		String result = "";
		TokenStream stream  = analyzer.tokenStream(field, new StringReader(keywords));

		while(stream.incrementToken())
			if(!stream.getAttribute(TermAttribute.class).term().matches("[0-9]+"))
				result += stream.getAttribute(TermAttribute.class).term() + " ";

		return result;
		
	} 
	
	public Set<Document> searchAllDocuments() {

		IndexReader reader;
		Set<Document> docs = new TreeSet<Document>(new Comparator<Document>() {
			@Override
			public int compare(Document o1, Document o2) {
				return o1.get("post").compareTo(o2.get("post"));
			}
		});

		try {

			reader = IndexReader.open(FSDirectory.open(new File(LUCENE_INDEX_PATH + "11-12-2014")));
			IndexSearcher searcher = new IndexSearcher(reader);
			Query query = new MatchAllDocsQuery();

			TopDocs topDocs = searcher.search(query, reader.maxDoc());
			for (ScoreDoc scoredoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoredoc.doc);
				docs.add(doc);
			}

		} catch (CorruptIndexException corruptIndexException) {
			System.out.println(corruptIndexException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		}

		return docs;
	}
	
	public Set<Document> searchIntoIndexBase() {

		Set<Document> docs = null;
		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_35);
		QueryParser queryParser = new QueryParser(Version.LUCENE_35, "post", analyzer);
		
		try {
			
			Query q = queryParser.parse("a*");
//			Directory directory = new SimpleFSDirectory(new File(LUCENE_INDEX_PATH));
			Directory directory = new SimpleFSDirectory(new File(LUCENE_INDEX_PATH + "08-12-2014"));
			IndexSearcher indexSearcher = new IndexSearcher(IndexReader.open(directory));
			TopDocs topDocs = indexSearcher.search(q, 100000);

			ScoreDoc[] scoredocs = topDocs.scoreDocs;

			docs = new TreeSet<Document>(new Comparator<Document>() {
				@Override
				public int compare(Document o1, Document o2) {
					return o1.get("post").compareTo(o2.get("post"));
				}
			});

			for (ScoreDoc d : scoredocs) {
				Document document = indexSearcher.doc(d.doc);
				docs.add(document);
			}
			
		} catch (ParseException parseException) {
			System.out.println(parseException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		}
		
		return docs;

	}

	private void closeWriter(IndexWriter indexWriter) {
		try {
			indexWriter.close();
		} catch (CorruptIndexException corruptIndexException) {
			System.out.println(corruptIndexException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		}
	}

}