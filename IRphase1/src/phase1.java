import myLuceneApp.MyIndexer;
import myLuceneApp.MyReader;
import myLuceneApp.MySearcher;

public class phase1 {

    public static void main(String [] args) {
        try {
            MyIndexer indexer = new MyIndexer();
            MyReader reader = new MyReader();
            MySearcher searcher = new MySearcher();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
