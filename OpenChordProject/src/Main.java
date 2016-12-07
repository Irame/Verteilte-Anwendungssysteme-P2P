import com.sun.istack.internal.NotNull;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.chord.service.impl.NodeImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
	private static HashMap<String, Chord> clients;

    public static void main(String[] args) throws ServiceException, IOException {
	    clients = new HashMap<>();
	    PropertiesLoader.loadPropertyFile();
	    BufferedReader stdInBr = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader fileInBr = null;

		boolean readingFile = false;
	    while (true) {
		    System.out.print(">");
			String[] input = null;
			if (readingFile){
				String readString = fileInBr.readLine();
				if (readString == null) {
					fileInBr.close();
					readingFile = false;
				} else {
                    input = readString.split(" +");
                    System.out.println(readString);
                }
			}
			if (!readingFile) {
				input = stdInBr.readLine().split(" +");
			}

		    input[0] = input[0].toLowerCase();
		    try {
			    if (input[0].equals("exit")) {
				    System.exit(0);
			    } else if (input[0].equals("create") && input.length == 4) {
				    createChordClient(input[1], input[2], input[3]);
			    } else if (input[0].equals("join") && input.length == 6) {
				    joinChordClient(input[1], input[2], input[3], input[4], input[5]);
			    } else if (input[0].equals("leave") && input.length == 2) {
				    leaveChordClient(input[1]);
			    } else if (input[0].equals("insert") && input.length == 4) {
					insertData(input[1], input[2], input[3]);
			    } else if (input[0].equals("insertimg") && input.length == 4) {
					insertImage(input[1], input[2], input[3]);
			    } else if (input[0].equals("retrieve") && input.length == 3) {
				    retrieveData(input[1], input[2]);
			    } else if (input[0].equals("retrieveimg") && input.length == 4) {
				    retrieveImage(input[1], input[2], input[3]);
			    } else if (input[0].equals("remove") && input.length == 4) {
				    removeData(input[1], input[2], input[3]);
			    } else if (input[0].equals("file") && input.length == 2) {
				    Path path = FileSystems.getDefault().getPath(input[1]);
				    fileInBr = Files.newBufferedReader(path, StandardCharsets.UTF_8);
				    readingFile = true;
			    } else if (input[0].equals("info") && input.length == 1) {
					printNodesInfo();
			    } else if (input[0].equals("info") && input.length == 2) {
				    printNodeInfo(input[1]);
			    } else if (input[0].equals("findall") && input.length == 2) {
				    findAllNodesAndWriteToFile(input[1]);
				} else {
                    System.out.println("Invalid Command!");
                }
		    } catch (Exception e) {
			    e.printStackTrace();
		    }
	    }
    }

    private static void findAllNodesAndWriteToFile(String path) throws InvocationTargetException, NoSuchMethodException, CommunicationException, IllegalAccessException, IOException {
    	List<Node> allNodes = findAllNodes();
		FileWriter fw = new FileWriter(path);
    	String data = allNodes.stream().map(node -> node.getNodeID().toHexString().trim() + ";" + node.getNodeURL()).collect(Collectors.joining("\n"));
	    fw.write(data);
	    fw.flush();
    }

    private static List<Node> findAllNodes() throws IllegalAccessException, CommunicationException, NoSuchMethodException, InvocationTargetException {
    	List<Node> result = new ArrayList<>();
	    Method findSuccessor = clients.get("c3").getClass().getDeclaredMethod("findSuccessor", ID.class);
	    findSuccessor.setAccessible(true);

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
			Node node = (Node) findSuccessor.invoke(clients.get("c3"), id);

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

    private static void printNodesInfo() {
	    clients.entrySet().stream()
			    .sorted(Comparator.comparing(o -> o.getValue().getID()))
			    .forEachOrdered(entry -> printNodeInfo(entry.getKey()));
    }

    private static void printNodeInfo(String name) {
	    WriteLine("Name: %s", name);
	    ChordImpl chordImpl = (ChordImpl) clients.get(name);
	    WriteLine("\t%s", chordImpl.printReferences().replace("\n", "\n\t"));
	    Write("\t%s", chordImpl.printEntries().replace("\n", "\n\t"));
	    WriteLine("");
    }

	private static void retrieveImage(String name, String key, String path) throws ServiceException, IOException {
		Chord chord = clients.get(name);
		if (chord == null) {
			System.out.println("Chord with name " + name + " not found!");
			return;
		}

		Set<Serializable> data = chord.retrieve(new StringKey(key));

		for (Serializable s : data) {
			ImageContainer img = (ImageContainer) s;
			ImageIO.write(img.getImage(), "png", new File(path));
			return;
		}
	}

	private static void insertImage(String name, String key, String path) throws IOException, ServiceException {
	    BufferedImage img = ImageIO.read(new File(path));
	    Chord chord = clients.get(name);
	    if (chord == null) {
		    System.out.println("Chord with name " + name + " not found!");
		    return;
	    }

	    chord.insert(new StringKey(key), new ImageContainer(img));
    }

	private static void removeData(String name, String key, String data) throws ServiceException {
		Chord chord = clients.get(name);
		if (chord == null) {
			System.out.println("Chord with name " + name + " not found!");
			return;
		}

		chord.remove(new StringKey(key), data);
	}

	private static void retrieveData(String name, String key) throws ServiceException {
		Chord chord = clients.get(name);
		if (chord == null) {
			System.out.println("Chord with name " + name + " not found!");
			return;
		}

		Set<Serializable> data = chord.retrieve(new StringKey(key));
		int i = 1;
		for (Serializable s : data) {
			System.out.println("Data [" + i++ + "]: " + s);
		}
	}

	private static void insertData(String name, String key, String data) throws ServiceException {
		Chord chord = clients.get(name);
		if (chord == null) {
			System.out.println("Chord with name " + name + " not found!");
			return;
		}

		chord.insert(new StringKey(key), data);
	}

	private static void leaveChordClient(String name) throws ServiceException {
		Chord chord = clients.remove(name);

		chord.leave();
	}

	private static void joinChordClient(String name, String localIp, String localPort, String otherIp, String otherPort) throws ServiceException, MalformedURLException {
		if (clients.containsKey(name)) {
			System.out.println("Chord with name '" + name + "' already present!");
            return;
		}

		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localUrl = new URL(protocol + "://" + localIp + ":" + localPort + "/");
		URL otherUrl = new URL(protocol + "://" + otherIp + ":" + otherPort + "/");

		Chord chord = new ChordImpl();
		chord.join(localUrl, otherUrl);

		clients.put(name, chord);
	}

	private static void createChordClient(String name, String ip, String port) throws MalformedURLException, ServiceException {
        if (clients.containsKey(name)) {
            System.out.println("Chord with name '" + name + "' already present!");
            return;
        }

		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL url = new URL(protocol + "://" + ip + ":" + port + "/");

		Chord chord = new ChordImpl();
		chord.create(url);

		clients.put(name, chord);
	}

    private static class StringKey implements Key {
        @NotNull
        private String key;

        public StringKey(String key) {
            this.key = key;
        }

        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }

	    @Override
	    public boolean equals(Object o) {
		    if (this == o) return true;
		    if (o == null || getClass() != o.getClass()) return false;

		    StringKey stringKey = (StringKey) o;

		    return key != null ? key.equals(stringKey.key) : stringKey.key == null;

	    }

	    @Override
	    public int hashCode() {
		    return key != null ? key.hashCode() : 0;
	    }
    }

    private static class ImageContainer implements Serializable {
	    public int width;
		public int height;
		public int imgType;
    	public int[] imgData;

	    public ImageContainer(BufferedImage img) {
		    this.width = img.getWidth();
		    this.height = img.getHeight();
		    this.imgType = img.getType();
		    this.imgData = new int[width*height];
		    this.imgData = img.getRGB(0, 0, width, height, imgData, 0, width);
	    }

	    public BufferedImage getImage() {
			BufferedImage result = new BufferedImage(width, height, imgType);
			result.setRGB(0, 0, width, height, imgData, 0, width);
	    	return result;
	    }
    }

    private static void WriteLine(String s) {
    	System.out.println(s);
    }

	private static void WriteLine(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	private static void Write(String s) {
    	System.out.print(s);
	}

	private static void Write(String format, Object... args) {
		System.out.print(String.format(format, args));
	}
}
