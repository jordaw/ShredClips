package ShredClips;

/**Class*/
import javafx.fxml.Initializable;

/**Object*/
import java.net.URL;
import java.io.File;
import java.util.Arrays;
import java.util.ResourceBundle;

/**Event*/
import javafx.event.ActionEvent;

/**Scene*/
import javafx.fxml.FXML;
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
        if(file != null){
            sourceField.setText(file.getAbsolutePath());
            sourceField.setStyle(null);
            invalidSource.setVisible(false);
            sourceFileDisplay(file);
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
    public void sourceFieldChange(ActionEvent event){
        File file = new File(sourceField.getCharacters().toString());
        if(file.isDirectory()){
            sourceField.setStyle(null);
            invalidSource.setVisible(false);
            sourceFileDisplay(file);
        }
        else{
            sourceField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
            invalidSource.setVisible(true);
            sourceList.getItems().clear();
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
        if(file != null){
            destField.setText(file.getAbsolutePath());
            destField.setStyle(null);
            invalidDest.setVisible(false);
            destFileDisplay(file);
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
    public void destFieldChange(ActionEvent event){
        File file = new File(destField.getCharacters().toString());
        if(file.isDirectory()){
            destField.setStyle(null);
            invalidDest.setVisible(false);
            destFileDisplay(file);
        }
        else{
            destField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
            invalidDest.setVisible(true);
            destList.getItems().clear();
        }
    }

    /**
     *  Populates files held within source directory utilizing the
     *  file object passed from sourcePathChoice()
     *
     * @param  file     File object passed from sourcePathChoice
     *                  function
     *
     */
    public void sourceFileDisplay(File file) {
        sourceList.getItems().clear();
        System.out.println(Arrays.toString(file.list()));
        String[] filenames = file.list();
        if(!(filenames == null)) {
            for (String filename : filenames) {
                sourceList.getItems().add(filename);
            }
        }
    }

    /**
     *  Tracks currently selected file held in the source directory
     *  ListView
     *
     * @param  arg0     top-most node under cursor selected
     *
     */
    @FXML
    public void handleSourceMouseClick(javafx.scene.input.MouseEvent arg0) {
        System.out.println("SOURCE MC: clicked on " + sourceList.getSelectionModel().getSelectedItem());
    }

    /**
     *  Populates files held within destination directory utilizing the
     *  file object passed from destPathChoice()
     *
     * @param  file     File object passed from destPathChoice function,
     *                  utilized for ListView population
     *
     */
    public void destFileDisplay(File file) {
        destList.getItems().clear();
        System.out.println(Arrays.toString(file.list()));
        String[] filenames = file.list();
        if (!(filenames == null)) {
            for (String filename : filenames) {
                destList.getItems().add(filename);
            }
        }
    }

    /**
     *  Tracks currently selected file held in the destination
     *  directory ListView
     *
     * @param  arg0     top-most node under cursor selected
     *
     */
    @FXML
    public void handleDestMouseClick(javafx.scene.input.MouseEvent arg0) {
        System.out.println("DEST MC: clicked on " + destList.getSelectionModel().getSelectedItem());
    }

    /**
     *  Called to initialize the controller after the root element
     *  has been completely processed.
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
    }

}
