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
 * Prepare bowContext features from the sentence containing the list
 * 
 * Input line format:"s_4_17_part1 \t list: <nonsteroidal anti-inflammatory drugs> in: 
 * Naproxen USP is a proprionic acid derivative related to the arylacetic acid group 
 * of nonsteroidal anti-inflammatory drugs ."
 * 
 * Output feature line format: s_4_17_part1 \t naproxen usp ....
 * All in lower case
 * 
 */
public class BowContextFeature {

	public static void printHelp() {

		System.out.println("Please give two args: infile and outfile.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// getFeatIDMap("testData\\SvmBowContextFeature_infile",
		// "testData\\SvmBowContextFeature_outfile");

		if (args.length != 2) {
			printHelp();
		}
		getFeatIDMap(args[0], args[1]);
	}

	public static void getFeatIDMap(String infile, String outFile)
			throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

		String line = br.readLine();
		String[] tokens = null;

		while (line != null) {
			if (line.trim().length() == 0) {
				line = br.readLine();
				continue;
			}
			tokens = line.split("\\t+");
			if (tokens.length < 2) {
				line = br.readLine();
				continue;
			}
			saveFeats(tokens[0], tokens[1], bw);
			line = br.readLine();
		}
		br.close();
		bw.close();
	}

	public static void saveFeats(String listID, String tail, BufferedWriter bw)
			throws IOException {
		String list = tail.substring(
				tail.indexOf("list: <") + "list: <".length(),
				tail.indexOf("> in: "));
		String words = tail.substring(tail.indexOf("> in: ")
				+ "> in: ".length());
		// words=words.replaceAll(list, "");
		String left = words.substring(0, words.indexOf(list));
		String right = words.substring(words.indexOf(list) + list.length(),
				words.length());

		// HashSet<String> listTokSet = new HashSet<String>();
		// // HashSet<String> contextTokSet = new HashSet<String>();
		//
		// HashMap<String, Integer> contextMap = new HashMap<String, Integer>();
		//
		// String[] listToks = list.split("\\s+");
		// for (String tok : listToks) {
		// listTokSet.add(tok);
		// }
		//
		// String[] contextToks = words.split("\\s+");
		//
		// for (String contextTok : contextToks) {
		// if (!listTokSet.contains(contextTok)) {
		// if (!contextMap.containsKey(contextTok))
		// contextMap.put(contextTok, 0);
		// contextMap.put(contextTok, contextMap.get(contextTok) + 1);
		// }
		// }
		//
		// StringBuffer sb = new StringBuffer(listID + "\t");
		// for (String key : contextMap.keySet()) {
		// sb.append(key + " ");
		// }

		words = left.toLowerCase() + " " + right.toLowerCase();
		String[] toks = words.split("\\s+");

		StringBuffer sb = new StringBuffer(listID + "\t");
		for (String tok : toks) {
			sb.append("bowContext=" + tok + " ");
		}

		bw.write(sb.toString());
		bw.newLine();

	}
}
