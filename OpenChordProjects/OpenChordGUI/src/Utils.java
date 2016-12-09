import javafx.scene.control.Alert;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;

public abstract class Utils {
    public static void openAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static Serializable readFile(String filePath) throws IOException{
        return new DataContainer(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void writeFile(Set<Serializable> data, String filePath) throws IOException{
        int pointIndex = filePath.lastIndexOf(".");
        String extension = filePath.substring(pointIndex);
        String fileName = filePath.substring(0, pointIndex);
        int i = 0;
        for(Serializable s : data){
            byte[] bytesToWrite = null;
            if (s instanceof DataContainer)
                bytesToWrite = ((DataContainer) s).data;
            else if (s instanceof String)
                bytesToWrite = ((String) s).getBytes();
            else if (s instanceof byte[])
                bytesToWrite = (byte[]) s;
            else
                continue;
            String name = i == 0 ? fileName + extension : fileName + i + extension;
            Files.write(Paths.get(name), bytesToWrite, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            i++;
        }
    }
}
