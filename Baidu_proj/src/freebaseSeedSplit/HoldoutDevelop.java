package freebaseSeedSplit;

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
		String seedPath = args[0];
		String runPath = args[1];
		double holdPercent = 0.5;
		String[] relations = args[2].split(",");
		if (!new File(runPath).exists())
			new File(runPath).mkdir();
		for (int run = 0; run < 10; run++) {
			if (!new File(runPath + run + "/").exists())
				new File(runPath + run + "/").mkdir();
			for (String relation : relations) {
				String infileName =seedPath + relation + "_single";
				String out1 = runPath + run + "/" + relation + "_single" + "_eval";
				String out2 = runPath + run + "/" + relation + "_single" + "_devel";
				generate(relation, infileName, holdPercent, out1, out2);
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
