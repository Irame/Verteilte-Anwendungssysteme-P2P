import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        PropertiesLoader.loadPropertyFile();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("OpenChordGUI");
        primaryStage.setScene(new Scene(root));

        mainController = fxmlLoader.getController();
        mainController.init();

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        mainController.deinit();
    }
}
