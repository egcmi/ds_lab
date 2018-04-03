import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 
 * Die Klasse HammingCode enthält Tools zum Kodieren und Dekodieren von Daten
 * mit dem Hamming Code (7,4). Aus diesem Grund ist sie abstrakt und alle
 * Klassenmethoden und Variablen sind statisch. Die zwei Hauptmethoden sind:
 * {@code void encode(String message, String filename)} und
 * {@code String decode(String filename)}, um jeweils eine String-Nachricht in
 * eine Datei zu kodieren und den Inhalt einer Datei in einen String von Zeichen
 * zu dekodieren. Beide Methoden sind die einzigen öffentlichen Methoden in der
 * Klasse. Die anderen Methoden sind privat und dienen lediglich als Hilfsmittel
 * zu den öffentlichen Methoden. Die Kodierung und Dekodierung wird durch
 * Multiplikation von booleschen Matrizen und Vektoren berechnet. Zu diesem
 * Zweck enthält diese Klasse drei zweidimensionale boolesche Arrays ({@code G},
 * {@code H} und {@code R}, auch private und statische) als Klassenvariablen,
 * die während der Berechnung verwendet werden. Aus Gründen der Einfachheit und
 * Effizienz der Berechnung wurden boolesche Datenstrukturen gegenüber anderen
 * ausgewählt.
 * 
 * @author Emanuela Giovanna Calabi - 13186
 * @author Angelo Rosace - 13386
 * 
 */
public abstract class HammingCode {

	/**
	 * G ist die Code-Generatormatrix. Die Zeilen 0, 1 und 3 von G bilden die
	 * Paritätsbits p1, p2 und p3 der Codewörter. Die {@code true} Werten der Zeilen
	 * geben hierbei an, welche Datenbitstellen in das jeweilige Paritätsbit
	 * eingerechnet werden. Die Zeilen 2, 4, 5 und 6 mit nur einer {@code true} Wert
	 * pro Zeile, bilden die Datenbits (d1, d2, d3, d4) im Codewort ab und bilden
	 * zusammen die Einheitsmatrix. (Wikipedia) Die Generatormatrix wird mit den
	 * vier Datenbits multipliziert, um ein gültiges Codewort zu erhalten.<br>
	 * Unten die grafische Darstellung der Matrix mit Nullen anstelle von Falschen
	 * und Einsen anstelle von Wahren zur einfacheren Visualisierung. <br>
	 * <br>
	 * {@code ---- d1 d2 d3 d4} <br>
	 * {@code p1 [ 1, 1, 0, 1 ]} <br>
	 * {@code p2 [ 1, 0, 1, 1 ]} <br>
	 * {@code d1 [ 1, 0, 0, 0 ]} <br>
	 * {@code p3 [ 0, 1, 1, 1 ]} <br>
	 * {@code d2 [ 0, 1, 0, 0 ]} <br>
	 * {@code d3 [ 0, 0, 1, 0 ]} <br>
	 * {@code d4 [ 0, 0, 0, 1 ]}
	 */
	private static final boolean[][] G = {
			{ true, true, false, true },
			{ true, false, true, true },
			{ true, false, false, false },
			{ false, true, true, true },
			{ false, true, false, false },
			{ false, false, true, false },
			{ false, false, false, true } };

	/**
	 * H ist die Kontrollmatrix. Sie wird verwendet, um den Syndromvektor auf der
	 * Empfängerseite zu berechnen, indem H mit einem Codewort multipliziert wird.
	 * Ist der Syndromvektor der Nullvektor (alle Nullen / Falschen), dann ist das
	 * empfangene Codewort fehlerfrei. Ansonsten ist sein Wert gleich einer Spalte
	 * von H, deren Index angibt, welches Bit umgedreht wurde, und das erlaubt seine
	 * Korrektur (Wikipedia)<br>
	 * Unten die grafische Darstellung der Matrix mit Nullen anstelle von Falschen
	 * und Einsen anstelle von Wahren zur einfacheren Visualisierung.<br>
	 * <br>
	 * {@code index 0--1--2--3--4--5--6} <br>
	 * {@code -p1 [ 1, 0, 1, 0, 1, 0, 1 ]}<br>
	 * {@code -p2 [ 0, 1, 1, 0, 0, 1, 1 ]}<br>
	 * {@code -p3 [ 0, 0, 0, 1, 1, 1, 1 ]}
	 */
	private static final boolean[][] H = {
			{ true, false, true, false, true, false, true },
			{ false, true, true, false, false, true, true },
			{ false, false, false, true, true, true, true } };

	/**
	 * R is die Einheitsmatrix für die Datenbits. Sie wird verwendet um eine
	 * korrekte Codewort zu dekodieren, indem R mit der Codewort multipliziert wird.
	 * Das Ergebnis sind die vier ursprünglichen Datenbits, wenn nicht mehr als ein
	 * Fehler aufgetreten ist. <br>
	 * Unten die grafische Darstellung der Matrix mit Nullen anstelle von Falschen
	 * und Einsen anstelle von Wahren zur einfacheren Visualisierung.<br>
	 * <br>
	 * {@code --p1 p2 d1 p3 d2 d3 d4} <br>
	 * {@code [ 0, 0, 1, 0, 0, 0, 0 ]} <br>
	 * {@code [ 0, 0, 0, 0, 1, 0, 0 ]} <br>
	 * {@code [ 0, 0, 0, 0, 0, 1, 0 ]} <br>
	 * {@code [ 0, 0, 0, 0, 0, 0, 1 ]}
	 */
	private static final boolean[][] R = {
			{ false, false, true, false, false, false, false },
			{ false, false, false, false, true, false, false },
			{ false, false, false, false, false, true, false },
			{ false, false, false, false, false, false, true } };

	/**
	 * 
	 * 
	 * @param message
	 *            eine zu kodierende Nachricht aus ASCII-Zeichen
	 * @param filename
	 *            eine Ausgabedatei, in der die kodierte Nachricht gespeichert wird.
	 */
	public static void encode(String message, String filename) {
		String res = "";
		for (int i = 0; i < message.length(); i++) {
			// read ith character, convert it to 8-character-long binary string, add leading
			// 0s if shorter
			String temp = String.format("%8s", Integer.toBinaryString(message.charAt(i))).replace(' ', '0');
			res += encodeBits(temp.substring(0, 4)) + encodeBits(temp.substring(4, 8));
		}

		try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
			out.print(res);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + filename + " not found.");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Diese Methode liest die Datei und speichert den Inhalt in einem String. 
	 * Die Stringlänge muss ein Vielfache von 14 sein.
	 * Der Dekodierungsprozess wird von den Hilfsmethoden durchgeführt.  
	 *
	 * @param filename
	 *            eine zu dekodierende Eingabedatei
	 * @return der dekodierte String aus ASCII-Zeichen
	 */
	public static String decode(String filename) {
		
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(filename)));
			if (content.length() % 14 != 0) {
				System.err.println("Input length " + content.length() + " not valid: must be multiple of 14.");
				return "";
			}
		} catch (IOException e) {
			System.err.println("File " + filename + " not found.");
			e.printStackTrace();
			return "";
		}

		return new String(binaryToCharArray(decodeBytes(correct(stringToBooleanArray(content)))));
	}

	/**
	 * Diese Methode berechnet die Multiplikation zweier boolescher Arrays gleicher
	 * Länge. Die Multiplikation boolescher Werte wird iterativ durch XORieren der
	 * Ergebnisse der UND-Verknüpfung zwischen den i-ten Werten der beiden
	 * eingegebenen booleschen Arrays berechnet.
	 * 
	 * @param a1
	 *            das erste zu multiplizierende boolesche Array
	 * @param a2
	 *            das zweite zu multiplizierende boolesche Array
	 * @return der boolesche Wert, Ergebnis der Multiplikation
	 */
	private static boolean product(boolean[] a1, boolean[] a2) {
		if (a1.length != a2.length)
			System.err.println("Cannot multiply arrays of different lengths: " + a1.length + ", " + a2.length);

		boolean res = false;
		for (int i = 0; i < a1.length; i++)
			res = res != (a1[i] && a2[i]);
		return res;
	}

	/**
	 * Diese Methode berechnet die Multiplikation eines zweidimensionalen booleschen
	 * Arrays mit einem eindimensionalen booleschen Array. Die Anzahl der Spalten
	 * des zweidimensionalen Arrays muss gleich der Länge des eindimensionalen
	 * Arrays sein. Das Produkt wird iterativ berechnet, indem die Methode
	 * {@link #product(boolean[], boolean[])} auf jeder der Zeilen des
	 * zweidimensionalen Arrays und des eindimensionalen Arrays aufgerufen wird.
	 * aufgerufen wird. Das resultierende boolesche Array ist so lang wie die Anzahl
	 * der Spalten des zweidimensionalen Arrays.
	 * 
	 * @param M
	 *            ein zu multiplizierendes zweidimensionales boolesche Array
	 * @param a
	 *            ein zu multiplizierendes eindimensionales boolesche Array
	 * @return das eindimensionale boolesche Array, Ergebnis der Multiplikation
	 */
	private static boolean[] product(boolean[][] M, boolean[] a) {
		if (M[0].length != a.length)
			System.err.println("Cannot multiply arrays of different lengths: " + M.length + ", " + a.length);

		boolean[] res = new boolean[M.length];
		for (int i = 0; i < M.length; i++)
			res[i] = product(M[i], a);
		return res;
	}

	/**
	 *	Diese Methode übersetzt einen String in eines boolesche Array.
	 *			 
	 * @param s
	 *            ein String von Nullen und Einsen, die in ein boolesches Array
	 *            übersetzt werden sollen.
	 * @return das boolesche Array entsprechend der Eingabe String
	 */
	private static boolean[] stringToBooleanArray(String s) {
		boolean[] res = new boolean[s.length()];

		for (int i = 0; i < s.length(); i++) {
			switch (s.charAt(i)) {
			case '0':
				res[i] = false;
				break;
			case '1':
				res[i] = true;
				break;
			default:
				System.err.println(
						"Invalid character at position " + i + ": expected 0 or 1 but found \'" + s.charAt(i) + "\'");
				System.exit(1);
			}
		}

		return res;
	}

	/**
	 *	Diese Methode übersetzt ein boolesches Array in einen Binärstring. 
	 *
	 * @param a
	 *            ein boolesches Array, das in einen String aus Nullen und Einsen
	 *            übersetzt werden soll.
	 * @return der String aus Nullen und Einsen entsprechend das eingegebene
	 *         boolesche Array
	 */
	private static String booleanArrayToString(boolean[] a) {
		char[] res = new char[a.length];
		for (int i = 0; i < a.length; i++)
			res[i] = a[i] ? '1' : '0';
		return new String(res);
	}

	/**
	 * @param a
	 *            ein boolesches Array, das auf Korrektheit geprüft werden soll. Es
	 *            entspricht dem Syndromvektor
	 * @return true, wenn das boolesche Array ein Nullvektor ist. false andernfalls
	 */
	private static boolean isCorrect(boolean[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i])
				return false;
		return true;
	}

	/**
	 * 
	 * @param data
	 *            ein vierstelliger String aus Nullen und Einsen, die kodiert werden
	 *            soll.
	 * @return der siebenstellige String aus Nullen und Einsen, der dem eingegebenen
	 *         String mit zusätzlichen Paritätsbits entspricht.
	 */
	private static String encodeBits(String data) {
		boolean[] temp = stringToBooleanArray(data);
		temp = product(G, temp);
		return booleanArrayToString(temp);
	}

	/**
	 * @param a
	 *            ein boolesches Array, das einer vollständig kodierten Nachricht
	 *            entspricht, die korrigiert werden soll.
	 * @return das fehlerfreie boolesche Array entsprechend dem eingegebenen
	 *         booleschen Array
	 */
	private static boolean[] correct(boolean[] a) {
		int l = 7;
		for (int i = 0; i < a.length; i += l) {
			boolean[] temp = new boolean[l];
			System.arraycopy(a, i, temp, 0, l);
			temp = correctError(temp);
			System.arraycopy(temp, 0, a, i, l);
		}
		return a;
	}
	
	/**
	 * @param a
	 *            ein kodiertes boolesches Array der Länge 7, das maximal einen
	 *            1-Bit-Fehler enthält
	 * @return das fehlerfreie boolesche Array entsprechend der Eingabe
	 */
	private static boolean[] correctError(boolean[] a) {
		boolean[] parity = product(H, a); // parity check
		if (isCorrect(parity))
			return a;

		int wrong = wrongBit(parity);
		a[wrong] = !a[wrong];
		return a;
	}

	/**
	 * @param encoded
	 *            ein fehlerfreies kodiertes boolesches Array, das dekodiert werden
	 *            soll.
	 * @return das dekodierte boolesche Array, das dem eingegebenen booleschen Array
	 *         ohne die Paritätsbits entspricht.
	 */
	private static boolean[] decodeBytes(boolean[] encoded) {
		int l = 7;
		boolean[] res = new boolean[encoded.length / l * 4];
		for (int i = 0; i < encoded.length / l; i++) {
			boolean[] temp = new boolean[l];
			System.arraycopy(encoded, i * l, temp, 0, l);
			temp = product(R, temp);
			System.arraycopy(temp, 0, res, i * 4, 4);
		}
		return res;
	}

	/**
	 * @param a
	 *            ein boolesches Array, dessen Werte in Gruppen von 8 als binäre
	 *            Ganzzahl einem ASCII-Zeichen entsprechen.
	 * @return das Zeichen-Array, dessen Werte aus dem eingegebenen booleschen Array
	 *         berechnet werden.
	 */
	private static char[] binaryToCharArray(boolean[] a) {
		int l = 8;
		String binary = booleanArrayToString(a);
		char[] res = new char[binary.length() / l];

		for (int i = 0; i < binary.length(); i += l) {
			String temp = binary.substring(i, i + l);
			System.arraycopy(Character.toChars(Integer.parseInt(temp, 2)), 0, res, i / l, 1);
		}
		return res;
	}

	/**
	 *	Diese Methode nimmt den Syndromvektor und sucht innerhalb der Kontrollmtrix eine Spalte,
	 *  die die gleiche einträge als das obergenannte boolesche Array hat. Der Index dieser Spalte 
	 *  entschpricht der Stelle des zu umwandelden falschen Bit. 
	 *
	 * @param col
	 *            ein boolesches Array entsprechend dem Syndromvektor eines
	 *            fehlerhaften Bits
	 * @return der Index des falschen Bits im kodierten booleschen Array (außerhalb
	 *         des Bereichs dieser Methode)
	 */
	private static int wrongBit(boolean[] col) {
		for (int j = 0; j < H[0].length; j++) {
			int count = 0;
			for (int i = 0; i < col.length; i++) {
				if (H[i][j] == col[i])
					count++;
				if (count == col.length)
					return j;
			}
		}
		return -1;
	}

}
