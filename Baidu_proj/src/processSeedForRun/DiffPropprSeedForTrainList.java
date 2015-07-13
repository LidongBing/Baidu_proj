package processSeedForRun;

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
		String basePath = "/remote/curtis/baidu/mingyanl/pipe_line/";
		String[] types = { "condition_this_may_prevent", "disease_or_condition_caused", "symptom_of", "used_to_treat" };
		double[] percentage = { 0.025, 0.075, 0.125, 0.25, 0.5, 0.75, 1 };
		String[] devSeedFiles = new String[types.length];
		String suffix = "_devel_50p_proppr_seed_forTrainList";
		for (int i = 0; i < types.length; i++) {
			devSeedFiles[i] = types[i] + suffix;
		}

		for (int run = 0; run < 10; run++) {
			for (int i = 0; i < types.length; i++) {
				for (double per : percentage) {
					String infileName = basePath + "single_runs/" + run + "/" + types[i] + "_single_devel_seed_for_train";
					String outFile = infileName + "_" + per * 100 + "p";
					generate(types[i], infileName, per, outFile);
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
