package ShredClips;

/**Class*/
import javafx.application.Application;

/**Scene*/
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    /**
     * Receives a stage from main(), sets the scene, and displays the primary
     * stage.
     *
     * @param  primaryStage  the primary stage for the application, onto which
     *                       the application scene can be set.
     *
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("directory.fxml"));
        primaryStage.setTitle("ShredClips");
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Calls the start() method once executed
     *
     * @param  args  a string array of arguments sent to the static launch
     *               method that is inherited from the Application parent
     *               class.
     */
    public static void main(String[] args) { launch(args); }
}
