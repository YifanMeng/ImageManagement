package model;

import java.util.ArrayList;

/**
 * Manage currently existing tags and write to tags.txt when the app closes. Inherits from
 * model.InfoManager
 */
public class TagManager extends InfoManager {

  /**
   * Instantiates a new Tag manager. Reads all the information recorded in txt file into arraylist
   * infos. @param infoPath the info path
   *
   * @param infoPath the info path
   */
  public TagManager(String infoPath) {
    super(infoPath);
  }

  /** For inheritance purposes. */
  void addInfo() {}

  /**
   * Add a tag into tags.
   *
   * @param nameOfTag new tags that is being added.
   */
  public void addInfo(String nameOfTag) {
    for (Object info : infos) {
      if (info.equals(nameOfTag)) {
        break;
      }
    }
    infos.add(nameOfTag);
    System.out.println(nameOfTag);
  }

  /**
   * Gets all tags.
   *
   * @return the array list tags
   */
  public ArrayList<String> getInfo() {
    return infos;
  }

  /**
   * Delete tag from currently existing tags.
   *
   * @param nameOfTag the name of tag
   */
  public void deleteTag(String nameOfTag) {
    infos.remove(nameOfTag);
  }
}
