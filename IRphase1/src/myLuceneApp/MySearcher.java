package myLuceneApp;


import java.io.FileWriter;
import java.nio.file.Paths;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import txtparsing.TXTParsing;

public class MySearcher {

    public MySearcher() {
        try {
            String indexLocation = ("index");
            String queriesLocation = "docs//queries.txt";
            String field = "contents";

            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(new BM25Similarity());

            //Execute a separate search for each k = 20, 30, 50
            search(indexSearcher, field, queriesLocation, 20);
            search(indexSearcher, field, queriesLocation, 30);
            search(indexSearcher, field, queriesLocation, 50);

            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search(IndexSearcher indexSearcher, String field, String filename, int k) {
        try{
            Analyzer analyzer = new EnglishAnalyzer();

            QueryParser parser = new QueryParser(field, analyzer);

            //Read queries from file
            String resultString = "";

            String queries[] = TXTParsing.fileToString(filename).split("///");

            for(String q:queries){
                q = q.trim();

                int breakpoint = q.indexOf("\n");
                int qid = Integer.parseInt(q.substring(0, breakpoint).trim());
                Query query = parser.parse(q.substring(breakpoint).trim());

                System.out.println("Searching for: " + query.toString(field));

                TopDocs results = indexSearcher.search(query, k);
                ScoreDoc[] hits = results.scoreDocs;
                //long numTotalHits = results.totalHits;

                for(int i=0; i<hits.length; i++){
                    Document hitDoc = indexSearcher.doc(hits[i].doc);
                    resultString = resultString + "Q" + ((qid>9)? qid : "0"+qid) + " Q0\t"+hitDoc.get("docID") +"\t0\t" + hits[i].score + "\tmyIRmethod\n";

                }
            }

            FileWriter results_txt = new FileWriter("docs//results" +k+".txt");
            results_txt.write(resultString);
            results_txt.close();


        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
