package org.lucassouza.icarrosreader.businessrule;

import java.io.IOException;
import java.util.HashMap;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lucassouza.icarrosreader.model.Year;
import org.lucassouza.navigation.model.Content;
import org.lucassouza.navigation.model.Navigation;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Opinions {

  private final Navigation navigation;
  private final Content.Initializer defaults;

  public Opinions(Navigation navigation, Content.Initializer defaults) {
    this.navigation = navigation;
    this.defaults = defaults;
  }

  public void read(Year year) throws IOException {
    Content opinions;

    opinions = this.defaults.initialize()
            .complement(year.getComplement() + "/ficha-tecnica")
            .build();

    this.navigation.request(opinions);
    this.readOpinions(year);
  }

  private void readOpinions(Year year) {
    Elements lines;

    lines = this.navigation.getPage().select("div.conteudo.opinioes table > tbody > tr");

    for (Element line : lines) {
      Elements columns;

      columns = line.select("td");

      if (columns.size() == 3) {
        year.getOpinions().put(columns.get(0).select("h3").text(), columns.get(2).text()); // Avaliação Geral
        year.getOpinions().put("Baseado em opiniões", columns.get(0).select("p").text().replaceAll("[^0-9]", ""));
      } else {
        year.getOpinions().put(columns.get(0).text(), columns.get(1).text());
      }
    }
  }
}
