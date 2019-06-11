package controller;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import model.FileManager;
import view.UserInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/** The Tree manager. */
public class TreeManager {
  private static TreeView<File> treeView = new TreeView<>();

  /**
   * Instantiates a new Tree manager.
   *
   * @param bp the bp
   * @param abs_path the abs path
   */
  public TreeManager(BorderPane bp, TextField abs_path) {
    /*
     * Change the displayed name of tree nodes
     * https://stackoverflow.com/questions/44210453/how-to-display-only-the-filename-in-a-javafx-treeview
     */
    treeView.setCellFactory(
        new Callback<TreeView<File>, TreeCell<File>>() {

          public TreeCell<File> call(TreeView<File> tv) {
            return new TreeCell<File>() {

              @Override
              protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                setText((empty || item == null) ? "" : item.getName());
              }
            };
          }
        });

    /* Display image when a tree node is double clicked
     https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/ImageView.html
    */
    treeView.setOnMouseClicked(
        mouseEvent -> {
          if (mouseEvent.getClickCount() == 2) {
            TreeItem<File> item = treeView.getSelectionModel().getSelectedItem();
            File img = item.getValue();
            String image_path = img.toString();
            abs_path.setText(image_path);

            bp.setCenter(UserInterface.imgView(image_path));
            bp.setRight(UserInterface.tagView(item));
          }
        });
  }

  /**
   * Gets tree.
   *
   * @return the tree
   */
  public static TreeView<File> getTree() {
    return treeView;
  }

  /** Reload tree of files in same directory. */
  public static void reloadTree() {
    treeView.setRoot(getNodesForDirectory(treeView.getRoot().getValue()));
    treeView.getRoot().setExpanded(true);
  }

  /**
   * Reload tree after update actions are performed.
   *
   * @param path the path
   */
  public static void reloadTree(File path) {
    treeView.setRoot(getNodesForDirectory(path));
    treeView.getRoot().setExpanded(true);
  }

  /**
   * Filter files in tree and reload it.
   *
   * @param tags the tags to be filtered
   */
  public static void reloadTreeFiltered(ArrayList<String> tags) {
    treeView.setRoot(getNodesForDirectoryFiltered(treeView.getRoot().getValue(), tags));
    treeView.getRoot().setExpanded(true);
  }

  /**
   * Build a tree of images from selected folder
   * https://stackoverflow.com/questions/35070310/javafx-representing-directories
   *
   * @param directory generate file tree from this directory
   * @return root of said directory and its internal files/folders
   */
  private static TreeItem<File> getNodesForDirectory(File directory) {
    TreeItem<File> root = new TreeItem<>(directory);
    File[] files = directory.listFiles();

    if (files != null) {
      for (File file : files) {
        String name = file.getName();
        if (file.isDirectory() && Objects.requireNonNull(file.listFiles()).length != 0) {
          root.getChildren().add(getNodesForDirectory(file));
        } else {
          if (name.endsWith("bmp")
              || name.endsWith("jpg")
              || name.endsWith("jpeg")
              || name.endsWith("gif")
              || name.endsWith("png")) {
            root.getChildren().add(new TreeItem<>(file));
          }
        }
      }
    } else {
      System.out.println("File error, please try again.");
    }

    return root;
  }

  private static TreeItem<File> getNodesForDirectoryFiltered(
      File directory, ArrayList<String> tags) {
    TreeItem<File> root = new TreeItem<>(directory);
    File[] files = directory.listFiles();

    if (files != null) {
      for (File file : files) {
        String name = file.getName();
        if (file.isDirectory() && Objects.requireNonNull(file.list()).length != 0) {
          TreeItem<File> nodes = getNodesForDirectoryFiltered(file, tags);
          /* Hide sub folder if no pictures that satisfy the condition are in the folder.*/
          if (nodes.getChildren().size() != 0) {
              root.getChildren().add(nodes);
          }
        } else {
          if (name.endsWith("bmp")
              || name.endsWith("jpg")
              || name.endsWith("jpeg")
              || name.endsWith("gif")
              || name.endsWith("png"))
            if (FileManager.containsTag(name, tags)) {
              root.getChildren().add(new TreeItem<>(file));
            }
        }
      }
    } else {
      System.out.println("File error, please try again.");
    }

    return root;
  }

  /**
   * Reload tree item to newest file name.
   *
   * @param ti item of tree (old image file)
   * @param file new image file
   */
  void updateTreeItem(TreeItem<File> ti, File file) {
    ti.setValue(null);
    ti.setValue(file);
  }
}
