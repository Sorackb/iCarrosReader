package org.lucassouza.icarrosreader.businessrule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lucassouza.icarrosreader.controller.Comunicator;
import org.lucassouza.icarrosreader.model.Model;
import org.lucassouza.icarrosreader.model.Version;
import org.lucassouza.icarrosreader.model.Year;
import org.lucassouza.icarrosreader.type.ResourceType;
import org.lucassouza.navigation.model.Content;
import org.lucassouza.navigation.model.Navigation;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Datasheet {

  private final Navigation navigation;
  private final Content.Initializer defaults;
  private final VersionsAndPrices versionsAndPrices;
  private final Opinions opinions;

  public Datasheet(Navigation navigation, Content.Initializer defaults) {
    this.navigation = navigation;
    this.defaults = defaults;

    this.versionsAndPrices = new VersionsAndPrices(this.navigation, this.defaults);
    this.opinions = new Opinions(this.navigation, this.defaults);
  }

  public void fillModels(List<Model> models) throws IOException {
    Comunicator.getInstance().informAmount(ResourceType.MODEL, models.size());

    for (Model model : models) {
      try {
        this.fillModel(model);
      } catch (HttpStatusException hse) {
        Logger.getLogger(Datasheet.class.getName()).log(Level.SEVERE, null, hse);
      } finally {
        Comunicator.getInstance().informIncrement(ResourceType.MODEL);
      }
    }
  }

  private void fillModel(Model model) throws IOException {
    Content general;

    general = this.defaults.initialize()
            .complement(model.getComplement() + "/ficha-tecnica")
            .build();

    this.navigation.request(general);
    this.fillYears(model);
  }

  private void fillYears(Model model) throws IOException {
    Elements options;

    options = this.navigation.getPage().select("select#anomodelo > option:not(:nth-of-type(1))");
    Comunicator.getInstance().informAmount(ResourceType.YEAR, options.size());

    for (Element option : options) {
      Year year;
      int numericYear;

      numericYear = Integer.parseInt(option.val());
      year = new Year(numericYear, model);
      model.getYears().add(year);
      this.fillYear(year);
      Comunicator.getInstance().informIncrement(ResourceType.YEAR);
    }
  }

  private void fillYear(Year year) throws IOException {
    Content byYear;

    byYear = this.defaults.initialize()
            .complement(year.getComplement() + "/ficha-tecnica")
            .build();

    this.navigation.request(byYear);
    this.fillVersions(year);
    this.versionsAndPrices.read(year);
    this.opinions.read(year);
  }

  private void fillVersions(Year year) throws IOException {
    Elements options;

    options = this.navigation.getPage().select("select#versaoId > option");

    for (Element option : options) {
      Version version;
      int id;

      id = Integer.parseInt(option.val());
      version = new Version(id, option.text(), year);
      year.getVersions().add(version);
      this.fillVersion(version);
    }
  }

  private void fillVersion(Version version) throws IOException {
    Content byVersion;
    String complement;

    complement = "/catalogo/fichatecnica.jsp?"
            + "modelo=" + version.getYear().getModel().getId()
            + "&anomodelo=" + version.getYear().getYear()
            + "&versao=" + version.getId();

    byVersion = this.defaults.initialize()
            .complement(complement)
            .build();

    this.navigation.request(byVersion);

    version.setPrice(this.navigation.getPage().select("div#preco > span").text());
    this.fillInformations(version);
  }

  private void fillInformations(Version version) {
    String[] transition;
    Elements lines;

    // General itens
    lines = this.navigation.getPage().select("table.zebra:not(#itensDeSerie) > tbody > tr");

    for (Element line : lines) {
      List<String> values = new ArrayList<>();
      Elements columns;
      String attribute;

      attribute = line.select("td:nth-of-type(1)").text();
      columns = line.select("td:not(:nth-of-type(1))");

      for (Element column : columns) {
        String value;

        value = column.text().trim();

        if (!value.isEmpty()) {
          values.add(value);
        }
      }

      transition = values.toArray(new String[values.size()]);
      version.getInformations().put(attribute, String.join(" - ", transition));
    }

    // Serial itens
    lines = this.navigation.getPage().select("table#itensDeSerie > tbody > tr");

    for (Element line : lines) {
      String serialItem;

      serialItem = line.text().trim();

      if (!serialItem.isEmpty()) {
        version.getInformations().put(line.text(), "Incluído");
      }
    }
  }
}
