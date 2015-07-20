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
 * Generate train feature file from a feature token file in format: 
 * s_7204153_15_part3 <tab> 4 <tab> mother 1.0 <tab> have 1.0 <tab> whose 1.0 <tab> there 1.0 <tab> 
 * 
 * The first step is to generate the feature dictionary  
 * 
 * According to the positive class to generate feature file, remove duplicated vectors
 * 
 * Save the dictionary for each training file, then use it to generate testing file
 */
public class TrainFeature {
	public static void printHelp() {

		System.out
				.println("Please give four args: inputTokFeatureFile, postiveLabel, outputVecFile, outputDictFile.");
		System.out.println("postiveLabel: the ID of the positive class");

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

		doOne(args[0], args[1], args[2], args[3]);
	}

	public static void doOne(String infile, String positiveLabel,
			String outfileVec, String outfileDict) throws IOException {
		ArrayList<String> tokVecs = loadVector(infile);

		HashMap<String, Integer> dict = buildDic(tokVecs);
//		HashSet<String> uniqVecs = new HashSet<String>();

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfileVec));
		for (String tokVec : tokVecs) {
			String vector = getOneVec(tokVec, positiveLabel, dict);
//			if (!uniqVecs.contains(vector)) {
				bw.write(vector);
				bw.newLine();
//				uniqVecs.add(vector);
//			}
		}
		bw.close();

		saveDict(outfileDict, dict);

	}

	public static void saveDict(String outfile, HashMap<String, Integer> dict)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		Set<Entry<String, Integer>> entries = dict.entrySet();
		for (Entry<String, Integer> entry : entries) {
			bw.write(entry.getKey() + "\t" + entry.getValue());
			bw.newLine();
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

	public static HashMap<String, Integer> buildDic(ArrayList<String> tokVecs) {
		HashMap<String, Integer> dict = new HashMap<String, Integer>();
		int cnt = 1;
		for (int i = 0; i < tokVecs.size(); i++) {
			String[] toks = tokVecs.get(i).split("\\t+");
			for (int j = 2; j < toks.length; j++) {
				String[] subtoks = toks[j].split(" ");
				if (subtoks.length == 2) {
					if (!dict.containsKey(subtoks[0])) {
						dict.put(subtoks[0], cnt++);
					}
				}
			}
		}

		return dict;
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
