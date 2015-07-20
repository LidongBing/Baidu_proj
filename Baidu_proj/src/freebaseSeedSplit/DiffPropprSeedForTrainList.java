package freebaseSeedSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
 * Generate seed files of particular percentage
 */
public class DiffPropprSeedForTrainList {

	public static void main(String[] args) throws IOException {
		String runPath = args[0];
		String[] percentages = args[2].split(",");
		String[] relations = args[1].split(",");
		for (int run = 0; run < 10; run++) {
			for (String relation : relations) {
				for (String percentage : percentages) {
					double per = Double.parseDouble(percentage);
					String infileName = runPath + run + "/" + relation + "_single_devel_seed_for_train";
					String outFile = infileName + "_" + per * 100 + "p";
					generate(relation, infileName, per, outFile);
				}
			}
		}
	}

	public static void generate(String type, String infileName, double percentage, String out1) throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(out1));
		BufferedReader br = new BufferedReader(new FileReader(infileName));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = line.toLowerCase();
			lines.add(line);
		}
		br.close();
		filterWithPercentage(percentage, lines);
		for (String filterLine : lines) {
			bw1.write(filterLine + "\n");
		}
		bw1.close();

	}

	static void filterWithPercentage(double percentage, ArrayList<String> lines) {
		Random rand = new Random();
		if (percentage == 1) {
			return;
		}
		else {
			int size = lines.size();
			int removeTime = (int) (size * (1 - percentage));
			for (int i = 0; i < removeTime; i++) {
				int index = rand.nextInt(size - i);
				lines.remove(index);
			}
		}
	}
}
