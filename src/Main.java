import indexer.Indexer;
import utils.FileReaderUtil;
import model.Document;
import preprocessing.TokenizerAndStemmer;
import query.BooleanQueryEngine;

import java.util.*;

/**
 * Class ini mengatur pembacaan dokumen, pra-pemrosesan (tokenisasi dan
 * stemming),
 * pembuatan inverted index, dan eksekusi mesin pencari (query engine).
 */
public class Main {
    /**
     * Method utama untuk menjalankan alur program Information Retrieval.
     * Method ini membaca dataset, memproses dokumen, membangun indeks, dan memulai
     * antarmuka
     *
     * @param args argumen baris perintah (tidak digunakan)
     */
    public static void main(String[] args) {

        String folderPath = "../dataset/Cranfield";

        FileReaderUtil fr = new FileReaderUtil();
        List<Document> docs = fr.readAllDocuments(folderPath);

        TokenizerAndStemmer ts = new TokenizerAndStemmer();

        Map<Integer, String[]> tokenizedDoc = ts.tokenization(docs);
        Map<Integer, List<String>> stemmedDoc = ts.stemming(tokenizedDoc);

        Indexer in = new Indexer();
        Map<String, List<Integer>> invertedIndex = in.buildInvertedIndex(stemmedDoc);

        Scanner sc = new Scanner(System.in);

        int totalDocs = docs.size();
        BooleanQueryEngine engine = new BooleanQueryEngine(invertedIndex, totalDocs);

        while (true) {
            System.out.print("Query: ");
            String input = sc.nextLine().trim();
            if ("exit".equalsIgnoreCase(input))
                break;
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