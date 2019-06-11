package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/** The type File manager. */
public class FileManager {
  private String imagePath;

  /**
   * Instantiates a new File manager.
   *
   * @param imagePath the image path
   */
  public FileManager(String imagePath) {
    this.imagePath = imagePath;
  }

  /**
   * Takes in a directory of image and returns all the tags that the image have.
   *
   * @param imagePath a particular image that needs to get tag from.
   * @return an array of image tags in that folder.
   */
  static ArrayList<String> getImageTags(String imagePath) {
    ArrayList<String> tags = new ArrayList<>();
    String imageName = StringManipulator.convertPathToName(imagePath);
    String tag = StringManipulator.getFirstTag(imageName);
    while (!tag.equals("")) {
      tags.add(tag);
      imageName = imageName.substring(imageName.indexOf(" @") + 2);
      tag = StringManipulator.getFirstTag(imageName);
    }
    return tags;
  }

  /**
   * Check if a particular image contains a particular tag.
   *
   * @param imagePath the path of a particular image that needs to check.
   * @param tags a list of tags that needs to be found.
   * @return true if the image contains tag, else false.
   */
  public static boolean containsTag(String imagePath, ArrayList<String> tags) {
    ArrayList<String> imageTags = getImageTags(imagePath);
    for (String tag : tags) {
      if (!imageTags.contains(tag)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Move/rename image file from old path to new path.
   *
   * @param newPath The new path/name of image.
   * @return The image file.
   */
  public File moveImage(String newPath) {
    try {
      Files.move(Paths.get(imagePath), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
      this.imagePath = newPath;
    } catch (IOException e) {
      System.out.println("File IO error at model.FileManager moveImage.");
    }
    return new File(newPath);
  }

  public File moveImage(String newPath) {
      imagePath = newPath;
  }

  /**
   * Change image name.
   *
   * @param newName The new name
   * @param logManager the log manager
   * @return the file
   */
  public File changeImageName(String newName, LogManager logManager) {
    String newPath = StringManipulator.changePathName(imagePath, newName);
    addNameToHistory(newPath);
    logManager.addInfo(imagePath, newPath);
    return moveImage(newPath);
  }

  /**
   * Add a currently existing tag to image and change its name.
   *
   * @param tag the tag to add
   * @param logManager the LM instance.
   * @return the image file
   */
  public File addTagToImage(String tag, LogManager logManager) {
    String newPath =
        imagePath.substring(0, imagePath.lastIndexOf("."))
            + " @"
            + tag
            + imagePath.substring(imagePath.lastIndexOf("."));
    addNameToHistory(newPath);
    logManager.addInfo(imagePath, newPath);
    return moveImage(newPath);
  }

  /**
   * Delete tag of image file.
   *
   * @param tag the tag to add
   * @param logManager the LM instance.
   * @return the image file
   */
  public File deleteTagToImage(String tag, LogManager logManager) {
    // replace tag with an empty string
    String newPath = imagePath.replaceFirst(" @" + tag, "");
    addNameToHistory(newPath);
    logManager.addInfo(imagePath, newPath);
    return moveImage(newPath);
  }

  /**
   * Takes in a directory of image and returns all the tags that the image have.
   *
   * @return an array of image tags in that folder.
   */
  public ArrayList<String> getImageTags() {
    return getImageTags(imagePath);
  }

  /**
   * Gets history of this image.
   *
   * @return the array list of image history
   */
  // a list of lists of history names of each image.
  public ArrayList<String> getImageHistory() {
    String imageName = StringManipulator.convertPathToName(imagePath);
    Path path = Paths.get(imagePath.substring(0, imagePath.lastIndexOf("/") + 1) + "history.txt");
    ArrayList<String> imageHistory = new ArrayList<>();
    try (BufferedReader fileInput = Files.newBufferedReader(path)) {
      String line = fileInput.readLine();
      while (line != null) {
        if (line.equals(imageName)) {
          while (!line.equals("")) {
            imageHistory.add(line);
            line = fileInput.readLine();
          }
          return imageHistory;
        }
        line = fileInput.readLine();
      }
    } catch (IOException | NullPointerException e) {
      System.out.println("Get history not working.");
    }
    return imageHistory;
  }

  /**
   * Add name to history.txt.
   *
   * @param newPath the new log_path of history.txt
   */
  // add a file's name to history.
  private void addNameToHistory(String newPath) {
    String historyPath = imagePath.substring(0, imagePath.lastIndexOf("/") + 1) + "history.txt";
    File file = new File(historyPath);

    // create a history.txt if it doesn't already exist in this directory.
    if (!file.exists()) {
      FileWriter fileWriter;
      try {
        fileWriter = new FileWriter(file);
        fileWriter.close();
      } catch (Throwable e) {
        System.out.println("IOError in HM.addNameToHistory.");
      }
    }

    Path path = Paths.get(historyPath);
    ArrayList<String> imageHistory = new ArrayList<>();
    boolean flag = false;
    try (BufferedReader fileInput = Files.newBufferedReader(path)) {
      String line = fileInput.readLine();
      String newName = StringManipulator.convertPathToName(newPath);
      // Add the newName in front of the line that matches oldName.
      while (line != null) {
        if (line.equals(StringManipulator.convertPathToName(imagePath))) {
          if (!flag) {
            imageHistory.add(newName);
            flag = true;
          }
        }
        imageHistory.add(line);
        line = fileInput.readLine();
      }
      // If no oldName found, add it at the very back of History.
      if (!flag) {
        imageHistory.add(StringManipulator.convertPathToName(newPath));
        imageHistory.add(StringManipulator.convertPathToName(imagePath));
        imageHistory.add("");
      }
    } catch (IOException | NullPointerException e) {
      System.out.println("End of history.txt");
    }

    // Write history back into history.txt.
    File file1 = new File(historyPath);
    FileWriter fileWriter;
    try {
      fileWriter = new FileWriter(file1);
      for (String info : imageHistory) {
        fileWriter.write(info + "\n");
      }
      fileWriter.close();
    } catch (Throwable e) {
      System.out.println("End of imageHistory array at HistoryManager.");
    }
  }
}
