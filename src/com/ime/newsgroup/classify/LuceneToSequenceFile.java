package com.ime.newsgroup.classify;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;

import com.ime.newsgroup.index.LuceneIndex;
import com.ime.newsgroup.util.FileUtil;

public class LuceneToSequenceFile {
	
	public static void main(String... args) throws IOException {

		String uri = FileUtil.HADOOP_PATH;
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		Path path = new Path(uri);
		IntWritable intKey = new IntWritable();
		Text textValue = new Text();
		SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path,
				intKey.getClass(), textValue.getClass());

		LuceneIndex newsgroup = new LuceneIndex();

		try {
			Set<Document> docs;
			docs = newsgroup.searchIntoIndexBase();
			int i = 0;
			for (Document doc : docs) {
				String category = doc.get("category");
				String post = doc.get("post");
				intKey.set(i);
				textValue.set(constructValue(category, post));

				writer.append(intKey, textValue);
				i++;
			}

			writer.close();

		} catch (CorruptIndexException corruptIndexException) {
			System.out.println(corruptIndexException.getMessage());
		} catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		} finally {
			IOUtils.closeStream(writer);
		}

		SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
		while (reader.next(intKey, textValue)) {
			System.out.println(intKey.toString() + " " + textValue);
		}

		reader.close();

	}
	
	private static String constructValue(String category, String post) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		if (StringUtils.isNotEmpty(category)) 
			buf.append(category).append(": ");
		if (StringUtils.isNotEmpty(post)) 
			buf.append(post);
		
		return buf.toString();
		
	}

}
