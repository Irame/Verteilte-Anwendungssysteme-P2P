import javafx.scene.control.Alert;

/**
 * Created by Felix on 25.11.2016.
 */
public abstract class Utils {
    public static void openAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
