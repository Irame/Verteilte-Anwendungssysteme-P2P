import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ChordExerciseExtension.ChordUtils;
import ChordExerciseExtension.StringKey;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Main {
	static Chord chord = null;
	
	public static void main(String[] args) throws ServiceException, IOException {
		PropertiesLoader.loadPropertyFile();

		// Aufgabe 1 Chord Ring Joinen
		joinChordRing("*.*.*.*");
		// Aufgabe 2 String runterladen
		List<String> list = retrieveStrings("stringKeyName");
		for(String s : list){
			System.out.println(s);
		}
		// Aufgabe 3 String bereitstellen
		publishString("stringKeyName", "text");
		// Aufgabe 4 & 6 Datei runterladen
		retrieveFile("fileKeyName", "D:\\Path\\to\\file.png");
		// Aufgabe 5 Eigene Datei bereitstellen
		publishFile("fileKeyName", "D:\\Path\\to\\file.png");
		// Aufgabe 7 Chord Ring erstellen
		createChordRing();
	}
	
	private static void createChordRing() throws UnknownHostException, MalformedURLException, ServiceException{
		chord = new ChordImpl();
		chord.create(ChordUtils.getLocalUrl());
	}
	private static void joinChordRing(String ip) throws UnknownHostException, MalformedURLException, ServiceException{
		chord = new ChordImpl();
		chord.join(ChordUtils.getLocalUrl(), ChordUtils.getUrl(ip));
	}
	private static void leaveChordRing() throws ServiceException{
		chord.leave();
	}
	
	
	
	private static void publishFile(String key, String path) throws ServiceException, IOException {
		chord.insert(new StringKey(key), ChordUtils.readFile(path));
	}
	
	private static void retrieveFile(String key, String path) throws ServiceException, IOException {
		Set<Serializable> result = chord.retrieve(new StringKey(key));
		ChordUtils.writeFile(result, path);
	}
	
	private static void publishString(String key, String text) throws ServiceException{
		chord.insert(new StringKey(key), text);
	}
	
	private static List<String> retrieveStrings(String key) throws ServiceException{
		Set<Serializable> result = chord.retrieve(new StringKey(key));
		List<String> list = new ArrayList<>();
		for(Serializable s : result){
			list.add((String)s);
		}
		return list;
	}
}
