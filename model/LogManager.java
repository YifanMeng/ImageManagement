package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Manage logs recorded in a txt file called log.txt in home directory. Inherits from
 * model.InfoManager
 */
public class LogManager extends InfoManager {

  /**
   * Instantiates a new Log manager. Reads all the log recorded in txt file into arraylist
   * infos. @param logPath the log path
   *
   * @param logPath the log path
   */
  public LogManager(String logPath) {
    super(logPath);
  }

  /**
   * Get current date & time and formats them.
   *
   * @return formatted date and time
   */
  private static String getCurrentDate() {
    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return simpleDateFormat.format(date);
  }

  public void addInfo() {}

  /**
   * Add a new change into log.
   *
   * @param oriPath original path of the image.
   * @param newPath newly changed path of the image.
   */
  void addInfo(String oriPath, String newPath) {
    String oriName = StringManipulator.convertPathToName(oriPath);
    String newName = StringManipulator.convertPathToName(oriPath);
    infos.add(0, oriName + "--->" + newName + " Changed at: " + getCurrentDate());
    System.out.println(infos);
  }

  /**
   * Return the array list that records log.
   *
   * @return array list of log
   */
  public ArrayList<String> getInfo() {
    return infos;
  }
}
