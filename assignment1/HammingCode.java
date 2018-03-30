import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HammingCode {
	
	//generating matrix
	public static final boolean[][] G = {		//data  d1 d2 d3 d4
			{true,	true,	false,	true},		// p1 [ 1, 1, 0, 1 ]
			{true,	false,	true,	true},		// p2 [ 1, 0, 1, 1 ]
			{true,	false,	false,	false},		// d1 [ 1, 0, 0, 0 ]
			{false,	true,	true,	true},		// p3 [ 0, 1, 1, 1 ]
			{false,	true,	false,	false},		// d2 [ 0, 1, 0, 0 ]
			{false,	false,	true,	false},		// d3 [ 0, 0, 1, 0 ]
			{false,	false,	false,	true}		// d4 [ 0, 0, 0, 1 ]
		};
	
	//parity-check matrix
	public static final boolean[][] H = {								//index 1  2  3  4  5  6  7
			{true,	false,	true,	false,	true,	false,	true},		// p1 [ 1, 0, 1, 0, 1, 0, 1 ]
			{false,	true,	true,	false,	false,	true,	true},		// p2 [ 0, 1, 1, 0, 0, 1, 1 ]
			{false,	false,	false,	true,	true,	true,	true}		// p3 [ 0, 0, 0, 1, 1, 1, 1 ]
		};
	
	// dot product
	public static boolean multiplyVector(boolean[] v1, boolean[] v2) {
		//handle exceptions
		if (v1.length != v2.length) { // TODO throw new Exception(); maybe throw exception if different lengths
			System.out.println("Different lengths: " + v1.length + ", " + v2.length);
		}
		
		boolean res = false;
		for (int i = 0; i < v1.length; i++) {
			res = res != (v1[i] && v2[i]);
		}
		return res;
	}

	// parity check
	public static boolean[] multiplyMatrix(boolean[][] m, boolean[] v) {
		boolean[] res = new boolean[m.length];

		for (int i = 0; i < m.length; i++) {
			res[i] = multiplyVector(m[i], v);
			if (res[i])
				System.out.println(i);
		}
		return res;
	}

	public static boolean[] stringToBooleanVector(String s) {
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
				// TODO throw new Exception(); throw exception if not 0 or 1 + print error message
				break; 
			}
		}

		return res;
	}

	public static String booleanVectorToString(boolean[] v) {
		char[] res = new char[v.length];
		for (int i = 0; i < v.length; i++) {
			res[i] = v[i] ? '1' : '0';
		}
		return new String(res);
	}

	public static boolean isCorrect(boolean[] v) {
		for (int i = 0; i < v.length; i++)
			if (v[i])
				return false;
		return true;
	}

	public static void writeToFile(boolean[] v, String filename) {
		// TODO transform boolean vector into string of 0s and 1s and print it into the
		// file
	}

	public static boolean[] readFromFile(String filename) {
		// TODO read the given file and transform it into a boolean vector
		boolean[] v = { true, false }; // dummy vector
		return v;
	}

	private static String encodeBits(String data) {
		// TODO maybe use vector-matrix multiplication for more efficiency

		int d1 = Integer.parseInt(data.charAt(0) + "");
		int d2 = Integer.parseInt(data.charAt(1) + "");
		int d3 = Integer.parseInt(data.charAt(2) + "");
		int d4 = Integer.parseInt(data.charAt(3) + "");

		int p1 = (d1 + d2 + d4) % 2;
		int p2 = (d1 + d3 + d4) % 2;
		int p3 = (d2 + d3 + d4) % 2;

		return "" + p1 + p2 + d1 + p3 + d2 + d3 + d4;
	}

	public static void encode(String message, String filename) {
		// TODO maybe use char[] for more efficiency

		String result = "";
		for (int i = 0; i < message.length(); i++) {
			// read ith character, convert it to 8-character-long binary string, add leading
			// 0s if shorter
			String temp = String.format("%8s", Integer.toBinaryString(message.charAt(i))).replace(' ', '0');
			result += encodeBits(temp.substring(0, 4)) + encodeBits(temp.substring(4, 8));
		}
		System.out.println(result);

		try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
			out.print(result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String decode(String filename) {
		String content = "";
		String result = "";
		try {
			// read input file in one step, store the content into a string, without having to iterate line by line
			// a valid input file will contain only one line of code anyway
			content = new String(Files.readAllBytes(Paths.get(filename)));
			if (content.length() % 7 != 0) {
				System.out.println("Input length " + content.length() + " not valid: must be multiple of 7");
				// TODO throw exception maybe: se la lunghezza della stringa non è multipla di 7
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//while string content not empty:
		// temp = read first 7 characters of content and transform it into boolean[]
		// do stuff: decode, correct errors if needed, concatenate correct result to res
		// reassignment: content = content MINUS first 7 characters
		while (content.length() != 0) {
			boolean[] temp = stringToBooleanVector(content.substring(0, 7));
			boolean[] correct = correctError(temp);
			result += booleanVectorToString(correct);
			content = content.substring(7);
		}
		//now result is correct, proceed to actual decoding
		
		//TODO:
		//	divide result in substrings of length 14
		//	split 14string into 2 7strings
		//	remove 1st, 2nd, 4th characters from each -> get 4 data bits from each
		//	concatenate 4+4 data bits into 1 byte
		//	transform byte into character
		//	concatenate all resulting characters
		//	return (correctly) decoded result
		
		return result;
	}

	public static int wrongColumn(boolean[] parity) {
		for (int j=1; j<H.length; j++) {
			for (int i=1; i<H[j].length; i++) {
				if (H[i][j] != parity[j]) {
					break;
				}
				if (i == parity.length) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public static boolean[] correctError(boolean[] v) {
		boolean[] parity = multiplyMatrix(H, v);	//parity check
		if (isCorrect(parity)) {
			return v;
		}
		
		int wrong = wrongColumn(parity);
		v[wrong] = !v[wrong];
		return v;
	}
}
