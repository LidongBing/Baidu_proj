package ClassifyItemEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Coverage {

	public static void printHelp() {

		System.out
				.println("Please give four args, freebaseInstanceFile, predictedInstanceFile, threshold, and outputFile");

		System.exit(0);
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// if (args.length != 4) {
		// System.out.println("ERROR happened !!");
		// printHelp();
		// }
		//
		// if (args.length > 0 && args[0].equals("-help"))
		// printHelp();
		//
		// String fbFile = args[0];
		// String predFile = args[1];
		// double thre = Double.parseDouble(args[2]);
		// String outFile = args[3];

		double thre = 0.2;
		String fbFile = "testData\\coverage_eva_multiAdded";
		String predFile = "testData\\oracle_merged_item.pred";
		String outFile = "testData\\out_oracle";
		// String fbFile = "testData\\testFBInstance";
		// String predFile = "testData\\testPreInstance";
		// String outFile = "testData\\testout";

		ArrayList<Pair> fbInstances = loadResults(fbFile);
		ArrayList<Pair> preds = loadResults(predFile);
		double[] predCnt = new double[5];
		double[] fbCnt = new double[5];

		System.out.println("Freebase Instance #: " + fbInstances.size());
		for (Pair fb : fbInstances) {
			fbCnt[0]++;
			int fbl = Integer.parseInt(fb.value);
			fbCnt[fbl]++;
			for (Pair pred : preds) {
				if (nLD(fb.key, pred.key) < thre && fb.value.equals(pred.value)) {
					predCnt[0]++;
					predCnt[fbl]++;
					break;
				}
			}

			if ((int) fbCnt[0] % 10 == 0)
				System.out.println("Processed #: " + fbCnt[0]);
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		bw.write("Overall Coverage: " + predCnt[0] / fbCnt[0] + ", "
				+ predCnt[0] + "/" + fbCnt[0]);
		bw.newLine();
		for (int i = 1; i < fbCnt.length; i++) {
			bw.write("Label " + i + " Coverage: " + predCnt[i] / fbCnt[i]
					+ ", " + predCnt[i] + "/" + fbCnt[i]);
			bw.newLine();
		}
		bw.close();

	}

	public static ArrayList<Pair> loadResults(String predFile)
			throws IOException {
		ArrayList<Pair> ret = new ArrayList<Pair>();
		BufferedReader br = new BufferedReader(new FileReader(predFile));
		String line = br.readLine();

		while (line != null) {
			String[] toks = line.split("\\t");
			if (toks.length > 1) {
				ret.add(new Pair(toks[0], toks[1]));
			}
			line = br.readLine();
		}
		br.close();
		return ret;
	}

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	public static double nLD(String str1, String str2) {
		double ld = LD(str1, str2);
		double len = str1.length();
		if (len < str2.length()) {
			len = str2.length();
		}
		return ld / len;
	}

	public static int LD(String str1, String str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
										: 1));

		return distance[str1.length()][str2.length()];
	}

}

class Pair {
	String key;
	String value;

	public Pair(String k, String v) {
		this.key = k;
		this.value = v;
	}

	public String toString() {
		return key + " " + value;
	}
}