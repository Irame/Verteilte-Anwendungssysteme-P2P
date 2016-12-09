import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;

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

    public static Serializable readFile(String filePath) throws IOException{
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public static void writeFile(Set<Serializable> data, String filePath) throws IOException {
        int pointIndex = filePath.lastIndexOf(".");
        String extension = filePath.substring(pointIndex);
        String fileName = filePath.substring(0, pointIndex);
        int i = 0;
        for(Serializable s : data){
            String name = i == 0 ? fileName + extension : fileName + i + extension;
            Files.write(Paths.get(name), (byte[]) s, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            i++;
        }
    }
}
