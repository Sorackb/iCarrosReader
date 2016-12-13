package org.lucassouza.icarrosreader.model;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Version {

  private final HashMap<String, String> attributes;
  private String name;
  private String price;
  private LocalDateTime read;

  public Version() {
    this.read = LocalDateTime.now();
    this.attributes = new HashMap<>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public void setRead(LocalDateTime read) {
    this.read = read;
  }

  public String getName() {
    return this.name;
  }

  public String getPrice() {
    return this.price;
  }

  public HashMap<String, String> getAttributes() {
    return attributes;
  }

  public LocalDateTime getRead() {
    return read;
  }
}
