package org.lucassouza.icarrosreader.businessrule;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lucassouza.icarrosreader.model.Version;
import org.lucassouza.icarrosreader.model.Year;
import org.lucassouza.navigation.model.Content;
import org.lucassouza.navigation.model.Navigation;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class VersionsAndPrices {

  private final Navigation navigation;
  private final Content.Initializer defaults;

  public VersionsAndPrices(Navigation navigation, Content.Initializer defaults) {
    this.navigation = navigation;
    this.defaults = defaults;
  }

  public void read(Year year) throws IOException {
    Content versionsPrices;
    String complement;

    complement = year.getComplement() + "/versoes-e-precos";
    versionsPrices = this.defaults.initialize()
            .complement(complement)
            .build();
    this.navigation.request(versionsPrices);
    this.readAttributes(year.getVersions());
  }

  private void readAttributes(List<Version> versions) {
    Element table;
    Elements lines;
    Elements columns;
    int versionIndex;

    table = this.navigation.getPage().select("table#dadosVersoes").first();

    if (table == null) {
      System.out.println(this.navigation.getLastResponse().url().toString());
      return;
    }

    lines = table.select("tbody > tr");

    for (Element line : lines) {
      String attribute;
      boolean equals;

      if (line.hasClass("secao")) { // Optional
        continue;
      }

      attribute = line.select("th").text();
      columns = line.select("td");
      equals = columns.size() == versions.size();
      versionIndex = 0;

      for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
        HashMap<String, String> versionAttributes;
        Element column = columns.get(columnIndex);
        Element nextColumn;
        String value;

        versionAttributes = versions.get(versionIndex).getAttributes();
        value = this.getValueOfAttribute(column);

        if (!equals && !column.hasAttr("colspan")) {
          columnIndex = columnIndex + 1;
          nextColumn = columns.get(columnIndex);
          value = value + " / " + this.getValueOfAttribute(nextColumn);
        }

        versionAttributes.put(attribute, value);
        versionIndex++;
      }
    }
  }

  private String getValueOfAttribute(Element column) {
    Element span;
    String value = "";

    span = column.select("span.icone").first();

    if (span != null) {
      if (span.hasClass("icoindisponivel")) {
        value = "Indisponível";
      } else if (span.hasClass("icoopcional")) {
        value = "Opcional";
      } else if (span.hasClass("icodisponivel")) {
        value = "Disponível";
      }
    } else {
      value = column.text();
    }

    return value;
  }
}
