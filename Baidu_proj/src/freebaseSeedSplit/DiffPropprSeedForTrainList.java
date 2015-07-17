package freebaseSeedSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Generate seed files of particular percentage
 */
public class DiffPropprSeedForTrainList {

	public static void main(String[] args) throws IOException {
		String runPath = args[0];
		double[] percentage = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.75, 1 };
		String[] relations = args[1].split(",");
		for (int run = 0; run < 10; run++) {
			for (String relation : relations) {
				for (double per : percentage) {
					String infileName = runPath + run + "/" + relation + "_single_devel_seed_for_train";
					String outFile = infileName + "_" + per * 100 + "p";
					generate(relation, infileName, per, outFile);
				}
			}
		}
	}

	public static void generate(String type, String infileName, double percetage, String out1) throws IOException {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(out1));
		BufferedReader br = new BufferedReader(new FileReader(infileName));
		String line = br.readLine();
		while (line != null) {
			line = line.toLowerCase();

			boolean keep = keep(percetage);

			if (keep) {
				bw1.write(line);
				bw1.newLine();
			}

			line = br.readLine();
		}
		bw1.close();
		br.close();
	}

	static boolean keep(double per) {
		double rand = Math.random();
		boolean keep = (rand <= per);
		if (per == 1.0 && keep == false) {
			System.out.println("ERROR " + rand);
			System.exit(0);
		}

		if (keep) {
			return true;
		}
		return false;
	}

}
