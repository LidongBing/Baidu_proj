package freebaseSeedSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/*
 * Prepare the Freebase instances for coverage evaluation.
 * For each run in singleClass/runs/x, the single class evaluation instances in
 * TYPE_heldout_eva-50p.
 * Add back the multi-class instances.
 * 
 * Equally, use the file in cleanedSeed folder as base, and remove the instances in 
 * singleClass/runs/x/disease_devel_50p
 */
public class NotUsePrepareFreebaseCoverageEva {
	public static HashMap<String, String> labelMap = new HashMap<String, String>();
	static {
		labelMap.put("disease", "1");
		labelMap.put("drug", "2");
		labelMap.put("ingredient", "3");
		labelMap.put("symptom", "4");
	};

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		for (int run = 0; run < 10; run++) {
			generate("data\\cleanedSeed\\", "data\\singleClassSeeds\\runs\\"
					+ run + "\\", "data\\singleClassSeeds\\runs\\" + run
					+ "\\coverage_eva_multiAdded");
		}
	}

	public static void generate(String cleanedSeedDir, String runDir,
			String outFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		ArrayList<String> types = new ArrayList<String>();
		types.addAll(labelMap.keySet());
		Collections.sort(types);

		for (int i = 0; i < types.size(); i++) {
			HashSet<String> develSet = loadSet(runDir + types.get(i)
					+ "_devel_50p");

			BufferedReader br = new BufferedReader(new FileReader(
					cleanedSeedDir + types.get(i) + "_FB_seed"));
			String line = br.readLine();

			while (line != null) {
				if (!develSet.contains(line)) {
					bw.write(line.replace(" ", "_") + "\t"
							+ labelMap.get(types.get(i)));
					bw.newLine();
				}

				line = br.readLine();
			}

			br.close();
		}

		bw.close();
	}

	private static HashSet<String> loadSet(String infile) throws IOException {
		HashSet<String> retSet = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line = br.readLine();

		while (line != null) {
			retSet.add(line);
			line = br.readLine();
		}

		br.close();
		return retSet;
	}

}
