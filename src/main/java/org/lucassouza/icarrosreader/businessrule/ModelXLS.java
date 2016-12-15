package org.lucassouza.icarrosreader.businessrule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
    Workbook workbook;
    
    workbook = new HSSFWorkbook();
    this.saveToInformationFile(workbook, brands);
    this.saveToOpinionFile(workbook, brands);
  }

  private void saveToInformationFile(Workbook workbook, List<Brand> brands) throws IOException {
    FileOutputStream file;
    ArrayList<String> informations;
    Sheet sheet;
    int index;

    informations = this.getInformations(brands);
    sheet = workbook.createSheet("Informações");
    this.createInformationHeader(informations, sheet, 0);
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

    file = new FileOutputStream(this.path + "informacoes.xlsx");
    workbook.write(file);
    file.close();
  }

  private void saveToOpinionFile(Workbook workbook, List<Brand> brands) throws IOException {
    FileOutputStream file;
    ArrayList<String> opinions;
    
    Sheet sheet;
    int index;

    opinions = this.getOpinions(brands);
    sheet = workbook.createSheet("Opiniões");
    this.createOpinionHeader(opinions, sheet, 0);
    index = 1;

    for (Brand brand : brands) {
      for (Model model : brand.getModels()) {
        for (Year year : model.getYears()) {
          this.createOpinionRow(opinions, sheet, year, index);
          index++;
        }
      }
    }

    file = new FileOutputStream(this.path + "opinioes.xlsx");
    workbook.write(file);
    file.close();
  }

  private void createInformationHeader(ArrayList<String> informations, Sheet sheet, int rowIndex) {
    CellStyle style;
    Row row;
    int index;

    style = sheet.getWorkbook().createCellStyle();
    Font font = sheet.getWorkbook().createFont();
    font.setBold(true);
    style.setFont(font);

    row = sheet.createRow(rowIndex);
    row.createCell(0).setCellValue("Marca");
    row.createCell(1).setCellValue("Modelo");
    row.createCell(2).setCellValue("Ano");
    row.createCell(3).setCellValue("Preço");

    for (index = 0; index < informations.size(); index++) {
      row.createCell(index + 4).setCellValue(informations.get(index));
    }

    row.createCell(index + 4).setCellValue("Data da Consulta");

    for (index = 0; index < row.getLastCellNum(); index++) {
      row.getCell(index).setCellStyle(style);
    }
  }

  private void createInformationRow(ArrayList<String> informations, Sheet sheet, Version version, int rowIndex) {
    Row row;
    int index;

    row = sheet.createRow(rowIndex);
    row.createCell(0).setCellValue(version.getYear().getModel().getBrand().getName());
    row.createCell(1).setCellValue(version.getName());
    row.createCell(2).setCellValue(version.getYear().getYear());
    row.createCell(3).setCellValue(version.getPrice());

    for (index = 0; index < informations.size(); index++) {
      row.createCell(index + 4).setCellValue(version.getAttributes().get(informations.get(index)));
    }

    row.createCell(index + 4).setCellValue(version.getRead().format(this.dateTimeFormat));
  }

  private void createOpinionHeader(ArrayList<String> opinions, Sheet sheet, int rowIndex) {
    CellStyle style;
    Row row;
    int index;

    style = sheet.getWorkbook().createCellStyle();
    Font font = sheet.getWorkbook().createFont();
    font.setBold(true);
    style.setFont(font);

    row = sheet.createRow(rowIndex);
    row.createCell(0).setCellValue("Marca");
    row.createCell(1).setCellValue("Modelo");
    row.createCell(2).setCellValue("Ano");

    for (index = 0; index < opinions.size(); index++) {
      row.createCell(index + 3).setCellValue(opinions.get(index));
    }

    row.createCell(index + 3).setCellValue("Data da Consulta");

    for (index = 0; index < row.getLastCellNum(); index++) {
      row.getCell(index).setCellStyle(style);
    }
  }

  private void createOpinionRow(ArrayList<String> opinions, Sheet sheet, Year year, int rowIndex) {
    Row row;
    int index;

    row = sheet.createRow(rowIndex);
    row.createCell(0).setCellValue(year.getModel().getBrand().getName());
    row.createCell(1).setCellValue(year.getModel().getName());
    row.createCell(2).setCellValue(year.getYear());

    for (index = 0; index < opinions.size(); index++) {
      row.createCell(index + 3).setCellValue(year.getOpinions().get(opinions.get(index)));
    }

    row.createCell(index + 3).setCellValue(year.getRead().format(this.dateTimeFormat));
  }

  private ArrayList<String> getInformations(List<Brand> brands) {
    ArrayList<String> informations = new ArrayList<>();

    brands.forEach((brand) -> {
      brand.getModels().forEach((model) -> {
        model.getYears().forEach((year) -> {
          year.getVersions().forEach((version) -> {
            for (String attribute : version.getAttributes().keySet()) {
              if (!informations.contains(attribute)) {
                informations.add(attribute);
              }
            }
          });
        });
      });
    });

    return informations;
  }

  private ArrayList<String> getOpinions(List<Brand> brands) {
    ArrayList<String> opinions = new ArrayList<>();

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
}
