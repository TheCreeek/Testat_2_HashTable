package lab;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Function;

import frame.Entry;

/*
 * Implements a Hash-Table structure as introduced in the 
 * lecture to store the information read by the RFID 
 * readers in the library.
 *	
 * Make sure that you have tested all the given test cases
 * given on the homepage before you submit your solution.
 */

public class HashTable {

	int cap; // number of entries actually saved in the hashtable
	int size; // the maximum size the HashTable can hold
	String hFunction; // the string which holds the hashFunction used
	String cResolution; // the string which holds the collision resolution used
	Entry[] hTable;// the hashtable itself
	ArrayList<Integer>[] insertionSeq; // saves the insertionSequence, meaning every failed attempt to store the entry
										// in the htable
	int addrL; // between 0 to 1, 0 meaning not loaded and 1 meaning fully loaded

	/**
	 * The constructor
	 * 
	 * @param initialCapacity
	 *            represents the initial size of the Hash Table.
	 * @param hashFunction
	 *            can have the following values: division folding mid_square
	 * @param collisionResolution
	 *            can have the following values: linear_probing quadratic_probing
	 * 
	 *            The Hash-Table itself should be implemented as an array of entries
	 *            (Entry[] in Java) and no other implementation will be accepted.
	 *            When the load factor exceeds 75%, the capacity of the Hash-Table
	 *            should be increased as described in the method rehash below. We
	 *            assume a bucket factor of 1.
	 */
	@SuppressWarnings("unchecked")
	public HashTable(int k, String hashFunction, String collisionResolution) {
		size = k;
		hFunction = hashFunction;
		cResolution = collisionResolution;

		hTable = new Entry[k];
		insertionSeq = new ArrayList[k];

		this.addrL = (int) Math.ceil(Math.log10(size));// Taken from
														// https://moodle.informatik.tu-darmstadt.de/mod/forum/discuss.php?d=14372

	}

	/**
	 * This method takes as input the name of a file containing a sequence of
	 * entries that should be inserted into the Hash-Table in the order they appear
	 * in the file. You cannot make any assumptions on the order of the entries nor
	 * is it allowed to change the order given in the file. You can assume that the
	 * file is located in the same directory as the executable program. The input
	 * file is similar to the input file for lab 1. The return value is the number
	 * of entries successfully inserted into the Hash-Table.
	 * 
	 * I did not use the provided Reader from LibraryFileReader because I had some
	 * Problems with it
	 * 
	 * @param filename
	 *            name of the file containing the entries
	 * @return returns the number of entries successfully inserted in the
	 *         Hash-Table.
	 */
	public int loadFromFile(String filename) {
		int count = 0;
		try {
			// Open the file
			FileInputStream fstream = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			// Read File Line By Line
			for (String curLine = br.readLine(); curLine != null; curLine = br.readLine()) {

				// Print the content on the console
				int firstSemi = curLine.indexOf(';', 0), secondSemi = curLine.indexOf(';', firstSemi + 1);

				String bookSerialNumber = curLine.substring(0, firstSemi),
						readerID = curLine.substring(firstSemi + 1, secondSemi),
						status = curLine.substring(secondSemi + 1);
				if (insert(new Entry(bookSerialNumber, readerID, status)))
					count++;
			}
			// Close the input stream
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * This method inserts the entry insertEntry into the Hash-Table. Note that you
	 * have to deal with collisions if you want to insert an entry into a slot which
	 * is not empty. This method returns true if the insertion of the entry
	 * insertEntry is successful and false if the key of this entry already exists
	 * in the Hash-Table (the existing key/value pair is left unchanged).
	 * 
	 * @param insertEntry
	 *            entry to insert into the Hash-table
	 * @return returns true if the entry insertEntry is successfully inserted false
	 *         if the entry already exists in the Hash-Table
	 */
	public boolean insert(Entry insertEntry) {
		return this.insert(this.hTable, insertEntry);
	}

	/**
	 * 
	 * @param hTable
	 *            the hashtable on which the entry will be inserted to
	 * @param insertEntry
	 *            the entry to insert into the hashtable
	 * @return
	 */
	private boolean insert(Entry[] hTable, Entry insertEntry) {

		ArrayList<Integer> tmpInsertionSeq = new ArrayList<Integer>();
		for (int iterations = 0; true; iterations++) {
			int index = calculateIndex(hTable, insertEntry.getKey(), iterations);

			if (hTable[index] != null && insertEntry.getKey().equals(hTable[index].getKey())
					&& !hTable[index].isDeleted())
				return false;

			if (indexAvailable(index)) {
				hTable[index] = insertEntry;
				if (hTable == this.hTable) {
					this.cap++;
					this.rehash();
					this.setInsertionSeq(index, tmpInsertionSeq);
				}
				return true;
			} else
				tmpInsertionSeq.add(index);
		}
	}

	/**
	 * This method deletes the entry from the Hash-Table, having deleteKey as key
	 * This method returns the entry, having deleteKey as key if the deletion is
	 * successful and null if the key deleteKey is not found in the Hash-Table.
	 * 
	 * @param deleteKey
	 *            key of the entry to delete from the Hash-Table
	 * @return returns the deleted entry if the deletion ends successfully null if
	 *         the entry is not found in the Hash-Table
	 */
	public Entry delete(String deleteKey) {
		Entry toDelete = find(deleteKey);

		if (toDelete == null) {
			return null;
		} else {
			toDelete.markDeleted();
			this.cap--;
			hTable[calculateIndex(deleteKey, 0)] = null;
			return toDelete;
		}
	}

	/**
	 * This method searches in the Hash-Table for the entry with key searchKey. It
	 * returns the entry, having searchKey as key if such an entry is found, null
	 * otherwise.
	 * 
	 * @param searchKey
	 *            key of the entry to find in the Hash-table
	 * @return returns the entry having searchKey as key if such an entry exists
	 *         null if the entry is not found in the Hash-Table
	 */
	public Entry find(String searchKey) {
		return find(this.hTable, searchKey);
	}

	/**
	 * 
	 * @param hTable
	 *            the hash table in which to search for the entry
	 * @param searchKey
	 *            key of the entry to find in the Hash-table
	 * @return returns the entry having searchKey as key if such an entry exists
	 *         null if the entry is not found in the Hash-Table
	 */
	private Entry find(Entry[] hTable, String searchKey) {
		for (int iteration = 0; true; iteration++) {
			Entry indexElement = hTable[calculateIndex(hTable, searchKey, iteration)];
			if (indexElement == null) {
				return null;
			} else if (indexElement.getKey().equals(searchKey) && indexElement.isDeleted() == false) {
				return indexElement;
			}
		}
	}

	/**
	 * This method returns a ArrayList<String> containing the output Hash-Table. The
	 * output should be directly interpretable dot code. Each item in the ArrayList
	 * corresponds to one line of the output Hash-Table. The nodes of the output
	 * Hash-Table should contain the keys of the entries and also the data.
	 * 
	 * @return returns the output Hash-Table in directly interpretable dot code
	 * @throws IOException
	 */
	public ArrayList<String> getHashTable() throws IOException {
		ArrayList<String> a = new ArrayList<String>();
		boolean debug = false;
		/**
		 * The Debug mode is for testing and printing the output into the console
		 */
		a.add("digraph {");
		if (debug) {
			a.add("\n");
		}
		a.add("splines=true;");
		if (debug) {
			a.add("\n");
		}
		a.add("nodesep=.01;");
		if (debug) {
			a.add("\n");
		}
		a.add("rankdir=LR;");
		if (debug) {
			a.add("\n");
		}
		a.add("node[fontsize=8,shape=record,height=.1];");
		a.add("ht[fontsize=12,label=\"" + getStringListOfNodes(hTable) + "\"];");

		int countEntry = 1;
		for (int index = 0; index < this.hTable.length; index++)
			if (!this.indexAvailable(index)) {
				a.add("node" + countEntry + "[label=" + this.entryToDotString(index) + "];");
				a.add("\n");
				countEntry++;
			}

		countEntry = 1;
		for (int index = 0; index < this.hTable.length; index++)
			if (!this.indexAvailable(index)) {
				a.add("ht:f" + index + "->node" + countEntry + ":l;");
				a.add("\n");
				countEntry++;
			}

		a.add("}");
		if (debug)
			System.out.println(a.toString());

		return a;
	}

	/**
	 * 
	 * @param hTable
	 *            the hashtable on which to apply the function
	 * @return returns the needed format for dot language
	 */
	private String getStringListOfNodes(Entry[] hTable) {
		String toReturn = "";
		for (int i = 0; i < hTable.length - 1; i++)
			toReturn += "<f" + i + ">" + i + "|";
		return toReturn + "<f" + (hTable.length - 1) + ">" + (hTable.length - 1);
	}

	/**
	 * This method increases the capacity of the Hash-Table and reorganizes it, in
	 * order to accommodate and access its entries more efficiently. This method is
	 * called automatically when the load factor exceeds 75%. To increase the size
	 * of the Hash-Table, you multiply the actual capacity by 10 and search for the
	 * closest primary number less than the result of this multiplication. For
	 * example if the actual capacity of the Hash-Table is 101, the capacity will be
	 * increased to 1009, which is the closest primary number less than (101*10).
	 */

	/**
	 * TODO: Vergleichen
	 */
	@SuppressWarnings("unchecked")
	private void rehash() {
		if (((double) cap) / size < 0.75)
			return;
		this.size = this.calcNewSize(this.size, 10);

		Entry[] oldHtable = this.hTable;
		this.hTable = new Entry[this.size];
		this.insertionSeq = new ArrayList[this.size];
		this.addrL = (int) Math.ceil(Math.log10(size)); // Taken from :
														// https://moodle.informatik.tu-darmstadt.de/mod/forum/discuss.php?d=14372
		this.cap = 0;

		for (Entry curEntry : oldHtable)
			if (!(curEntry == null || curEntry.isDeleted())) {
				this.insert(curEntry);
			}
	}

	/**
	 * Calculates a new size for a full hash table by finding the closest Prime
	 * number to the calculation 10* old factor
	 * 
	 * @param oldSize
	 *            the old size of the hashtable
	 * @param increaseFactor
	 *            the factor of which the hash table will be increased ( default is
	 *            set to 10)
	 * @return returns the new size of the hashtable
	 */
	private int calcNewSize(int oldSize, int increaseFactor) {
		BigInteger size1 = BigInteger.valueOf((oldSize) * increaseFactor / 2).nextProbablePrime();
		BigInteger size2 = BigInteger.valueOf(size1.longValue() + 1).nextProbablePrime();

		int upperBound = oldSize * increaseFactor;

		while (size2.longValue() < upperBound) {
			size1 = size2;
			size2 = BigInteger.valueOf(size1.longValue() + 1).nextProbablePrime();
		}
		return (int) size1.longValue();
	}

	/**
	 * Default case for the hashFunction is division; Default case for the collision
	 * resolution function is linear probing
	 * 
	 * @param entryKey the string of the Key
	 * @param numberOfIterations the number of iterations done by the collision resolution function
	 * @return returns the final position of the value. Collision resultion is already applied
	 */

	public int hash(String entryKey) {
		return calculateIndex(this.hTable, entryKey, 0);
	}

	public int calculateIndex(String entryKey, int numberOfIterations) {
		return calculateIndex(this.hTable, entryKey, numberOfIterations);
	}

	private int calculateIndex(Entry[] hTable, String entryKey, int numberOfIterations) {
		// A String Builder so we can make a Ascii Value out of the String
		StringBuilder asciiHelper = new StringBuilder();

		// BigInteger to store the Ascii Value as an (Big)Integer
		BigInteger key = BigInteger.valueOf(0);

		// First 5 characters of the Array are made into an Ascii-String
		for (int i = 0; i < 5; i++) {
			int ascii = (int) entryKey.charAt(i);
			asciiHelper.append(ascii);
		}

		// Now we make a Int out of the String out of the
		// Key is now our Ascii-Value represented as an BigInteger!
		key = BigInteger.valueOf(Long.parseLong(asciiHelper.toString()));

		// default case is division (to catch mistakes...)
		Function<BigInteger, Integer> hFunc = this::divison;
		switch (this.hFunction) {
		case "folding":
			hFunc = this::folding;
			break;
		case "mid_square":
			hFunc = this::mid_square;
			break;
		}

		if (this.cResolution.equals("quadratic_probing"))
			return quadratic_probing(hFunc, key, numberOfIterations, hTable.length);
		return linear_probing(hFunc, key, numberOfIterations, hTable.length);
	}

	/**
	 * the key is divided modulo by the size of the hashtable
	 * @param value
	 *            receives the key of the entry as a BigInteger value already
	 *            formatted ascii
	 * @return returns the result of the value hashed via the division method
	 */

	private int divison(BigInteger value) {
		// Calculates the value mod the size and turns the big Integer into a int value
		return value.mod(BigInteger.valueOf(size)).intValue();
	} // End of division

	/**
	 * folding splits the ascii representation of the value into even parts which are determined 
	 * Adresslength. these parts are then folded together from the right by reading alternating 
	 * from right to left
	 * @param value
	 *            receives the key of the entry as a BigInteger value already
	 *            formatted into ascii
	 * @return returns the result of the value hashed via the folding method
	 */

	private int folding(BigInteger value) {
		// the int that will store our result
		int key = 0;
		// String representation of our value with which we will calculate the hashIndex
		String keyAsString = value.toString();

		// Add zeroes to the beginning until it matches the format
		while (keyAsString.length() % this.addrL != 0) {
			keyAsString = "0" + keyAsString;
		}

		// a helpfull int which will return us if we need to read from left to right or
		// right to left
		int alternating = 0;

		// we continue until all "blocks" are read
		for (int i = keyAsString.length() / this.addrL - 1; i >= 0; i--) {
			int index = i * this.addrL;

			// this ensures that each second result (beginning with 0) will be read from
			// right to left
			if (alternating % 2 == 0) {
				String reverse = new StringBuffer(keyAsString.substring(index, index + this.addrL)).reverse()
						.toString();

				key += Integer.parseInt(reverse);
			}

			else {
				key += Integer.parseInt(keyAsString.substring(index, index + this.addrL));
			}

			alternating++;
		}

		keyAsString = String.valueOf(key);

		// If the value we just calculated is bigger then the adress length, just remove
		// the first element of the string until it matches
		while (String.valueOf(key).length() > this.addrL) {
			keyAsString = keyAsString.substring(1);
			key = Integer.parseInt(keyAsString);
		}

		// If the key is bigger then our actual size of the hash table it wont fit, so
		// we just divide it modulo so it will fit
		key = key % this.size;
		return key;

	} // end of folding

	/**
	 * 
	 * @param value
	 *            receives the key of the entry as a BigInteger value already
	 *            formatted in ascii
	 * @return returns the result of the value hashed via the mid square method
	 */
	private int mid_square(BigInteger value) {

		BigInteger bigKey = value;
		BigInteger square = bigKey.pow(2);
		String squareString = square.toString();
		String sKey = squareString.substring((squareString.length() - 10) - (addrL - 1), squareString.length() - 9);
		return Integer.parseInt(sKey) % size;
	}// end of mid square

	/**
	 Receives a index in the hash table which is occupied and calculates with the
	 * given hashfunction the new index. 
	 * The collision resolution is quadratic Probing, which means that the new Index is calculated
	 * Linear Probing means that the next free index is choosen.
	 * This means that clusters will occur more often, but this is also less demanding
	 * @param hFunction
	 *            receives the hashfunction which is used in the hashtable
	 * @param key
	 *            receives the key which will be calculated (already in
	 *            ascii-format!)
	 * @param numberOfIterations
	 *            needs to be greater or equals to zero
	 * @return
	 */
	private int linear_probing(Function<BigInteger, Integer> hFunction, BigInteger key, int numberOfIterations,
			int size) {
		int tmp = (hFunction.apply(key) + numberOfIterations) % size;
		while (tmp < 0)
			tmp += size;
		return tmp % size;
	}

	/**
	 * Receives a index in the hash table which is occupied and calculates with the
	 * given hashfunction the new index. 
	 * The collision resolution is quadratic Probing, which means that the new Index is calculated:
	 * 
	 * @param hFunction
	 *            receives the type of hashfunction which is set by the Hashtable
	 * @param key
	 *            the value which was calculated (which was occupied)
	 * @param numberOfIterations
	 *            needs to be greater or equals to zero
	 * @return
	 */

	private int quadratic_probing(Function<BigInteger, Integer> hFunction, BigInteger key, int numberOfIterations,
			int size) {
		int h0 = (hFunction.apply(key));
		double h1 = (((double) numberOfIterations) / 2) * (((double) numberOfIterations) / 2);
		int h2 = 1;

		if (numberOfIterations % 2 == 1)
			h2 = -1;

		int tmp = (h0 - (((int) Math.ceil(h1)) * h2)) % size;

		if (tmp < 0) {
			tmp += size;
		}

		return tmp;
	}

	/**
	 * Receives an index and checks whether the index is available. This is the case
	 * either if the given index is null or if the previous index is marked as
	 * deleted
	 * 
	 * @param index
	 *            the index on which to check
	 * @return returns true if the index is available ( deleted or free )
	 */

	private boolean indexAvailable(int index) {
		if (this.hTable[index] == null)
			return true;
		if (this.hTable[index].isDeleted())
			return true;
		return false;
	}

	/**
	 * 
	 * @param indexEntry
	 * @param insertionSeq
	 */

	private void setInsertionSeq(int indexEntry, ArrayList<Integer> insertionSeq) {
		this.insertionSeq[indexEntry] = insertionSeq;
	}

	/**
	 * 
	 * @param indexOfEntry
	 * @return returns the entry as a Dot-Language value which directly can be
	 *         inserted into the output being as the format:
	 *         {<l>"BOOKSERIALNUMBER""READERID"|STATUS(OPTIONAL): |PREVIOUS
	 *         CALCULATED INDEXES, SEPARATED BY ,}
	 */
	private String entryToDotString(int indexOfEntry) {
		Entry e = this.hTable[indexOfEntry];
		ArrayList<Integer> insertionSeq = this.insertionSeq[indexOfEntry];
		String r = "\"{<l>";
		if (e.getKey() != null)
			r += e.getKey();
		if (e.getData() != null)
			r += "|" + e.getData();
		if (this.insertionSeq[indexOfEntry].size() > 0) {
			r += "|";
			for (int i = 0; i < insertionSeq.size() - 1; i++)
				r += insertionSeq.get(i) + ", ";
			r += insertionSeq.get(insertionSeq.size() - 1);
		}
		return r + "}\"";
	}
}