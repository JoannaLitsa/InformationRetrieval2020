import myLuceneApp.MyIndexer;
import myLuceneApp.MySearcher;
import txtparsing.TXTParsing;

public class phase2 {

    public static void main(String [] args) {

        try {
            //TXTParsing.editWN("src/wn_s.pl");
            MyIndexer indexer = new MyIndexer();
            MySearcher searcher = new MySearcher();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
