package org.lucassouza.icarrosreader.businessrule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import org.lucassouza.tools.CMD;
import org.lucassouza.tools.Hardware4Win;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class Start {

  private final CMD cmd;
  private final Path path;

  public Start() {
    this.cmd = new CMD();
    String systemPath;
    File system;

    systemPath = ModelCSV.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    system = new File(systemPath);

    if (system.getParent().contains("target")) {
      this.path = Paths.get("C:/iCarrosReader");
    } else {
      this.path = Paths.get(system.getParent());
    }
  }

  public void checkFolders() {
    Paths.get(path.toString() + "/files").toFile().mkdirs();
  }

  public void register() {
    this.cmd.execute("cd \"" + this.path.toString() + "\"", "java -jar Register.jar", "del register.jar");
  }

  public void checkSerial() throws Exception {
    MessageDigest digest;
    String serialNumber;
    String md5;

    serialNumber = Hardware4Win.getSerialNumber();
    digest = MessageDigest.getInstance("MD5");
    digest.update(serialNumber.getBytes(), 0, serialNumber.length());
    md5 = new BigInteger(1, digest.digest()).toString(16);

    if (!md5.equals(Configuration.getIni().getProperty("serial", "").trim())) {
      throw new Exception("Permissão de uso negada. Contate o fornecedor para desbloquear o uso.\nsorackb@gmail.com");
    }
  }

  public void createShortcut() throws IOException {
    StringBuilder output = new StringBuilder();
    InputStream input;
    BufferedReader reader;
    String line;
    String vbscript;
    Path destination;
    List<String> lines;

    // Lê o arquivo de modelo
    input = getClass().getResourceAsStream("/resource/CreateShortcut.vbs");
    reader = new BufferedReader(new InputStreamReader(input));

    while ((line = reader.readLine()) != null) {
      output.append(line).append("\n");
    }

    vbscript = output.toString();
    // Substitui as variáveis para gerar o arquivo de destino
    vbscript = vbscript.replace("{shortcutName}", "iCarros");
    vbscript = vbscript.replace("{pathToFolder}", this.path.toString());
    vbscript = vbscript.replace("{jarName}", "iCarrosReader.jar");
    vbscript = vbscript.replace("{iconName}", "iCarros.ico");

    destination = Paths.get(this.path.toString() + "/CreateShortcut.vbs");
    lines = Arrays.asList(vbscript.split(System.lineSeparator())); // Transforma as linhas em lista
    Files.write(destination, lines, UTF_8, CREATE_NEW); // Salva o arquivo com as informações corretas
    this.cmd.execute("cd \"" + this.path.toString() + "\"", "cscript CreateShortcut.vbs", "del CreateShortcut.vbs");
  }
}
