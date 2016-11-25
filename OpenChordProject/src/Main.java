import com.sun.istack.internal.NotNull;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class Main {
	private static Chord networkChord;
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
				if (readString == null)
					readingFile = false;
				else {
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
			    } else if (input[0].equals("join") && input.length == 4) {
				    joinChordClient(input[1], input[2], input[3]);
			    } else if (input[0].equals("leave") && input.length == 2) {
				    leaveChordClient(input[1]);
			    } else if (input[0].equals("insert") && input.length == 4) {
					insertData(input[1], input[2], input[3]);
			    } else if (input[0].equals("retrieve") && input.length == 3) {
				    retrieveData(input[1], input[2]);
			    } else if (input[0].equals("remove") && input.length == 4) {
				    removeData(input[1], input[2], input[3]);
			    } else if (input[0].equals("file") && input.length == 2) {
					Path path = FileSystems.getDefault().getPath(input[1]);
					fileInBr = Files.newBufferedReader(path, StandardCharsets.UTF_8);
					readingFile = true;
				} else {
                    System.out.println("Invalid Command!");
                }
		    } catch (Exception e) {
			    e.printStackTrace();
		    }
	    }
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
			System.out.println("Data [" + i + "]: " + s);
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
		if (networkChord == chord && clients.size() > 0) {
			networkChord = clients.entrySet().iterator().next().getValue();
		}

		chord.leave();
	}

	private static void joinChordClient(String name, String ip, String port) throws ServiceException, MalformedURLException {
		if (clients.containsKey(name)) {
			System.out.println("Chord with name '" + name + "' already present!");
		}

		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL url = new URL(protocol + "://" + ip + ":" + port + "/");

		Chord chord = new ChordImpl();
		chord.join(url, networkChord.getURL());

		clients.put(name, chord);
	}

	private static void createChordClient(String name, String ip, String port) throws MalformedURLException, ServiceException {
		if (networkChord != null) {
			System.out.println("Network already created!");
			return;
		}

		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL url = new URL(protocol + "://" + ip + ":" + port + "/");

		Chord chord = new ChordImpl();
		chord.create(url);

		clients.put(name, chord);
		networkChord = chord;
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
}