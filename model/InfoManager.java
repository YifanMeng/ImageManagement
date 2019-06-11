package model;

import java.io.*;
import java.util.ArrayList;

/** Manage informations recorded in a txt file in home directory. */
abstract class InfoManager {
  /** The Infos. */
  ArrayList<String> infos = new ArrayList<>();

  private String path;

  /**
   * Instantiates a new Info manager. Reads all the information recorded in txt file into arraylist
   * infos.
   *
   * @param path the path of txt file that records info
   */
  InfoManager(String path) {
    this.path = path;
    File file = new File(this.path);
    if (file.exists() && !file.isDirectory()) {
      try (BufferedReader fileInput = new BufferedReader(new FileReader(file))) {
        String line = fileInput.readLine();
        while (line != null) {
          infos.add(line);
          line = fileInput.readLine();
        }
      } catch (IOException e) {
        System.out.print("Error reading .txt");
      }
    } else {
      System.out.println("Not stored yet.");
    }
  }

  /** Add a new piece of information into arraylist. */
  abstract void addInfo();

  /**
   * Return the arraylist that records informations.
   *
   * @return the info
   */
  abstract ArrayList<String> getInfo();

  /** Writes informations from arraylist back into txt file. */
  public void writeInfo() {
    FileWriter fileWriter;
    File file = new File(path);
    try {
      fileWriter = new FileWriter(file);
      for (String info : infos) {
        fileWriter.write(info + "\n");
      }
      fileWriter.close();
    } catch (Throwable e) {
      System.out.println("Info are not successfully saved.");
    }
  }
}
