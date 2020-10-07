package myLuceneApp;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class MyReader {

    public MyReader() {
        try {

            String indexLocation = ("index");

            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));

            //printIndexDocuments(indexReader);

            indexReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void printIndexDocuments(IndexReader indexReader) {
        try {
            System.out.println("--------------------------");
            System.out.println("Documents in the index...");

            for (int i=0; i<indexReader.maxDoc(); i++) {
                Document doc = indexReader.document(i);
                System.out.println("\tdocID="+doc.getField("docID")+"\ttitle:"+doc.get("title")+"\tbody:"+doc.get("body"));
            }
        } catch (CorruptIndexException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
