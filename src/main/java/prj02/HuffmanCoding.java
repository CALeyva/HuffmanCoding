package prj02;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import HashTable.*;
import List.*;
import SortedList.*;
import Tree.*;

/* Uncomment for testing Huffman tree generation */
// import utils.BinaryTreePrinter;


/**
 * The Huffman Encoding Algorithm
 *
 * This is a data compression algorithm designed by David A. Huffman and published in 1952
 *
 * What it does is it takes a string and by constructing a special binary tree with the frequencies of each character.
 * This tree generates special prefix codes that make the size of each string encoded a lot smaller, thus saving space.
 *
 * @author Fernando J. Bermudez Medina (Template)
 * @author A. ElSaid (Review)
 * @author Carlos A. Leyva Capote 802204825 (Implementation)
 * @version 2.0
 * @since 10/16/2021
 */
public class HuffmanCoding {

	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		String data = load_data("input1.txt"); // You can create other test input files and add them to the inputData Folder

		/*If input string is not empty we can encode the text using our algorithm*/
		if(!data.isEmpty()) {
			Map<String, Integer> fD = compute_fd(data);
			BTNode<Integer,String> huffmanRoot = huffman_tree(fD);
			Map<String,String> encodedHuffman = huffman_code(huffmanRoot);
			String output = encode(encodedHuffman, data);
			process_results(fD, encodedHuffman,data,output);
		} else {
			System.out.println("Input Data Is Empty! Try Again with a File that has data inside!");
		}

	}

	/**
	 * Receives a file named in parameter inputFile (including its path),
	 * and returns a single string with the contents.
	 *
	 * @param inputFile name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/*We create a new reader that accepts UTF-8 encoding and extract the input string from the file, and we return it*/
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));

			/*If input file is empty just return an empty string, if not just extract the data*/
			String extracted = in.readLine();
			if(extracted != null)
				line = extracted;

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		return line;
	}

	/**
	 * Receives a string and returns a Map with the computed 
	 * symbol frequency distribution of each character inside the input string.
	 *
	 * @param inputString string for which the symbol frequency distribution map will be computed
	 * @return Map<String, Integer> whose key is a character converted to a string appearing 
	 *                              in the input string and whose value is the amount 
	 *                              of times it appears in that string
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		/* Compute Symbol Frequency Distribution of each character inside input string of size len */
		int len = inputString.length();
		/* Allocate frequency distribution map */
		Map<String, Integer> freqDist = new HashTableSC<String, Integer>(len, new SimpleHashFunction<String>());
		for (int i = 0; i < len; i++) {
			String c = Character.toString(inputString.charAt(i));
			/* If character appears in the freqDist Map, add 1 to its value otherwise, initialize value to 1 */
			freqDist.put(c, freqDist.containsKey(c) ? freqDist.get(c) + 1 : 1);
		}
		return freqDist;
	}

	/**
	 * Receives a Map with the frequency distribution and returns the root 
	 * node of the constructed Huffman tree.
	 *
	 * @param fD Map whose key is a character converted to a string appearing in the input string 
	 *           and whose value is the amount of times it appears in that string
	 * @return BTNode<Integer, String> root node of the corresponding Huffman tree whose key is
	 *                                 the frequency and the value is its corresponding symbol of type string
	 */
	public static BTNode<Integer, String> huffman_tree(Map<String, Integer> fD) {
		/* Declare and initialize new sorted list  */
		SortedList<BTNode<Integer, String>> SL = new SortedLinkedList<BTNode<Integer, String>>();
		/* Declare and initialize keys and values list from frequency distribution map  */
		List<String> keys = fD.getKeys();
		List<Integer> values = fD.getValues();
		int size = fD.size();
		/* Traverse over keys and values list and add a new node to the sorted list */
		for (int i = 0; i < size; i++)
			SL.add(new BTNode<Integer, String>(values.get(i), keys.get(i)));
		/* Traverse over sorted list until there is only one node left */
		for (int i = 1; i < size; i++) {
			if (SL.size() <= 1) break;
			/* Create a new node and set left and right children to the minimum frequency nodes in the sorted list */
			BTNode<Integer, String> node = new BTNode<Integer, String>();
			node.setLeftChild(SL.removeIndex(0));
			node.setRightChild(SL.removeIndex(0));
			/* Set new node's key and value to the combination of the children and add node to sorted list */
			node.setKey(node.getLeftChild().getKey() + node.getRightChild().getKey());
			node.setValue(node.getLeftChild().getValue() + node.getRightChild().getValue());
			SL.add(node);
		}
		/* Return root node for the Huffman tree (remaining node in sorted list) */
		return SL.removeIndex(0);
	}

	/**
	 * Receives the root of a Huffman tree and returns a mapping of every symbol to its corresponding Huffman code.
	 *
	 * @param huffmanRoot BTNode<Integer, String> root node of the corresponding Huffman tree whose key is
	 *                    the frequency and the value is its corresponding symbol of type string
	 * @return Map<String, String> whose key is a character string to be encoded and its value is its Huffman code
	 */
	public static Map<String, String> huffman_code(BTNode<Integer,String> huffmanRoot) {
		// Check for empty or size 1 Huffman tree and return map
		if (huffmanRoot == null) return new HashTableSC<String, String>(new SimpleHashFunction<String>());
		if (huffmanRoot.getLeftChild() == null && huffmanRoot.getRightChild() == null) {
			Map<String, String> encodingMap = new HashTableSC<String, String>(new SimpleHashFunction<String>());
			encodingMap.put(huffmanRoot.getValue(), "0");
			return encodingMap;
		}
		/* Call overloaded auxiliary method with root, an empty map, and an empty string container for computing the Huffman code */
		return huffman_code(huffmanRoot, new HashTableSC<String, String>(new SimpleHashFunction<String>()), "");
	}

	/**
	 * Receives the Huffman code map and the input string and returns the encoded string.
	 *
	 * @param encodingMap Map<String, String> whose key is a character string to be encoded and its value is its Huffman code
	 * @param inputString String to be encoded
	 * @return String inputString encoded using Huffman lookup table
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) {
		/* Declare and initialize empty encoded string container */
		String encodedString = "";
		/* Traverse input string and concatenate the corresponding string in the Huffman lookup table */
		for (int i = 0; i < inputString.length(); i++)
			encodedString = encodedString.concat(encodingMap.get(inputString.substring(i, i+1)));
		return encodedString;
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable, the input string,
	 * and the output string, and prints the results to the screen (per specifications).
	 *
	 * Output Includes: symbol, frequency and code.
	 * Also includes how many bits has the original and encoded string, plus how much space was saved using this encoding algorithm
	 *
	 * @param fD Frequency Distribution of all the characters in input string
	 * @param encodedHuffman Prefix Code Map
	 * @param inputData text string from the input file
	 * @param output processed encoded string
	 */
	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData, String output) {
		/*To get the bytes of the input string, we just get the bytes of the original string with string.getBytes().length*/
		int inputBytes = inputData.getBytes().length;

		/**
		 * For the bytes of the encoded one, it's not so easy.
		 *
		 * Here we have to get the bytes the same way we got the bytes for the original one but we divide it by 8,
		 * because 1 byte = 8 bits and our huffman code is in bits (0,1), not bytes.
		 *
		 * This is because we want to calculate how many bytes we saved by counting how many bits we generated with the encoding
		 */
		DecimalFormat d = new DecimalFormat("##.##");
		double outputBytes = Math.ceil((float) output.getBytes().length / 8);

		/**
		 * to calculate how much space we saved we just take the percentage.
		 * the number of encoded bytes divided by the number of original bytes will give us how much space we "chopped off"
		 *
		 * So we have to subtract that "chopped off" percentage to the total (which is 100%)
		 * and that's the difference in space required
		 */
		String savings =  d.format(100 - (( (float) (outputBytes / (float)inputBytes) ) * 100));


		/**
		 * Finally we just output our results to the console
		 * with a more visual pleasing version of both our Hash Tables in decreasing order by frequency.
		 *
		 * Notice that when the output is shown, the characters with the highest frequency have the lowest amount of bits.
		 *
		 * This means the encoding worked and we saved space!
		 */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		SortedList<BTNode<Integer,String>> sortedList = new SortedLinkedList<BTNode<Integer,String>>();

		/* To print the table in decreasing order by frequency, we do the same thing we did when we built the tree
		 * We add each key with it's frequency in a node into a SortedList, this way we get the frequencies in ascending order*/
		for (String key : fD.getKeys()) {
			BTNode<Integer,String> node = new BTNode<Integer,String>(fD.get(key),key);
			sortedList.add(node);
		}

		/**
		 * Since we have the frequencies in ascending order,
		 * we just traverse the list backwards and start printing the nodes key (character) and value (frequency)
		 * and find the same key in our prefix code "Lookup Table" we made earlier on in huffman_code().
		 *
		 * That way we get the table in decreasing order by frequency
		 * */
		for (int i = sortedList.size() - 1; i >= 0; i--) {
			BTNode<Integer,String> node = sortedList.get(i);
			System.out.println(node.getValue() + "\t" + node.getKey() + "\t    " + encodedHuffman.get(node.getValue()));
		}

		System.out.println("\nOriginal String: \n" + inputData);
		System.out.println("Encoded String: \n" + output);
		System.out.println("Decoded String: \n" + decodeHuff(output, encodedHuffman) + "\n");
		System.out.println("The original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + (int) outputBytes + " bytes.");
		System.out.println("Difference in space requiered is " + savings + "%.");
	}


	/*************************************************************************************
	 **                                AUXILIARY METHODS                                **
	 *************************************************************************************/
	
	/**
	 * Overloaded auxiliary method for huffman_code that receives the root of a Huffman tree and 
	 * returns a mapping of every symbol to its corresponding Huffman code.
	 *
	 * @param huffmanNode BTNode<Integer, String> node of the corresponding Huffman tree whose key is
	 *                    the frequency and the value is its corresponding symbol of type string
	 * @param huffmanEncoding Map<String, String> current state of the huffman encoding look up table to be returned
	 * @param huffmanCode huffman code generated for current node (huffmanNode)
	 * @return Map<String, String> final encoding look up table whose key is a character string to be encoded 
	 *                             and its value is its Huffman code
	 */
	public static Map<String, String> huffman_code(BTNode<Integer,String> huffmanNode, Map<String, String> huffmanEncoding, String huffmanCode) {
		/* Add "0" to Huffman code for left child node in Huffman tree */
		if (huffmanNode.getLeft() != null) 
			huffmanEncoding = huffman_code(huffmanNode.getLeftChild(), huffmanEncoding, huffmanCode + "0");
		/* Add "1" to Huffman code for right child node in Huffman tree */
		if (huffmanNode.getRight() != null) 
			huffmanEncoding = huffman_code(huffmanNode.getRightChild(), huffmanEncoding, huffmanCode + "1");
		/* If node is a leaf, add Huffman code for the node to the huffmanEncoding map */
		if (huffmanNode.getLeftChild() == null && huffmanNode.getRightChild() == null)
			huffmanEncoding.put(huffmanNode.getValue(), huffmanCode);
		return huffmanEncoding;
	}

	/**
	 * Auxiliary Method that decodes the generated string by the Huffman Coding Algorithm.
	 *
	 * Used for output Purposes
	 *
	 * @param output - Encoded String
	 * @param lookupTable
	 * @return The decoded String, this should be the original input string parsed from the input file
	 */
	public static String decodeHuff(String output, Map<String, String> lookupTable) {
		String result = "";
		int start = 0;
		List<String>  prefixCodes = lookupTable.getValues();
		List<String> symbols = lookupTable.getKeys();

		/*looping through output until a prefix code is found on map and
		 * adding the symbol that the code that represents it to result */
		for(int i = 0; i <= output.length();i++){

			String searched = output.substring(start, i);

			int index = prefixCodes.firstIndex(searched);

			if(index >= 0) { //Found it
				result= result + symbols.get(index);
				start = i;
			}
		}
		return result;
	}


}
