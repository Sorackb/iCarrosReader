package org.lucassouza.icarrosreader.businessrule;

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.lucassouza.icarrosreader.model.Brand;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class ModelCSV {

  private final DateTimeFormatter dateFormat;
  private final DateTimeFormatter dateTimeFormat;

  public ModelCSV() {
    this.dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    this.dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
  }

  public void saveToFiles(List<Brand> brands) throws IOException {
    HashSet<String> attributes = new HashSet<>();
    HashSet<String> opinions = new HashSet<>();

    brands.forEach((brand) -> {
      brand.getModels().forEach((model) -> {
        model.getYears().forEach((year) -> {
          year.getVersions().forEach((version) -> {
            attributes.addAll(version.getAttributes().keySet());
          });

          opinions.addAll(year.getOpinions().keySet());
        });
      });
    });

    this.saveInformationsToFile(attributes, brands);
    this.saveOpinionsToFile(opinions, brands);
  }

  private void saveInformationsToFile(HashSet<String> attributes, List<Brand> brands) throws IOException {
    List<String> lines = new ArrayList<>();
    LocalDate now;
    Path path;
    String systemPath;
    File system;

    lines.add("sep=,");
    lines.add("Marca,Modelo,Ano,Preço," + String.join(",", attributes) + ",\"Data da Consulta\"");

    brands.forEach((brand) -> {
      brand.getModels().forEach((model) -> {
        model.getYears().forEach((year) -> {
          year.getVersions().forEach((version) -> {
            String line;

            line = String.join(",",
                    "\"" + brand.getName() + "\"",
                    "\"" + version.getName() + "\"",
                    String.valueOf(year.getYear()),
                    "\"" + version.getPrice() + "\"",
                    this.concat(attributes, version.getAttributes()),
                    "\"" + version.getRead().format(this.dateTimeFormat) + "\"");

            lines.add(line);
          });
        });
      });
    });

    now = LocalDate.now();
    systemPath = ModelCSV.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    system = new File(systemPath);

    if (system.getParent().contains("target")) {
      path = Paths.get("C:/iCarrosReader/files/informacoes-" + now.format(this.dateFormat) + ".csv");
    } else {
      path = Paths.get(system.getParent() + "/files/informacoes-" + now.format(this.dateFormat) + ".csv");
    }

    this.saveToFile(path, lines);
  }

  private void saveOpinionsToFile(HashSet<String> opinions, List<Brand> brands) throws IOException {
    List<String> lines = new ArrayList<>();
    LocalDate now;
    Path path;
    String systemPath;
    File system;

    lines.add("sep=,");
    lines.add("Marca,Modelo,Ano," + String.join(",", opinions) + ",\"Data da Consulta\"");

    brands.forEach((brand) -> {
      brand.getModels().forEach((model) -> {
        model.getYears().forEach((year) -> {
          String line;

          line = String.join(",",
                  "\"" + brand.getName() + "\"",
                  "\"" + model.getName() + "\"",
                  String.valueOf(year.getYear()),
                  this.concat(opinions, year.getOpinions()),
                  "\"" + year.getRead().format(this.dateTimeFormat) + "\"");

          lines.add(line);
        });
      });
    });

    now = LocalDate.now();
    systemPath = ModelCSV.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    system = new File(systemPath);

    if (system.getParent().contains("target")) {
      path = Paths.get("C:/iCarrosReader/files/opinioes-" + now.format(this.dateFormat) + ".csv");
    } else {
      path = Paths.get(system.getParent() + "/files/opinioes-" + now.format(this.dateFormat) + ".csv");
    }

    this.saveToFile(path, lines);
  }

  private String concat(HashSet<String> names, HashMap<String, String> values) {
    String result = "";

    for (String attribute : names) {
      if (!result.isEmpty()) {
        result = result + ",";
      }

      if (values.containsKey(attribute)) {
        result = result + "\"" + values.get(attribute) + "\"";
      }
    }

    return result;
  }

  private void saveToFile(Path path, List<String> lines) throws IOException {
    Files.write(path, lines, UTF_8, APPEND, CREATE);
  }
}
