package org.lucassouza.icarrosreader.model;

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
import org.lucassouza.icarrosreader.controller.Comunicator;
import org.lucassouza.icarrosreader.type.ResourceType;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class ModelCSV {

  private final DateTimeFormatter dateFormat;
  private final DateTimeFormatter dateTimeFormat;

  public ModelCSV() {
    this.dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    this.dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  }

  public void saveToFile(HashSet<String> attributes, List<Model> models) throws IOException {
    List<String> lines = new ArrayList<>();
    LocalDate now;
    Path path;
    String systemPath;
    File system;

    lines.add("sep=,");
    lines.add("Marca,Modelo,Ano,Preço," + String.join(",", attributes) + ",\"Data da Consulta\"");

    models.forEach((model) -> {
      model.getYears().forEach((year) -> {
        year.getVersions().forEach((version) -> {
          String line;

          line = String.join(",", model.getBrand().getName(),
                  version.getName(),
                  String.valueOf(year.getYear()),
                  "\"" + version.getPrice() + "\"",
                  this.concatAttributes(attributes, version.getAttributes()),
                  "\"" + version.getRead().format(this.dateTimeFormat) + "\"");

          lines.add(line);
        });
      });
    });

    now = LocalDate.now();
    systemPath = ModelCSV.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    system = new File(systemPath);

    if (system.getParent().contains("target")) {
      path = Paths.get("C:/iCarrosReader/files/icarros-" + now.format(this.dateFormat) + ".csv");
    } else {
      path = Paths.get(system.getParent() + "/files/icarros-" + now.format(this.dateFormat) + ".csv");
    }

    this.saveToFile(path, lines);
    Comunicator.getInstance().informIncrement(ResourceType.STEP);
  }

  private String concatAttributes(HashSet<String> attributes, HashMap<String, String> versionAttributes) {
    String result = "";

    for (String attribute : attributes) {
      if (!result.isEmpty()) {
        result = result + ",";
      }

      if (versionAttributes.containsKey(attribute)) {
        result = result + "\"" + versionAttributes.get(attribute) + "\"";
      }
    }

    return result;
  }

  private void saveToFile(Path path, List<String> lines) throws IOException {
    Files.write(path, lines, UTF_8, APPEND, CREATE);
  }
}
