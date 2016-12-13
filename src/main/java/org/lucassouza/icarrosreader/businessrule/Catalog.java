package org.lucassouza.icarrosreader.businessrule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jfree.data.Value;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lucassouza.icarrosreader.controller.Comunicator;
import org.lucassouza.icarrosreader.model.Brand;
import org.lucassouza.icarrosreader.model.Model;
import org.lucassouza.icarrosreader.type.ResourceType;
import org.lucassouza.navigation.model.Content;
import org.lucassouza.navigation.model.Navigation;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Catalog {

  private final HashMap<String, String> fields;
  private final HashMap<String, String> cookies;
  private final Navigation navigation;
  private final Content.Initializer defaults;
  private final Gson converter;
  private final Datasheet datasheet;

  public Catalog() {
    this.fields = new HashMap<>();
    this.cookies = new HashMap<>();
    this.converter = new Gson();
    this.defaults = Content.initializer()
            .domain("http://www.icarros.com.br/")
            .attempts(30);
    this.navigation = new Navigation(this.fields, this.cookies);
    this.datasheet = new Datasheet(this.navigation, this.defaults);
  }

  public List<Brand> readBrands() throws IOException {
    HashMap<Integer, Brand> brands = new HashMap<>();
    Elements options;
    Content access;

    access = this.defaults.initialize()
            .complement("/catalogo/index.jsp")
            .build();

    this.navigation.request(access);
    options = this.navigation.getPage().select("select#marca > optgroup > option");

    // Run all options and convert to brand
    options.forEach(option -> {
      int id;

      id = Integer.valueOf(option.val());
      brands.put(id, new Brand(id, option.text()));
    });

    this.readModels(brands);

    return new ArrayList<>(brands.values());
  }

  private List<Model> readModels(HashMap<Integer, Brand> brands) throws IOException {
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

    Comunicator.getInstance().informAmount(ResourceType.BRAND, brands.size());

    for (Brand brand : brands.values()) {
      this.datasheet.fillModels(brand.getModels());

      Comunicator.getInstance().informIncrement(ResourceType.BRAND);
    }

    return models;
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
