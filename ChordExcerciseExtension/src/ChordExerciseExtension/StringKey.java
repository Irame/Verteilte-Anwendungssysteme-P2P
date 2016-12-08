package ChordExerciseExtension;
import de.uniba.wiai.lspi.chord.service.Key;

public class StringKey implements Key {
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
        if (this == o) 
        	return true;
        if (o == null || getClass() != o.getClass()) 
        	return false;

        StringKey stringKey = (StringKey) o;

        return key != null ? key.equals(stringKey.key) : stringKey.key == null;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
