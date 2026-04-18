package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;
import model.Document;

public class FileReaderUtil {
  /**
   * @param folderPath
   *                   lokasi path dimana dataset berada
   * @return List<Document>
   *         mengembalikan list dokumen yang sudah di membaca
   */
  public List<Document> readAllDocuments(String folderPath) {
    List<Document> documents = new ArrayList<>();
    File folder = new File(folderPath);

    File[] listOfFiles = folder.listFiles();

    if (listOfFiles != null) {
      for (File file : listOfFiles) {
        if (file.isFile() && file.getName().endsWith(".txt")) {
          try {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            documents.add(new Document(Integer.parseInt(file.getName().replace(".txt","")), content));
          } catch (IOException e) {
            System.out.println("Gagal membaca file: " + file.getName());
          }
        }
      }
    }
    return documents;
  }

}
