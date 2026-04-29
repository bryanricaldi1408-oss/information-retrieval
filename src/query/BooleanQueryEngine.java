package query;

import preprocessing.PorterStemmer;
import java.util.*;

/*
 * Cara kerja:
 *
 * 1. Shunting-Yard:
 *   Ubah query infix menjadi urutan postfix 
 *   Contoh: "lift AND (drag OR pressure)"
 *       --> ["lift", "drag", "pressure", "OR", "AND"]
 *
 *   Aturan:
 *   - Term       --> langsung masuk outputQueue
 *   - Operator   --> push ke operatorStack
 *                    (tapi pop dulu operator lama yg prioritasnya >= operator ini)
 *   - "("        --> push ke operatorStack
 *   - ")"        --> pop operatorStack ke outputQueue sampai ketemu "("
 *
 * 2. Evaluasi:
 *   Baca outputQueue satu per satu, pakai resultStack:
 *   - Term     --> ambil posting list --> push ke resultStack
 *   - NOT      --> pop 1 posting list --> negate --> push hasilnya
 *   - AND/OR   --> pop 2 posting list --> intersect/union --> push hasilnya
 *   - Akhir    --> resultStack.pop() = hasil akhir
 */
public class BooleanQueryEngine {

    private final Map<String, List<Integer>> invertedIndex;
    private final int totalDocs;
    private final PorterStemmer stemmer;

    // Prioritas operator (semakin besar angka, semakin tinggi prioritas)
    // NOT > AND > OR 
    private static final Map<String, Integer> PRIORITY = new HashMap<>();
    static {
        PRIORITY.put("OR",  1);
        PRIORITY.put("AND", 2);
        PRIORITY.put("NOT", 3);
    }

    public BooleanQueryEngine(Map<String, List<Integer>> invertedIndex, int totalDocs) {
        this.invertedIndex = invertedIndex;
        this.totalDocs = totalDocs;
        this.stemmer = new PorterStemmer();
    }

    public List<Integer> search(String query) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();

        // Langkah 1: pecah query jadi token
        List<String> tokens = tokenizeQuery(query.trim());

        // Langkah 2: Fase 1 - ubah ke postfix dengan Shunting-Yard
        List<String> postfix = shuntingYard(tokens);

        // Langkah 3: Fase 2 - evaluasi postfix dengan result stack
        List<Integer> result = evaluate(postfix);

        Collections.sort(result);
        return result;
    }


    //SHUNTING-YARD ALGORITHM
    private List<String> shuntingYard(List<String> tokens) {
        Queue<String> outputQueue  = new LinkedList<>();  // hasil postfix
        Deque<String> operatorStack = new ArrayDeque<>(); // stack operator sementara

        for (String raw : tokens) {
            // Normalisasi: operator selalu uppercase agar PRIORITY.get() tidak null
            String token = isOperator(raw) ? raw.toUpperCase() : raw;
 
            if (isOperator(token)) {
                // Sebelum push, pop operator lama yang prioritasnya >= token ini
                // (karena operator dengan prioritas lebih tinggi harus dikerjakan dulu)
                while (!operatorStack.isEmpty()
                        && isOperator(operatorStack.peek())
                        && PRIORITY.get(operatorStack.peek()) >= PRIORITY.get(token)) {
                    outputQueue.add(operatorStack.pop());
                }
                operatorStack.push(token);
 
            } else if (token.equals("(")) {
                // Kurung buka: langsung push, jadi "pembatas" di stack
                operatorStack.push(token);
 
            } else if (token.equals(")")) {
                // Kurung tutup: pop semua operator sampai ketemu "("
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    outputQueue.add(operatorStack.pop());
                }
                if (!operatorStack.isEmpty()) {
                    operatorStack.pop(); // buang "(" dari stack (jangan masuk queue)
                }
 
            } else {
                // Term biasa: langsung masuk output queue
                outputQueue.add(token);
            }
        }

        // Pop sisa operator yang masih ada di stack ke output queue
        while (!operatorStack.isEmpty()) {
            outputQueue.add(operatorStack.pop());
        }

        return new ArrayList<>(outputQueue);
    }


    //EVALUASI POSTFIX DENGAN RESULT STACK
    private List<Integer> evaluate(List<String> postfix) {
        Deque<List<Integer>> resultStack = new ArrayDeque<>();

        for (String token : postfix) {

            if (token.equalsIgnoreCase("NOT")) {
                // NOT: pop 1 posting list, negate, push hasilnya
                if (resultStack.isEmpty()) return new ArrayList<>();
                List<Integer> operand = resultStack.pop();
                resultStack.push(negate(operand));

            } else if (token.equalsIgnoreCase("AND")) {
                // AND: pop 2 posting list, intersect, push hasilnya
                if (resultStack.size() < 2) return new ArrayList<>();
                List<Integer> right = resultStack.pop();
                List<Integer> left  = resultStack.pop();
                resultStack.push(intersect(left, right));

            } else if (token.equalsIgnoreCase("OR")) {
                // OR: pop 2 posting list, union, push hasilnya
                if (resultStack.size() < 2) return new ArrayList<>();
                List<Integer> right = resultStack.pop();
                List<Integer> left  = resultStack.pop();
                resultStack.push(union(left, right));

            } else {
                // Term: ambil posting list dari index, push ke result stack
                resultStack.push(getPostingList(token));
            }
        }

        return resultStack.isEmpty() ? new ArrayList<>() : resultStack.pop();
    }


    /**
     * INTERSECTION - operasi AND
     */
    public List<Integer> intersect(List<Integer> pL1, List<Integer> pL2) {
        List<Integer> answer = new ArrayList<>();
        int p1 = 0, p2 = 0;

        while (p1 < pL1.size() && p2 < pL2.size()) {
            int d1 = pL1.get(p1), d2 = pL2.get(p2);
            if      (d1 == d2) { answer.add(d1); p1++; p2++; }
            else if (d1 <  d2) { p1++; }
            else               { p2++; }
        }
        return answer;
    }

    /**
     * UNION - operasi OR
     */
    public List<Integer> union(List<Integer> pL1, List<Integer> pL2) {
        List<Integer> answer = new ArrayList<>();
        int p1 = 0, p2 = 0;

        while (p1 < pL1.size() && p2 < pL2.size()) {
            int d1 = pL1.get(p1), d2 = pL2.get(p2);
            if      (d1 == d2) { answer.add(d1); p1++; p2++; }
            else if (d1 <  d2) { answer.add(d1); p1++; }
            else               { answer.add(d2); p2++; }
        }
        while (p1 < pL1.size()) answer.add(pL1.get(p1++));
        while (p2 < pL2.size()) answer.add(pL2.get(p2++));
        return answer;
    }

    /**
     * NOT - komplemen 
     */
    public List<Integer> negate(List<Integer> postingList) {
        Set<Integer> excluded = new HashSet<>(postingList);
        List<Integer> answer = new ArrayList<>();
        for (int docId = 1; docId <= totalDocs; docId++) {
            if (!excluded.contains(docId)) answer.add(docId);
        }
        return answer;
    }


    // HELPER
    private List<String> tokenizeQuery(String query) {
        List<String> result = new ArrayList<>();
        query = query.replace("(", " ( ").replace(")", " ) ");
        for (String token : query.split("\\s+")) {
            if (!token.isEmpty()) result.add(token);
        }
        return result;
    }

    private boolean isOperator(String token) {
        return PRIORITY.containsKey(token.toUpperCase());
    }

    private List<Integer> getPostingList(String term) {
        String stemmed = stemmer.stem(term.toLowerCase());
        List<Integer> result = invertedIndex.get(stemmed);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }
}
