package freebaseSeedMatch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SeedCfactsEditDistance {
	/*
	 * args[0] all seed file args[1] inList.cfacts file args[2] output new
	 * allseed file args[3] mapping file
	 */
	private static HashSet<String> set = new HashSet<String>();
	private static HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	private static ArrayList<String> reflections = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		loadMap(args[0]);
		loadSet(args[1]);
		System.out.println("finished load");
		check();
		System.out.println("finished check");
		output(args[2], args[3]);
	}

	public static void output(String allFile, String mappingFile) throws IOException {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(allFile));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(mappingFile));
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			HashSet<String> set = new HashSet<String>(entry.getValue());
			for (String pair : set) {
				bw1.write("seed\t" + entry.getKey() + "\t" + pair + "\n");
			}
		}
		bw1.close();
		for (String ref : reflections) {
			bw2.write(ref + "\n");
		}
		bw2.close();
	}

	public static void check() {
		long begintime = System.currentTimeMillis();
		int count = 0;
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			System.out.println("check key : " + entry.getKey());
			ArrayList<String> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				count++;
				String seedPair = list.get(i);
				String bestCfacts = null;
				double bestDistance = Double.MAX_VALUE;
				System.out.println("seed pair: "+seedPair);
				System.out.println("count: " + count + "time " + (System.currentTimeMillis() - begintime) / 1000);
				for (String cfactsPair : set) {
					if(seedPair.equals(cfactsPair)){
						bestCfacts=null;
						break;
					}
					double distance = compare(seedPair, cfactsPair);
					if (distance != -1) {
						if (distance < bestDistance) {
							bestDistance = distance;
							bestCfacts = cfactsPair;
						}
					}
				}
				if (bestCfacts != null) {
					System.out.println("New seed "+bestCfacts);
					reflections.add(entry.getKey() + "\t" + seedPair + "\t" + bestCfacts);
					list.set(i, bestCfacts);
				}else{
					System.out.println("Not change");
				}
			}
		}
	}

	private static double compare(String seedPair, String cfactsPair) {
		String drug1 = seedPair.split("@")[0];
		String item1 = seedPair.split("@")[1];
		String drug2 = cfactsPair.split("@")[0];
		String item2 = cfactsPair.split("@")[1];
		double lenDrug = Math.max(drug1.length(), drug2.length());
		double lenItem = Math.max(item1.length(), item2.length());
		double disDrug = 0.0 + computeLevenshteinDistance(drug1, drug2) / lenDrug;
		double disItem = 0.0 + computeLevenshteinDistance(item1, item2) / lenItem;
		if (disDrug <= 0.2 && disItem <= 0.2) {
			return disDrug * disItem;
		}
		else {
			return -1;
		}
	}

	public static void loadMap(String seedFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(seedFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (map.containsKey(tokens[1])) {
				map.get(tokens[1]).add(tokens[2]);
			}
			else {
				ArrayList<String> set = new ArrayList<String>();
				set.add(tokens[2]);
				map.put(tokens[1], set);
			}
		}
		br.close();
	}

	public static void loadSet(String cfactsFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(cfactsFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			set.add(tokens[1]);
		}
		br.close();
	}

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	public static int computeLevenshteinDistance(String str1, String str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

		return distance[str1.length()][str2.length()];
	}
}
