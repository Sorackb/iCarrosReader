package org.lucassouza.icarrosreader.businessrule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.lucassouza.icarrosreader.model.Brand;
import org.lucassouza.icarrosreader.model.Model;
import org.lucassouza.icarrosreader.model.Version;
import org.lucassouza.icarrosreader.model.Year;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class ModelXLS {

  private final DateTimeFormatter dateTimeFormat;
  private final String path;

  public ModelXLS() {
    String systemPath;
    File system;

    this.dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    systemPath = ModelCSV.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    system = new File(systemPath);

    if (system.getParent().contains("target")) {
      this.path = "C:/iCarrosReader/files/";
    } else {
      this.path = system.getParent() + "/files/";
    }
  }

  public void saveToFiles(List<Brand> brands) throws IOException {
    FileOutputStream file;
    Workbook workbook;

    workbook = new XSSFWorkbook();
    this.writeVersionsAndPricesSheet(workbook, brands);
    this.writeInformationSheet(workbook, brands);
    this.writeToOpinionSheet(workbook, brands);

    file = new FileOutputStream(this.path + "icarros.xlsx");
    workbook.write(file);
    file.close();
  }

  private void writeVersionsAndPricesSheet(Workbook workbook, List<Brand> brands) throws IOException {
    HashSet<String> versionsAndPrices;
    Sheet sheet;
    int index;

    versionsAndPrices = this.getVersionsAndPrices(brands);
    sheet = workbook.createSheet("Versões e preços");
    this.createVersionsAndPricesHeader(versionsAndPrices, sheet);
    index = 1;

    for (Brand brand : brands) {
      for (Model model : brand.getModels()) {
        for (Year year : model.getYears()) {
          for (Version version : year.getVersions()) {
            this.createVersionsAndPricesRow(versionsAndPrices, sheet, version, index);
            index++;
          }
        }
      }
    }
  }

  private void writeInformationSheet(Workbook workbook, List<Brand> brands) throws IOException {
    HashSet<String> informations;
    Sheet sheet;
    int index;

    informations = this.getInformations(brands);
    sheet = workbook.createSheet("Informações técnicas");
    this.createInformationHeader(informations, sheet);
    index = 1;

    for (Brand brand : brands) {
      for (Model model : brand.getModels()) {
        for (Year year : model.getYears()) {
          for (Version version : year.getVersions()) {
            this.createInformationRow(informations, sheet, version, index);
            index++;
          }
        }
      }
    }
  }

  private void writeToOpinionSheet(Workbook workbook, List<Brand> brands) throws IOException {
    HashSet<String> opinions;

    Sheet sheet;
    int index;

    opinions = this.getOpinions(brands);
    sheet = workbook.createSheet("Opiniões");
    this.createOpinionHeader(opinions, sheet);
    index = 1;

    for (Brand brand : brands) {
      for (Model model : brand.getModels()) {
        for (Year year : model.getYears()) {
          this.createOpinionRow(opinions, sheet, year, index);
          index++;
        }
      }
    }
  }

  private void createVersionsAndPricesHeader(HashSet<String> versionsAndPrices, Sheet sheet) {
    ArrayList<String> labels = new ArrayList<>();

    labels.add("Marca");
    labels.add("Modelo");
    labels.add("Ano");
    labels.add("Preço");
    labels.addAll(versionsAndPrices);
    labels.add("Data da Consulta");

    this.createHeader(labels, sheet);
  }

  private void createInformationHeader(HashSet<String> informations, Sheet sheet) {
    ArrayList<String> labels = new ArrayList<>();

    labels.add("Marca");
    labels.add("Modelo");
    labels.add("Ano");
    labels.add("Preço");
    informations.forEach(System.out::println);
    labels.addAll(informations);
    labels.add("Data da Consulta");

    this.createHeader(labels, sheet);
  }

  private void createOpinionHeader(HashSet<String> opinions, Sheet sheet) {
    ArrayList<String> labels = new ArrayList<>();

    labels.add("Marca");
    labels.add("Modelo");
    labels.add("Ano");
    labels.addAll(opinions);
    labels.add("Data da Consulta");

    this.createHeader(labels, sheet);
  }

  private void createVersionsAndPricesRow(HashSet<String> versionsAndPrices, Sheet sheet, Version version, int rowIndex) {
    ArrayList<String> cells = new ArrayList();

    cells.add(version.getYear().getModel().getBrand().getName());
    cells.add(version.getName());
    cells.add(String.valueOf(version.getYear().getYear()));
    cells.add(version.getPrice());

    versionsAndPrices.forEach((versionAndPrice) -> {
      cells.add(version.getAttributes().get(versionAndPrice));
    });

    cells.add(version.getRead().format(this.dateTimeFormat));

    this.createRow(cells, sheet, rowIndex);
  }

  private void createInformationRow(HashSet<String> informations, Sheet sheet, Version version, int rowIndex) {
    ArrayList<String> cells = new ArrayList();

    cells.add(version.getYear().getModel().getBrand().getName());
    cells.add(version.getName());
    cells.add(String.valueOf(version.getYear().getYear()));
    cells.add(version.getPrice());

    informations.forEach((information) -> {
      cells.add(version.getInformations().get(information));
    });

    cells.add(version.getRead().format(this.dateTimeFormat));

    this.createRow(cells, sheet, rowIndex);
  }

  private void createOpinionRow(HashSet<String> opinions, Sheet sheet, Year year, int rowIndex) {
    ArrayList<String> cells = new ArrayList();

    cells.add(year.getModel().getBrand().getName());
    cells.add(year.getModel().getName());
    cells.add(String.valueOf(year.getYear()));

    opinions.forEach((opinion) -> {
      cells.add(year.getOpinions().get(opinion));
    });

    cells.add(year.getRead().format(this.dateTimeFormat));

    this.createRow(cells, sheet, rowIndex);
  }

  private HashSet<String> getVersionsAndPrices(List<Brand> brands) {
    HashSet<String> attributes = new HashSet<>();

    brands.forEach((brand) -> {
      brand.getModels().forEach((model) -> {
        model.getYears().forEach((year) -> {
          year.getVersions().forEach((version) -> {
            for (String attribute : version.getAttributes().keySet()) {
              if (!attributes.contains(attribute)) {
                attributes.add(attribute);
              }
            }
          });
        });
      });
    });

    return attributes;
  }

  private HashSet<String> getInformations(List<Brand> brands) {
    HashSet<String> informations = new HashSet<>();

    brands.forEach((brand) -> {
      brand.getModels().forEach((model) -> {
        model.getYears().forEach((year) -> {
          year.getVersions().forEach((version) -> {
            for (String information : version.getInformations().keySet()) {
              if (!informations.contains(information)) {
                informations.add(information);
              }
            }
          });
        });
      });
    });

    return informations;
  }

  private HashSet<String> getOpinions(List<Brand> brands) {
    HashSet<String> opinions = new HashSet<>();

    brands.forEach((brand) -> {
      brand.getModels().forEach((model) -> {
        model.getYears().forEach((year) -> {
          for (String opinion : year.getOpinions().keySet()) {
            if (!opinions.contains(opinion)) {
              opinions.add(opinion);
            }
          }
        });
      });
    });

    return opinions;
  }

  private void createHeader(ArrayList<String> labels, Sheet sheet) {
    CellStyle style;
    Row row;
    int index;

    row = sheet.createRow(0);

    for (index = 0; index < labels.size(); index++) {
      row.createCell(index).setCellValue(labels.get(index));
    }

    // Apply bold style    
    style = sheet.getWorkbook().createCellStyle();
    Font font = sheet.getWorkbook().createFont();
    font.setBold(true);
    style.setFont(font);

    for (index = 0; index < row.getLastCellNum(); index++) {
      row.getCell(index).setCellStyle(style);
    }
  }

  private void createRow(ArrayList<String> cells, Sheet sheet, int rowIndex) {
    Row row;

    row = sheet.createRow(rowIndex);

    for (int index = 0; index < cells.size(); index++) {
      row.createCell(index).setCellValue(cells.get(index));
    }
  }
}
