package frame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lab.HashTable;

public class AllTests {
	private Duration timeout = Duration.ofMillis(350);
	private Duration largeAddressTimeout = Duration.ofMillis(6500);
	private static ArrayList<String> testData;

	private int[] getAdresses(ArrayList<String> table, String key) {

		for (String line : table) {
			if (line.matches(".*" + key + ".*")) {
				String[] lastPart = line.split("\\x7C");
				if (lastPart.length == 3) {
					String allNumbersAndEnd = lastPart[line.split("\\x7C").length - 1].substring(0,
							lastPart[line.split("\\x7C").length - 1].indexOf("}"));
					String[] allNumbers = allNumbersAndEnd.split(",");
					int[] numbers = new int[allNumbers.length];
					for (int i = 0; i < allNumbers.length; i++) {
						numbers[i] = Integer.valueOf(allNumbers[i].trim());
					}
					return numbers;
				}
			}
		}

		return null;
	}

	private int getPosition(ArrayList<String> table, String key) {

		String nodeName = "";
		int position = -1;
		for (String line : table) {
			if (line.matches(".*" + key + ".*")) {
				String[] parts = line.split("\\x5B");
				nodeName = parts[0];
				break;

			}
		}
		if (!nodeName.equals("")) {
			for (String line : table) {
				if (line.matches(".*" + nodeName + ":" + ".*")) {
					String[] parts = line.split("\\x2D\\x3E");
					if (parts.length > 1) {
						String firstPart = parts[0];
						firstPart = firstPart.replace("ht:f", "");
						position = Integer.parseInt(firstPart);
						break;
					}
				}

			}
		}

		return position;
	}

	private int getCapacity(ArrayList<String> table) {
		int capacity = 0;

		String line = table.get(5);

		String[] parts = line.split("\\x7C");

		String lastEntry = parts[parts.length - 1];

		int firstIndex = lastEntry.indexOf(">");
		int lastIndex = lastEntry.indexOf("\"");

		lastEntry = lastEntry.substring(firstIndex + 1, lastIndex);

		capacity = Integer.parseInt(lastEntry);

		return capacity + 1;

	}

	private void printArrayList(ArrayList<String> dot, String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName + ".txt");
			BufferedWriter bw = new BufferedWriter(fw);

			for (String string : dot) {
				bw.write(string + System.getProperty("line.separator"));
			}

			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<String> getTestData() {
		ArrayList<String> testData = new ArrayList<>();
		String keyPrefix = "ABCDEA";
		for (char c1 = 'A'; c1 != 'Z'; c1++) {
			for (char c2 = 'A'; c2 != 'Z'; c2++) {
				for (char c3 = 'A'; c3 != 'C'; c3++) {
					testData.add(keyPrefix + c1 + c2 + c3);
				}
			}
		}
		return testData;
	}

	@BeforeAll
	public static void init() {
		testData = AllTests.getTestData();
	}

	@Test
	public void testLargeAddressLengths_FoldingLinear() {
		assertTimeoutPreemptively(largeAddressTimeout, () -> {
			String testkey = "ABCDEANXA";
			String KeyAtHomePosition = "ABCDEANBA";
			HashTable table = new HashTable(10, "folding", "linear_probing");
			for (String s : testData) {
				Entry e = new Entry();
				e.setKey(s);
				e.setData("ok");
				table.insert(e);
			}
			ArrayList<String> dot = table.getHashTable();
			int[] addresses = getAdresses(dot, testkey);
			int[] addresses2 = getAdresses(dot, KeyAtHomePosition);
			int tableCapacity = getCapacity(dot);
			assertEquals(44, addresses.length, "folding error at large addresses");
			assertEquals(9661, tableCapacity, "Hashtable capacity must be equal to 9661");
			assertNull(addresses2, "key ABCDEANBA must be at home position");
		});
	}

	@Test
	public void testLargeAddressLengths_FoldingQuadratic() {
		assertTimeoutPreemptively(largeAddressTimeout, () -> {
			String testkey = "ABCDEAJQA";
			String KeyAtHomePosition = "ABCDEALJA";
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			for (String s : testData) {
				Entry e = new Entry();
				e.setKey(s);
				e.setData("ok");
				table.insert(e);
			}
			ArrayList<String> dot = table.getHashTable();
			int[] addresses = getAdresses(dot, testkey);
			int[] addresses2 = getAdresses(dot, KeyAtHomePosition);
			int tableCapacity = getCapacity(dot);
			assertEquals(160, addresses.length, "folding error at large addresses");
			assertEquals(9661, tableCapacity, "Hashtable capacity must be equal to 9661");
			assertNull(addresses2, "key ABCDEALJA must be at home position");
		});
	}

	@Test
	public void testLargeAddressLengths_MidsquareLinear() {
		assertTimeoutPreemptively(largeAddressTimeout, () -> {
			String testkey = "ABCDEAJQA";
			String KeyAtHomePosition = "ABCDEAAAA";
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			for (String s : testData) {
				Entry e = new Entry();
				e.setKey(s);
				e.setData("ok");
				table.insert(e);
			}
			ArrayList<String> dot = table.getHashTable();
			int[] addresses = getAdresses(dot, testkey);
			int[] addresses2 = getAdresses(dot, KeyAtHomePosition);
			int tableCapacity = getCapacity(dot);
			assertEquals(482, addresses.length, "folding error at large addresses");
			assertEquals(9661, tableCapacity, "Hashtable capacity must be equal to 9661");
			assertNull(addresses2, "key ABCDEALJA must be at home position");
		});
	}

	@Test
	public void testLargeAddressLengths_MidsquareQuadratic() {
		assertTimeoutPreemptively(largeAddressTimeout, () -> {
			String testkey = "ABCDEAJQA";
			String KeyAtHomePosition = "ABCDEACLB";
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			for (String s : testData) {
				Entry e = new Entry();
				e.setKey(s);
				e.setData("ok");
				table.insert(e);
			}
			ArrayList<String> dot = table.getHashTable();
			int[] addresses = getAdresses(dot, testkey);
			int[] addresses2 = getAdresses(dot, KeyAtHomePosition);
			int tableCapacity = getCapacity(dot);
			assertEquals(736, addresses.length, "folding error at large addresses");
			assertEquals(9661, tableCapacity, "Hashtable capacity must be equal to 9661");
			assertNull(addresses2, "key ABCDEALJA must be at home position");
		});
	}

	@Test
	public void testCollisionsTestFile2_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "S04XXGKQ3");
			assertNotNull(addresses, "a collision must have happened for key S04XXGKQ3");
			assertEquals(144, addresses[0], "Home address must be at 144");
			addresses = getAdresses(dotOutput, "DJF2K2E7O");
			assertNotNull(addresses, "a collision must have happened for key DJF2K2E7O");
			assertEquals(371, addresses[0], "Home address must be at 371");
			addresses = getAdresses(dotOutput, "9SIVZX8HM");
			assertNotNull(addresses, "a collision must have happened for key 9SIVZX8HM");
			assertEquals(485, addresses[0], "Home address must be at 485");
			addresses = getAdresses(dotOutput, "JQ645DIXK");
			assertNotNull(addresses, "a collision must have happened for key JQ645DIXK");
			assertEquals(666, addresses[0], "Home address must be at 666");
			addresses = getAdresses(dotOutput, "GZYOHTCZ5");
			assertNotNull(addresses, "a collision must have happened for key GZYOHTCZ5");
			assertEquals(707, addresses[0], "Home address must be at 707");
		});
	}

	@Test
	public void testCorrectPositionTestFile2_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "XQ6GDLTHX");
			assertNull(addresses, "key XQ6GDLTHX is not at original position");
			int position = getPosition(dotOutput, "XQ6GDLTHX");
			assertEquals(288, position, "Home address must be at 288");
			addresses = getAdresses(dotOutput, "PBNXCLREK");
			assertNull(addresses, "key PBNXCLREK is not at original position");
			position = getPosition(dotOutput, "PBNXCLREK");
			assertEquals(408, position, "Home address must be at 408");
			addresses = getAdresses(dotOutput, "C4X08HFX4");
			assertNull(addresses, "key C4X08HFX4 is not at original position");
			position = getPosition(dotOutput, "C4X08HFX4");
			assertEquals(878, position, "Home address must be at 878");
		});
	}

	@Test
	public void testCapacityTestFile2_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			assertEquals(967, getCapacity(dotOutput), "Capacity must be 967");
		});
	}

	@Test
	public void testCollisionsTestFile2_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "S04XX3HO6");
			assertNotNull(addresses, "a collision must have happened for key S04XX3HO6");
			assertEquals(144, addresses[0], "Home address must be at 144");
			addresses = getAdresses(dotOutput, "DJF2K2E7O");
			assertNotNull(addresses, "a collision must have happened for key DJF2K2E7O");
			assertEquals(371, addresses[0], "Home address must be at 371");
			addresses = getAdresses(dotOutput, "9SIVZX8HM");
			assertNotNull(addresses, "a collision must have happened for key 9SIVZX8HM");
			assertEquals(485, addresses[0], "Home address must be at 485");
			addresses = getAdresses(dotOutput, "XHOY5BAKC");
			assertNotNull(addresses, "a collision must have happened for key XHOY5BAKC");
			assertEquals(522, addresses[0], "Home address must be at 522");
			addresses = getAdresses(dotOutput, "JQ645DIXK");
			assertNotNull(addresses, "a collision must have happened for key JQ645DIXK");
			assertEquals(666, addresses[0], "Home address must be at 666");
			addresses = getAdresses(dotOutput, "GZYOHTCZ5");
			assertNotNull(addresses, "a collision must have happened for key GZYOHTCZ5");
			assertEquals(707, addresses[0], "Home address must be at 707");
		});
	}

	@Test
	public void testCorrectPositionTestFile2_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "G4BFF9ZFS");
			assertNull(addresses, "key G4BFF9ZFS is not at original position");
			int position = getPosition(dotOutput, "G4BFF9ZFS");
			assertEquals(150, position, "Home address must be at 150");
			addresses = getAdresses(dotOutput, "6QCD2DO9K");
			assertNull(addresses, "key 6QCD2DO9K is not at original position");
			position = getPosition(dotOutput, "6QCD2DO9K");
			assertEquals(435, position, "Home address must be at 435");
			addresses = getAdresses(dotOutput, "A3HSF7DJL");
			assertNull(addresses, "key A3HSF7DJL is not at original position");
			position = getPosition(dotOutput, "A3HSF7DJL");
			assertEquals(699, position, "Home address must be at 699");
		});
	}

	@Test
	public void testCapacityTestFile2_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			assertEquals(967, getCapacity(dotOutput), "Capacity must be 967");
		});
	}

	@Test
	public void testCollisionsTestFile2_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "UGWAQDPJJ");
			assertNotNull(addresses, "a collision must have happened for key UGWAQDPJJ");
			assertEquals(119, addresses[0], "Home address must be at 119");
			addresses = getAdresses(dotOutput, "9SIVZX8HM");
			assertNotNull(addresses, "a collision must have happened for key 9SIVZX8HM");
			assertEquals(234, addresses[0], "Home address must be at 234");
			addresses = getAdresses(dotOutput, "7M5BEM4PI");
			assertNotNull(addresses, "a collision must have happened for key 7M5BEM4PI");
			assertEquals(294, addresses[0], "Home address must be at 294");
			addresses = getAdresses(dotOutput, "TTJADFKQ4");
			assertNotNull(addresses, "a collision must have happened for key TTJADFKQ4");
			assertEquals(323, addresses[0], "Home address must be at 323");
			addresses = getAdresses(dotOutput, "JQ645DIXK");
			assertNotNull(addresses, "a collision must have happened for key JQ645DIXK");
			assertEquals(372, addresses[0], "Home address must be at 372");
			addresses = getAdresses(dotOutput, "S04XXGKQ3");
			assertNotNull(addresses, "a collision must have happened for key S04XXGKQ3");
			assertEquals(593, addresses[0], "Home address must be at 593");
			addresses = getAdresses(dotOutput, "Q04KJYCC4");
			assertNotNull(addresses, "a collision must have happened for key Q04KJYCC4");
			assertEquals(624, addresses[0], "Home address must be at 624");
		});
	}

	@Test
	public void testCorrectPositionTestFile2_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "6QCD2DO9K");
			assertNull(addresses, "key 6QCD2DO9K is not at original position");
			int position = getPosition(dotOutput, "6QCD2DO9K");
			assertEquals(87, position, "Home address must be at 87");
			addresses = getAdresses(dotOutput, "T29EP81RL");
			assertNull(addresses, "key T29EP81RL is not at original position");
			position = getPosition(dotOutput, "T29EP81RL");
			assertEquals(294, position, "Home address must be at 294");
			addresses = getAdresses(dotOutput, "EFL0DH5HI");
			assertNull(addresses, "key EFL0DH5HI is not at original position");
			position = getPosition(dotOutput, "EFL0DH5HI");
			assertEquals(844, position, "Home address must be at 844");
		});
	}

	@Test
	public void testCapacityTestFile2_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			assertEquals(967, getCapacity(dotOutput), "Capacity must be 967");
		});
	}

	@Test
	public void testCollisionsTestFile2_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "UGWAQDPJJ");
			assertNotNull(addresses, "a collision must have happened for key UGWAQDPJJ");
			assertEquals(119, addresses[0], "Home address must be at 119");
			addresses = getAdresses(dotOutput, "9SIVZX8HM");
			assertNotNull(addresses, "a collision must have happened for key 9SIVZX8HM");
			assertEquals(234, addresses[0], "Home address must be at 234");
			addresses = getAdresses(dotOutput, "7M5BEM4PI");
			assertNotNull(addresses, "a collision must have happened for key 7M5BEM4PI");
			assertEquals(294, addresses[0], "Home address must be at 294");
			addresses = getAdresses(dotOutput, "TTJADFKQ4");
			assertNotNull(addresses, "a collision must have happened for key TTJADFKQ4");
			assertEquals(323, addresses[0], "Home address must be at 323");
			addresses = getAdresses(dotOutput, "JQ645DIXK");
			assertNotNull(addresses, "a collision must have happened for key JQ645DIXK");
			assertEquals(372, addresses[0], "Home address must be at 372");
			addresses = getAdresses(dotOutput, "S04XXGKQ3");
			assertNotNull(addresses, "a collision must have happened for key S04XXGKQ3");
			assertEquals(593, addresses[0], "Home address must be at 593");
			addresses = getAdresses(dotOutput, "Q04KJYCC4");
			assertNotNull(addresses, "a collision must have happened for key Q04KJYCC4");
			assertEquals(624, addresses[0], "Home address must be at 624");
		});
	}

	@Test
	public void testCorrectPositionTestFile2_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "XQ6GDLTHX");
			assertNull(addresses, "key XQ6GDLTHX is not at original position");
			int position = getPosition(dotOutput, "XQ6GDLTHX");
			assertEquals(97, position, "Home address must be at 97");
			addresses = getAdresses(dotOutput, "G4BFF9ZFS");
			assertNull(addresses, "key G4BFF9ZFS is not at original position");
			position = getPosition(dotOutput, "G4BFF9ZFS");
			assertEquals(214, position, "Home address must be at 214");
			addresses = getAdresses(dotOutput, "8AJFD2PFF");
			assertNull(addresses, "key 8AJFD2PFF is not at original position");
			position = getPosition(dotOutput, "8AJFD2PFF");
			assertEquals(838, position, "Home address must be at 838");
		});
	}

	@Test
	public void testCapacityTestFile2_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			assertEquals(967, getCapacity(dotOutput), "Capacity must be 967");
		});
	}

	@Test
	public void testCollisionsTestFile2_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "JQ645DIXK");
			assertNotNull(addresses, "a collision must have happened for key JQ645DIXK");
			assertEquals(88, addresses[0], "Home address must be at 88");
			addresses = getAdresses(dotOutput, "BR1LSTBSC");
			assertNotNull(addresses, "a collision must have happened for key BR1LSTBSC");
			assertEquals(175, addresses[0], "Home address must be at 175");
			addresses = getAdresses(dotOutput, "9SIVZX8HM");
			assertNotNull(addresses, "a collision must have happened for key 9SIVZX8HM");
			assertEquals(226, addresses[0], "Home address must be at 226");
			addresses = getAdresses(dotOutput, "S04XXGKQ3");
			assertNotNull(addresses, "a collision must have happened for key S04XXGKQ3");
			assertEquals(267, addresses[0], "Home address must be at 267");
		});
	}

	@Test
	public void testCorrectPositionTestFile2_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "HGY62F5SR");
			assertNull(addresses, "key HGY62F5SR is not at original position");
			int position = getPosition(dotOutput, "HGY62F5SR");
			assertEquals(128, position, "Home address must be at 128");
			addresses = getAdresses(dotOutput, "L97IOPCZY");
			assertNull(addresses, "key L97IOPCZY is not at original position");
			position = getPosition(dotOutput, "L97IOPCZY");
			assertEquals(293, position, "Home address must be at 293");
			addresses = getAdresses(dotOutput, "LMD2QXICN");
			assertNull(addresses, "key LMD2QXICN is not at original position");
			position = getPosition(dotOutput, "LMD2QXICN");
			assertEquals(648, position, "Home address must be at 648");
		});
	}

	@Test
	public void testCapacityTestFile2_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "linear_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			assertEquals(967, getCapacity(dotOutput), "Capacity must be 967");
		});
	}

	@Test
	public void testCollisionsTestFile2_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "JQ645DIXK");
			assertNotNull(addresses, "a collision must have happened for key JQ645DIXK");
			assertEquals(88, addresses[0], "Home address must be at 88");
			addresses = getAdresses(dotOutput, "BR1LSTBSC");
			assertNotNull(addresses, "a collision must have happened for key BR1LSTBSC");
			assertEquals(175, addresses[0], "Home address must be at 175");
			addresses = getAdresses(dotOutput, "9SIVZX8HM");
			assertNotNull(addresses, "a collision must have happened for key 9SIVZX8HM");
			assertEquals(226, addresses[0], "Home address must be at 226");
			addresses = getAdresses(dotOutput, "S04XXGKQ3");
			assertNotNull(addresses, "a collision must have happened for key S04XXGKQ3");
			assertEquals(267, addresses[0], "Home address must be at 267");
		});
	}

	@Test
	public void testCorrectPositionTestFile2_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			int[] addresses = getAdresses(dotOutput, "XQ6GDLTHX");
			assertNull(addresses, "key XQ6GDLTHX is not at original position");
			int position = getPosition(dotOutput, "XQ6GDLTHX");
			assertEquals(604, position, "Home address must be at 604");
			addresses = getAdresses(dotOutput, "G4BFF9ZFS");
			assertNull(addresses, "key G4BFF9ZFS is not at original position");
			position = getPosition(dotOutput, "G4BFF9ZFS");
			assertEquals(28, position, "Home address must be at 28");
			addresses = getAdresses(dotOutput, "8AJFD2PFF");
			assertNull(addresses, "key 8AJFD2PFF is not at original position");
			position = getPosition(dotOutput, "8AJFD2PFF");
			assertEquals(178, position, "Home address must be at 178");
		});
	}

	@Test
	public void testCapacityTestFile2_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			table.loadFromFile("TestFile2");
			ArrayList<String> dotOutput = table.getHashTable();
			assertEquals(967, getCapacity(dotOutput), "Capacity must be 967");
		});
	}

	@Test
	public void testReadTestFile1_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "linear_probing");
			int loaded = table.loadFromFile("TestFile1");
			assertTrue(loaded == 19, "Didn't load 19 entries from TestFile1 with division and linear_probing.");
		});
	}

	@Test
	public void testReadTestFile2_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "linear_probing");
			int loaded = table.loadFromFile("TestFile2");
			assertTrue(loaded == 99, "Didn't load 99 entries from TestFile2 with division and linear_probing.");
		});
	}

	@Test
	public void testInsert_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "linear_probing");
			assertTrue(table.insert(testEntry1));
		});
	}

	@Test
	public void testFind_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "linear_probing");
			boolean inserted = table.insert(testEntry1);
			assertTrue(inserted);
			Entry foundEntry = table.find(testEntry1.getKey());
			assertNotNull(foundEntry, "Didn't find Entry " + testEntry1.getKey() + ".");
			assertTrue(foundEntry == testEntry1, "Inserted Entry " + testEntry1.getKey() + " and found Entry "
					+ foundEntry.getKey() + " are not the same.");
		});
	}

	@Test
	public void testDelete_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "linear_probing");
			boolean inserted = table.insert(testEntry1);
			assertTrue(inserted);
			Entry deletedEntry = table.delete(testEntry1.getKey());
			assertNotNull(deletedEntry);
			Entry e = table.find(testEntry1.getKey());
			assertNull(e);
		});
	}

	@Test
	public void testHomeAdress_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "linear_probing");
			table.insert(testEntry1);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry1.getKey());
			assertTrue(adresses == null, testEntry1.getKey() + " is not at home adress.");
		});
	}

	@Test
	public void testCollisionAdress_DivisionLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			Entry testEntry2 = new Entry();
			testEntry2.setKey("X8IG4LDXS");
			testEntry2.setData("OK");
			HashTable table = new HashTable(10, "division", "linear_probing");
			table.insert(testEntry1);
			table.insert(testEntry2);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry2.getKey());
			assertTrue(adresses != null, testEntry2.getKey() + " is at home adress.");
			assertTrue(adresses.length == 1 && adresses[0] == 2,
					testEntry2.getKey() + " should have home adress 2.");
		});
	}

	@Test
	public void testReadTestFile1_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			assertTrue(table.loadFromFile("TestFile1") == 19,
					"Didn't load 19 entries from TestFile1 with division and quadratic_probing.");
		});
	}

	@Test
	public void testReadTestFile2_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			assertTrue(table.loadFromFile("TestFile2") == 99,
					"Didn't load 99 entries from TestFile2 with division and quadratic_probing.");
		});
	}

	@Test
	public void testInsert_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
		});
	}

	@Test
	public void testDelete_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
			assertNotNull(table.delete(testEntry1.getKey()));
			assertNull(table.find(testEntry1.getKey()));
		});
	}

	@Test
	public void testFind_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
			Entry foundEntry = table.find(testEntry1.getKey());
			assertNotNull(foundEntry, "Didn't find Entry " + testEntry1.getKey() + ".");
			assertTrue(foundEntry == testEntry1, "Inserted Entry " + testEntry1.getKey() + " and found Entry "
					+ foundEntry.getKey() + " are not the same.");
		});
	}

	@Test
	public void testHomeAdress_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			table.insert(testEntry1);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry1.getKey());
			assertTrue(adresses == null, testEntry1.getKey() + " is not at home adress.");
		});
	}

	@Test
	public void testCollisionAdress_DivisionQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			Entry testEntry2 = new Entry();
			testEntry2.setKey("X8IG4LDXS");
			testEntry2.setData("OK");
			HashTable table = new HashTable(10, "division", "quadratic_probing");
			table.insert(testEntry1);
			table.insert(testEntry2);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry2.getKey());
			assertTrue(adresses != null, testEntry2.getKey() + " is at home adress.");
			assertTrue(adresses.length == 1 && adresses[0] == 2,
					testEntry2.getKey() + " should have home adress 2.");
		});
	}

	@Test
	public void testReadTestFile1_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			assertTrue(table.loadFromFile("TestFile1") == 19,
					"Didn't load 19 entries from TestFile1 with mid_square and linear_probing.");
		});
	}

	@Test
	public void testReadTestFile2_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			assertTrue(table.loadFromFile("TestFile2") == 99,
					"Didn't load 99 entries from TestFile2 with mid_square and linear_probing.");
		});
	}

	@Test
	public void testInsert_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			assertNotNull(table.insert(testEntry1));
		});
	}

	@Test
	public void testFind_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			assertNotNull(table.insert(testEntry1));
			Entry foundEntry = table.find(testEntry1.getKey());
			assertNotNull(foundEntry, ("Didn't find Entry " + testEntry1.getKey() + "."));
			assertTrue(foundEntry == testEntry1, "Inserted Entry " + testEntry1.getKey() + " and found Entry "
					+ foundEntry.getKey() + " are not the same.");
		});
	}

	@Test
	public void testDelete_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			assertNotNull(table.insert(testEntry1));
			assertNotNull(table.delete(testEntry1.getKey()));
			assertNull(table.find(testEntry1.getKey()));
		});
	}

	@Test
	public void testHomeAdress_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			table.insert(testEntry1);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry1.getKey());
			assertTrue(adresses == null, testEntry1.getKey() + " is not at home adress.");
		});
	}

	@Test
	public void testCollisionAdress_MidSquareLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			Entry testEntry2 = new Entry();
			testEntry2.setKey("F5HF8MECA");
			testEntry2.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "linear_probing");
			table.insert(testEntry1);
			table.insert(testEntry2);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry2.getKey());
			assertTrue(adresses != null, testEntry2.getKey() + " is at home adress.");
			assertTrue(adresses.length == 1 && adresses[0] == 0,
					testEntry2.getKey() + " should have home adress 0.");
		});
	}

	@Test
	public void testReadTestFile1_MidSquareQuadractic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			assertTrue(table.loadFromFile("TestFile1") == 19,
					"Didn't load 19 entries from TestFile1 with mid_square and quadratic_probing.");
		});
	}

	@Test
	public void testReadTestFile2_MidSquareQuadractic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			int loaded = table.loadFromFile("TestFile2");
			assertTrue(loaded == 99, "Didn't load 99 entries from TestFile2 with mid_square and quadratic_probing.");
		});
	}

	@Test
	public void testInsert_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
		});
	}

	@Test
	public void testFind_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
			Entry foundEntry = table.find(testEntry1.getKey());
			assertNotNull(foundEntry, "Didn't find Entry " + testEntry1.getKey() + ".");
			assertTrue(foundEntry == testEntry1, "Inserted Entry " + testEntry1.getKey() + " and found Entry "
					+ foundEntry.getKey() + " are not the same.");
		});
	}

	@Test
	public void testDelete_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
			assertNotNull(table.delete(testEntry1.getKey()));
			assertNull(table.find(testEntry1.getKey()));
		});
	}

	@Test
	public void testHomeAdress_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			table.insert(testEntry1);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry1.getKey());
			assertTrue(adresses == null, testEntry1.getKey() + " is not at home adress.");
		});
	}

	@Test
	public void testCollisionAdress_MidSquareQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			Entry testEntry2 = new Entry();
			testEntry2.setKey("F5HF8MECA");
			testEntry2.setData("OK");
			HashTable table = new HashTable(10, "mid_square", "quadratic_probing");
			table.insert(testEntry1);
			table.insert(testEntry2);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry2.getKey());
			assertTrue(adresses != null, testEntry2.getKey() + " is at home adress.");
			assertTrue(adresses.length == 1 && adresses[0] == 0,
					testEntry2.getKey() + " should have home adress 0.");
		});
	}

	@Test
	public void testReadTestFile1_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "linear_probing");
			assertTrue(table.loadFromFile("TestFile1") == 19,
					"Didn't load 19 entries from TestFile1 with folding and linear_probing.");
		});
	}

	@Test
	public void testReadTestFile2_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "linear_probing");
			assertTrue(table.loadFromFile("TestFile2") == 99,
					"Didn't load 99 entries from TestFile2 with folding and linear_probing.");
		});
	}

	@Test
	public void testInsert_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "linear_probing");
			assertNotNull(table.insert(testEntry1));
		});
	}

	@Test
	public void testFind_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "linear_probing");
			assertNotNull(table.insert(testEntry1));
			Entry foundEntry = table.find(testEntry1.getKey());
			assertNotNull(foundEntry, "Didn't find Entry " + testEntry1.getKey() + ".");
			assertTrue(foundEntry == testEntry1, "Inserted Entry " + testEntry1.getKey() + " and found Entry "
					+ foundEntry.getKey() + " are not the same.");
		});
	}

	@Test
	public void testDelete_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "linear_probing");
			assertNotNull(table.insert(testEntry1));
			assertNotNull(table.delete(testEntry1.getKey()));
			assertNull(table.find(testEntry1.getKey()));
		});
	}

	@Test
	public void testHomeAdress_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "linear_probing");
			table.insert(testEntry1);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry1.getKey());
			assertTrue(adresses == null, testEntry1.getKey() + " is not at home adress.");
		});
	}

	@Test
	public void testCollisionAdress_FoldingLinear() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			Entry testEntry2 = new Entry();
			testEntry2.setKey("Z7IG5LDXS");
			testEntry2.setData("OK");
			HashTable table = new HashTable(10, "folding", "linear_probing");
			table.insert(testEntry1);
			table.insert(testEntry2);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry2.getKey());
			assertTrue(adresses != null, testEntry2.getKey() + " is at home adress.");
			assertTrue(adresses.length == 1 && adresses[0] == 5,
					testEntry2.getKey() + " should have home adress 5.");
		});
	}

	@Test
	public void testReadTestFile1_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			assertTrue(table.loadFromFile("TestFile1") == 19,
					"Didn't load 19 entries from TestFile1 with folding and quadratic_probing.");
		});
	}

	@Test
	public void testReadTestFile2_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			assertTrue(table.loadFromFile("TestFile2") == 99,
					"Didn't load 99 entries from TestFile2 with folding and quadratic_probing.");
		});
	}

	@Test
	public void testInsert_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
		});
	}

	@Test
	public void testFind_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
			Entry foundEntry = table.find(testEntry1.getKey());
			assertNotNull(foundEntry, "Didn't find Entry " + testEntry1.getKey() + ".");
			assertTrue(foundEntry == testEntry1, "Inserted Entry " + testEntry1.getKey() + " and found Entry "
					+ foundEntry.getKey() + " are not the same.");
		});
	}

	@Test
	public void testDelete_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("ABCDELDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			assertNotNull(table.insert(testEntry1));
			assertNotNull(table.delete(testEntry1.getKey()));
			assertNull(table.find(testEntry1.getKey()));
		});
	}

	@Test
	public void testHomeAdress_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			table.insert(testEntry1);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry1.getKey());
			assertTrue(adresses == null, testEntry1.getKey() + " is not at home adress.");
		});
	}

	@Test
	public void testCollisionAdress_FoldingQuadratic() {
		assertTimeoutPreemptively(timeout, () -> {
			Entry testEntry1 = new Entry();
			testEntry1.setKey("Z8IG4LDXS");
			testEntry1.setData("OK");
			Entry testEntry2 = new Entry();
			testEntry2.setKey("Z7IG5LDXS");
			testEntry2.setData("OK");
			HashTable table = new HashTable(10, "folding", "quadratic_probing");
			table.insert(testEntry1);
			table.insert(testEntry2);
			ArrayList<String> tableArrayList = table.getHashTable();
			int[] adresses = getAdresses(tableArrayList, testEntry2.getKey());
			assertTrue(adresses != null, testEntry2.getKey() + " is at home adress.");
			assertTrue(adresses.length == 1 && adresses[0] == 5,
					testEntry2.getKey() + " should have home adress 5.");
		});
	}
}