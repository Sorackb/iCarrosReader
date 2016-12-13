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

  @SerializedName("nome")
  private String name;

  @SerializedName("marcaId")
  private int brandId;

  private int id;
  private Brand brand;
  private final List<Year> years;

  // Constructors
  public Model() {
    this.years = new ArrayList<>();
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
    String complement;

    complement = "/"
            + Utils.stripAccents(this.brand.getName().replace("/", "-"))
            + "/"
            + Utils.stripAccents(this.name.replace("/", "-"));

    complement = complement.replace(" ", "-").toLowerCase();

    return complement;
  }
}
