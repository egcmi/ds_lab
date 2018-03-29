
public class Main {

	public static void main(String[] args) {
		String file = "result.txt";
		
		boolean[] b1 = {false, true, true, false, false, true, true};
		boolean[] b2 = {true, true, true, false, false, true, true};
		
		HammingCode.multiplyVector(b1, b2);

		
		HammingCode.multiplyMatrix(HammingCode.H, b2);
		
//		HammingCode.encode("blablaciao", file);
//		System.out.println(HammingCode.decode(file));
	}

}
