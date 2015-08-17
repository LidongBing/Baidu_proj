package seed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/*
 * Generate two seed sets, one set is used to get training list set from proppr,
 * another set is to get testing list set
 */
public class PropprSeedForTrainTestList {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] types = { "ingredient", "drug", "disease", "symptom" };

		double testPercent = 0.2; // total's 10%, since the development set
									// contains 50% of total
		String[] develpSeedFiles = new String[types.length];
		String suffix = "_devel_50p";
		for (int i = 0; i < types.length; i++) {
			develpSeedFiles[i] = types[i] + suffix;
		}

		for (int run = 0; run < 10; run++) {
			for (int i = 0; i < types.length; i++) {
				generate(types[i], "data\\singleClassSeeds\\runs\\" + run
						+ "\\" + develpSeedFiles[i], testPercent,
						"data\\singleClassSeeds\\runs\\" + run + "\\"
								+ types[i] + "_devel_50p"
								+ "_proppr_seed_forTestList",
						"data\\singleClassSeeds\\runs\\" + run + "\\"
								+ types[i] + "_devel_50p"
								+ "_proppr_seed_forTrainList");
			}
		}
	}

	public static void generate(String type, String infileName,
			double percetage, String out1, String out2) throws IOException {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(out1));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(out2));
		BufferedReader br = new BufferedReader(new FileReader(infileName));

		int cnt = 0; // count from 1 to 100
		String line = br.readLine();
		while (line != null) {
			line = line.toLowerCase();

			if (keep(percetage)) {
				bw1.write("seed\t" + type + "\t"
						+ line.replaceAll(" ", "_").toLowerCase());
				bw1.newLine();
			} else {
				bw2.write("seed\t" + type + "\t"
						+ line.replaceAll(" ", "_").toLowerCase());
				bw2.newLine();
			}

			// if (cnt < percetage * 100) {
			// bw1.write(line);
			// bw1.newLine();
			// cnt++;
			// } else {
			// bw2.write(line);
			// bw2.newLine();
			// cnt++;
			// }
			// if (cnt == 100) {
			// cnt = 0;
			// }

			line = br.readLine();
		}

		bw1.close();
		bw2.close();
		br.close();
	}

	static boolean keep(double per) {
		// int maxRand = 10000;
		// int cnt = 0;
		// for (int i = 0; i < maxRand; i++) {
		// if (Math.random() < per)
		// cnt++;
		// if (Math.random() >= (1 - per))
		// cnt++;
		// }
		// System.out.println(cnt);
		// if (cnt < maxRand * per * 2)
		if ((Math.random() <= per))
			return true;
		return false;
	}

}
