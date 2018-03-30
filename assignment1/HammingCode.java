import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HammingCode {
	
	//generating matrix
	public static final boolean[][] G = {
			{true,	true,	false,	true},		// [ 1, 1, 0, 1 ]
			{true,	false,	true,	true},		// [ 1, 0, 1, 1 ]
			{true,	false,	false,	false},		// [ 1, 0, 0, 0 ]
			{false,	true,	true,	true},		// [ 0, 1, 1, 1 ]
			{false,	true,	false,	false},		// [ 0, 1, 0, 0 ]
			{false,	false,	true,	false},		// [ 0, 0, 1, 0 ]
			{false,	false,	false,	true}		// [ 0, 0, 0, 1 ]
		};
	
	//parity-check matrix
	public static final boolean[][] H = {
			{true,	false,	true,	false,	true,	false,	true},		// [ 1, 0, 1, 0, 1, 0, 1 ]
			{false,	true,	true,	false,	false,	true,	true},		// [ 0, 1, 1, 0, 0, 1, 1 ]
			{false,	false,	false,	true,	true,	true,	true}		// [ 0, 0, 0, 1, 1, 1, 1 ]
		};
	
	//dot product
	public static boolean multiplyVector(boolean[] v1, boolean[] v2) { 
		if (v1.length != v2.length)
			System.out.println("Different lengths: " + v1.length + ", " + v2.length);
		boolean[] temp = new boolean[v1.length];
		boolean res = false;
		int i;
		
		//from 0 to 7
		for (i=0; i<v1.length; i++) {
			temp[i] = v1[i] && v2[i];
			System.out.print("" + i + temp[i] + ", ");
		}
		
		//from 0 to 7
		for(i=0; i < temp.length; i++) {
			res = res != temp[i];
		}
		System.out.println(res);
		return res;
	}
	
	//parity check
	public static boolean[] multiplyMatrix(boolean[][] m, boolean[] v){
		boolean[] res = new boolean[m.length];
		
		//from 0 to 2
		for (int i=0; i<m.length; i++) {
			res[i] = multiplyVector(m[i], v);
		}
		System.out.println(res[0]+ ", "+ res[1] + ", "+res[2]);
		return res;
	}
	
	private static void writeToFile(boolean[] v, String filename) {
		// TODO transform boolean vector into string of 0s and 1s and print it into the file
	}
	
	private static boolean[] readFromFile(String filename) {
		// TODO read the given file and transform it into a boolean vector
		boolean[] v = {true, false};	//dummy vector
		return v;
	}
	
	public static void encode(String message, String filename) {
		String result = "";
		for(int i = 0; i<message.length(); i++) {
			//read ith character, convert it to 8-character-long binary string, add leading 0s if shorter
			String temp = String.format("%8s", Integer.toBinaryString(message.charAt(i))).replace(' ', '0');
			result += encodeBits(temp.substring(0,4)) + encodeBits(temp.substring(4,8));
			}
		
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
			//read input file in one step and store the content into a string, without having to iterate line by line
			//a valid input file will contain only one line of code anyway
			content = new String(Files.readAllBytes(Paths.get(filename)));
			if (content.length() % 7 != 0) {
				//throw exception: se la lunghezza della stringa non è multipla di 7
				throw new Exception();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Input length " + content.length() + " not valid: must be multiple of 7");
			e.printStackTrace();
		}
		
		String temp = "";
		
		//finché la stringa content non è vuota:
		//	temp = leggi i primi 7 caratteri di content,
		//	fai cose (decodifica, correzione degli errori, concateni il risultato a result)
		//	modifichi content: content MENO i primi 7 caratteri
		while (content.length() != 0) {
			boolean[] tempArray = new boolean[6];
			temp = content.substring(0,7);
			// TODO do stuff with temp: check for errors and decode

			//trasformo temp in array di boolean
			for(int i = 0; i<temp.length(); i++){
				if(temp.charAt(i) == '0'){
					tempArray[i] = false;
				}
				if(temp.charAt(i) == '1'){
					tempArray[i] = true;
				}
			}

			//parity check
			boolean[] checkedVector = multiplyMatrix(H,tempArray);
			
			//all 0?
			boolean all0 = true;
			for(int j = 0; j<checkedVector.length; j++){
				if(checkedVector[j]){
					all0 = false;
				}
			}
			
			String correctStr = "";

			//error correction
			if(all0 == false){
				boolean[] correctVector = errorCorrector(tempArray);
					for(int k = 0; k<correctVector.length; k++){
						if(correctVector[k] == true){
							correctStr += 1;
						}
						if(correctVector[k] == false){
							correctStr += 0;
						}
					}
					result += correctStr;
			}else{
				result += temp;
			}
			//concatenate correctStr to result string
			content = content.substring(7);
		}
		
		return result;
	}
	
	private static String encodeBits(String data) {
		int p1, p2, p3;
		int d1, d2, d3, d4;
		
		d1 = Integer.parseInt(data.charAt(0)+"");
		d2 = Integer.parseInt(data.charAt(1)+"");
		d3 = Integer.parseInt(data.charAt(2)+"");
		d4 = Integer.parseInt(data.charAt(3)+"");
		
		p1 = (d1 + d2 + d4)%2;
		p2 = (d1 + d3 + d4)%2;
		p3 = (d2 + d3 + d4)%2;
		
		return ""+p1+p2+d1+p3+d2+d3+d4;
	}
	
	private static boolean[] errorCorrector(boolean[] vector) {
		
		boolean[] toBeCorrected = multiplyMatrix(H, vector);

		//transform boolean values in 0s and 1s (binary string) 
		//and calculate the equivalent integer
		String temp = "";
		for(int g = 0; g<toBeCorrected.length; g++){
			temp = toBeCorrected[g] + temp;
		}
		
		int numOfColumn = Integer.parseInt(temp,2);

		for(int h = 0; h<vector.length; h++){
			if(h == numOfColumn){
				vector[h] = !vector[h]; 
			}
		}

		return vector;
	}
}

