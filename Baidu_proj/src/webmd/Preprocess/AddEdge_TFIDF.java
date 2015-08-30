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
public class AddEdge_TFIDF {

	// pair TO feature List
	public static HashMap<String, ArrayList<String>> featureMap = new HashMap<String, ArrayList<String>>();
	// feature TO pair set
	public static HashMap<String, HashSet<String>> invertedIdx = new HashMap<String, HashSet<String>>();

	public static HashMap<Long, Double> idfMap = new HashMap<Long, Double>();

	public static HashMap<String, Long> termID = new HashMap<String, Long>();

	public static HashMap<String, Double> edgeMap = new HashMap<String, Double>();

	// the items that are matchec by second string
	public static HashSet<String> ssItemMatching = new HashSet<String>();

	public static HashSet<String> stopword;

	public static double itemMatchThre = 0;
	public static double edgeWeithThreshold = 0;

	public static long totalFeatNO = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// String[] x = { "aa", "ab" };
		// String[] y = { "aa", "aa" };
		// System.out.println(similarity(x, y));
		// System.out.println(similarityDup(Arrays.asList(x),
		// Arrays.asList(y)));
		// System.out
		// .println(Arrays.asList(" 1.00 bone_marrow_stem_cells
		// bone_marrow_stem_cells"
		// .split("\\s+")));

		if (args.length != 6) {
			System.out.println("ERROR: please give six parameters: hasFeature.cfacts, stopWord.txt, itemMapFile.txt, ");
			System.out.println(
					"      matchingThreshold (0.8 recommended), edgeWeithThreshold (0.1 recommended), and outputFile.");
			System.exit(0);
		}

		itemMatchThre = Double.parseDouble(args[3]);
		edgeWeithThreshold = Double.parseDouble(args[4]);
		stopword = loadStopWord(args[1]);

		loadSSItemMatching(args[2]);
		loadHasFeature(args[0]);

		getIDF();
		buildIdx();

		computeTFIDFEdge();

		saveEdge(args[5]);

	}

	public static void loadHasFeature(String infile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(infile));

		String line = null;
		String[] toks;
		String pair;
		String feature;

		long cnt = 0;

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

			if (!termID.containsKey(feature))
				termID.put(feature, cnt++);
		}
		totalFeatNO = cnt;
		br.close();
	}

	public static void buildIdx() {
		ArrayList<String> feats;

		for (String pair : featureMap.keySet()) {
			feats = featureMap.get(pair);
			for (String feat : feats) {
				if (!invertedIdx.containsKey(feat)) {
					invertedIdx.put(feat, new HashSet<String>());
				}
				invertedIdx.get(feat).add(pair);
			}
		}

	}

	public static void getIDF() {
		HashSet<String> feats;

		Long id;
		for (String pair : featureMap.keySet()) {
			feats = new HashSet<String>();
			feats.addAll(featureMap.get(pair));
			for (String feat : feats) {
				id = termID.get(feat);
				if (!idfMap.containsKey(id)) {
					idfMap.put(id, 0.0);
				}
				idfMap.put(id, idfMap.get(id) + 1);
			}
		}

		for (Long idd : idfMap.keySet()) {
			idfMap.put(idd, Math.log(totalFeatNO / idfMap.get(idd)));

		}
	}

	public static void saveEdge(String outfile) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

		for (Entry<String, Double> entry : edgeMap.entrySet()) {
			bw.write("edge\t" + entry.getKey() + "\t" + entry.getValue());
			bw.newLine();
		}

		bw.close();
	}

	public static void computeTFIDFEdge() throws IOException {
		HashSet<String> toPairs;
		ArrayList<String> feats;
		double weight;

		HashMap<Long, Double> fromVecMap, toVecMap;

		String fromItem, toItem;

		for (String fromP : featureMap.keySet()) {

			fromItem = fromP.substring(fromP.indexOf('@') + 1);
			feats = featureMap.get(fromP);

			fromVecMap = getFeatVect(feats);

			toPairs = new HashSet<String>();
			for (String f : feats) {
				toPairs.addAll(invertedIdx.get(f));
			}

			for (String toP : toPairs) {
				if (fromP.equals(toP))
					continue;

				toItem = toP.substring(toP.indexOf('@') + 1);

				if (!(fromItem.equals(toItem) || ssItemMatching.contains(fromItem + "\t" + toItem)
						|| ssItemMatching.contains(toItem + "\t" + fromItem)))
					continue;

				if (!edgeMap.containsKey(fromP + "\t" + toP)) {

					toVecMap = getFeatVect(featureMap.get(toP));
					weight = cosine(fromVecMap, toVecMap);

					if (weight >= edgeWeithThreshold) {
						edgeMap.put(fromP + "\t" + toP, weight);
						edgeMap.put(toP + "\t" + fromP, weight);
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

	private static double cosine(HashMap<Long, Double> vecF, HashMap<Long, Double> vecT) {
		double FdotT = 0;
		double eucNormF = 0, eucNormT = 0;

		HashMap<Long, Double> tmpF = vecF.size() > vecT.size() ? vecT : vecF;
		HashMap<Long, Double> tmpT = tmpF == vecF ? vecT : vecF;

		for (Long id : tmpF.keySet()) {
			if (tmpT.containsKey(id)) {
				FdotT += tmpF.get(id) * tmpT.get(id);
			}
		}

		for (Long id : vecF.keySet()) {
			eucNormF += Math.pow(vecF.get(id), 2);
		}
		eucNormF = Math.sqrt(eucNormF);
		for (Long id : vecT.keySet()) {
			eucNormT += Math.pow(vecT.get(id), 2);
		}
		eucNormT = Math.sqrt(eucNormT);

		return FdotT / (eucNormF * eucNormT);
	}

	public static HashMap<Long, Double> getFeatVect(ArrayList<String> feats) {
		HashMap<Long, Double> vecMap = new HashMap<Long, Double>();

		// get TF
		for (String feat : feats) {
			Long id = termID.get(feat);
			if (!vecMap.containsKey(id))
				vecMap.put(id, 0.0);
			vecMap.put(id, vecMap.get(id) + 1);

		}
		for (Long id : vecMap.keySet()) {
			vecMap.put(id, Math.log(vecMap.get(id)) + 1);
		}

		// get IDF
		HashSet<String> uniqFeats = new HashSet<String>();
		uniqFeats.addAll(feats);
		for (String feat : uniqFeats) {
			Long id = termID.get(feat);
			double idf = idfMap.get(id);

			vecMap.put(id, vecMap.get(id) * idf);
		}

		return vecMap;
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

	class Pair {
		long ID;
		double tfidf;

		Pair(long id, double w) {
			ID = id;
			tfidf = w;
		}
	}

}
