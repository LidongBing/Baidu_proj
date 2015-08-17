package ClassifyExample;

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
 * Generate test feature file from a feature token file in format: 
 * s_7204153_15_part3	4	mother 1.0	have 1.0	whose 1.0	there 1.0	phenothiazine 1.0	infant 1.0	report 1.0	. 1.0
 * 
 *  */
public class TestFeature {

	public static void printHelp() {

		System.out
				.println("Please give four args: inputTokFeatureFile, dictFile, postiveLabel, outputVecFile.");

		System.out
				.println("postiveLabel: the ID of the positive class.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 4) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();
		HashMap<String, Integer> dict = loadMap(args[1]);

		doOne(args[0], args[2], args[3], dict);

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

	public static void doOneExcludeTrainExmps(String intestfile,
			String intrainfile, String positiveLabel, String outfileVec,
			HashMap<String, Integer> dict) throws IOException {
		ArrayList<String> tokVecs = loadVector(intestfile);

		HashSet<String> trainExmpSet = new HashSet<String>();
		trainExmpSet.addAll(loadVector(intrainfile));

		HashSet<String> uniqVecs = new HashSet<String>();

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfileVec));
		for (String tokVec : tokVecs) {
			String vector = getOneVec(tokVec, positiveLabel, dict);
			if (!uniqVecs.contains(vector)) {
				String oneVec = getOneVec(tokVec, positiveLabel, dict);
				uniqVecs.add(vector);
				if (!trainExmpSet.contains(oneVec)) {
					bw.write(getOneVec(tokVec, positiveLabel, dict));
					bw.newLine();
				}
			}
		}
		bw.close();

	}

	public static void doOne(String infile, String positiveLabel,
			String outfileVec, HashMap<String, Integer> dict)
			throws IOException {
		ArrayList<String> tokVecs = loadVector(infile);

//		HashSet<String> uniqVecs = new HashSet<String>();

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfileVec));
		for (String tokVec : tokVecs) {
			String vector = getOneVec(tokVec, positiveLabel, dict);
//			if (!uniqVecs.contains(vector)) {
				bw.write(vector);
				bw.newLine();
			// uniqVecs.add(vector);
			// }
		}
		bw.close();

	}

	public static String getOneVec(String tokVec, String positiveLabel,
			HashMap<String, Integer> dict) {
		StringBuffer sb = new StringBuffer();
		String[] toks = tokVec.split("\\t+");
		if (toks[1].equals(positiveLabel)) {
			sb.append("1 ");
		} else {
			sb.append("-1 ");
		}

		HashMap<Integer, Double> feats = new HashMap<Integer, Double>();
		ArrayList<Integer> featIds = new ArrayList<Integer>();
		for (int j = 2; j < toks.length; j++) {
			String[] subtoks = toks[j].split(" ");
			if (subtoks.length == 2) {
				if (dict.containsKey(subtoks[0])) {
					int featID = dict.get(subtoks[0]);
					if (!feats.containsKey(featID)) {
						feats.put(featID, Double.parseDouble(subtoks[1]));
						featIds.add(featID);
					} else
						feats.put(featID, Double.parseDouble(subtoks[1])
								+ feats.get(featID));
				}
			}
		}

		Collections.sort(featIds);
		for (Integer id : featIds) {

			sb.append(id.toString() + ":");
			sb.append(feats.get(id) + " ");

		}

		return sb.toString();
	}

	public static ArrayList<String> loadVector(String tokFeatFile)
			throws IOException {
		ArrayList<String> vectors = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(tokFeatFile));
		String line = br.readLine();

		while (line != null) {
			vectors.add(line);

			line = br.readLine();
		}
		br.close();
		return vectors;
	}

}
