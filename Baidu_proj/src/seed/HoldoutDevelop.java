package seed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/*
 * Generate heldout set and development set
 */
public class HoldoutDevelop {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] types = { "ingredient", "drug", "disease", "symptom" };
		// double[] percentage = { 0.01, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6,
		// 0.7,
		// 0.8, 0.9, 0.99 };
		double holdPercent = 0.5;
		String[] wholeSeedFiles = new String[types.length];
		String suffix = "_FB_seed_singleClass";
		for (int i = 0; i < types.length; i++) {
			wholeSeedFiles[i] = types[i] + suffix;
		}

		if (!new File("data\\singleClassSeeds\\runs\\").exists())
			new File("data\\singleClassSeeds\\runs\\").mkdir();
		for (int run = 0; run < 10; run++) {
			if (!new File("data\\singleClassSeeds\\runs\\" + run + "\\")
					.exists())
				new File("data\\singleClassSeeds\\runs\\" + run + "\\").mkdir();
			for (int i = 0; i < types.length; i++) {
				generate(types[i], "data\\singleClassSeeds\\"
						+ wholeSeedFiles[i], holdPercent,
						"data\\singleClassSeeds\\runs\\" + run + "\\"
								+ types[i] + "_heldout_eva_50p",
						"data\\singleClassSeeds\\runs\\" + run + "\\"
								+ types[i] + "_devel_50p");
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
				bw1.write(line);
				bw1.newLine();
			} else {
				bw2.write(line);
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
