package indexer;

import java.util.*;


/**
 * Class yang bertanggung jawab untuk membangun inverted index dari dokumen yang telah diproses.
 * Inverted index memetakan setiap term ke daftar ID dokumen tempat term tersebut muncul.
 */
public class Indexer {

  /**
   * Membangun inverted index dari map dokumen yang telah di-stem.
   * Inverted index yang dihasilkan berisi term sebagai kunci dan daftar ID dokumen yang terurut sebagai nilai.
   *
   * @param docs map di mana kuncinya adalah ID dokumen dan nilainya adalah daftar token yang telah di-stem
   * @return map yang merepresentasikan inverted index (term -> daftar ID dokumen)
   */
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
