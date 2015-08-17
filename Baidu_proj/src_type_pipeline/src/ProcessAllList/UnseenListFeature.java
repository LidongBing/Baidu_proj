package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/*
 * Generate test feature file from a feature token file of unseen examples in format: 
 * s_7204153_15_part3 <tab> mother have whose there phenothiazine
 * 
 */
public class UnseenListFeature {

	public static void printHelp() {

		System.out
				.println("Please give three args: inputTokFeatureFile, dictFile, outputVecFile.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 3) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();
		HashMap<String, Integer> dict = loadMap(args[1]);

		doOne(args[0], args[2], dict);

	}

	public static HashMap<String, Integer> loadMap(String dictFile)
			throws IOException {
		HashMap<String, Integer> dict = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(dictFile));
		String line = br.readLine();
		while (line != null) {
			String[] tokens = line.split("\\s+");
			if (tokens.length == 2) {
				dict.put(tokens[0], Integer.parseInt(tokens[1]));
			}
			line = br.readLine();
		}
		br.close();

		return dict;
	}

	public static void doOne(String infile, String outfileVec,
			HashMap<String, Integer> dict) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfileVec));

		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line = br.readLine();

		while (line != null) {
			String vector = getOneVec(line, dict);
			bw.write(vector);
			bw.newLine();
			line = br.readLine();
		}
		br.close();

		bw.close();

	}

	public static String getOneVec(String tokVec, HashMap<String, Integer> dict) {
		StringBuffer sb = new StringBuffer("0 ");
		String[] toks = tokVec.split("\\s+");

		HashMap<Integer, Double> feats = new HashMap<Integer, Double>();
		ArrayList<Integer> featIds = new ArrayList<Integer>();
		for (int j = 1; j < toks.length; j++) {

			if (dict.containsKey(toks[j])) {
				int featID = dict.get(toks[j]);
				if (!feats.containsKey(featID)) {
					feats.put(featID, 1.0);
					featIds.add(featID);
				} else
					feats.put(featID, 1 + feats.get(featID));
			}

		}

		Collections.sort(featIds);
		for (Integer id : featIds) {

			sb.append(id.toString() + ":");
			sb.append(feats.get(id) + " ");

		}

		return sb.toString();
	}
}
