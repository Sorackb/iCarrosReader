package org.lucassouza.icarrosreader.model;

import org.lucassouza.navigation.model.Utils;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Year {

  private int year;
  private Model model;

  public Year(int year, Model model) {
    this.year = year;
    this.model = model;
  }

  public int getYear() {
    return year;
  }

  public Model getModel() {
    return this.model;
  }

  public String getComplement() {
    return "/" + Utils.stripAccents(this.model.getBrand().getName() + "/" + this.model.getName()).replace(" ", "-").toLowerCase()
            + "/" + this.year + "/versoes-e-precos";
  }
}
