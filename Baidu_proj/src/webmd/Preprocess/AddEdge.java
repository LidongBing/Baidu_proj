package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/*
 * Add edges for drug@item pairs, with Jaccard Index, from hasFeature.cfacts
 */
public class AddEdge {

	// pair TO feature List
	public static HashMap<String, ArrayList<String>> featureMap = new HashMap<String, ArrayList<String>>();
	// feature TO pair set
	public static HashMap<String, HashSet<String>> invertedIdx = new HashMap<String, HashSet<String>>();

	public static HashMap<String, Double> edgeMap = new HashMap<String, Double>();

	// the items that are matchec by second string
	public static HashSet<String> ssItemMatching = new HashSet<String>();

	public static HashSet<String> stopword;

	public static double itemMatchThre = 0;
	public static double edgeWeithThreshold = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// String[] x = { "aa", "ab" };
		// String[] y = { "aa", "aa" };
		// System.out.println(similarity(x, y));
		// System.out.println(similarityDup(Arrays.asList(x),
		// Arrays.asList(y)));
		// System.out
		// .println(Arrays.asList("   1.00 bone_marrow_stem_cells  bone_marrow_stem_cells"
		// .split("\\s+")));

		if (args.length != 6) {
			System.out
					.println("ERROR: please give six parameters: hasFeature.cfacts, stopWord.txt, itemMapFile.txt, ");
			System.out
					.println("      matchingThreshold (0.8 recommended), edgeWeithThreshold (0.1 recommended), and outputFile.");
			System.exit(0);
		}

		itemMatchThre = Double.parseDouble(args[3]);
		edgeWeithThreshold = Double.parseDouble(args[4]);
		stopword = loadStopWord(args[1]);
		loadSSItemMatching(args[2]);
		loadHasFeature(args[0]);
		buildIdx();
		computeEdge();
		saveEdge(args[5]);

	}

	public static void buildIdx() {
		ArrayList<String> feats;

		for (String pair : featureMap.keySet()) {
			feats = featureMap.get(pair);
			for (String f : feats) {
				if (!invertedIdx.containsKey(f)) {
					invertedIdx.put(f, new HashSet<String>());
				}
				invertedIdx.get(f).add(pair);
			}
		}

	}

	public static void loadHasFeature(String infile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(infile));

		String line = null;
		String[] toks;
		String pair;
		String feature;
		while ((line = br.readLine()) != null) {
			toks = line.trim().split("\\s+");
			if (toks.length < 3)
				continue;

			pair = toks[1];
			feature = toks[2];
			if (stopword.contains(feature))
				continue;

			if (!featureMap.containsKey(pair)) {
				featureMap.put(pair, new ArrayList<String>());
			}
			featureMap.get(pair).add(feature);
		}

		br.close();
	}

	public static void saveEdge(String outfile) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

		for (Entry<String, Double> entry : edgeMap.entrySet()) {
			bw.write("edge\t" + entry.getKey() + "\t" + entry.getValue());
			bw.newLine();
		}

		bw.close();
	}

	public static void computeEdge() throws IOException {
		HashSet<String> toPairs;
		ArrayList<String> feats;
		double weight;
		List<String> xd, yd;

		String fromItem, toItem;

		int cnt = 0;

		for (String pair : featureMap.keySet()) {

			fromItem = pair.substring(pair.indexOf('@') + 1);
			feats = featureMap.get(pair);
			xd = new ArrayList<String>();
			xd.addAll(dupEleSet(feats));

			toPairs = new HashSet<String>();
			for (String f : feats) {
				toPairs.addAll(invertedIdx.get(f));
			}

			for (String toP : toPairs) {
				if (pair.equals(toP))
					continue;

				toItem = toP.substring(toP.indexOf('@') + 1);

				if (!(fromItem.equals(toItem)
						|| ssItemMatching.contains(fromItem + "\t" + toItem) || ssItemMatching
							.contains(toItem + "\t" + fromItem)))
					continue;

				if (!edgeMap.containsKey(pair + "\t" + toP)) {

					if (featureMap.get(toP).size() / (double) feats.size() > 10
							|| featureMap.get(toP).size()
									/ (double) feats.size() < 0.1)
						continue;

					yd = new ArrayList<String>();
					yd.addAll(dupEleSet(featureMap.get(toP)));
					weight = similarity(xd, yd);
					if (weight >= edgeWeithThreshold) {
						edgeMap.put(pair + "\t" + toP, weight);
						edgeMap.put(toP + "\t" + pair, weight);
					}
				}
			}
			// cnt++;
			// if (cnt % 10000 == 0) {
			// System.out.println("finished " + cnt + " outof "
			// + featureMap.keySet().size());
			//
			// }
			// if (cnt == 20000)
			// return;
		}
	}

	public static double similarity(String[] x, String[] y) {
		if ((x != null && y != null) && (x.length > 0 || y.length > 0)) {
			return similarity(Arrays.asList(x), Arrays.asList(y));
		} else {
			return 0;
		}
	}

	public static double similarity(List<String> x, List<String> y) {

		if (x == null || y == null) {
			return 0.0;
		}
		if (x.size() == 0 || y.size() == 0) {
			return 0.0;
		}

		HashSet<String> unionXY = new HashSet<String>(x);
		unionXY.addAll(y);

		HashSet<String> intersectionXY = new HashSet<String>(x);
		intersectionXY.retainAll(y);

		return (double) intersectionXY.size() / (double) unionXY.size();
	}

	public static double similarityDup(List<String> x, List<String> y) {

		if (x == null || y == null) {
			return 0.0;
		}
		if (x.size() == 0 || y.size() == 0) {
			return 0.0;
		}

		List<String> xd = new ArrayList<String>();

		List<String> yd = new ArrayList<String>();

		xd.addAll(dupEleSet(x));
		yd.addAll(dupEleSet(y));
		return similarity(xd, yd);

	}

	private static HashSet<String> dupEleSet(List<String> x) {
		HashSet<String> ret = new HashSet<String>();
		HashMap<String, Integer> cntMap = new HashMap<String, Integer>();
		for (String item : x) {
			int cnt = 0;
			if (cntMap.containsKey(item)) {
				cnt = cntMap.get(item);
			}
			cntMap.put(item, ++cnt);

			ret.add(item + "_ID=" + cnt);
		}

		return ret;
	}

	public static void loadSSItemMatching(String file) throws IOException {
		String[] toks;

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			toks = line.trim().split("\\s+");
			if (toks.length < 3)
				continue;
			if (Double.parseDouble(toks[0]) >= itemMatchThre) {
				ssItemMatching.add(toks[1] + "\t" + toks[2]);
			}

		}
		br.close();
	}

	public static HashSet<String> loadStopWord(String file) throws IOException {
		HashSet<String> ret = new HashSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {

			line = cleanUnwantedChars(line);
			ret.add(line.trim().toLowerCase());
			ret.add("bowContext=" + line.trim().toLowerCase());
		}
		br.close();
		return ret;
	}

	private static String cleanUnwantedChars(String line) {
		String ret = "";
		char c;
		for (int i = 0; i < line.length(); i++) {
			c = line.charAt(i);
			if (c > 32 && c < 127)
				ret += c;
		}
		return ret;
	}

}
