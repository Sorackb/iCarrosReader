package org.lucassouza.icarrosreader.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import org.lucassouza.navigation.model.Utils;

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

  private Brand brand;

  private List<Year> years;

  // Constructors
  public Model() {
    this.years = new ArrayList<Year>();
  }

  // Setters
  public void setBrand(Brand brand) {
    this.brand = brand;
  }

  // Getters
  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public int getBrandId() {
    return this.brandId;
  }

  public Brand getBrand() {
    return brand;
  }

  public List<Year> getYears() {
    return this.years;
  }

  public String getComplement() {
    return "/" + Utils.stripAccents(this.brand.getName() + "/" + this.name).replace(" ", "-").toLowerCase();
  }
}
