package myLuceneApp;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import txtparsing.TXTParsing;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collection;

public class MySearcher {

    public MySearcher() {
        try {
            String indexLocation = ("index");
            String queriesLocation = "docs/queries.txt";
            String field = "contents";
            Directory directory = FSDirectory.open(Paths.get(indexLocation));

            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(new BM25Similarity());
            
            search(indexSearcher, field, queriesLocation, 50);

            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search(IndexSearcher indexSearcher, String field, String filename, int k) {
        try{

            File gModel = new File("w2vmodel/model.zip");
            Word2Vec vec = WordVectorSerializer.readWord2VecModel(gModel, false);

            QueryParser parser = new QueryParser(field, new StandardAnalyzer());

            //Read queries from file
            StringBuilder resultString = new StringBuilder();

            String queries[] = TXTParsing.fileToString(filename).split("///");

            for(String q:queries){
                q = q.trim();

                int breakpoint = q.indexOf("\n");
                int qid = Integer.parseInt(q.substring(0, breakpoint).trim());


                Query query = parser.parse(q.substring(breakpoint).trim());

                System.out.println("Initial query: " + q.substring(breakpoint).trim());

                //Expanded query
                String finalQueryText ="";

                String queryTerms[] = query.toString("contents").split(" ");

                for (String s: queryTerms){

                    Collection<String> wordsNearest = vec.wordsNearest(s, 2);
                    finalQueryText += s + " ";
                    for (String wn: wordsNearest){
                        double similarity = vec.similarity(s, wn);
                        if(similarity > 0.1)
                            finalQueryText += wn + " ";
                    }

                }

                query = parser.parse(finalQueryText.trim());

                System.out.println("Processed query: " + finalQueryText);



                TopDocs results = indexSearcher.search(query, k);
                ScoreDoc[] hits = results.scoreDocs;
                //long numTotalHits = results.totalHits;

                for(int i=0; i<hits.length; i++){
                    Document hitDoc = indexSearcher.doc(hits[i].doc);
                    resultString.append("Q").append((qid > 9) ? qid : "0" + qid).append(" Q0\t").append(hitDoc.get("docID")).append("\t0\t").append(hits[i].score).append("\tmyIRmethod\n");

                }
            }

            FileWriter results_txt = new FileWriter("docs/results" +k+".txt");
            results_txt.write(resultString.toString());
            results_txt.close();


        } catch(Exception e){
            e.printStackTrace();
        }
    }





    public static void main(String[] args){
        myLuceneApp.MySearcher searcherDemo = new myLuceneApp.MySearcher();

    }
}
