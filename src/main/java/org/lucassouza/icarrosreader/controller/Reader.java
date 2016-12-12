package org.lucassouza.icarrosreader.controller;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.lucassouza.icarrosreader.businessrule.ICarros;
import org.lucassouza.icarrosreader.model.Brand;
import org.lucassouza.icarrosreader.model.Configuration;
import org.lucassouza.icarrosreader.model.Model;
import org.lucassouza.icarrosreader.model.ModelCSV;
import org.lucassouza.icarrosreader.type.ResourceType;
import org.lucassouza.tools.Hardware4Win;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Reader extends Thread {

  @Override
  public void run() {
    ICarros iCarros;
    ModelCSV modelCSV;
    HashMap<Integer, Brand> brands;
    HashSet<String> attributes;
    List<Model> models;
    MessageDigest digest;
    String serialNumber;
    String md5;

    try {
      modelCSV = new ModelCSV();
      iCarros = new ICarros();

      serialNumber = Hardware4Win.getSerialNumber();
      digest = MessageDigest.getInstance("MD5");
      digest.update(serialNumber.getBytes(), 0, serialNumber.length());
      md5 = new BigInteger(1, digest.digest()).toString(16);
      
      if (!md5.equals(Configuration.getIni().getProperty("serial", "").trim())) {
        throw new Exception("Permissão de uso negada. Contate o fornecedor para desbloquear o uso.\nsorackb@gmail.com");
      }
      
      Comunicator.getInstance().informAmount(ResourceType.STEP, 5);
      brands = iCarros.readBrands();
      models = iCarros.readModels(brands);
      iCarros.readYears(brands);
      attributes = iCarros.readVersions(brands);
      modelCSV.saveToFile(attributes, models);
    } catch (Exception ex) {
      Comunicator.getInstance().showError(ex.getMessage());
    }
  }
}
