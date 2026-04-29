package preprocessing;

import model.Document;
import java.util.*;

/**
 * Class yang bertanggung jawab dalam tokenisasi dan stemming.
 */
public class TokenizerAndStemmer {

    /**
     * Melakukan tokenisasi pada daftar dokumen dengan mengubah teks menjadi huruf kecil,
     * menghapus karakter non-alfabet, dan memisahkannya berdasarkan spasi.
     *
     * @param docs daftar objek Document yang akan ditokenisasi
     * @return map di mana kuncinya adalah ID dokumen dan nilainya adalah array dari token (kata)
     */
    public Map<Integer, String[]> tokenization(List<Document> docs) {

        Map<Integer, String[]> docTokens = new HashMap<>();

        for (Document doc : docs) {
            String rawText = doc.content;
            String lowerCaseText = rawText.toLowerCase();

            String cleanText = lowerCaseText.replaceAll("[^a-z\\s]", "");
            String[] tokens = cleanText.split("\\s+");

            docTokens.put(doc.id, tokens);
        }

        return docTokens;
    }

    /**
     * Melakukan stemming pada token dari setiap dokumen menggunakan algoritma Porter Stemming.
     * Method ini juga menghapus kata hasil stemming yang duplikat dalam dokumen yang sama.
     *
     * @param tokenized map di mana kuncinya adalah ID dokumen dan nilainya adalah array dari token
     * @return map di mana kuncinya adalah ID dokumen dan nilainya adalah daftar kata unik hasil stemming
     */
    public Map<Integer, List<String>> stemming(Map<Integer, String[]> tokenized) {
        PorterStemmer ps = new PorterStemmer();
        Map<Integer, List<String>> docStem = new HashMap<>();

        for (Integer docId : tokenized.keySet()) {
            List<String> wordDoc = new ArrayList<>();

            for (String w : tokenized.get(docId)) {
                String stemmed = ps.stem(w);

                if (!wordDoc.contains(stemmed)) {
                    wordDoc.add(stemmed);
                }
            }

            docStem.put(docId, wordDoc);
        }

        return docStem;
    }

}
