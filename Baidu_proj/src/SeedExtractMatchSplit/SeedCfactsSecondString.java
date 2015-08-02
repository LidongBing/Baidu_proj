package SeedExtractMatchSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.wcohen.ss.DistanceLearnerFactory;
import com.wcohen.ss.api.StringDistanceLearner;
import com.wcohen.ss.expt.Blocker;
import com.wcohen.ss.expt.MatchData;
import com.wcohen.ss.expt.MatchExpt;

public class SeedCfactsSecondString {
	private static HashSet<String> graphSet = new HashSet<String>();
	private static HashSet<String> seedSet = new HashSet<String>();
	private static HashMap<String, ArrayList<String>> drugMap = new HashMap<>();
	private static HashMap<String, ArrayList<String>> itemMap = new HashMap<>();
	private static HashMap<String, ArrayList<String>> validMapping = new HashMap<>();
	public static final String BLOCKER_PACKAGE = "com.wcohen.ss.expt.";

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String seedFile = args[0];
		String graphFile = args[1];
		String drugDataFile = args[2];
		String itemDataFile = args[3];
		String drugMappingFile = args[4];
		String itemMappingFile = args[5];
		String validMappingFile = args[6];
		double boundary = Double.parseDouble(args[7]);
		String outputFile = args[8];
		loadSet(seedFile, seedSet);
		loadSet(graphFile, graphSet);
		generateDataFile(drugDataFile, itemDataFile);
		mappingGenericName(drugMappingFile, drugDataFile);
		mappingGenericName(itemMappingFile, itemDataFile);
		loadMappingResult(drugMappingFile, boundary+0.05, drugMap);
		loadMappingResult(itemMappingFile, boundary, itemMap);
		getCombinedMapping(validMappingFile);
		output(outputFile, seedFile);
	}

	public static void getCombinedMapping(String validMappingFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(validMappingFile));
		for (String str : seedSet) {
			String[] pair = str.split("@");
			String drugName = pair[0];
			String itemName = pair[1];
			ArrayList<String> drugList = null;
			ArrayList<String> itemList = null;
			if (drugMap.containsKey(drugName)) {
				drugList = drugMap.get(drugName);
			}
			if (itemMap.containsKey(itemName)) {
				itemList = itemMap.get(itemName);
			}
			ArrayList<String> result = new ArrayList<String>();
			if (drugList != null && itemList != null) {
				for (int i = 0; i < drugList.size(); i++) {
					for (int j = 0; j < itemList.size(); j++) {
						String tmpCombo = drugList.get(i) + "@" + itemList.get(j);
						if (graphSet.contains(tmpCombo)) {
							result.add(tmpCombo);
							bw.write(str + "\t" + tmpCombo + "\n");
						}
					}
				}
			}
			validMapping.put(str, result);
		}
		bw.close();
	}

	public static void output(String outputFile, String seedFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedReader br = new BufferedReader(new FileReader(seedFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String pair = line.split("\t")[2];
			for (String str : validMapping.get(pair)) {
				bw.write(line.replace(pair, str) + "\n");
			}
		}
		bw.close();
		br.close();
	}

	public static void loadSet(String file, HashSet<String> set) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			set.add(tokens[2]);
		}
		br.close();
	}

	public static void generateDataFile(String drugDataFile, String itemDataFile) throws IOException {
		BufferedWriter bwDrug = new BufferedWriter(new FileWriter(drugDataFile));
		BufferedWriter bwItem = new BufferedWriter(new FileWriter(itemDataFile));
		HashSet<String> graphSetItem = new HashSet<String>();
		HashSet<String> graphSetDrug = new HashSet<String>();
		HashSet<String> seedSetItem = new HashSet<String>();
		HashSet<String> seedSetDrug = new HashSet<String>();
		for (String str : seedSet) {
			String[] pair = str.split("@");
			seedSetItem.add(pair[1]);
			seedSetDrug.add(pair[0]);
		}
		for (String str : graphSet) {
			String[] pair = str.split("@");
			graphSetItem.add(pair[1]);
			graphSetDrug.add(pair[0]);
		}

		for (String str : seedSetItem) {
			bwItem.write("seed\t1\t" + str + "\n");
		}
		for (String str : seedSetDrug) {
			bwDrug.write("seed\t1\t" + str + "\n");
		}
		for (String str : graphSetItem) {
			bwItem.write("graph\t1\t" + str + "\n");
		}
		for (String str : graphSetDrug) {
			bwDrug.write("graph\t1\t" + str + "\n");
		}
		bwDrug.close();
		bwItem.close();
	}

	public static void mappingGenericName(String mappingFile, String dataFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Blocker blocker = (Blocker) Class.forName(BLOCKER_PACKAGE + "TokenBlocker").newInstance();
		StringDistanceLearner learner = DistanceLearnerFactory.build("SoftTFIDF");
		MatchData data = new MatchData(dataFile);
		MatchExpt expt = new MatchExpt(data, learner, blocker);
		expt.dumpResults(new PrintStream(new FileOutputStream(mappingFile)));
	}

	public static void loadMappingResult(String mappingFile, double boundary, HashMap<String, ArrayList<String>> map) throws IOException {
		BufferedReader bw = new BufferedReader(new FileReader(mappingFile));
		String line = null;
		while ((line = bw.readLine()) != null) {
			String[] tokens = line.split("\t");
			double score = Double.parseDouble(tokens[0].trim());
			String key = tokens[2];
			String value = tokens[1];
			if (score < boundary)
				break;
			if (map.containsKey(key)) {
				map.get(key).add(value);
			}
			else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(value);
				map.put(key, list);
			}
		}
		bw.close();
	}
}
