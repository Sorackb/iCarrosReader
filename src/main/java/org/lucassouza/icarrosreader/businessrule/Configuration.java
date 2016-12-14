package org.lucassouza.icarrosreader.businessrule;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lucassouza.tools.PropertyTool;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class Configuration {

  private static PropertyTool iniFile;

  public static void readFile() {
    String systemPath;
    File system;

    if (iniFile == null) {
      iniFile = new PropertyTool();
      systemPath = Configuration.class.getProtectionDomain().getCodeSource()
              .getLocation().getPath();
      system = new File(systemPath);

      try {
        if (system.getParent().contains("target")) {
          iniFile.readPropertyFile("C:/iCarrosReader/config.ini");
        } else {
          iniFile.readPropertyFile(system.getParent() + "/config.ini");
        }
      } catch (IOException ex) {
        Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static PropertyTool getIni() {
    if (iniFile == null) {
      readFile();
    }

    return iniFile;
  }
}
