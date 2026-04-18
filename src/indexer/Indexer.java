package indexer;

import java.util.*;

import model.Document;

public class Indexer {

  public Map<String, List<Integer>> buildInvertedIndex(List<Document> docs) {

    Map<String, List<Integer>> invertedIndex = new HashMap<>();

    for (Document doc : docs) {
      String rawText = doc.content;
      String lowerCaseText = rawText.toLowerCase();

      String cleanText = lowerCaseText.replaceAll("[^a-z\\s]", "");
      String[] tokens = cleanText.split("\\s+");

      for (String t : tokens) {
        if (!invertedIndex.containsKey(t)) {
          List<Integer> postingList = new ArrayList<>();
          postingList.add(doc.id);

          invertedIndex.put(t, postingList);
        } else {
          List<Integer> updatedPostings = invertedIndex.get(t);

          Integer lastDocId = updatedPostings.get(updatedPostings.size() - 1);
          if (!lastDocId.equals(doc.id)) {
            updatedPostings.add(doc.id);
          }
        }
      }
    }
    for (Map.Entry<String, List<Integer>> entry : invertedIndex.entrySet()) {
        Collections.sort(entry.getValue());
    }
    return invertedIndex;
  }
}
