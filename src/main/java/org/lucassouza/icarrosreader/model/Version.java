package org.lucassouza.icarrosreader.model;

import java.util.HashMap;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Version {

  private String name;
  private String price;
  private HashMap<String, String> attributes;

  public Version() {
    this.attributes = new HashMap<>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(String price) {
    this.price = price;
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
}
