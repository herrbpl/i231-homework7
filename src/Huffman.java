
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

	public class Node {
		int frequency;
		byte data;
		int bitlength;
		int bitmask;
		Node right = null;
		Node left = null;

		boolean isLeaf() {
			return (right == null && left == null);
		}

		// return data back as index;
		int index() {
			if (!this.isLeaf())
				return -1; // not sure if i should return this.
			return data & 0xFF;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("%s %d %d", (char) data, data & 0xFF, frequency);
		}

		/**
		 * Builds mapping table for decoding
		 * 
		 * @param p
		 * @param bm
		 * @param bl
		 * @param codetable
		 */
		void walkTree(String p, int bm, int bl, Node[] codetable) {
			if (this.isLeaf()) {
				if (bl == 0)
					bl = bl + 1;
				this.bitlength = bl;
				this.bitmask = bm;
				codetable[this.index()] = this;
			} else {
				if (this.left != null) {
					bm = bm << 1; // shift to left
					this.left.walkTree(p + "0", bm, bl + 1, codetable);
				}
				if (this.right != null) {
					bm = bm + 1; // add single digit to end

					this.right.walkTree(p + "1", bm, bl + 1, codetable);
				}
			}
		}

		/**
		 * Finds if given input is found in tree leaves.
		 * @param workarea - bit combination to search
		 * @return -1 if not found, data byte as int when found (You must convert to byte)
		 */
		public int findNode(int workarea) {
			
			int result;
			
			if (this.isLeaf()) {				
				if (this.bitmask == workarea) return this.data;
			}
			
			if (this.left != null) {				
				result = this.left.findNode(workarea);
				if (result != -1) return result;
			}
			if (this.right != null) {								
				result = this.right.findNode(workarea);
				if (result != -1) return result;
			}

			return -1;
		}

		/**
		 * Gets maximum code length in bits in frequency tree/table
		 * @param i
		 * @return
		 */
		public int maxCodeLength(int i) {
			// TODO Auto-generated method stub
			
			if (this.isLeaf()) {
				return i+1;
			}
			
			int l = i, r = i;
			if (this.left != null) {
				l = this.left.maxCodeLength(i+1);
			}
			if (this.right != null) {
				r = this.right.maxCodeLength(i+1);
			}
			return Math.max(l, r);
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
			if (frequency[data[i] & 0xFF] == 0)
				count++;
			frequency[data[i] & 0xFF]++;
		}

		Node[] result = new Node[count];
		count = 0;

		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] > 0) {
				Node n = new Node();
				n.data = (byte) i;
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

		while (!finished) {
			// find first item
			min1 = findMin(workarray);
			if (min1 == -1) {
				finished = true;
				// System.out.printf("Found nothing\n");
				//
				return null;
			} else {
				nl = workarray[min1];
				workarray[min1] = null;
				// System.out.printf("Found left, %s\n", nl);

				// find min2
				min2 = findMin(workarray);
				if (min2 == -1) {
					// nl is root
					finished = true;
					// System.out.printf("Right not found, returning root %s\n",
					// nl);

					return nl;
				} else {
					nr = workarray[min2];
					workarray[min2] = null;
					// System.out.printf("Found right, %s\n", nr);
					// lets find sum of those frequencies
					np = new Node();
					np.frequency = nl.frequency + nr.frequency;
					np.left = nl;
					np.right = nr;
					// insert back into array
					workarray[min1] = np;
					// System.out.printf("created root, %s\n", np);
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
		if (original == null) {
			throw new RuntimeException("Empty input");
		}
		// TODO!!! Your constructor here!
		// We do nothing here.
	}

	/**
	 * Length of encoded data in bits.
	 * 
	 * @return number of bits
	 */
	public int bitLength() {
		int resultbits = 0;
		for (int i = 0; i < leaves.length; i++) {
			if (leaves[i] != null) {
				resultbits += leaves[i].bitlength * leaves[i].frequency;
			}
		}
		return resultbits;
	}

	private String bitStr(int i) {
		int mask = 0x8000;
		return Integer.toBinaryString(i | mask).substring(8, 16);
	}

	/**
	 * Encoding the byte array using this prefixcode.
	 * 
	 * @param origData
	 *            original data
	 * @return encoded data
	 */
	public byte[] encode(byte[] origData) {

		// build frequency table
		Node[] nn = buildFrequencyTable(origData);

		// reset this leaves
		this.leaves = new Node[256];

		// build tree
		this.root = buildTree(nn);

		// create code table in leaves.
		this.root.walkTree("", 0, 0, this.leaves);

		// create output buffer
		byte[] result = new byte[this.calculateOutbytes()];

//		System.out.println("total length of output is in bytes: " + result.length);

		int currentlength = 0, currentbyte = 0, shift = 0, position = 0;

		// into first three bits, we add length of padding bits in the end.
		currentlength = 3;

		Node n;
		// encode data
		for (int i = 0; i < origData.length; i++) {
			// get frequency and bitlength information
			n = this.leaves[origData[i] & 0xFF];
			if (n == null) {
				throw new RuntimeException("Error in frequency table mapping!");
			}
			int a = n.bitmask;
//			System.out.printf("Current value  %d '%s', length %d\n", currentbyte, bitStr(currentbyte), currentlength);
//			System.out.printf("Adding  %d '%s'\n", a, bitStr(a));
			// if there is room left in byte, fill it.
			if (n.bitlength + currentlength <= 8) {
				a = a << (8 - currentlength - n.bitlength);
				currentlength = currentlength + n.bitlength;
				currentbyte = currentbyte | a;
//				System.out.printf("C1 Value after adding %d '%s'\n", currentbyte, bitStr(currentbyte));
			} else { // new bitlength does not fit into current byte. We have to
						// split it between two bytes.

//				System.out.println("-----------------------------------------------------------------------------");
				int over = -((8 - currentlength) - n.bitlength);
//				System.out.printf("Room left %d, length %d, over %d\n", 8 - currentlength, currentlength, over);
				// a = a >>> (n.bitlength - over);
				a = a >>> over;
				currentbyte = currentbyte | a;
//				System.out.printf("Storing %d '%s'\n", currentbyte, bitStr(currentbyte));
				result[position] = (byte) currentbyte;
				position++;
				currentbyte = (n.bitmask << (8 - over)) & 0x00FF;
				currentlength = over;
//				System.out.printf("C2 Value after adding %d '%s'\n", currentbyte, bitStr(currentbyte));
			}
		}

		int paddinglength = (8 - currentlength);

//		System.out.printf("Padding length: %d, '%s',  first byte: '%s'\n", paddinglength, bitStr(paddinglength),
//				bitStr((int) result[0]));

		paddinglength = paddinglength << 5;

//		System.out.println(bitStr(paddinglength));
//		System.out.println(bitStr((int) result[0] | paddinglength));

		result[0] = (byte) (((int) result[0] | paddinglength));

		if (currentlength > 0) {
			result[position] = (byte) currentbyte;
			// add padding length add beginning

		}

//		System.out.printf("Padding length: %d, first byte: '%s'\n", paddinglength, bitStr((int) result[0] & 0xFF));

		return result; // TODO!!!
	}

	/**
	 * Calculates length of bytes needed for output buffer given data in input.
	 * First three bits are padding length required.
	 * 
	 * @return
	 */
	private int calculateOutbytes() {
		// TODO Auto-generated method stub

		return (int) Math.ceil((this.bitLength() + 3) / 8.0);
	}

	/**
	 * Decoding the byte array using this prefixcode.
	 * 
	 * @param encodedData
	 *            encoded data
	 * @return decoded data (hopefully identical to original) TODO: add
	 *         dictionary export/import
	 */
	public byte[] decode(byte[] encodedData) {
		if (this.root == null) {
			throw new RuntimeException("Dictionary missing, run encode first");
		}

		if (encodedData.length == 0) {
			return null;
		}

		// we do not know input length in bits. Should we include this in
		// header? padding length of last byte?

		// get length of padding in last byte.
		int paddinglength = (encodedData[0] & 0xFF) >>> 5;
//		System.out.printf("Decoding Padding length: %d '%s'\n", paddinglength, bitStr(paddinglength));

		// i need to know size for output array;
		int currentcodelength = 0;
		int workarea = 0;

		// max length of encoding in tree?
		int maxcodelength = root.maxCodeLength(0); // get from root.				
		int databitlength = this.bitLength();
		int bitcounter = 0;
		
		ArrayList<Byte> al = new ArrayList<Byte>();

		// iterate over bytes
		for (int i = 0; i < encodedData.length; i++) {
			int b = encodedData[i] & 0xFF;
			// iterate bit by bit, from most signifigant to least.
			for (int j = 7; j >= 0; j--) {
				bitcounter++;
				// only if we are not in header area or padding
				if ((bitcounter > 3) && ((bitcounter - 3) <= databitlength)) {
					boolean bit = ((b & (1 << j)) == (1 << j));
					if (currentcodelength < maxcodelength) {
						currentcodelength++;

						workarea = workarea << 1; // move existing data to left
						if (bit)
							workarea = workarea + 1; // set last bit if set

						int decodedbyte = root.findNode(workarea);
						if (decodedbyte >= 0) {
//							System.out.printf("input code %s mapped to %d\n", bitStr(workarea), decodedbyte);
							al.add((byte)decodedbyte);
							currentcodelength = 0;
							workarea = 0;
						} else {
//							System.out.printf("input code %s not mapped to anything\n", bitStr(workarea));
						}
					} else {
						throw new RuntimeException("Code length exceeds maximum code length in dictionary");
					}
				}

			}

			// bit

		}

		byte[] result = new byte[al.size()];
		
		for (int i = 0; i < al.size(); i++) { 
			result[i] = al.get(i);
		}
		
//		System.out.println(new String(result));
		
		return result; // TODO!!!
	}

	/** Main method. */
	public static void main(String[] params) {

		String tekst = "AAAAAAAAAAAAABBBBBBCCCDDEEF";
		// String tekst = "A";
		byte[] orig = tekst.getBytes();

		Huffman huf = new Huffman(orig);

		byte[] kood = huf.encode(orig);

		byte[] orig2 = huf.decode(kood);
		// must be equal: orig, orig2
		System.out.println(Arrays.equals(orig, orig2));
		int lngth = huf.bitLength();
		System.out.println("Length of encoded data in bits: " + lngth);
		// TODO!!! Your tests here!
	}
}
