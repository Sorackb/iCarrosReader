package org.lucassouza.icarrosreader.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Year {

  private final int year;
  private final Model model;
  private final List<Version> versions;
  private final HashMap<String, String> opinions;

  public Year(int year, Model model) {
    this.year = year;
    this.model = model;
    this.versions = new ArrayList<>();
    this.opinions = new HashMap<>();
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

  public HashMap<String, String> getOpinions() {
    return opinions;
  }

  public String getComplement() {
    return this.model.getComplement() + "/" + this.year;
  }
}
