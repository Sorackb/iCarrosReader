package org.lucassouza.icarrosreader.businessrule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lucassouza.icarrosreader.controller.Comunicator;
import org.lucassouza.icarrosreader.model.Brand;
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
public class ICarros {

  private final HashMap<String, String> fields;
  private final HashMap<String, String> cookies;
  private final Navigation navigation;
  private final Content.Initializer defaults;
  private final Gson converter;

  public ICarros() {
    this.fields = new HashMap<>();
    this.cookies = new HashMap<>();
    this.converter = new Gson();
    this.defaults = Content.initializer()
            .domain("http://www.icarros.com.br/")
            .attempts(30);
    this.navigation = new Navigation(this.fields, this.cookies);
  }

  public HashMap<Integer, Brand> readBrands() throws IOException {
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

    Comunicator.getInstance().informIncrement(ResourceType.STEP);

    return brands;
  }

  public List<Model> readModels(HashMap<Integer, Brand> brands) {
    List<Model> models = null;
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

    Comunicator.getInstance().informIncrement(ResourceType.STEP);

    return models;
  }

  public void readYears(HashMap<Integer, Brand> brands) throws IOException {
    Comunicator.getInstance().informAmount(ResourceType.BRAND, brands.size());

    for (Brand brand : brands.values()) {
      Comunicator.getInstance().informAmount(ResourceType.MODEL, brand.getModels().size());

      for (Model model : brand.getModels()) {
        Content summary;
        String complement;
        Elements options;

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
        Comunicator.getInstance().informIncrement(ResourceType.MODEL);
      }

      Comunicator.getInstance().informIncrement(ResourceType.BRAND);
    }

    Comunicator.getInstance().informIncrement(ResourceType.STEP);
  }

  public HashSet<String> readVersions(HashMap<Integer, Brand> brands) throws IOException {
    HashSet<String> attributes = new HashSet<>();

    Comunicator.getInstance().informAmount(ResourceType.BRAND, brands.size());

    for (Brand brand : brands.values()) {
      Comunicator.getInstance().informAmount(ResourceType.MODEL, brand.getModels().size());

      for (Model model : brand.getModels()) {
        Comunicator.getInstance().informAmount(ResourceType.YEAR, model.getYears().size());

        for (Year year : model.getYears()) {
          Content versionsPrices;
          String complement;

          complement = year.getComplement();
          versionsPrices = this.defaults.initialize()
                  .complement(complement)
                  .build();
          this.navigation.request(versionsPrices);
          year.setVersions(this.parseVersions(attributes));

          Comunicator.getInstance().informIncrement(ResourceType.YEAR);
        }

        Comunicator.getInstance().informIncrement(ResourceType.MODEL);
      }

      Comunicator.getInstance().informIncrement(ResourceType.BRAND);
    }
    
    Comunicator.getInstance().informIncrement(ResourceType.STEP);

    return attributes;
  }

  private List<Version> parseVersions(HashSet<String> attributes) {
    List<Version> versions = new ArrayList<>();
    Elements headers;
    Element table;

    table = this.navigation.getPage().select("table#dadosVersoes").first();

    if (table == null) {
      System.out.println(this.navigation.getLastResponse().url().toString());
      return versions;
    }

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

      prices = header.select(".laranja");

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
    int versionIndex;

    lines = table.select("tbody > tr");

    for (Element line : lines) {
      String attribute;
      boolean equals;

      if (!line.select("tr.secao").isEmpty()) { // Optional
        break;
      }

      attribute = line.select("th").text();
      attributes.add(attribute);
      columns = line.select("td");
      equals = columns.size() == versions.size();
      versionIndex = 0;

      for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
        HashMap<String, String> versionAttributes;
        Element column = columns.get(columnIndex);
        String value = "";

        versionAttributes = versions.get(versionIndex).getAttributes();
        value = column.text();

        if (!equals && !column.hasAttr("colspan")) {
          columnIndex = columnIndex + 1;
          value = value + " / " + columns.get(columnIndex).text();
        }

        versionAttributes.put(attribute, value);
        versionIndex++;
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
