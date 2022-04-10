package Model;

import java.io.Serializable;
import java.util.Arrays;

@SuppressWarnings("serial")
public class MySet<T> implements Serializable {

	private T[] array;
	private int numOfElements;

	@SuppressWarnings("unchecked")
	public MySet() {
		array = (T[]) new Object[5];
		numOfElements = 0;
	}

	public boolean add(T element) {
		for (int i = 0; i < array.length; i++) {
			if ((array[i] != null) && (array[i].equals(element))) {
				return false;
			}
		}

		if (numOfElements == array.length) {
			array = Arrays.copyOf(array, array.length * 2);
		}
		array[numOfElements] = element;
		numOfElements++;

		return true;
	}

	public boolean isEmpty() {
		if (numOfElements == 0) {
			return true;
		}
		return false;
	}

	public boolean remove(int index) {
		array[index] = null;
		for (int i = index + 1; i < array.length; i++) {
			if (array[i] != null) {
				for (int j = index; j < array.length; j++) {
					if (array[j] == null) {
						array[j] = array[i];
						array[i] = null;
					}
				}
			}
		}
		return false;
	}

	public int size() {
		return numOfElements;
	}

	public T getObject(int index) {
		return array[index];
	}

	public boolean setObject(int index, T object) {
		array[index] = object;
		return true;
	}

}
