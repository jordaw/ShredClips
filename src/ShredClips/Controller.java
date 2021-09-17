package ShredClips;

/**Class*/
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

/**Object*/
import org.jetbrains.annotations.NotNull;
import java.awt.Desktop;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.util.Duration;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**Event*/
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

/**Scene*/
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private ListView<DirectoryItem> sourceList;

    @FXML
    private ListView<DirectoryItem> destList;

    @FXML
    private ListView<LogItem> logList;

    @FXML
    private MediaView mediaView;

    @FXML
    private StackPane mediaStack;

    @FXML
    private Slider mediaProgress;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView imageView;

    @FXML
    private StackPane imageStack;

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
        sourceList.getItems().add(new DirectoryItem("..", file.getPath()));
        for (String fileName : file.list()) {
            sourceList.getItems().add(new DirectoryItem(fileName, sourceField.getCharacters().toString()));
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
        try {
            mediaClick(sourceList.getSelectionModel().getSelectedItem().getFullPath().toFile());
        } catch (Exception e) {
            //false error on double click
        }
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
        destList.getItems().add(new DirectoryItem("..", file.getPath()));
        for (String fileName : file.list()) {
            destList.getItems().add(new DirectoryItem(fileName, destField.getCharacters().toString()));
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
        try {
            mediaClick(destList.getSelectionModel().getSelectedItem().getFullPath().toFile());
        } catch (Exception e) {
            //false error on double click
        }
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
        DirectoryItem file;
        if(!sourceList.getSelectionModel().isEmpty()) {
            file = sourceList.getSelectionModel().getSelectedItem();
            String to = destField.getCharacters().toString();
            moveLogic(file.getPath(), to, file.getFileName());
        }
        if(!destList.getSelectionModel().isEmpty()) {
            file = destList.getSelectionModel().getSelectedItem();
            String to = sourceField.getCharacters().toString();
            moveLogic(file.getPath(), to, file.getFileName());
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
                Path from = FileSystems.getDefault().getPath(filePath(fromPath, fileName));
                Path to = FileSystems.getDefault().getPath(filePath(toPath, fileName));
                if (to.toFile().isFile()) {
                    String rename = fileRename(toPath, fileName);
                    to = FileSystems.getDefault().getPath(filePath(toPath, rename));
                    Dialog dialog = dialogWindow("ShredClips Alert", "Renamed file to " + rename, "caution");
                    dialog.showAndWait();
                    moveFile(from, to, fileName);
                } else if (to.toFile().isDirectory()) {
                    String rename = directoryRename(toPath, fileName);
                    to = FileSystems.getDefault().getPath(filePath(toPath, rename));
                    Dialog dialog = dialogWindow("ShredClips Alert", "Renamed directory to " + rename, "caution");
                    dialog.showAndWait();
                    moveFile(from, to, fileName);
                } else if (!from.equals(to)) {
                    moveFile(from, to, fileName);
                } else {
                    logAdd("Cannot move " + fileName + " to the same directory.", "Error");
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
                invalidMedia();
                Files.move(from, to);
                logAdd("Moved File: " + fileName + " To " + to.toFile().getAbsolutePath(), "Move");
                refreshListViews();
            } catch (IOException e) {
                 logAdd("Unable to Move File: " + fileName, "Error");
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

        Path newpath = FileSystems.getDefault().getPath(filePath(fileDirectory, fileName));
        Boolean exists = newpath.toFile().exists();
        int i = 1;

        while(exists) {
            rename = file + "_" + i + "." + extension;
            newpath = FileSystems.getDefault().getPath(filePath(fileDirectory, rename));
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
        Path newpath = FileSystems.getDefault().getPath(filePath(dirDirectory, rename));
        Boolean exists = newpath.toFile().exists();
        int i = 1;

        while(exists) {
            rename = dirName + "_" + i;
            newpath = FileSystems.getDefault().getPath(filePath(dirDirectory, rename));
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
            DirectoryItem file = sourceList.getSelectionModel().getSelectedItem();
            deleteLogic(file.getPath(), file.getFileName());
        }
        if (!destList.getSelectionModel().isEmpty()) {
            DirectoryItem file = destList.getSelectionModel().getSelectedItem();
            deleteLogic(file.getPath(), file.getFileName());
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
     *
     */
    private void deleteLogic(String path, String fileName) {
        if (!path.isEmpty()) {
            File delFile = new File(path);
            if (delFile.isDirectory()) {
                Path delPath = FileSystems.getDefault().getPath(filePath(path, fileName));
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
                invalidMedia();
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
     *  Helper function to append a file's path and file's name
     *
     * @param  path         path to the parent directory of file
     * @param  fileName     name of file appended to the path
     *
     */
    private String filePath(String path, String fileName){
        return path + "\\" + fileName;
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
     * @param  item     Currently selected DirectoryItem object
     *                  utilized to determine if the user has
     *                  selected a directory or to navigate
     *                  up one directory level
     *
     */
    public void directoryChange(DirectoryItem item) {
        if(item.getFileName().equals("..")){
            int i = item.getPath().lastIndexOf('\\');
            String back = item.getPath().substring(0,i);
            long count = back.chars().filter(ch -> ch == '\\').count();
            if (count <= 1) {
                String seperator ="\\";
                String[] drive = back.split(Pattern.quote(seperator));
                back = drive[0].concat(seperator);
            }
            if (!sourceList.getSelectionModel().isEmpty()) {
                sourceField.setText(back);
            }
            if (!destList.getSelectionModel().isEmpty()) {
                destField.setText(back);
            }
        }
        else if (item.getFullPath().toFile().isDirectory()) {
            if (!sourceList.getSelectionModel().isEmpty()) {
                sourceField.setText(filePath(item.getPath(), item.getFileName()));
            }
            if (!destList.getSelectionModel().isEmpty()) {
                destField.setText(filePath(item.getPath(), item.getFileName()));
            }
        }
        refreshListViews();
    }

    /**
     *  Tracks currently selected file in the event log ListView
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
    private void logAdd(String text, String choice) {
        logList.getItems().add(new LogItem(text, choice));
    }


    /**
     * Sets MediaPlayer to state of playing
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="playButton"
     *
     */
    @FXML
    private void playClick(ActionEvent event) {
        mediaView.getMediaPlayer().setRate(1);
        playMedia();
    }


    /**
     * Sets MediaPlayer to state of stalled
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="playButton"
     *
     */
    @FXML
    private void pauseClick(ActionEvent event) { pauseMedia(); }


    /**
     * Decreases playback rate of MediaPlayer by 0.1 on
     * each click up until max
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="rwButton"
     *
     */
    @FXML
    private void rwClick(ActionEvent event) {
        mediaView.getMediaPlayer().setRate(mediaView.getMediaPlayer().getRate()-0.1);
    }

    /**
     * Increases playback rate of MediaPlayer by 0.5 on
     * each click up until max
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="ffButton"
     *
     */
    @FXML
    private void ffClick(ActionEvent event) {
        mediaView.getMediaPlayer().setRate(mediaView.getMediaPlayer().getRate()+0.5);
    }

    /**
     * Jumps MediaPlayer playback time back three seconds
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="minusButton"
     *
     */
    @FXML
    private void minusClick(ActionEvent event) {
        mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getCurrentTime().add(Duration.seconds(-3)));
    }

    /**
     * Jumps MediaPlayer playback time ahead three seconds
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="plusButton"
     *
     */
    @FXML
    private void plusClick(ActionEvent event) {
        mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getCurrentTime().add(Duration.seconds(3)));
    }

    /**
     * Handles user input for selecting the last available image file
     * to display in the ImageView. Chooses strictly image files in
     * the selected ListView and iterates through them.
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="backwardsButton"
     *
     */
    @FXML
    private void backwardClick(ActionEvent event) {
        if (!sourceList.getSelectionModel().isEmpty()) {
            DirectoryItem lastImage = getLastImage(sourceList.getItems(), sourceList.getSelectionModel().getSelectedItem());
            sourceList.getSelectionModel().select(lastImage);
            imageView.setImage(new Image(lastImage.getFullPath().toFile().toURI().toString()));
        }
        if (!destList.getSelectionModel().isEmpty()) {
            DirectoryItem lastImage = getLastImage(destList.getItems(), destList.getSelectionModel().getSelectedItem());
            destList.getSelectionModel().select(lastImage);
            imageView.setImage(new Image(lastImage.getFullPath().toFile().toURI().toString()));
        }
    }

    /**
     * Handles user input for selecting the next available image file
     * to display in the ImageView. Chooses strictly image files in
     * the selected ListView and iterates through them.
     *
     * @param  event    an ActionEvent triggered when the user clicks
     *                  the fx:id="forwardButton"
     *
     */
    @FXML
    private void forwardClick(ActionEvent event) {
        if (!sourceList.getSelectionModel().isEmpty()) {
            DirectoryItem nextImage = getNextImage(sourceList.getItems(), sourceList.getSelectionModel().getSelectedItem());
            sourceList.getSelectionModel().select(nextImage);
            imageView.setImage(new Image(nextImage.getFullPath().toFile().toURI().toString()));
        }
        if (!destList.getSelectionModel().isEmpty()) {
            DirectoryItem nextImage = getNextImage(destList.getItems(), destList.getSelectionModel().getSelectedItem());
            destList.getSelectionModel().select(nextImage);
            imageView.setImage(new Image(nextImage.getFullPath().toFile().toURI().toString()));
        }
    }

    /**
     *  Helper function to determine if a passed in file is a
     *  video or not
     *
     * @param  file     utilized to retrieve the abstract file
     *                  pathname
     *
     */
    private Boolean isVideo(File file) {
        String type = URLConnection.guessContentTypeFromName(file.toURI().toString());
        return (type != null && type.startsWith("video"));
    }

    /**
     *  Helper function to determine if a passed in file is an
     *  image or not
     *
     * @param  file     utilized to retrieve the abstract file
     *                  pathname
     *
     */
    private Boolean isImage(File file) {
        String type = URLConnection.guessContentTypeFromName(file.toURI().toString());
        return (type != null && type.startsWith("image"));
    }

    /**
     *  Returns a list of all the image files in the currently
     *  selected directory ListView
     *
     * @param  items    List of all DirectoryList objects
     *                  in the currently selected directory
     *                  ListView
     *
     */
    private ObservableList<DirectoryItem> getImageList(ObservableList<DirectoryItem> items) {
        ObservableList<DirectoryItem> images = FXCollections.observableArrayList();
        for (DirectoryItem item : items) {
            if (isImage(item.getFullPath().toFile())) {
                images.add(item);
            }
        }
        return images;
    }

    /**
     *  Iterates forward over the ObservableList of images
     *  in the currently selected directory ListView. Loops
     *  back to the top of the list if at the end
     *
     * @param  items    ObservableList of images given by
     *                  getImageList()
     * @param  item     DirectoryItem object of the currently
     *                  selected image
     *
     */
    private DirectoryItem getNextImage (ObservableList<DirectoryItem> items, DirectoryItem item) {
        ObservableList<DirectoryItem> images = getImageList(items);
        int i = images.indexOf(item);
        if (i == images.size()-1) {
            i = 0;
        }
        else { ++i; }
        return images.get(i);
    }

    /**
     *  Iterates backwards over the ObservableList of images
     *  in the currently selected directory ListView. Loops
     *  to the end of the list if at the top
     *
     * @param  items    ObservableList of images given by
     *                  getImageList()
     * @param  item     DirectoryItem object of the currently
     *                  selected image
     *
     */
    private DirectoryItem getLastImage (ObservableList<DirectoryItem> items, DirectoryItem item) {
        ObservableList<DirectoryItem> images = getImageList(items);
        int i = images.indexOf(item);
        if (i == 0) {
            i = images.size()-1;
        }
        else { --i; }
        return images.get(i);
    }

    /**
     *  Helper function for when fx:id="sourceList" or
     *  fx:id="destList" is clicked to determine if
     *  MediaView should be shown
     *
     * @param  file     the object currently clicked on
     *                  in the file ListView
     *
     */
    private void mediaClick(File file) {
        if (isVideo(file)) {
            imageViewInvalid();
            mediaPlayerValid();
            disposeMedia();
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            initializeMediaProgressSlider(mediaPlayer, media);
            initializeMediaVolumeSlider(mediaPlayer);
            playMedia();
        }
        else if (isImage(file)) {
            mediaPlayerInvalid();
            imageViewValid();
            imageView.setImage(new Image(file.toURI().toString()));
        }
        else {
            invalidMedia();
        }
    }

    /**
     *  Helper function for mediaClick() that sets the default
     *  value, mouse click functionality, mouse drag
     *  functionality, and the onReady state of the progress
     *  slider bar
     *
     * @param  mediaPlayer  MediaPlayer object that holds and
     *                      controls the current Media
     * @param  media        A media resource
     *
     */
    private void initializeMediaProgressSlider(MediaPlayer mediaPlayer, Media media) {
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                mediaProgress.setValue(t1.toSeconds());
            }
        });

        mediaProgress.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(mediaProgress.getValue()));
            }
        });

        mediaProgress.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(mediaProgress.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = media.getDuration();
                mediaProgress.setMax(total.toSeconds());
            }
        });
    }

    /**
     *  Helper function for mediaClick() that sets the default
     *  value and establishes listener functionality for the
     *  volume slider bar
     *
     * @param  mediaPlayer  MediaPlayer object that holds and
     *                      controls the current Media
     *
     */
    private void initializeMediaVolumeSlider(MediaPlayer mediaPlayer) {
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue()/100);
            }
        });
    }

    /**
     *  Plays current MediaPlayer held in MediaView
     *
     */
    private void playMedia() {
        try {
            mediaView.getMediaPlayer().play();
        } catch (Exception e) {
            //no media playing
        }
    }

    /**
     *  Pauses current MediaPlayer held in MediaView
     *
     */
    private void pauseMedia() {
        try {
            mediaView.getMediaPlayer().pause();
        } catch (Exception e) {
            //no media playing
        }
    }

    /**
     *  Puts MediaPlayer held in MediaView in a state
     *  of DISPOSED, which frees all resources from
     *  the player. MediaPlayer SHOULD NOT be used
     *  again once called.
     *
     */
    private void disposeMedia() {
        try {
            mediaView.getMediaPlayer().dispose();
        } catch (Exception e) {
            //no media playing
        }
    }

    /**
     *  Displays red highlighting CSS around the TextField
     *  fx:id="sourceField" and shows fx:id="invalidSource" error
     *  message
     *
     */
    @FXML
    private void sourceListInvalid() {
        if (!sourceList.getSelectionModel().isEmpty()) {
            invalidMedia();
        }
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
        if (!destList.getSelectionModel().isEmpty()) {
            invalidMedia();
        }
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
     * Sets the StackPane encapsulating the MediaView and related
     * buttons to invisible
     *
     */
    @FXML
    private void mediaPlayerInvalid() {
        disposeMedia();
        mediaStack.setVisible(false);
    }

    /**
     * Sets the StackPane encapsulating the MediaView and related
     * buttons to visible
     *
     */
    @FXML
    private void mediaPlayerValid() {
        mediaStack.setVisible(true);
    }

    /**
     * Sets the StackPane encapsulating the ImageView and related
     * buttons to invisible
     *
     */
    @FXML
    private void imageViewInvalid() {
        imageStack.setVisible(false);
    }

    /**
     * Sets the StackPane encapsulating the ImageView and related
     * buttons to visible
     *
     */
    @FXML
    private void imageViewValid() {
        imageStack.setVisible(true);
    }

    /**
     * Sets the StackPane encapsulating both the MediaPlayer and
     * the ImageView to invisible and disposes of media.
     *
     */
    @FXML
    private void invalidMedia() {
        mediaPlayerInvalid();
        imageViewInvalid();
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
                    setText(item.getText());
                    setStyle(item.getCSS());
                }
            }
        });

        sourceList.setCellFactory(param -> new ListCell<DirectoryItem>() {
            @Override
            protected void updateItem(DirectoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getFileName() == null) {
                    setText(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item.getFileName());
                    setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getClickCount() > 1){
                                directoryChange(getItem());
                            }
                        }
                    });
                }
            }
        });

        destList.setCellFactory(param -> new ListCell<DirectoryItem>() {
            @Override
            protected void updateItem(DirectoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getFileName() == null) {
                    setText(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item.getFileName());
                    setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getClickCount() > 1){
                                directoryChange(getItem());
                            }
                        }
                    });
                }
            }
        });
    }
}

class DirectoryItem {
    private final StringProperty fileName;
    private final StringProperty filePath;
    private final Path fullPath;

    public DirectoryItem(String file, String path) {
        this.fileName = new SimpleStringProperty(file);
        this.filePath = new SimpleStringProperty(path);
        this.fullPath = initializeFullPath(path, file);
    }

    public String getFileName() { return fileName.get(); }

    public String getPath() { return filePath.get(); }

    public Path getFullPath() { return fullPath; }

    private Path initializeFullPath(String path, String filename) {
        return Paths.get(path, filename);
    }
}

class LogItem {
    private final StringProperty text;
    private final StringProperty choice;
    private final StringProperty css;

    public LogItem(String text, String choice) {
        this.text = new SimpleStringProperty(text);
        this.choice = new SimpleStringProperty(choice);
        this.css = new SimpleStringProperty(setCSS(choice));
    }

    public String getText() {
        return text.get();
    }

    public String getChoice() { return choice.get(); }

    public String getCSS() { return css.get(); }

    private String setCSS(String choice) {
        if (choice.equals("Source") || choice.equals("Destination")) {
            return "-fx-control-inner-background: green ; -fx-focus-color: green ;";
        } else if (choice.equals("Move")) {
            return "-fx-control-inner-background: blue; -fx-focus-color: blue ;";
        } else if (choice.equals("Delete") || choice.equals("Error")) {
            return "-fx-control-inner-background: red; -fx-focus-color: red ;";
        }
        return null;
    }
}
