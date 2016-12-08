import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Felix on 08.12.2016.
 */
public class Main {
	public static void main(String[] args) throws MalformedURLException, ServiceException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
		PropertiesLoader.loadPropertyFile();
		int waitTime = 5000;
		File scanResultFile = new File(args[0]);

		String localIp = args[1];
		String localPort = "42423";

		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);

		byte[] idBytes = new byte[20];
		Arrays.fill(idBytes, (byte) 0xff);

		ChordImpl chord = new ChordImpl();
		chord.create(new URL(protocol + "://" + localIp + ":" + localPort + "/"), new ID(idBytes));

		WriteLine("Chord client created with with URL '%s' ", chord.getURL());

		while (true) {
			WriteLine("Scanning Network...");
			List<Node> nodes = scanNetwork(chord);
			String data = nodes.stream().map(node -> node.getNodeID().toHexString().trim() + ";" + node.getNodeURL()).collect(Collectors.joining("\n\t"));
			WriteLine("Nodes:\n\t" + data);

			WriteLine("Writing to File...");
			try (FileWriter fw = new FileWriter(scanResultFile)) {
				fw.write(data);
				fw.flush();
			} catch(IOException e){
				e.printStackTrace();
			}
			WriteLine("Waiting for %d ms...", waitTime);
			Thread.sleep(waitTime);
		}
	}

	public static List<Node> scanNetwork(ChordImpl chordNode) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		List<Node> result = new ArrayList<>();

		BigInteger idBigInt = BigInteger.ZERO;

		BigInteger newIdBigInt = null;
		while (true) {
			byte[] bigIntBytes = idBigInt.toByteArray();
			byte[] buffer = new byte[20];
			Arrays.fill(buffer, (byte) 0);

			if (bigIntBytes.length > 20)
				System.arraycopy(bigIntBytes, bigIntBytes.length-20, buffer, 0, 20);
			else
				System.arraycopy(bigIntBytes, 0, buffer, 20-bigIntBytes.length, bigIntBytes.length);

			ID id = new ID(buffer);
			Node node = chordNode.findSuccessor(id);

			newIdBigInt = new BigInteger(node.getNodeID().toHexString().replace(" ", ""), 16);
			if (newIdBigInt.compareTo(idBigInt) < 0)
				break;

			result.add(node);

			if (newIdBigInt.compareTo(idBigInt) == 0)
				break;
			idBigInt = newIdBigInt.add(BigInteger.ONE);
		}

		return result;
	}

	private static DateFormat df = new SimpleDateFormat("HH:mm:ss");

	private static void WriteLine(String s) {
		System.out.println(getCurTimestempStirng() + s);
	}

	private static void WriteLine(String format, Object... args) {
		System.out.println(getCurTimestempStirng() + String.format(format, args));
	}

	private static void Write(String s) {
		System.out.print(getCurTimestempStirng() + s);
	}

	private static void Write(String format, Object... args) {
		System.out.print(getCurTimestempStirng() + String.format(format, args));
	}

	private static String getCurTimestempStirng() {
		return "[" + df.format(Calendar.getInstance().getTime()) + "] ";
	}
}
