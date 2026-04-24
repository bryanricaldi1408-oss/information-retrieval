import indexer.Indexer;
import utils.FileReaderUtil;
import model.Document;
import preprocessing.PorterStemmer;
import preprocessing.TokenizerAndStemmer;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
        String folderPath = "../dataset/Cranfield";

        FileReaderUtil fr = new FileReaderUtil();
        List<Document> docs = fr.readAllDocuments(folderPath);

        TokenizerAndStemmer ts = new TokenizerAndStemmer();

        Map<Integer, String[]> tokenizedDoc = ts.tokenization(docs);
        Map<Integer, List<String>> stemmedDoc = ts.stemming(tokenizedDoc);

        
        Indexer in = new Indexer();
        Map<String, List<Integer>> invertedIndex = in.buildInvertedIndex(stemmedDoc);

        invertedIndex.forEach((key, value) -> { 

            System.out.printf("[%s] -> {%s}\n", key, value.toString());
        });

    }
}