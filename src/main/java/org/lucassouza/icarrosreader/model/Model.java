package org.lucassouza.icarrosreader.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Model {

  private int id;

  @SerializedName("nome")
  private String name;

  @SerializedName("marcaId")
  private int brandId;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getBrandId() {
    return brandId;
  }
}
