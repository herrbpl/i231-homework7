
import java.util.*;

/**
 * Prefix codes and Huffman tree. Tree depends on source data.
 */
public class Huffman {

	private Node[] leaves = null; // acts as frequency table
	private Node root = null; // tree root.
	// TODO!!! Your instance variables here!
	// we need only 256 possible positions in tree as there are 256 possible
	// symbols. We dont care about unicode here i think. Just bytes.
	// we need to create tree only for getting optimal coding

	public  class Node {
		int frequency;
		byte data;		
		Node right = null;
		Node left = null;
		boolean isLeaf() {
			return (right == null && left == null);
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("%s %d %d", (char)data, data & 0xFF, frequency );
		}
	
		void walkTree(String p, int bl) {
			if (this.isLeaf() ) {
				System.out.printf("%s bitlength %d\n", this.toString() + " = " + p, bl );
			} else {
				if (this.left != null) {
					this.left.walkTree(p+"0", bl+1);
				}
				if (this.right != null) {
					this.right.walkTree(p+"1", bl+1);
				}
			}
		}
		
	}

	// returns node with minimal frequency
	private int findMin(Node[] input) {
		int c = Integer.MAX_VALUE;
		int p = -1;
		for (int i = 0; i < input.length; i++) {
			if (input[i] != null && input[i].frequency < c) { 
				c = input[i].frequency;
				p = i;
			}
		}
		return p;
	}
	
	
	// NB! byte is from -128 to 127.
	 private Node[] buildFrequencyTable(byte[] data) {
		int[] frequency = new int[256];
		int count = 0;
		for (int i = 0; i < data.length; i++) {
			if (frequency[data[i] & 0xFF] == 0) count++;
			frequency[data[i] & 0xFF]++;
		}
		
		Node[] result = new Node[count];
		count = 0;
		
		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] > 0) {				
				Node n = new Node();
				n.data = (byte)i;
				n.frequency = frequency[i];
				result[count++] = n;
			}
		}
		
		return result;
	}
	 
	// returns root node of built tree.
	private Node buildTree(Node[] input) {
		
		boolean finished = false;
		
		Node[] workarray = input.clone();
		
		Node nl, nr, np = null;
		int min1, min2;
		
		
		while(!finished) {
			// find first item
			min1 = findMin(workarray);
			if (min1 == -1) {
				finished = true;
				System.out.printf("Found nothing\n");
				// 
				return null;
			} else {
				nl = workarray[min1];
				workarray[min1] = null;
				System.out.printf("Found left, %s\n", nl);
				
				// find min2
				min2 = findMin(workarray);
				if (min2 == -1) {
					// nl is root
					finished = true;
					System.out.printf("Right not found, returning root %s\n", nl);
					
					return nl;
				} else {
					nr = workarray[min2];
					workarray[min2] = null;
					System.out.printf("Found right, %s\n", nr);
					// lets find sum of those frequencies
					np = new Node();
					np.frequency = nl.frequency+nr.frequency;
					np.left = nl;
					np.right = nr;
					// insert back into array
					workarray[min1] = np;
					System.out.printf("created root, %s\n", np);
				}								
			}
		} // while
		return null;
	}

	/**
	 * Constructor to build the Huffman code for a given bytearray.
	 * 
	 * @param original
	 *            source data
	 */
	Huffman(byte[] original) {
		// TODO!!! Your constructor here!

	}

	/**
	 * Length of encoded data in bits.
	 * 
	 * @return number of bits
	 */
	public int bitLength() {
		return 0; // TODO!!!
	}

	/**
	 * Encoding the byte array using this prefixcode.
	 * 
	 * @param origData
	 *            original data
	 * @return encoded data
	 */
	public byte[] encode(byte[] origData) {
		return null; // TODO!!!
	}

	/**
	 * Decoding the byte array using this prefixcode.
	 * 
	 * @param encodedData
	 *            encoded data
	 * @return decoded data (hopefully identical to original)
	 */
	public byte[] decode(byte[] encodedData) {
		return null; // TODO!!!
	}

	/** Main method. */
	public static void main(String[] params) {

		byte bbb = -1;

		System.out.println((bbb & 0xFF));
		System.out.println((bbb));
		
		
		

		String tekst = "AAAAAAAAAAAAABBBBBBCCCDDEEF";
		byte[] orig = tekst.getBytes();
		
		
		
		
		
		Huffman huf = new Huffman(orig);
		
		Node[] nn = huf.buildFrequencyTable(orig);
		
		
		
		Node root = huf.buildTree(nn);
		
		for (int i = 0; i < nn.length; i++) {
			System.out.println(nn[i]);
		}
		
		root.walkTree("",0);
		
		int i1, i2;
		
		i1 = 1;
		System.out.println(i1);
		i1 = i1 << 1;
		System.out.println(i1);
		
		i1 = i1 << 1;		
		System.out.println(i1);
		i1 = i1 + 1;
		
		System.out.println(i1);
		i1 = i1 >> 1;
		System.out.println(i1);
		
		
		byte[] kood = huf.encode(orig);
		byte[] orig2 = huf.decode(kood);
		// must be equal: orig, orig2
		System.out.println(Arrays.equals(orig, orig2));
		int lngth = huf.bitLength();
		System.out.println("Length of encoded data in bits: " + lngth);
		// TODO!!! Your tests here!
	}
}
