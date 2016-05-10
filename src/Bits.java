
public class Bits {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 1111 1111
		int[] bitlengths = {1,2,3,3};
		int[] bitmasks = {0b0, 0b10, 0b100, 0b101};
		
		System.out.println(bitStr(0));
		System.out.println(bitStr(5));
		System.out.println(bitStr(12));
		
		
		
	}
	public static String bitStr(int i) {
		int mask = 0x8000;
		return Integer.toBinaryString(i | mask);
	}

}
