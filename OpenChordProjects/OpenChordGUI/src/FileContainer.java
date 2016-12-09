import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class FileContainer implements Serializable {
	private byte[] data;

	FileContainer(String filePath) throws IOException {
		data = Files.readAllBytes(Paths.get(filePath));
	}

	public void writeToFile(String filePath) throws IOException {
		Files.write(Paths.get(filePath), data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FileContainer that = (FileContainer) o;

		return Arrays.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
}
