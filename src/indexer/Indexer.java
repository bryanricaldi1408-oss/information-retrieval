package indexer;

import java.util.*;


public class Indexer {

  public Map<String, List<Integer>> buildInvertedIndex(Map<Integer, List<String>> docs) {

    Map<String, List<Integer>> invertedIndex = new HashMap<>();

    for (Integer docId : docs.keySet()) {

      for(String t : docs.get(docId)){
        if (!invertedIndex.containsKey(t)) {
          List<Integer> postingList = new ArrayList<>();
          postingList.add(docId);

          invertedIndex.put(t, postingList);
        } else {
          List<Integer> updatedPostings = invertedIndex.get(t);

          if (!updatedPostings.contains(docId)) {
            updatedPostings.add(docId);
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
