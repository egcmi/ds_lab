import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HammingCode {
	
	public static void encode(String message, String filename) {
		String result = "";
		for(int i = 0; i<message.length(); i++) {
			//legge il carattere in posizione i, lo converte in una stringa binaria lunga 8 caratteri, aggiunge zeri all'inizio della stringa se più corta
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
			//più compatto per leggere tutto il file in una volta sola e farlo diventare una stringa, senza dover iterare riga per riga
			content = new String(Files.readAllBytes(Paths.get(filename)));
			if (content.length() % 7 != 0) {
				// TODO throw exception/error: se la lunghezza della stringa non è multipla di 7
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		//loop per iterare lungo la stringa e cominciare a decodificare
//		for(int j = 0; j<content.length(); j++) {
//			
//			String tmpStr = "";
//			
//			//finch� j non � multiplo di 6 aggiungo i caratteri che trovo
//			//ad una stringa temporanea.
//			//faccio il check degli errori
//			//decodifico
//			
//			if ((j%6 != 0) && (j != 0)) {
//				tmpStr += content.charAt(j);
//				//guardalo dopo
//				String string_without_error = errorCorrector(tmpStr);
//			} 
//			
//			if(j%6 == 0){
//				tmpStr = "";
//			}
//		}
		
		//problema con il codice sopra (che ho commentato):
		//modulo 6 -> vuol dire che legge 6 caratteri (non 7) e salta il primo perché 0%6=0 quindi non entra nell'if
		//propongo di fare così:
		//finché la stringa content non è vuota:
		//	temp = leggi i primi 7 caratteri di content,
		//	fai cose (decodifica, correzione degli errori, concateni il risultato a result)
		//	modifichi content: content MENO i primi 7 caratteri
		String temp = "";
		while (content.length() != 0) {
			temp = content.substring(0,7);
			// TODO do stuff with temp
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
	
	private static String errorCorrector(String vector) {
		
		
		
		return vector;
	}
}

