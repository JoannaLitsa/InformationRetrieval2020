package myLuceneApp;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
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
            
            search(indexSearcher, field, queriesLocation, 50);

            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search(IndexSearcher indexSearcher, String field, String filename, int k) {
        try{

            CustomAnalyzer analyzer = customAnalyzerForQueryExpansion();

            QueryParser parser = new QueryParser(field, analyzer);

            //Read queries from file
            StringBuilder resultString = new StringBuilder();

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
                    resultString.append("Q").append((qid > 9) ? qid : "0" + qid).append(" Q0\t").append(hitDoc.get("docID")).append("\t0\t").append(hits[i].score).append("\tmyIRmethod\n");

                }
            }

            FileWriter results_txt = new FileWriter("docs//results" +k+".txt");
            results_txt.write(resultString.toString());
            results_txt.close();


        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static CustomAnalyzer customAnalyzerForQueryExpansion() throws IOException {
        //Read synonyms from wn_s.pl file
        Map<String, String> sffargs = new HashMap<>();
        sffargs.put("synonyms", "wn_s_noverbs.pl");
        sffargs.put("format", "wordnet");

        CustomAnalyzer.Builder builder = CustomAnalyzer.builder()
                .withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(EnglishPossessiveFilterFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(StopFilterFactory.class)
                .addTokenFilter(PorterStemFilterFactory.class)
                .addTokenFilter(SynonymGraphFilterFactory.class, sffargs);
        CustomAnalyzer analyzer = builder.build();

        return analyzer;
    }


    public static void main(String[] args){
        MySearcher searcherDemo = new MySearcher();
    }
}
