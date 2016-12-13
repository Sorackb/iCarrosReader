package org.lucassouza.icarrosreader.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Year {

  private final HashMap<String, String> opinions;
  private final int year;
  private final Model model;
  private final List<Version> versions;
  private LocalDateTime read;

  public Year(int year, Model model) {
    this.year = year;
    this.model = model;

    this.read = LocalDateTime.now();
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

  public LocalDateTime getRead() {
    return read;
  }

  public String getComplement() {
    return this.model.getComplement() + "/" + this.year;
  }
}
