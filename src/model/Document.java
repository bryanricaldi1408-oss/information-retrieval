package model;

/**
 * class Document
 * merupakan abstraksi data dari datase
 */
public class Document {
  public Integer id;
  public String content;

  public Document(Integer id, String content) {
    this.id = id;
    this.content = content;
  }
}
