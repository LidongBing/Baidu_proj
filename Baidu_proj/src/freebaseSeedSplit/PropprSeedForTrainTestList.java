package freebaseSeedSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Generate two seed sets, one set is used to get training list set from proppr,
 * another set is to get testing list set
 */
public class PropprSeedForTrainTestList {

	public static void main(String[] args) throws IOException {
		String runPath=args[0];
		String[] relations = args[1].split(",");
		double testPercent = 0.2; // total's 10%, since the development set contains 50% of total
		for (int run = 0; run < 10; run++) {
			for (String relation : relations) {
				String inputFile = runPath + run + "/" + relation + "_single" + "_devel";
				String out1 = inputFile + "_seed_for_test";
				String out2 = inputFile + "_seed_for_train";
				generate(relation, inputFile, testPercent, out1, out2);
			}
		}
	}

	public static void generate(String type, String infileName, double percetage, String out1, String out2) throws IOException {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(out1));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(out2));
		BufferedReader br = new BufferedReader(new FileReader(infileName));

		String line = br.readLine();
		while (line != null) {
			line = line.toLowerCase();

			if (keep(percetage)) {
				bw1.write(line);
				bw1.newLine();
			}
			else {
				bw2.write(line);
				bw2.newLine();
			}
			line = br.readLine();
		}

		bw1.close();
		bw2.close();
		br.close();
	}

	static boolean keep(double per) {
		if ((Math.random() <= per))
			return true;
		return false;
	}

}
