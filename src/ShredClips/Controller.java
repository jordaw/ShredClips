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
    private TextField destField;

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
            sourceFileDisplay(file);
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
            destFileDisplay(file);
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
