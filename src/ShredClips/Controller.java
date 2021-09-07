package ShredClips;

/**Class*/
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

/**Object*/
import org.jetbrains.annotations.NotNull;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**Event*/
import javafx.event.ActionEvent;

/**Scene*/
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorId;

    @FXML
    private TextField sourceField;

    @FXML
    private Label invalidSource;

    @FXML
    private TextField destField;

    @FXML
    private Label invalidDest;

    @FXML
    private ListView<String> sourceList;

    @FXML
    private ListView<String> destList;

    @FXML
    private ListView<LogItem> logList;

    /**
     * Initializes the DirectoryChooser object, gets the current stage,
     * retrieves the chosen user source path, and displays it in the
     * TextField
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  fx:id="dirButton" in directory.fxml
     *
     */
    @FXML
    private void sourcePathChoice(ActionEvent event) {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        Stage stage = (Stage) anchorId.getScene().getWindow();
        File file = dirChooser.showDialog(stage);
        if(file != null) {
            sourceField.setText(file.getAbsolutePath());
            sourceListValid();
            sourceFileDisplay(file);
            logAdd("Source Directory Change: " + file.getAbsolutePath(), "Source");
        }
    }

    /**
     * Detects change to the source directory TextField. Determines
     * if the entered path is a valid directory or not. If true,
     * updates fx:id="sourceList". If false, highlights the TextField red
     * and displays "invalid source directory" in fx:id="sourceList"
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the Enter button on their keyboard after
     *                  clicking fx:id="sourceField"
     *
     */
    @FXML
    private void sourceFieldChange(ActionEvent event) {
        File file = new File(sourceField.getCharacters().toString());
        if(file.isDirectory()) {
            sourceListValid();
            sourceFileDisplay(file);
            logAdd("Source Directory Change: " + file.getAbsolutePath(), "Source");
        } else {
            sourceListInvalid();
            logAdd("Source Directory Invalid: " + file.getAbsolutePath(), "Error");
        }
    }

    /**
     * Initializes the DirectoryChooser object, gets the current stage,
     * retrieves the chosen user destination path, and displays it in
     * the TextField
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  fx:id="dirButton" in directory.fxml
     *
     */
    @FXML
    private void destPathChoice(ActionEvent event) {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        Stage stage = (Stage) anchorId.getScene().getWindow();
        File file = dirChooser.showDialog(stage);
        if(file != null) {
            destField.setText(file.getAbsolutePath());
            destListValid();
            destFileDisplay(file);
            logAdd("Destination Directory Change: " + file.getAbsolutePath(), "Destination");
        }
    }

    /**
     * Detects change to the destination directory TextField. Determines
     * if the entered path is a valid directory or not. If true,
     * updates fx:id="destList". If false, highlights the TextField red
     * and displays "invalid destination directory" in fx:id="destList"
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the Enter button on their keyboard after
     *                  clicking fx:id="destField"
     *
     */
    @FXML
    private void destFieldChange(ActionEvent event) {
        File file = new File(destField.getCharacters().toString());
        if(file.isDirectory()) {
            destListValid();
            destFileDisplay(file);
            logAdd("Destination Directory Change: " + file.getAbsolutePath(), "Destination");
        } else {
            destListInvalid();
            logAdd("Destination Directory Invalid: " + file.getAbsolutePath(), "Error");
        }
    }

    /**
     *  Populates files held within the source directory utilizing the
     *  file object passed from sourcePathChoice()
     *
     * @param  file     File object passed from sourcePathChoice
     *                  function
     *
     */
    private void sourceFileDisplay(@NotNull File file) {
        sourceList.getItems().clear();
        System.out.println(Arrays.toString(file.list()));
        for (String fileName : file.list()) {
            sourceList.getItems().add(fileName);
        }
    }

    /**
     *  Tracks currently selected file in the source directory
     *  ListView
     *
     * @param  arg0     top-most node under cursor selected
     *
     */
    @FXML
    private void handleSourceMouseClick(javafx.scene.input.MouseEvent arg0) {
        destList.getSelectionModel().clearSelection();
        System.out.println("SOURCE MC: clicked on " + sourceList.getSelectionModel().getSelectedItem());
    }

    /**
     *  Populates files held within the destination directory utilizing the
     *  file object passed from destPathChoice()
     *
     * @param  file     File object passed from destPathChoice function,
     *                  utilized for ListView population
     *
     */
    private void destFileDisplay(@NotNull File file) {
        destList.getItems().clear();
        System.out.println(Arrays.toString(file.list()));
        for (String fileName : file.list()) {
            destList.getItems().add(fileName);
        }
    }

    /**
     *  Tracks currently selected file in the destination
     *  directory ListView
     *
     * @param  arg0     top-most node under cursor selected
     *
     */
    @FXML
    private void handleDestMouseClick(javafx.scene.input.MouseEvent arg0) {
        sourceList.getSelectionModel().clearSelection();
        System.out.println("DEST MC: clicked on " + destList.getSelectionModel().getSelectedItem());
    }

    /**
     * Detects ListView selected item, gets the path for both the file
     * being moved and the directory the file is being moved to and calls
     * the moveLogic helper function.
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="moveFile"
     *
     */
    @FXML
    private void moveFileHandle(ActionEvent event) {
        if(!sourceList.getSelectionModel().isEmpty()) {
            String from = sourceField.getCharacters().toString();
            String to = destField.getCharacters().toString();
            String file = sourceList.getSelectionModel().getSelectedItem();
            moveLogic(from, to, file);
        }
        if(!destList.getSelectionModel().isEmpty()) {
            String from = destField.getCharacters().toString();
            String to = sourceField.getCharacters().toString();
            String file = destList.getSelectionModel().getSelectedItem();
            moveLogic(from, to, file);
        }
    }

    /**
     * Helper function and error handling for moveFileHandle. Determines
     * if the source/destination path are valid and if the object
     * being moved is a file or a directory. Calls moveFile()
     * or throws error
     *
     * @param  fromPath     Directory the object is moving from
     * @param  toPath       Directory the object is moving to
     * @param  fileName     Name of the object
     *
     */
    private void moveLogic(String fromPath, String toPath, String fileName) {
        if(!toPath.isEmpty()) {
            File fromFile = new File(fromPath);
            File toFile = new File(toPath);
            if (toFile.isDirectory() && fromFile.isDirectory()) {
                Path from = FileSystems.getDefault().getPath(fromPath + "\\" + fileName);
                Path to = FileSystems.getDefault().getPath(toPath + "\\" + fileName);
                if (to.toFile().isFile()) {
                    String rename = fileRename(toPath, fileName);
                    to = FileSystems.getDefault().getPath(toPath + "\\" + rename);
                    Dialog dialog = dialogWindow("ShredClips Alert", "Renamed file to " + rename, "caution");
                    dialog.showAndWait();
                    moveFile(from, to, fileName);
                } else if (to.toFile().isDirectory()) {
                    String rename = directoryRename(toPath, fileName);
                    to = FileSystems.getDefault().getPath(toPath + "\\" + rename);
                    Dialog dialog = dialogWindow("ShredClips Alert", "Renamed directory to " + rename, "caution");
                    dialog.showAndWait();
                    moveFile(from, to, fileName);
                } else if (!from.equals(to)) {
                    moveFile(from, to, fileName);
                } else {
                    Dialog dialog = dialogWindow("ShredClips Alert", "Cannot move selected file to the same directory.", "caution");
                    dialog.showAndWait();
                }
            } else {
                refreshListViews();
            }
        } else {
            refreshListViews();
        }
    }

    /**
     * Helper function for moveFileHandle and moveLogic. Determines whether
     * file being moved is a file or a directory. Throws warning for user
     * moving a directory.
     *
     * @param  from     Path to the file getting moved
     * @param  to       Path to the directory the file will be moved to
     *
     */
    private void moveFile(Path from, Path to, String fileName) {
        if(from.toFile().isFile()) {
             try {
                Files.move(from, to);
                logAdd("Moved File: " + fileName + " To " + to.toFile().getAbsolutePath(), "Move");
                refreshListViews();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(from.toFile().isDirectory()) {
            Dialog dialog = dialogWindow("ShredClips Move Warning", "Are you sure you want to move this folder?", "warning");
            Optional<ButtonType> choice = dialog.showAndWait();
            if(choice.get() == ButtonType.YES) {
                new File (from.toString()).renameTo(new File(to.toString()));
                logAdd("Moved Directory: " + fileName + " To " + to.toFile().getAbsolutePath(), "Move");
                refreshListViews();
            }
        }
    }

    /**
     * Renames file with underscore and number appended to the
     * end of the original file
     *
     * @param  fileDirectory    directory the renamed file will
     *                          be moved to
     * @param  fileName         name of the current file
     *
     */
    private String fileRename(String fileDirectory, String fileName) {
        int index = fileName.lastIndexOf(".");
        String file = fileName.substring(0, index);
        String extension = fileName.substring(index+1);
        String rename;

        Path newpath = FileSystems.getDefault().getPath(fileDirectory + "\\" + fileName);
        Boolean exists = newpath.toFile().exists();
        int i = 1;

        while(exists) {
            rename = file + "_" + i + "." + extension;
            newpath = FileSystems.getDefault().getPath(fileDirectory + "\\" + rename);
            exists = newpath.toFile().exists();
            if(!exists) {
                return rename;
            }
            i++;
        }
        return null;
    }

    /**
     * Renames directory with underscore and number appended to the
     * end of the original directory
     *
     * @param  dirDirectory     directory the renamed directory will
     *                          be moved to
     * @param  dirName          name of the current directory
     *
     */
    private String directoryRename(String dirDirectory, String dirName) {
        String rename = dirName;
        Path newpath = FileSystems.getDefault().getPath(dirDirectory + "\\" + rename);
        Boolean exists = newpath.toFile().exists();
        int i = 1;

        while(exists) {
            rename = dirName + "_" + i;
            newpath = FileSystems.getDefault().getPath(dirDirectory + "\\" + rename);
            exists = newpath.toFile().exists();
            if(!exists) {
                return rename;
            }
            i++;
        }
        return null;
    }

    /**
     * Detects ListView selected item, gets the path for the file being
     * deleted and calls the deleteFile helper function.
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="deleteFile"
     *
     */
    @FXML
    private void deleteFileHandle(ActionEvent event) {
        if(!sourceList.getSelectionModel().isEmpty()) {
            String path = sourceField.getCharacters().toString();
            String file = sourceList.getSelectionModel().getSelectedItem();
            deleteLogic(path, file, "source");
        }
        if (!destList.getSelectionModel().isEmpty()) {
            Path p = FileSystems.getDefault().getPath(destField.getCharacters().toString()+"\\"+destList.getSelectionModel().getSelectedItem());
            String path = destField.getCharacters().toString();
            String file = destList.getSelectionModel().getSelectedItem();
            deleteLogic(path, file, "dest");
        }
    }

    /**
     * Helper function and error handling for deleteFileHandle. Determines
     * if the path is valid and if the object being deleted exists. Calls
     * deleteFile() or throws error
     *
     * @param  path         Directory the object is being deleted
     *                      from
     * @param  fileName     Name of the object
     * @param  type         Whether the object was selected in the
     *                      source ListView or destination ListView
     *
     */
    private void deleteLogic(String path, String fileName, String type) {
        if (!path.isEmpty()) {
            File delFile = new File(path);
            if (delFile.isDirectory()) {
                Path delPath = FileSystems.getDefault().getPath(path + "\\" + fileName);
                if (delPath.toFile().isFile() || delPath.toFile().isDirectory()) {
                    deleteFile(delPath, fileName);
                }
            } else {
                refreshListViews();
            }
        } else {
            refreshListViews();
        }
    }

    /**
     * Helper function for deleteFileHandle. Throws warning to
     * ensure user wants to delete the selected file or directory.
     * Depending on OS is able to move file or directory to the
     * recycling bin.
     *
     * @param  p            Path to the file getting deleted
     * @param  fileName     Name of the object
     *
     */
    private void deleteFile(Path p, String fileName) {
        if (p.toFile().isFile()) {
            Dialog dialog = dialogWindow("ShredClips Delete Warning", "Are you sure you want to delete this file?", "warning");

            Optional<ButtonType> choice = dialog.showAndWait();
            if (choice.get() == ButtonType.YES) {
                try {
                    Desktop.getDesktop().moveToTrash(p.toFile());
                    logAdd("Moved File: " + fileName + " To Recycle Bin", "Delete");
                } catch (Exception e) {
                    p.toFile().delete();
                    logAdd("Deleted File: " + fileName, "Delete");
                    e.printStackTrace();
                }
                refreshListViews();
            }
        }
        if (p.toFile().isDirectory()) {
            Dialog dialog = dialogWindow("ShredClips Delete Warning", "Are you sure you want to delete this folder?", "warning");

            Optional<ButtonType> choice = dialog.showAndWait();
            if (choice.get() == ButtonType.YES){
                try {
                    Desktop.getDesktop().moveToTrash(p.toFile());
                    logAdd("Moved Directory: " + fileName + " To Recycle Bin", "Delete");
                } catch (Exception e) {
                    logAdd("Could not safely delete the folder: " + fileName, "Error");
                    e.printStackTrace();
                }
                refreshListViews();
            }
        }
    }

    /**
     * Calls FXMLLoader to generate a contentless dialog box. Determines
     * whether caution or warning is necessary based on choice parameter
     *
     * @param  title        Title of dialog window
     * @param  content      Text displayed within dialog window
     * @param  choice       Determines use of caution or warning dialog
     *
     */
    private Dialog dialogWindow(String title, String content, String choice) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            switch (choice){
                case "caution":
                    fxmlLoader.setLocation(getClass().getResource("caution.fxml"));
                    break;
                case "warning":
                    fxmlLoader.setLocation(getClass().getResource("warning.fxml"));
                    break;
            }
            DialogPane dialogPane = fxmlLoader.load();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(title);
            dialog.setContentText(content);
            dialog.setResizable(false);
            return dialog;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Clears populated ListViews and repopulates with files in
     * current fx:id="sourceField" and fx:id="destField"
     * directories. Determines if directory paths are an
     * existing directory or not, if not throws invalid error.
     *
     */
    @FXML
    private void refreshListViews() {
        if (!sourceList.getItems().isEmpty()) {
            File sFile = new File(sourceField.getCharacters().toString());
            if (sFile.isDirectory()){
                sourceList.getItems().clear();
                sourceFileDisplay(sFile);
            } else {
                sourceListInvalid();
                logAdd("Source Directory Invalid: " + sFile.getAbsolutePath(), "Error");
            }
        }

        if (!destList.getItems().isEmpty()) {
            File dFile = new File(destField.getCharacters().toString());
            if (dFile.isDirectory()){
                destList.getItems().clear();
                destFileDisplay(dFile);
            } else {
                destListInvalid();
                logAdd("Destination Directory Invalid: " + dFile.getAbsolutePath(), "Error");
            }
        }
    }

    /**
     *  Tracks currently selected file in the source directory
     *  ListView
     *
     * @param  arg0     top-most node under cursor selected
     *
     */
    @FXML
    private void handleLogMouseClick(javafx.scene.input.MouseEvent arg0) {
        System.out.println("LOG: clicked on " + logList.getSelectionModel().getSelectedItem());
    }

    /**
     *  Adds new LogItem object to fx:id="logList", subsequently utilizing
     *  the overridden updateItem  function for cell styling
     *
     * @param  text     Text displayed in the log cell
     * @param  choice   Determines whether action is a source directory
     *                  change, destination directory change, move,
     *                  delete, or error
     *
     */
    @FXML
    private void logAdd(String text, String choice) {
        logList.getItems().add(new LogItem(text, choice));
    }

    /**
     *  Displays red highlighting CSS around the TextField
     *  fx:id="sourceField" and shows fx:id="invalidSource" error
     *  message
     *
     */
    @FXML
    private void sourceListInvalid() {
        sourceField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
        invalidSource.setVisible(true);
        sourceList.getItems().clear();
    }

    /**
     *  Displays red highlighting CSS around the TextField
     *  fx:id="destField" and shows fx:id="invalidDest" error
     *  message
     *
     */
    @FXML
    private void destListInvalid() {
        destField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
        invalidDest.setVisible(true);
        destList.getItems().clear();
    }

    /**
     * Clears all CSS styling from fx:id="sourceField" and hides
     * fx:id="invalidSource" error message
     *
     */
    @FXML
    private void sourceListValid() {
        sourceField.setStyle(null);
        invalidSource.setVisible(false);
    }

    /**
     * Clears all CSS styling from fx:id="destField" and hides
     * fx:id="invalidDest" error message
     *
     */
    @FXML
    private void destListValid() {
        destField.setStyle(null);
        invalidDest.setVisible(false);
    }

    /**
     *  Called to initialize the controller after the root element
     *  has been completely processed.
     *
     *  Overrides the fx:id="logList" updateItem function to determine
     *  cell styling upon insertion.
     *
     * @param  url location used to resolve relative paths for the
     *             root paths for the root object, or null if the
     *             location is not known
     * @param  rb  the resources used to localize the root object,
     *             or null if the root object was not localized.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logList.setCellFactory(param -> new ListCell<LogItem>() {
            @Override
            protected void updateItem(LogItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getText() == null) {
                    setText(null);
                } else {
                    if (item.getChoice().equals("Source") || item.getChoice().equals("Destination")) {
                        setText(item.getText());
                        setStyle("-fx-control-inner-background: green ; -fx-focus-color: green ;");
                    } else if (item.getChoice().equals("Move")) {
                        setText(item.getText());
                        setStyle("-fx-control-inner-background: blue; -fx-focus-color: blue ;");
                    } else if (item.getChoice().equals("Delete") || item.getChoice().equals("Error")) {
                        setText(item.getText());
                        setStyle("-fx-control-inner-background: red; -fx-focus-color: red ;");
                    }
                }
            }
        });
    }
}

class LogItem {
    private final StringProperty text;
    private final StringProperty choice;

    public LogItem(String text, String choice) {
        this.text = new SimpleStringProperty(text);
        this.choice = new SimpleStringProperty(choice);
    }

    public String getText() {
        return text.get();
    }

    public String getChoice() {
        return choice.get();
    }

}