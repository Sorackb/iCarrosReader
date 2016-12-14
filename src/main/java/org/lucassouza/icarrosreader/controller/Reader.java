package org.lucassouza.icarrosreader.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lucassouza.icarrosreader.businessrule.Catalog;
import org.lucassouza.icarrosreader.model.Brand;
import org.lucassouza.icarrosreader.businessrule.ModelCSV;
import org.lucassouza.icarrosreader.businessrule.Start;
import org.lucassouza.icarrosreader.type.ResourceType;

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
    Start start;

    try {
      start = new Start();
      start.checkFolders();
      start.register();
      start.checkSerial();
      start.createShortcut();

      modelCSV = new ModelCSV();
      catalog = new Catalog();

      Comunicator.getInstance().informAmount(ResourceType.STEP, 2);
      brands = catalog.readBrands();
      Comunicator.getInstance().informIncrement(ResourceType.STEP);
      modelCSV.saveToFiles(brands);
      Comunicator.getInstance().informIncrement(ResourceType.STEP);
      Comunicator.getInstance().finish();
    } catch (Exception ex) {
      Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
      Comunicator.getInstance().showError(ex.getMessage());
    }
  }
}
