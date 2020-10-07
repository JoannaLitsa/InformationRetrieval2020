import myLuceneApp.MyIndexer;
import myLuceneApp.MySearcher;

public class Phase3 {

    public static void main(String [] args) {

        try {

            MyIndexer indexer = new MyIndexer();
            MySearcher searcher = new MySearcher();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}