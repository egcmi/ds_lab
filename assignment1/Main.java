
public class Main {

	public static void main(String[] args) {
		String file = "result.txt";
		HammingCode.encode("", file);
		System.out.println(HammingCode.decode(file));
	}

}
