package PostProcessGPig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * Prepare prefix and suffix features from listsAsGraph.gp.
 * 
 * Input line format: s_4_17_part1 <TAB> nonsteroidal anti-inflammatory drugs
 * 
 * Output feature line format: s_4_17_part1 <TAB> suffix=xx  prefix=x
 * 
 * The considered length of prefix and suffix is 3 and 4.
 * All in lower case
 * 
 */
public class PreSuffixFeature {

	public static int[] fixLen = { 3, 4 };

	public static void printHelp() {

		System.out.println("Please give two args: infile and outfile.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// process("testData\\SvmPreSuffixFeature_infile",
		// "testData\\SvmPreSuffixFeature_outfile");

		if (args.length != 2) {
			printHelp();
		}
		process(args[0], args[1]);
	}

	public static void process(String infile, String outFile)
			throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

		String line = br.readLine();
		String[] tokens = line.split("\\t+");

		String curID = tokens[0];
		String preID = curID;
		String listItems = "";

		while (line != null) {
			if(line.trim().length() == 0 ){
				line = br.readLine();
				continue;
			}
			tokens = line.split("\\t+");
			if(tokens.length<2){
				line = br.readLine();
				continue;
			}
			curID = tokens[0];
			if (!curID.equals(preID)) {
				saveFeats(preID, listItems, bw);
				preID = curID;
				listItems = (tokens[1] + " ");
			} else {
				listItems += (tokens[1] + " ");
			}
			line = br.readLine();
		}
		saveFeats(curID, listItems, bw);
		br.close();
		bw.close();
	}

	// public static void saveFeats(String listID, String tokens, BufferedWriter
	// bw)
	// throws IOException {
	// HashMap<String, Integer> prefixMap = new HashMap<String, Integer>();
	// HashMap<String, Integer> suffixMap = new HashMap<String, Integer>();
	// String[] itemToks = tokens.split("\\s+");
	// for (String iTok : itemToks) {
	// for (int flen : fixLen)
	// if (iTok.length() > flen) {
	// String pre = iTok.substring(0, flen);
	// String suf = iTok.substring(iTok.length() - flen,
	// iTok.length());
	//
	// if (!prefixMap.containsKey(pre))
	// prefixMap.put(pre, 0);
	// prefixMap.put(pre, prefixMap.get(pre) + 1);
	//
	// if (!suffixMap.containsKey(suf))
	// suffixMap.put(suf, 0);
	// suffixMap.put(suf, suffixMap.get(suf) + 1);
	// }
	// }
	// StringBuffer sb = new StringBuffer(listID + "\t");
	// for (String pre : prefixMap.keySet()) {
	// sb.append("prefix=" + pre + " ");
	// }
	// for (String suf : suffixMap.keySet()) {
	// sb.append("suffix=" + suf + " ");
	// }
	// bw.write(sb.toString().toLowerCase());
	// bw.newLine();
	//
	// }

	public static void saveFeats(String listID, String tokens, BufferedWriter bw)
			throws IOException {
		String[] itemToks = tokens.toLowerCase().split("\\s+");
		StringBuffer sb = new StringBuffer(listID + "\t");
		for (String iTok : itemToks) {
			for (int flen : fixLen)
				if (iTok.length() > flen) {
					String pre = iTok.substring(0, flen);
					String suf = iTok.substring(iTok.length() - flen,
							iTok.length());
					sb.append("prefix=" + pre + " ");
					sb.append("suffix=" + suf + " ");
				}
		}
		bw.write(sb.toString());
		bw.newLine();

	}

}
