package org.lucassouza.icarrosreader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lucassouza.icarrosreader.model.Brand;
import org.lucassouza.icarrosreader.model.Model;
import org.lucassouza.navigation.model.Content;
import org.lucassouza.navigation.model.Content.Initializer;
import org.lucassouza.navigation.model.Navigation;
import org.lucassouza.navigation.model.Utils;

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

  private void readVersions(HashMap<Integer, Brand> brands) throws IOException {
    Integer count = 0;

    for (Brand brand : brands.values()) {
      for (Model model : brand.getModels()) {
        Content versionsPrices;
        String complement;

        complement = "/" + Utils.stripAccents(brand.getName() + "/" + model.getName()).replace(" ", "-").toLowerCase() + "/versoes-e-precos";
        versionsPrices = this.defaults.initialize()
                .complement(complement)
                .build();
        this.navigation.request(versionsPrices);
        count++;
        System.out.println(count);
        //System.out.println("------------------------------------------------------------------------");
        //System.out.println(this.navigation.getPage());
      }
    }
  }

  private void arrange(HashMap<Integer, Brand> brands, List<Model> models) {
    models.forEach(model -> {
      Brand brand;

      brand = brands.get(model.getBrandId());
      brand.getModels().add(model);
    });
  }
}
