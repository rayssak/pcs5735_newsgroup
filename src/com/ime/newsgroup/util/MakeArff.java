package com.ime.newsgroup.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.lucene.document.Document;

import com.ime.newsgroup.index.LuceneIndex;

public class MakeArff {
	
	public static void main(String[] args) {
		
		PrintWriter out = null;
		FileUtil fileUtil = new FileUtil();
		LuceneIndex index = new LuceneIndex();
		Set<Document> posts = index.searchAllDocuments();

		try {
			
			out = new PrintWriter(fileUtil.PROJECT_PATH + fileUtil.ARFF_FILE);
			writeArffHeader(out);
			
			for(Document doc : posts)
				out.println("'" + doc.getField("post").toString().replace("stored,indexed,tokenized,termVector<post:", "").replace(">", "") + 
							"'," + doc.getField("category").toString().replace("stored,indexed,tokenized,termVector<category:", "").replace(">", ""));
			
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println(fileNotFoundException.getMessage());
		} finally {
			out.close();
		}
		
	}

	private static void writeArffHeader(PrintWriter out) {
		out.println("@relation newsgroup" + FileUtil.LINE_SEPARATOR);
		out.println("@attribute post string");
		out.println("@attribute category {alt.atheism,comp.graphics,comp.os.ms-windows.misc,comp.sys.ibm.pc.hardware,"
				  + "comp.sys.mac.hardware,comp.windows.x,misc.forsale,rec.autos,rec.motorcycles,rec.sport.baseball,"
				  + "rec.sport.hockey,sci.crypt,sci.electronics,sci.med,sci.space,soc.religion.christian,talk.politics.guns,"
				  + "talk.politics.mideast,talk.politics.misc,talk.religion.misc}" + FileUtil.LINE_SEPARATOR);
		out.println("@data");
	}

}