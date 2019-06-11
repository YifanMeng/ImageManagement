package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.FileManager;
import model.LogManager;
import model.TagManager;
import view.UserInterface;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/** Actions when buttons are clicked. */
public class  ButtonActions {
  private static Stage stage;
  private static TagManager tagManager;
  private static TreeManager treeManager;
  private static LogManager logManager;

  /**
   * Initiate controller.ButtonActions and its static variables.
   *
   * @param tagManager model.TagManager
   * @param treeManager tree manager
   * @param logManager the log manager
   * @param stage the stage
   */
  public ButtonActions(
      TagManager tagManager, TreeManager treeManager, LogManager logManager, Stage stage) {
    ButtonActions.tagManager = tagManager;
    ButtonActions.treeManager = treeManager;
    ButtonActions.logManager = logManager;
    ButtonActions.stage = stage;
  }

  /**
   * Generate custom ToggleButton that acts as a tag for use in this project. Deletes tag from tag
   * set when double clicked. Removes/adds tag from image if clicked once.
   *
   * @param name name of button
   * @param item the image file
   * @param flow tags are added to this FlowPane
   * @return a new toggle button
   */
  public static ToggleButton customTB(String name, TreeItem<File> item, FlowPane flow) {
    ToggleButton toggleButton = new ToggleButton(name);

    /* Add tag to image when selected, remove it otherwise. */
    toggleButton.setOnAction(
        event -> {
          try {
            File file;
            String newest_path = item.getValue().toString();
            FileManager fm = new FileManager(newest_path); // newest path of file item
            String t = toggleButton.getText();

            if (toggleButton.isSelected()) {
              file = fm.addTagToImage(t, logManager);
            } else {
              file = fm.deleteTagToImage(t, logManager);
            }

            treeManager.updateTreeItem(item, file);

          } catch (NullPointerException e) {
            System.out.println("No images loaded yet.");
          }
        });

    /*Double click to delete tag from tag set.*/
    toggleButton.setOnMouseClicked(
        mouseEvent -> {
          if (mouseEvent.getClickCount() == 2) {
            Alert alert =
                genConfAlert("Delete Tag", "Are you sure that you want to delete this tag?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
              try {
                new FileManager(item.getValue().toString()).deleteTagToImage(name, logManager);
              } catch (NullPointerException e) {
                System.out.println("No image loaded yet.");
              }
              tagManager.deleteTag(name);
              flow.getChildren().remove(toggleButton);
            }
          }
        });
    return toggleButton;
  }

  /** Generates a new log dialog which lists all changes */
  public static void viewLog() {
    ObservableList<String> lines = FXCollections.observableArrayList(logManager.getInfo());
    ListView<String> listView = new ListView<>(lines);

    final Stage dialog = new Stage();
    dialog.initOwner(stage);
    Scene dialogScene = new Scene(listView);
    dialog.setScene(dialogScene);
    dialog.setWidth(700);
    dialog.setTitle("View Log");
    dialog.show();
  }

  /**
   * Add tag action.
   *
   * @param flow the flow pane where the tags are on
   * @param item the selected tree node
   */
  public static void addTagAction(FlowPane flow, TreeItem<File> item) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("New Tag");
    dialog.setHeaderText("Please input your new tag.");

    Optional<String> result = dialog.showAndWait();
    result.ifPresent(
        name -> {
          if (!tagManager.getInfo().contains(name)) {
            tagManager.addInfo(name);
            flow.getChildren().add(customTB(name, item, flow));
            showAlert("info", "", "You have successfully added a new tag.");
          } else {
            showAlert("error", "This tag already exists.", "");
          }
        });
  }

  /**
   * View folder action.
   *
   * @param item the selected tree node
   */
  public static void viewFolderAction(TreeItem<File> item) {
    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().open(new File(item.getValue().getParent()));
      } catch (IOException e) {
        System.out.println("Open folder failed. Filesystem IO error.");
      }
    }
  }

  /**
   * View history action.
   *
   * @param item the selected tree node
   */
  public static void viewHistoryAction(TreeItem<File> item) {
    FileManager fileManager = new FileManager(item.getValue().toString());
    ObservableList<String> h = FXCollections.observableArrayList(fileManager.getImageHistory());

    Alert alert =
        genConfAlert("Select Name", "Please select the name that you want to revert back to.");

    ListView<String> history = new ListView<>(h);
    history.setEditable(false);

    history.setMaxWidth(Double.MAX_VALUE);
    history.setMaxHeight(Double.MAX_VALUE);

    GridPane list = new GridPane();
    list.setMaxWidth(Double.MAX_VALUE);
    list.add(history, 0, 1);

    // Set expandable Exception into the dialog pane.
    alert.getDialogPane().setContent(list);

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
      File file = item.getValue();
      String s = history.getSelectionModel().getSelectedItem();
      System.out.println(s);
      FileManager fm = new FileManager(file.toString());
      file = fm.changeImageName(s, logManager);
      treeManager.updateTreeItem(item, file);
      UserInterface.refreshTagView(item);
    }
  }

  /**
   * Move file action.
   *
   * @param item the selected tree node
   */
  public static void moveFileAction(TreeItem<File> item) {
    try {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      directoryChooser.setInitialDirectory(item.getValue().getParentFile());
      File choice = directoryChooser.showDialog(stage);
      if (!choice.isDirectory()) {
        showAlert("error", "This is not a directory.", "");
      } else {
        String new_path = choice.toString() + "/" + item.getValue().getName();
        new FileManager(item.getValue().getPath()).moveImage(new_path);
        TreeManager.reloadTree();

        showAlert("info", "Move File Success", "Successfully moved file to " + new_path);
      }
    } catch (NullPointerException e) {
      System.out.println("User cancelled select folder action.");
    }
  }

  /** Load button action. */
  public static void loadAction() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    try {
      File choice = directoryChooser.showDialog(stage);
      if (!choice.isDirectory()) {
        showAlert("error", "This is not a directory.", "");
      } else {
        TreeManager.reloadTree(choice);
      }
    } catch (NullPointerException ex) {
      System.out.println("User cancelled select folder action.");
    }
  }

  /** Filter images by tags action. */
  public static void filterImgByTags() {
    try {
      TreeManager.getTree().getRoot().getValue();
    } catch (NullPointerException e) {
      showAlert("error", "You haven't selected a folder yet", "");
      return;
    }

    ArrayList<String> selected_tags = new ArrayList<>();
    FlowPane flow = new FlowPane();
    flow.setHgap(2);

    for (String tag : tagManager.getInfo()) {
      ToggleButton toggleButton = new ToggleButton(tag);
      toggleButton.setOnAction(
          event -> {
            if (toggleButton.isSelected()) {
              selected_tags.add(toggleButton.getText());
            } else {
              selected_tags.remove(toggleButton.getText());
            }
          });
      flow.getChildren().add(toggleButton);
    }

    Alert alert = genConfAlert("Select Tags", "Please select the tags for filtering.");
    alert.getDialogPane().setContent(flow);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      TreeManager.reloadTreeFiltered(selected_tags);
    }
  }

  /**
   * Show information or error alerts.
   *
   * @param type type of alert
   * @param title title of alert
   * @param content content of alert (not required in error)
   */
  private static void showAlert(String type, String title, String content) {
    Alert alert = new Alert(Alert.AlertType.NONE);

    if (type.equals("info")) {
      alert.setAlertType(Alert.AlertType.INFORMATION);
      alert.setTitle(title);
      alert.setContentText(content);
    }

    if (type.equals("error")) {
      alert.setAlertType(Alert.AlertType.ERROR);
      alert.setHeaderText(title);
    }
    alert.showAndWait();
  }

  /**
   * Generate confirmation alerts.
   *
   * @param title title of alert
   * @param header header of alert
   * @return generated configuration alert
   */
  private static Alert genConfAlert(String title, String header) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    return alert;
  }
}
