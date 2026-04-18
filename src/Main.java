import indexer.Indexer;
import utils.FileReaderUtil;
import model.Document;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        
        String folderPath = "../dataset/Cranfield";

        FileReaderUtil fr = new FileReaderUtil();
        List<Document> docs = fr.readAllDocuments(folderPath);

        Indexer in = new Indexer();
        Map<String, List<Integer>> invertedIndex = in.buildInvertedIndex(docs);

        invertedIndex.forEach((key, value) -> { 

            System.out.printf("[%s] -> {%s}\n", key, value.toString());
        });
    }
}