import com.sun.istack.internal.NotNull;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 25.11.2016.
 */
public class ChordNode {
    public String name;
    private ChordImpl chord;

    public ChordNode(String name) {
        this.name = name;
        chord = new ChordImpl();
    }

    public void setUrl(String ip, String port) throws MalformedURLException {
        String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        chord.setURL(new URL(protocol + "://" + ip + ":" + port + "/"));
    }

    public void create() throws ServiceException {
        chord.create();
    }

    public void join(String ip, String port) throws MalformedURLException, ServiceException {
        String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
        chord.join(new URL(protocol + "://" + ip + ":" + port + "/"));
    }

    public void leave() throws ServiceException {
        chord.leave();
    }

    public void insert(String key, Serializable data) {
        chord.insert(new StringKey(key), data);
    }

    public Set<Serializable> retrieve(String key) {
        return chord.retrieve(new StringKey(key));
    }

    public void remove(String key, Serializable data) {
        chord.remove(new StringKey(key), data);
    }

    public String getEntriesAsString() {
        return chord.printEntries();
    }

    public String getReferencesAsString() {
        return chord.printReferences();
    }

    public URL getUrl() {
        return chord.getURL();
    }

    public List<Node> scanNetworkFromThisNode() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Node> result = new ArrayList<>();

        BigInteger idBigInt = BigInteger.ZERO;
        BigInteger newIdBigInt = null;

        while (true) {
            ID id = BigIntToID(idBigInt);
            Node node = chord.findSuccessor(id);

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

    private ID BigIntToID(BigInteger bigInt)
    {
        byte[] bigIntBytes = bigInt.toByteArray();
        byte[] buffer = new byte[20];

        if (bigIntBytes.length > 20)
            System.arraycopy(bigIntBytes, bigIntBytes.length-20, buffer, 0, 20);
        else
            System.arraycopy(bigIntBytes, 0, buffer, 20-bigIntBytes.length, bigIntBytes.length);

        return new ID(buffer);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, chord.toString());
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
