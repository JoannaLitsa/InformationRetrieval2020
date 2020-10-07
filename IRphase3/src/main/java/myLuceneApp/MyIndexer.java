package myLuceneApp;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;

import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import txtparsing.MyDoc;
import txtparsing.TXTParsing;
import w2v.FieldValuesSentenceIterator;
import w2v.LuceneTokenizerFactory;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyIndexer {

    public MyIndexer() throws Exception {
        String filename = "docs/documents.txt";
        String indexLocation = ("index");
        System.out.println("Indexing to directory '" + indexLocation + "'...");

        Directory directory = FSDirectory.open(Paths.get(indexLocation));
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();

        perFieldAnalyzers.put("docID", new KeywordAnalyzer());
        perFieldAnalyzers.put("title", new WhitespaceAnalyzer());
        perFieldAnalyzers.put("contents", new EnglishAnalyzer());

        Analyzer analyzer = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), perFieldAnalyzers);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);

        //Add documents
        List<MyDoc> docs = TXTParsing.parse(filename);
        for (MyDoc doc : docs) {
            indexDoc(writer, doc);
        }

        writer.commit();
        writer.close();

        trainNN();


    }

    private void indexDoc(IndexWriter indexWriter, MyDoc mydoc) {

        try {

            Document doc = new Document();

            StoredField docID = new StoredField("docID", mydoc.getId());
            doc.add(docID);
            StoredField title = new StoredField("title", mydoc.getTitle());
            doc.add(title);

            String searchableText = mydoc.getTitle() + " " + mydoc.getBody();
            TextField contents = new TextField("contents", searchableText, Field.Store.YES);
            doc.add(contents);

            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                indexWriter.addDocument(doc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trainNN(){

        try{
            Path path = Paths.get("index");
            Directory directory = FSDirectory.open(path);
            IndexReader reader = DirectoryReader.open(directory);

            SentenceIterator iter = new FieldValuesSentenceIterator(reader, "contents");
            Word2Vec vec = new Word2Vec.Builder()
                    .layerSize(110)
                    .windowSize(8)
                    .epochs(5)
                    .elementsLearningAlgorithm(new CBOW<>())
                    .tokenizerFactory(new LuceneTokenizerFactory(new StandardAnalyzer()))
                    .iterate(iter)
                    .build();
            vec.fit();

            WordVectorSerializer.writeWord2VecModel(vec, "w2vmodel/model.zip");

        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws Exception {
        MyIndexer myIndexer = new MyIndexer();
    }

}
