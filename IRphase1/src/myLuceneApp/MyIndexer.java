package myLuceneApp;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import txtparsing.MyDoc;
import txtparsing.TXTParsing;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class MyIndexer {

    public MyIndexer() throws Exception {

        String filename = "docs//documents.txt";
        String indexLocation = ("index");

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexLocation + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexLocation));

            Analyzer analyzer = new EnglishAnalyzer();
            Similarity similarity = new BM25Similarity();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            iwc.setOpenMode(OpenMode.CREATE);

            IndexWriter indexWriter = new IndexWriter(dir, iwc);

            List<MyDoc> docs = TXTParsing.parse(filename);
            for (MyDoc doc : docs) {
                indexDoc(indexWriter, doc);
            }

            indexWriter.close();

            Date end = new Date();

            System.out.println((end.getTime() - start.getTime()) + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }

    private void indexDoc(IndexWriter indexWriter, MyDoc mydoc) {

        try {

            Document doc = new Document();

            StoredField docID = new StoredField("docID", mydoc.getId());
            doc.add(docID);
            StoredField title = new StoredField("title", mydoc.getTitle());
            doc.add(title);
            //StoredField body = new StoredField("body", mydoc.getBody());
            //doc.add(body);

            String searchableText = mydoc.getTitle() + " " + mydoc.getBody();
            TextField contents = new TextField("contents", searchableText, Field.Store.NO);
            doc.add(contents);

            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                //System.out.println("adding" + mydoc);
                indexWriter.addDocument(doc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
