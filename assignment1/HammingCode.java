import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class HammingCode {
	
	public static void encode(String message, String filename) {
		String result = "";
		for(int i = 0; i<message.length(); i++) {
			String temp = String.format("%8s", Integer.toBinaryString(message.charAt(i))).replace(' ', '0');
			String t1 = temp.substring(0,4);
			String t2 = temp.substring(4, 8);
			
			String h1 = parity(t1);
			String h2 = parity(t2);
			result += h1+h2;
			}
		
		try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
		    out.print(result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String decode(String filename) {
		
		//Buffered Reader per leggere cosa c'è nel file
		
		BufferedReader br = null;
		//Stringa che contiene il contenuto del file
		String content = "";
		
		try {
			br = new BufferedReader(new FileReader(filename));
			
			String contentLine = br.readLine();
			
			while (contentLine !=null) {
				content += contentLine;
				contentLine = br.readLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(br != null) {
					br.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//loop per iterare lungo la stringa e cominciare a decodificare
		for(int j = 0; j<content.length(); j++) {
			
			String tmpStr = "";
			
			//finchè j non è multiplo di 6 aggiungo i caratteri che trovo
			//ad una stringa temporanea.
			//faccio il check degli errori
			//decodifico
			if ((j%6 != 0) && (j != 0)) {
				tmpStr += content.charAt(j);
				//guardalo dopo
				String string_without_error = errorCorrector(tmpStr);
			} 
			
			if(j%6 == 0){
				tmpStr = "";
			}
		}
		
		return content;
	}
	
	private static String parity(String data) {
		
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
	
	private static String errorCorrector(String vector) {
		
		
		
		return vector;
	}
}

