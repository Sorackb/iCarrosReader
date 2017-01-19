package org.lucassouza.icarrosreader.businessrule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  private static final Pattern AIRBAG;
  private static final Pattern AR_CONDICIONADO;
  private static final Pattern DESCANSA;
  private static final String[] PREFIXES;

  private final Navigation navigation;
  private final Content.Initializer defaults;
  private final VersionsAndPrices versionsAndPrices;
  private final Opinions opinions;

  static {
    AIRBAG = Pattern.compile("(.*)(air)([\\s\\-]*)(bag)(.*)", Pattern.CASE_INSENSITIVE);
    AR_CONDICIONADO = Pattern.compile("(.*)(ar)([\\s\\-]*)(condicionado)(.*)", Pattern.CASE_INSENSITIVE);
    DESCANSA = Pattern.compile("(.*)(descansa)([\\s\\-]*)(braço|pé)(.*)", Pattern.CASE_INSENSITIVE);

    PREFIXES = new String[]{
      "Alteração de preços",
      "Capacidade de carga:",
      "Carroceria com",
      "Coeficiente aerodinâmico:",
      "Consumo de combustível secundário:",
      "Consumo de combustível:",
      "Critério de Classificação do Pesquisador",
      "Dimensões Internas:",
      "Equipamento de som",
      "Especificações de SUV:",
      "Pneus:",
      "Pneus",
      "Pneu",
      "Relação de transmissão",
      "SUV especificações",
      "Suspensão",
      "Tela com multi-funções",
      "Transmissão",
      "Travamento",
      "Tração"
    };
  }

  public Datasheet(Navigation navigation, Content.Initializer defaults) {
    this.navigation = navigation;
    this.defaults = defaults;

    this.versionsAndPrices = new VersionsAndPrices(this.navigation, this.defaults);
    this.opinions = new Opinions(this.navigation, this.defaults);
  }

  public void fillModels(List<Model> models) throws IOException {
    Comunicator.getInstance().informAmount(ResourceType.MODEL, models.size());

    for (Model model : models) {
      if (!model.getName().startsWith("Palio Weekend")) {
        continue;
      }
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
    HashMap<String, String> fields;

    fields = this.navigation.getFields();
    fields.put("modelo", String.valueOf(version.getYear().getModel().getId()));
    fields.put("anomodelo", String.valueOf(version.getYear().getYear()));
    fields.put("versao", String.valueOf(version.getId()));

    complement = "/catalogo/fichatecnica.jsp";

    byVersion = this.defaults.initialize()
            .fields("modelo",
                    "anomodelo",
                    "versao")
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
      this.addInformation(version.getInformations(), line.text());
    }
  }

  private void addInformation(HashMap<String, String> informations, String information) {
    String key = "";
    String value = "";
    String last;

    information = this.normalizeInformation(information);

    if (information.isEmpty()) {
      return;
    }

    for (String prefix : PREFIXES) {
      String real;

      real = prefix.replaceAll("[:\\.]$", ""); // Remove ":" no final

      if (information.startsWith(real)) {
        key = real;
        value = information.substring(prefix.length()).trim();
        break;
      }
    }

    if (key.trim().isEmpty()) {
      key = information;
    }

    if (value.trim().isEmpty()) {
      value = "Incluído";
    }

    value = value.replaceAll("\\.$", "").trim();

    // Concatena caso já haja informação para a mesma chave
    if (informations.containsKey(key)) {
      last = informations.get(key);

      if (!value.trim().equalsIgnoreCase(last.trim())) {
        value = informations.get(key) + "\n" + value;
      }
    }

    informations.put(key, value);
  }

  private String normalizeInformation(String information) {
    Matcher matcher;

    information = information.replaceAll("^-", "");
    information = information.replace(" ", " "); // caracter estranho
    information = information.replaceAll("\\s+", " "); // Remove espaços duplicados

    // Airbag
    matcher = AIRBAG.matcher(information);

    if (matcher.find()) {
      information = matcher.replaceAll("$1$2$4$5");
    }

    // Ar-condicionado
    matcher = AR_CONDICIONADO.matcher(information);

    if (matcher.find()) {
      information = matcher.replaceAll("$1$2\\-$4$5");
    }

    // Descança-braço
    matcher = DESCANSA.matcher(information);

    if (matcher.find()) {
      information = matcher.replaceAll("$1$2\\-$4$5");
    }

    information = information.trim();

    return information;
  }
}
