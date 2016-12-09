package org.lucassouza.icarrosreader.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Brand {

  private final int id;
  private final String name;
  private final List<Model> models;

  public Brand(int id, String name) {
    this.models = new ArrayList<>();
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }
  
  public List<Model> getModels() {
    return this.models;
  }
}
