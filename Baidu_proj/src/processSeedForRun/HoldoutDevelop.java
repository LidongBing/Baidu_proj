package processSeedForRun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Generate heldout set and development set
 */
public class HoldoutDevelop {

	public static void main(String[] args) throws IOException {
		String basePath = "/remote/curtis/baidu/mingyanl/pipe_line/";
		String[] types = { "condition_this_may_prevent", "disease_or_condition_caused", "symptom_of", "used_to_treat" };
		double holdPercent = 0.5;

		if (!new File(basePath + "single_runs/").exists())
			new File(basePath + "single_runs/").mkdir();
		for (int run = 0; run < 10; run++) {
			if (!new File(basePath + "single_runs/" + run + "/").exists())
				new File(basePath + "single_runs/" + run + "/").mkdir();
			for (int i = 0; i < types.length; i++) {
				String infileName = basePath + "relationSeed/seed_seperated/" + types[i] + "_single";
				String out1 = basePath + "single_runs/" + run + "/" + types[i] + "_single" + "_eval";
				String out2 = basePath + "single_runs/" + run + "/" + types[i] + "_single" + "_devel";
				generate(types[i], infileName, holdPercent, out1, out2);
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
