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
	}
}
