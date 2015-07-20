package ClassifyExample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/*
 * Generate feature token file for binary class classification
 * 
 * Each feature type is separately generated and they are merged later
 * 
 * The features are filtered for training examples, but no need for testing
 */
public class TokFeature {
	public static int minFre = 3;

	// after filtering with minFre, filter the filterTop ones
	public static double filterTop = 0.1;

	public static void printHelp() {

		System.out.println("Please give five args: tokFeatFile, doFilter, labelMapFile, inputExmpFile, and outputFeatureFile.");

		System.out.println("tokFeatFile: file containing tok features ");
		System.out.println("doFilter: dofilter OR nofilter, whether filter the features in training examples");
		System.out.println("inputExmpFile: contains the label ID map, in format 'symptom\t id'");
		System.out.println("outputFeatureFile: the output file");
		System.out.println("If input example file is a testing file, it may or may not have labels");
		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 5) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();

		if (!args[0].endsWith(File.separator))
			args[0] += File.separator;

		// String[] featFiles = { "bow_context.tok_feat",
		// "close_context.tok_feat", "dep.tok_feat", "list_bow.tok_feat",
		// "pre_suffix.tok_feat" };

		boolean doFilter = false;
		if (args[1].equals("dofilter"))
			doFilter = true;
		if (doFilter) {
			if (args[0].contains("list_bow.tok_feat")) {
				minFre = 2;
				filterTop = 0.0;
			}
			else if (args[0].contains("pre_suffix.tok_feat")) {
				minFre = 2;
				filterTop = 0.05;
			}
			else {
				minFre = 3;
				filterTop = 0.1;
			}
		}

		HashSet<String> labels = new HashSet<String>();
		labels.addAll(loadLabel(args[2]).values());

		String exmpFile = args[3];

		ArrayList<String> exmps = null;
		ArrayList<String> exmpTypes = null;

		exmps = new ArrayList<String>();
		exmpTypes = new ArrayList<String>();
		loadExmpList(exmpFile, exmps, exmpTypes);
		// labels.addAll(trainExmpTypes);

		// for (String featFile : featFiles) {

		HashMap<String, HashMap<String, Double>> trainFeatMap = getFeat(doFilter, args[0], exmps);

		saveFeatVect(trainFeatMap, exmps, exmpTypes, args[4]);

		// }

	}

	public static void saveFeatVect(HashMap<String, HashMap<String, Double>> featureMap, ArrayList<String> exmpLists, ArrayList<String> exmpTypes, String outputFile) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		StringBuffer sb = null;
		Set<Entry<String, Double>> featSet = null;

		for (int i = 0; i < exmpLists.size(); i++) {
			sb = new StringBuffer(exmpLists.get(i) + "\t" + exmpTypes.get(i) + "\t");

			featSet = featureMap.get(exmpLists.get(i)).entrySet();
			for (Entry<String, Double> entry : featSet) {
				sb.append(entry.getKey() + " " + entry.getValue() + "\t");
			}
			bw.write(sb.toString());
			bw.newLine();
		}

		bw.close();

	}

	// return: <listID, <featureTok, weight>>
	// each line of featfile: listID \t feature1 feature 2
	public static HashMap<String, HashMap<String, Double>> getFeat(boolean filter, String featfile, ArrayList<String> exmpLists) throws IOException {

		// listID to {feature, IDs}
		HashMap<String, HashMap<String, Double>> featNestedMap = new HashMap<String, HashMap<String, Double>>();
		for (String exmp : exmpLists) {
			featNestedMap.put(exmp, new HashMap<String, Double>());
		}

		HashMap<String, Integer> featureFreMap = new HashMap<String, Integer>();

		BufferedReader brFact = new BufferedReader(new FileReader(featfile));
		String line = brFact.readLine();
		String[] tokens = null;
		double weight = 0;
		while (line != null) {
			tokens = line.split("\\s+");
			if (tokens.length == 0) {
				line = brFact.readLine();
				continue;
			}

			if (featNestedMap.containsKey(tokens[0])) {
				for (int j = 1; j < tokens.length; j++) {
					HashMap<String, Double> tmpMap = featNestedMap.get(tokens[0]);
					if (!tmpMap.containsKey(tokens[j]))
						tmpMap.put(tokens[j], (double) 0);
					tmpMap.put(tokens[j], tmpMap.get(tokens[j]) + 1);

					if (!featureFreMap.containsKey(tokens[j]))
						featureFreMap.put(tokens[j], 0);
					featureFreMap.put(tokens[j], featureFreMap.get(tokens[j]) + 1);
				}
			}
			line = brFact.readLine();
		}
		brFact.close();

		if (filter) {

			HashMap<String, Integer> featFiltered = new HashMap<String, Integer>();
			Set<Entry<String, Integer>> featSet = featureFreMap.entrySet();
			for (Entry<String, Integer> entry : featSet) {
				if (entry.getValue() >= minFre) {
					featFiltered.put(entry.getKey(), entry.getValue());
				}
			}
			featureFreMap = featFiltered;

			ArrayList<Integer> freqList = new ArrayList<Integer>();
			freqList.addAll(featureFreMap.values());
			Collections.sort(freqList);
			int cutFre = 0;
			if (freqList.size() != 0) {
				cutFre = freqList.get((int) (freqList.size() * (1 - filterTop - 0.0001)));
			}

			featFiltered = new HashMap<String, Integer>();
			featSet = featureFreMap.entrySet();
			for (Entry<String, Integer> entry : featSet) {
				if (entry.getValue() <= cutFre) {
					featFiltered.put(entry.getKey(), entry.getValue());
				}
			}

			Set<Entry<String, HashMap<String, Double>>> nestedEntrySet = featNestedMap.entrySet();
			for (Entry<String, HashMap<String, Double>> nestedEntry : nestedEntrySet) {
				ArrayList<String> feats = new ArrayList<String>();
				feats.addAll(nestedEntry.getValue().keySet());
				for (String feat : feats) {
					if (!featFiltered.containsKey(feat))
						nestedEntry.getValue().remove(feat);
				}
			}
		}

		return featNestedMap;
	}

	/*
	 * each line is a training example "listID \t class"
	 */
	public static void loadExmpList(String exampleFile, ArrayList<String> examples, ArrayList<String> exmpTypes) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(exampleFile));
		String line = br.readLine();
		String[] tokens = null;
		while (line != null && line.trim().length() > 0) {
			tokens = line.split("\\s+");
			if (tokens.length == 2) {
				examples.add(tokens[0]);
				exmpTypes.add(tokens[1]);
			}
			else {
				examples.add(tokens[0]);
				exmpTypes.add("na");
			}

			line = br.readLine();
		}
		br.close();

	}

	public static HashMap<String, String> loadLabel(String labelFile) throws IOException {

		HashMap<String, String> labelMap = new HashMap<String, String>();

		BufferedReader br = new BufferedReader(new FileReader(labelFile));
		String line = br.readLine();
		String[] tokens = null;
		while (line != null && line.trim().length() != 0) {
			tokens = line.split("\\s+");

			if (tokens.length != 2) {
				System.out.println("ERROR: label map file should have two columns: labelName \t labelID");
				System.exit(0);

			}
			labelMap.put(tokens[0], tokens[1]);
			line = br.readLine();
		}
		br.close();

		return labelMap;
	}
}
