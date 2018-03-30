
public class Main {

	public static void main(String[] args) {
		String file = "result.txt";
		
//		boolean[] b1 = {false, true, true, false, false, true, true};
//		boolean[] b2 = {true, true, true, false, false, true, true};
//		boolean[] b3 = {false, true, true, false, true, true, true};
//		HammingCode.multiplyVector(b1, b1);
//		HammingCode.multiplyVector(b1, b2);
//		System.out.println();
//		HammingCode.multiplyMatrix(HammingCode.H, b1);
//		System.out.println();
//		HammingCode.multiplyMatrix(HammingCode.H, b2);
//		System.out.println();
//		HammingCode.multiplyMatrix(HammingCode.H, b3);
		
		HammingCode.encode("blablaciao", file);
		System.out.println(HammingCode.decode(file));
	}

}
