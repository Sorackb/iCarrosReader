package org.lucassouza.icarrosreader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lucassouza.icarrosreader.model.Brand;
import org.lucassouza.icarrosreader.model.Model;
import org.lucassouza.icarrosreader.model.Version;
import org.lucassouza.icarrosreader.model.Year;
import org.lucassouza.navigation.model.Content;
import org.lucassouza.navigation.model.Content.Initializer;
import org.lucassouza.navigation.model.Navigation;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Reader {

  private final HashMap<String, String> fields;
  private final HashMap<String, String> cookies;
  private final Navigation navigation;
  private final Initializer defaults;
  private final Gson converter;

  public static void main(String[] args) {
    Reader reader = new Reader();

    try {
      reader.read();
    } catch (IOException ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public Reader() {
    this.fields = new HashMap<>();
    this.cookies = new HashMap<>();
    this.converter = new Gson();
    this.defaults = Content.initializer()
            .domain("http://www.icarros.com.br/");
    this.navigation = new Navigation(this.fields, this.cookies);
  }

  public void read() throws IOException {
    HashMap<Integer, Brand> brands;

    brands = this.readBrands();
    this.readModels(brands);
    this.readYears(brands);
    this.readVersions(brands);
  }

  private HashMap<Integer, Brand> readBrands() throws IOException {
    HashMap<Integer, Brand> brands = new HashMap<>();
    Elements options;
    Content access;

    access = this.defaults.initialize()
            .complement("catalogo/index.jsp")
            .build();

    this.navigation.request(access);
    options = this.navigation.getPage().select("select#marca > optgroup > option");

    // Run all options and convert to brand
    options.forEach(option -> {
      int id;

      id = Integer.valueOf(option.val());
      brands.put(id, new Brand(id, option.text()));
    });

    return brands;
  }

  private void readModels(HashMap<Integer, Brand> brands) {
    List<Model> models;
    Elements scripts;
    Type type;

    type = new TypeToken<List<Model>>() {
    }.getType();
    scripts = this.navigation.getPage().head().select("script");

    for (Element script : scripts) {
      String content = script.data();

      if (content.startsWith("var mdls")) {
        String array;

        array = content.substring(content.indexOf("["), content.lastIndexOf("]") + 1);
        models = converter.fromJson(array, type);
        this.arrange(brands, models);
        break;
      }
    }
  }

  private void readYears(HashMap<Integer, Brand> brands) throws IOException {
    for (Brand brand : brands.values()) {
      for (Model model : brand.getModels()) {
        Content summary;
        String complement;
        Elements options;

        //TODO remover
        if (!model.getName().startsWith("Celta")) {
          continue;
        }

        complement = model.getComplement();
        summary = this.defaults.initialize()
                .complement(complement)
                .build();
        this.navigation.request(summary);
        options = this.navigation.getPage().select("select#anomodelo > option:not([value=0])");

        options.forEach(option -> {
          int year;

          year = Integer.parseInt(option.val());
          model.getYears().add(new Year(year, model));
        });
      }
    }
  }

  private void readVersions(HashMap<Integer, Brand> brands) throws IOException {
    HashSet<String> attributes = new HashSet<>();

    for (Brand brand : brands.values()) {
      for (Model model : brand.getModels()) {
        //TODO remover
        if (!model.getName().startsWith("Celta")) {
          continue;
        }

        for (Year year : model.getYears()) {
          Content versionsPrices;
          String complement;

          complement = year.getComplement();
          versionsPrices = this.defaults.initialize()
                  .complement(complement)
                  .build();
          this.navigation.request(versionsPrices);
          model.setVersions(this.parseVersions(attributes));
        }
      }
    }
  }

  private List<Version> parseVersions(HashSet<String> attributes) {
    List<Version> versions = new ArrayList<>();
    Elements headers;
    Element table;

    table = this.navigation.getPage().select("table#dadosVersoes").first();
    headers = table.select("thead > tr:nth-child(1) > th.fundo_cinza_escuro > h2");

    for (Element header : headers) {
      Version version = new Version();

      version.setName(header.text());
      versions.add(version);
    }

    this.readPrices(table, versions);
    this.readAttributes(table, versions, attributes);

    return versions;
  }

  private void readPrices(Element table, List<Version> versions) {
    Elements headers;
    Integer index;

    headers = table.select("thead > tr:nth-child(2) > th:not(:nth-child(1))");

    index = 0;

    for (Element header : headers) {
      String text = "";
      Elements prices;

      prices = header.select("span.laranja");

      for (Element price : prices) {
        if (!text.isEmpty()) {
          text = text + " / ";
        }

        text = text + price.text();
      }

      versions.get(index).setPrice(text);
      index++;
    }
  }

  private void readAttributes(Element table, List<Version> versions, HashSet<String> attributes) {
    Elements lines;
    Elements columns;
    Integer index;

    lines = table.select("tbody > tr");

    for (Element line : lines) {
      String attribute;
      int realIndex;
      boolean equals;

      attribute = line.select("th").text();
      attributes.add(attribute);
      columns = line.select("td");
      equals = columns.size() == versions.size();
      realIndex = 0;
      index = 0;

      for (Element column : columns) {
        HashMap<String, String> versionAttributes;
        String value = "";
        boolean next;

        if (column.hasClass("secao")) { // Optional
          break;
        }

        versionAttributes = versions.get(index).getAttributes();

        if (versionAttributes.containsKey(attribute)) {
          value = versionAttributes.get(attribute) + " / ";
        }

        value = value + column.text();
        versionAttributes.put(attribute, value);
        next = (realIndex + 1) % 2 == 0;

        if (equals || column.hasAttr("colspan") || next) {
          index++;
        }

        realIndex++;
      }
    }
  }

  private void arrange(HashMap<Integer, Brand> brands, List<Model> models) {
    models.forEach(model -> {
      Brand brand;

      brand = brands.get(model.getBrandId());
      model.setBrand(brand);
      brand.getModels().add(model);
    });
  }
}
