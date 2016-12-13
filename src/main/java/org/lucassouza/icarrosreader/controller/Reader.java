package org.lucassouza.icarrosreader.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.lucassouza.icarrosreader.businessrule.Catalog;
import org.lucassouza.icarrosreader.model.Brand;
import org.lucassouza.icarrosreader.model.Configuration;
import org.lucassouza.icarrosreader.model.Model;
import org.lucassouza.icarrosreader.businessrule.ModelCSV;
import org.lucassouza.icarrosreader.type.ResourceType;
import org.lucassouza.tools.Hardware4Win;

/**
 *
 * @author Lucas Souza [sorack@gmail.com]
 */
public class Reader extends Thread {

  @Override
  public void run() {
    Catalog catalog;
    ModelCSV modelCSV;
    List<Brand> brands;
    MessageDigest digest;
    String serialNumber;
    String md5;

    try {
      modelCSV = new ModelCSV();
      catalog = new Catalog();

      serialNumber = Hardware4Win.getSerialNumber();
      digest = MessageDigest.getInstance("MD5");
      digest.update(serialNumber.getBytes(), 0, serialNumber.length());
      md5 = new BigInteger(1, digest.digest()).toString(16);

      if (!md5.equals(Configuration.getIni().getProperty("serial", "").trim())) {
        throw new Exception("Permissão de uso negada. Contate o fornecedor para desbloquear o uso.\nsorackb@gmail.com");
      }

      Comunicator.getInstance().informAmount(ResourceType.STEP, 2);
      brands = catalog.readBrands();
      Comunicator.getInstance().informIncrement(ResourceType.STEP);
      modelCSV.saveToFiles(brands);
      Comunicator.getInstance().informIncrement(ResourceType.STEP);
      Comunicator.getInstance().finish();
    } catch (Exception ex) {
      Comunicator.getInstance().showError(ex.getMessage());
    }
  }
}
