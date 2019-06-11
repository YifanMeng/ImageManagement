package model;

/** The type String manipulator. */
class StringManipulator {

  /**
   * Convert path to name string.
   *
   * @param imagePath The image path.
   * @return Name of image.
   */
  static String convertPathToName(String imagePath) {
    return imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.lastIndexOf("."));
  }

  /**
   * Change a image's name with given path into a new name.
   *
   * @param imagePath The image path.
   * @param newName the new name to be changed into.
   * @return new path of the image.
   */
  static String changePathName(String imagePath, String newName) {
    return imagePath.substring(0, imagePath.lastIndexOf("/") + 1)
        + newName
        + imagePath.substring(imagePath.lastIndexOf("."));
  }

  /**
   * Get the first tag of a given image name.
   *
   * @param imageName The image's name.
   * @return first tag if there is any tag else an empty string.
   */
  static String getFirstTag(String imageName) {
    if (imageName.contains(" @")) {
      int start = imageName.indexOf(" @");
      if (imageName.indexOf(" @", start + 2) != -1) {
        return imageName.substring(start + 2, imageName.indexOf(" @", start + 2));
      } else {
        return imageName.substring(start + 2);
      }
    } else {
      return "";
    }
  }
}
