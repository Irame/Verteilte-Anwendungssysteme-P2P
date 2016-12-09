package ChordExerciseExtension;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.URL;

public abstract class ChordUtils {
	public static URL getLocalUrl() throws UnknownHostException, MalformedURLException{
		return getLocalUrl("1337");
	}
	public static URL getLocalUrl(String port) throws UnknownHostException, MalformedURLException{
		InetAddress localAddress = null;
		localAddress = InetAddress.getLocalHost();
		String ip = localAddress.getHostAddress();
		return getUrl(ip, port);
	}

	public static URL getUrl(String ip) throws MalformedURLException{
		return getUrl(ip, "1337");
	}
	public static URL getUrl(String ip, String port) throws MalformedURLException{
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL url = new URL(protocol + "://" + ip + ":" + port + "/");
		return url;
	}
	
	public static Serializable readFile(String filePath) throws IOException{
		Path path = Paths.get(filePath);
		return Files.readAllBytes(path);
	}
	
	public static void writeFile(Set<Serializable> data, String filePath) throws IOException{
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
