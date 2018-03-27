import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
		return "aaa";
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
}

