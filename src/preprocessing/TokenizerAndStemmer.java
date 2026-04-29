package preprocessing;

import model.Document;
import java.util.*;

public class TokenizerAndStemmer {

    public Map<Integer,String[]> tokenization(List<Document> docs){

        Map<Integer, String[]> docTokens = new HashMap<>();

        for(Document doc : docs){
            String rawText = doc.content;
            String lowerCaseText = rawText.toLowerCase();

            String cleanText = lowerCaseText.replaceAll("[^a-z\\s]", "");
            String[] tokens = cleanText.split("\\s+");

            docTokens.put(doc.id, tokens);
        }

        return docTokens;
    }

    public Map<Integer, List<String>> stemming(Map<Integer, String[]> tokenized){
        PorterStemmer ps = new PorterStemmer();
        Map<Integer, List<String>> docStem = new HashMap<>();

        for(Integer docId : tokenized.keySet()){
            List<String> wordDoc = new ArrayList<>();

            for(String w : tokenized.get(docId)){
                String stemmed = ps.stem(w);

                if(!wordDoc.contains(stemmed)){
                    wordDoc.add(stemmed);
                }
            }
            
            docStem.put(docId, wordDoc);
        }

        return docStem;
    }

}
    
