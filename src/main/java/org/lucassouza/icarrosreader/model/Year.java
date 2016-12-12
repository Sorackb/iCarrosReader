package org.lucassouza.icarrosreader.model;

import java.util.List;
import org.lucassouza.navigation.model.Utils;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Year {

  private List<Version> versions;
  private final int year;
  private final Model model;

  public Year(int year, Model model) {
    this.year = year;
    this.model = model;
  }

  public void setVersions(List<Version> versions) {
    this.versions = versions;
  }

  public int getYear() {
    return year;
  }

  public Model getModel() {
    return this.model;
  }

  public List<Version> getVersions() {
    return versions;
  }

  public String getComplement() {
    return this.model.getComplement() + "/" + this.year + "/versoes-e-precos";
  }
}
