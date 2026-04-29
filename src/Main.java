import indexer.Indexer;
import utils.FileReaderUtil;
import model.Document;
import preprocessing.PorterStemmer;
import preprocessing.TokenizerAndStemmer;
import query.BooleanQueryEngine;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        String folderPath = "information-retrieval/dataset/Cranfield";

        FileReaderUtil fr = new FileReaderUtil();
        List<Document> docs = fr.readAllDocuments(folderPath);

        TokenizerAndStemmer ts = new TokenizerAndStemmer();

        Map<Integer, String[]> tokenizedDoc = ts.tokenization(docs);
        Map<Integer, List<String>> stemmedDoc = ts.stemming(tokenizedDoc);

        Indexer in = new Indexer();
        Map<String, List<Integer>> invertedIndex = in.buildInvertedIndex(stemmedDoc);
        /*
        invertedIndex.forEach((key, value) -> {

            System.out.printf("[%s] -> {%s}\n", key, value.toString());
        });
        */

        Scanner sc = new Scanner(System.in);

        int totalDocs = docs.size();
        BooleanQueryEngine engine = new BooleanQueryEngine(invertedIndex, totalDocs);

        //ini buat debug
        //System.out.println("Jumlah dokumen: " + docs.size());

        while (true) {
            System.out.print("Query: ");
            String input = sc.nextLine().trim();
            if ("exit".equalsIgnoreCase(input)) break;
            try {
                List<Integer> result = engine.search(input);
                System.out.println("Hasil: " + (result.isEmpty() ? "(tidak ada hasil)" : result));
            } catch (IllegalArgumentException e) {
                System.out.println("Query tidak valid: " + e.getMessage());
            }
        }
        sc.close();
    }
}