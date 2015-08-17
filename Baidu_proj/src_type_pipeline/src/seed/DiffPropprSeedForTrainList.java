package seed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/*
 * Generate seed files of particular percentage
 */
public class DiffPropprSeedForTrainList {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] types = { "ingredient", "drug", "disease", "symptom" };
		double[] percentage = { 0.025, 0.075, 0.125, 0.25, 0.5, 0.75, 1 };
		// 0.01 0.03 0.05 0.1 0.2 0.3 0.4
		String[] devSeedFiles = new String[types.length];
		String suffix = "_devel_50p_proppr_seed_forTrainList";
		for (int i = 0; i < types.length; i++) {
			devSeedFiles[i] = types[i] + suffix;
		}

		for (int run = 0; run < 10; run++) {
			for (int i = 0; i < types.length; i++) {
				for (double per : percentage)
					generate(types[i], "data\\singleClassSeeds\\runs\\" + run
							+ "\\" + devSeedFiles[i], per,
							"data\\singleClassSeeds\\runs\\" + run + "\\"
									+ types[i] + suffix + "_" + per * 100 + "p");
			}
		}

	}

	public static void generate(String type, String infileName,
			double percetage, String out1) throws IOException {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(out1));
		BufferedReader br = new BufferedReader(new FileReader(infileName));

		int cnt = 0; // count from 1 to 100
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
