package org.lucassouza.icarrosreader.model;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Version {

  private final HashMap<String, String> attributes;
  private final HashMap<String, String> informations;
  private final int id;
  private final String name;
  private final Year year;
  private String price;
  private LocalDateTime read;

  public Version(int id, String name, Year year) {
    this.id = id;
    this.name = name;
    this.year = year;

    this.read = LocalDateTime.now();
    this.attributes = new HashMap<>();
    this.informations = new HashMap<>();
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public int getId() {
    return id;
  }

  public Year getYear() {
    return year;
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
  
  public HashMap<String, String> getInformations() {
    return informations;
  }

  public LocalDateTime getRead() {
    return read;
  }
}
