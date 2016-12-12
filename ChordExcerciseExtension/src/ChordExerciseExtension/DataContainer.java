package ChordExerciseExtension;

import java.io.Serializable;
import java.util.Arrays;

public class DataContainer implements Serializable {
	public final byte[] data;

	public DataContainer(byte[] data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataContainer that = (DataContainer) o;

		return Arrays.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
}
