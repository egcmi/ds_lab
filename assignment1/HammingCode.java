import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HammingCode {
	
	// code-generating matrix
	public static final boolean[][] G = {		//data  d1 d2 d3 d4
			{true,	true,	false,	true},		// p1 [ 1, 1, 0, 1 ]
			{true,	false,	true,	true},		// p2 [ 1, 0, 1, 1 ]
			{true,	false,	false,	false},		// d1 [ 1, 0, 0, 0 ]
			{false,	true,	true,	true},		// p3 [ 0, 1, 1, 1 ]
			{false,	true,	false,	false},		// d2 [ 0, 1, 0, 0 ]
			{false,	false,	true,	false},		// d3 [ 0, 0, 1, 0 ]
			{false,	false,	false,	true}		// d4 [ 0, 0, 0, 1 ]
		};
	
	// parity-check matrix
	public static final boolean[][] H = {								//index 1  2  3  4  5  6  7
			{true,	false,	true,	false,	true,	false,	true},		// p1 [ 1, 0, 1, 0, 1, 0, 1 ]
			{false,	true,	true,	false,	false,	true,	true},		// p2 [ 0, 1, 1, 0, 0, 1, 1 ]
			{false,	false,	false,	true,	true,	true,	true}		// p3 [ 0, 0, 0, 1, 1, 1, 1 ]
		};
	
	public static void encode(String message, String filename) {
		String res = "";
		for (int i = 0; i < message.length(); i++) {
			// read ith character, convert it to 8-character-long binary string, add leading
			// 0s if shorter
			String temp = String.format("%8s", Integer.toBinaryString(message.charAt(i))).replace(' ', '0');
			res += encodeBits(temp.substring(0, 4)) + encodeBits(temp.substring(4, 8));
		}
		System.out.println(res);

		try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
			out.print(res);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + filename + " not found.");
			e.printStackTrace();
		}
	}

	public static String decode(String filename) {
		// read the whole input file, store the content into this string
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

		return new String(binaryToChar(decodeBytes(correct(stringToBooleanArray(content)))));
	}

	private static boolean product(boolean[] a1, boolean[] a2) {
		if (a1.length != a2.length) {
			System.err.println("Cannot multiply arrays of different lengths: " + a1.length + ", " + a2.length);
		}

		boolean res = false;
		for (int i = 0; i < a1.length; i++) {
			res = res != (a1[i] && a2[i]);
		}
		return res;
	}

	private static boolean[] product(boolean[][] M, boolean[] a) {
		boolean[] res = new boolean[M.length];
		for (int i = 0; i < M.length; i++)
			res[i] = product(M[i], a);
		return res;
	}

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
				System.err.println("Invalid character: must be 0 or 1");
				System.exit(1);
			}
		}

		return res;
	}

	private static String booleanArrayToString(boolean[] a) {
		char[] res = new char[a.length];
		for (int i = 0; i < a.length; i++) {
			res[i] = a[i] ? '1' : '0';
		}
		return new String(res);
	}

	private static boolean isCorrect(boolean[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i])
				return false;
		return true;
	}

	private static String encodeBits(String data) {
		boolean[] temp = stringToBooleanArray(data);
		temp = product(G, temp);
		return booleanArrayToString(temp);
	}

	private static boolean[] correctError(boolean[] a) {
		boolean[] parity = product(H, a); // parity check
		if (isCorrect(parity)) {
			return a;
		}

		for (int i = 0; i < parity.length; i++) {
			System.out.print(parity[i] + ", ");
		} System.out.println();
		
		int wrong = wrongColumn(parity);
		a[wrong] = !a[wrong];
		return a;
	}

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
	
	private static boolean[] decodeBytes(boolean[] parity) {
		// identity matrix for data bits
		boolean[][] R =  {													//   p1 p2 d1 p3 d2 d3 d4
				{false,	false,	true,	false,	false,	false,	false},		// [ 0, 0, 1, 0, 0, 0, 0 ]
				{false,	false,	false,	false,	true,	false,	false},		// [ 0, 0, 0, 0, 1, 0, 0 ]
				{false,	false,	false,	false,	false,	true,	false},		// [ 0, 0, 0, 0, 0, 1, 0 ]
				{false,	false,	false,	false,	false,	false,	true}		// [ 0, 0, 0, 0, 0, 0, 1 ]
		};
		
		int l = 7;
		boolean[] res = new boolean[parity.length / l * 4];
		for (int i = 0; i < parity.length / l; i++) {
			boolean[] temp = new boolean[l];
			System.arraycopy(parity, i * l, temp, 0, l);
			temp = product(R, temp);
			System.arraycopy(temp, 0, res, i * 4, 4);
		}
		return res;
	}

	private static char[] binaryToChar(boolean[] a) {
		int l = 8;
		String binary = booleanArrayToString(a);
		char[] res = new char[binary.length() / l];

		for (int i = 0; i < binary.length(); i += l) {
			String temp = binary.substring(i, i + l);
			System.arraycopy(Character.toChars(Integer.parseInt(temp, 2)), 0, res, i / l, 1);
		}
		return res;
	}

	public static int wrongColumn(boolean[] col) {
		for (int j=0; j<H.length; j++) {
			int count = 0;
			for (int i=0; i<col.length; i++) {
				if (H[j][i] == col[j]) {
					count++;
				}
			}
			System.out.printf("count: %d, j: %d, col length: %d\n", count, j, col.length);
			if (count == col.length) {
				return j;
			}
		}
		return -1;
	}

}
