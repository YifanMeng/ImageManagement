package view;

import controller.ButtonActions;
import controller.TreeManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.FileManager;
import model.LogManager;
import model.TagManager;

import java.io.File;

/** The User interface. */
public class UserInterface extends Application {
  private static BorderPane bp = new BorderPane();
  private static String home = System.getProperty("user.home");
  private static TagManager tagManager = new TagManager(home + "/tags.txt");
  private static LogManager logManager = new LogManager(home + "/log.txt");

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Return a HBox for the top section of the app.
   *
   * @param abs_path the text field that displays absolute path
   * @return the HBox
   */
  private static HBox topBar(TextField abs_path) {
    Button load = new Button("Load Folder");
    load.setOnAction(e -> ButtonActions.loadAction());

    Button open_log = new Button("View Log");
    open_log.setOnMouseClicked(event -> ButtonActions.viewLog());

    Button filter_by_tag = new Button("Filter Images By Tag");
    filter_by_tag.setOnMouseClicked(event -> ButtonActions.filterImgByTags());

    HBox top = new HBox();
    top.setSpacing(10);
    top.getChildren().addAll(load, open_log, filter_by_tag, abs_path);
    return top;
  }

  /**
   * Return a ImageView when a image in the file tree is selected.
   *
   * @param path Path of image.
   * @return the image view
   */
  public static ImageView imgView(String path) {
    String img_path = "file:" + path;
    Image image = new Image(img_path);

    ImageView img = new ImageView();
    img.setImage(image);
    img.setFitWidth(600);
    img.setPreserveRatio(true);
    img.setSmooth(true);
    img.setCache(true);

    return img;
  }

  /**
   * Return a flow pane with tags. @param item the item
   *
   * @param item the item
   * @return the flow pane
   */
  public static FlowPane tagView(TreeItem<File> item) {
    FlowPane flow = new FlowPane();
    flow.setPadding(new Insets(5, 0, 5, 0));
    flow.setVgap(4);
    flow.setHgap(2);
    flow.setPrefWrapLength(200);

    String image_path = "";

    /*Add a tag to existing ones.*/
    Button add_tag = new Button("Add Tag");
    add_tag.setOnMouseClicked(event -> ButtonActions.addTagAction(flow, item));
    flow.getChildren().add(add_tag);

    /*If an image is selected.*/
    if (item.getValue() != null) {
      image_path = item.getValue().toString(); // initial path when flow pane is generated

      /* Add new buttons to flow pane. */

      /* View history of a file and revert to previous name.*/
      Button view_history = new Button("View History");
      view_history.setOnMouseClicked(event -> ButtonActions.viewHistoryAction(item));

      /*Open folder containing the image.*/
      Button view_folder = new Button("View Folder");
      view_folder.setOnMouseClicked(event -> ButtonActions.viewFolderAction(item));

      /*Move file to another directory*/
      Button move_file = new Button("Move Picture");
      move_file.setOnMouseClicked(event -> ButtonActions.moveFileAction(item));

      flow.getChildren().addAll(view_history, view_folder, move_file);
    }

    Text title = new Text("Tags: ");
    flow.getChildren().add(title);
    /* Add all available tags to the flow pane. */
    for (String tag : tagManager.getInfo()) {
      ToggleButton tb = ButtonActions.customTB(tag, item, flow);

      /* highlight existing tags of an image */
      if (!image_path.equals("")) {
        FileManager fm = new FileManager(image_path);
        if (fm.getImageTags().contains(tag)) {
          tb.setSelected(true);
        }
      }

      flow.getChildren().add(tb);
    }

    return flow;
  }

  /**
   * Refresh tag view after tag changes, e.g. renaming a file
   *
   * @param item the tree node with the image
   */
  public static void refreshTagView(TreeItem<File> item) {
    bp.setRight(tagView(item));
  }

  @Override
  public void start(Stage stage) {
    /*Text field that shows absolute path.*/
    TextField abs_path = new TextField();
    abs_path.setPrefWidth(750);
    abs_path.setEditable(false);

    /*Manage tree functions*/
    TreeManager treeManager = new TreeManager(bp, abs_path);
    TreeView<File> tree = TreeManager.getTree();

    ButtonActions buttonActions = new ButtonActions(tagManager, treeManager, logManager, stage);

    bp.setTop(topBar(abs_path));
    bp.setLeft(tree);
    bp.setCenter(imgView(""));
    bp.setRight(tagView(new TreeItem<>()));

    stage.setScene(new Scene(bp, 1100, 600));
    stage.setTitle("Photo Tag Manager");
    stage.show();
  }

  /** Save tags and history to file on app close. */
  @Override
  public void stop() {
    tagManager.writeInfo();
    logManager.writeInfo();
  }
}
