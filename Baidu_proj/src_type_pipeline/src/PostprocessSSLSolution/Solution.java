package PostprocessSSLSolution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

public class Solution {

	public HashMap<String, ArrayList<Pair>> solMap = new HashMap<String, ArrayList<Pair>>();
	public HashSet<String> solsAppearOnce = new HashSet<String>();

	// allSols: some solutions may appear as solution for different predict, for
	// training
	// example purpose, it is better not use them in a single-label
	// classification
	public HashSet<String> allSols = new HashSet<String>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Solution ss = new Solution(
				"C:\\Users\\User\\Desktop\\Baidu_Drug\\williamCP\\coord-lists\\ssl.solutions.txt");

		System.out.println(ss.solMap.keySet());
		System.out.println(ss.solMap.get("symptom"));
		System.out.println(ss.solMap.get("drug"));
		System.out.println(ss.solMap.get("ingredient"));

	}

	public Solution(String solFile) throws IOException {
		this.parseSolFile(solFile);
		this.setSolSets();
	}

	public void parseSolFile(String infileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(infileName));
		String type = null;
		String line = null;
		StringTokenizer st = null;
		String sol = null;
		String score = null;
		String[] toks = null;

		line = br.readLine();
		while (line != null) {
			if (line.startsWith("# proved")) {
				type = line.substring(line.indexOf('(') + 1, line.indexOf(','));
				solMap.put(type, new ArrayList<Pair>());
			} else {
				toks = line.split("\\t+");

				score = toks[1];
				sol = toks[2].substring(toks[2].indexOf(',') + 1,
						toks[2].indexOf(')'));
				type = toks[2].substring(toks[2].indexOf('(') + 1,
						toks[2].indexOf(','));

				solMap.get(type).add(new Pair(sol, new Double(score)));
			}

			line = br.readLine();
		}
		br.close();
	}

	/*
	 * Only return the solutions that appear one time in all types of predict
	 * goals.
	 */
	public void setSolSets() {
		ArrayList<String> tmplist = new ArrayList<String>();
		HashMap<String, Integer> cnt = new HashMap<String, Integer>();

		ArrayList<ArrayList<Pair>> sols = new ArrayList<ArrayList<Pair>>();
		sols.addAll(this.solMap.values());
		for (ArrayList<Pair> solOfOneType : sols) {
			for (Pair sol : solOfOneType) {
				if (cnt.containsKey(sol.key)) {
					cnt.put(sol.key, cnt.get(sol.key) + 1);
				} else {
					cnt.put(sol.key, 1);
				}
			}
		}
		// System.out.println("cnt:" + cnt.size());

		allSols.addAll(cnt.keySet());

		tmplist.addAll(cnt.keySet());
		for (int i = 0; i < tmplist.size(); i++) {
			if (cnt.get(tmplist.get(i)) == 1) {
				solsAppearOnce.add(tmplist.get(i));
			}
		}

	}

	public ArrayList<Pair> getSolsAppearingOnceByThreWithScore(String arg1, double thre) {

		ArrayList<Pair> strSol = new ArrayList<Pair>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (Pair p : pairSol) {
				if (p.value >= thre) {

					if (this.solsAppearOnce.contains(p.key)) {
						strSol.add(p);
					}
				}
			}

		return strSol;
	}

	public ArrayList<String> getSolsAppearingOnceByThre(String arg1,
			double thre) {

		ArrayList<String> strSol = new ArrayList<String>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (Pair p : pairSol) {
				if (p.value >= thre) {

					if (this.solsAppearOnce.contains(p.key)) {
						strSol.add(p.key);
					}
				}
			}

		return strSol;
	}

	public ArrayList<String> getSolsByThre(String arg1, double thre) {

		ArrayList<String> strSol = new ArrayList<String>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (Pair p : pairSol) {
				if (p.value >= thre) {
					strSol.add(p.key);
				}
			}

		return strSol;
	}

	public ArrayList<Pair> getSolsByThreWithScore(String arg1, double thre) {

		ArrayList<Pair> strSol = new ArrayList<Pair>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (Pair p : pairSol) {
				if (p.value >= thre) {
					strSol.add(p);
				}
			}

		return strSol;
	}

	public ArrayList<String> getSolsAppearingOnceByTop(String arg1, int topN) {

		ArrayList<String> strSol = new ArrayList<String>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (int i = 0; i < topN && i < pairSol.size(); i++) {
				if (this.solsAppearOnce.contains(pairSol.get(i).key)) {
					strSol.add(pairSol.get(i).key);
				} else {
					topN++;
				}
			}

		return strSol;
	}

	public ArrayList<Pair> getSolsAppearingOnceByTopWithScore(String arg1, int topN) {

		ArrayList<Pair> strSol = new ArrayList<Pair>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (int i = 0; i < topN && i < pairSol.size(); i++) {
				if (this.solsAppearOnce.contains(pairSol.get(i).key)) {
					strSol.add(pairSol.get(i));
				} else {
					topN++;
				}
			}

		return strSol;
	}
	public ArrayList<String> getSolsByTop(String arg1, int topN) {

		ArrayList<String> strSol = new ArrayList<String>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (int i = 0; i < topN && i < pairSol.size(); i++) {
				strSol.add(pairSol.get(i).key);
			}

		return strSol;
	}

	public ArrayList<Pair> getSolsByTopWithScore(String arg1, int topN) {

		ArrayList<Pair> strSol = new ArrayList<Pair>();

		ArrayList<Pair> pairSol = this.solMap.get(arg1);
		if (pairSol != null)
			for (int i = 0; i < topN && i < pairSol.size(); i++) {
				strSol.add(pairSol.get(i));
			}

		return strSol;
	}

}
