package SeedExtractMatchSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
 * Generate two seed sets, one set is used to get training list set from proppr,
 * another set is to get testing list set
 */
public class PropprSeedForTrainTestList {

	public static void main(String[] args) throws IOException {
		String runPath = args[0];
		String[] relations = args[1].split(",");
		double testPercent = 0.2; // total's 10%, since the development set
									// contains 50% of total
		for (int run = 0; run < 10; run++) {
			for (String relation : relations) {
				String inputFile = runPath + run + "/" + relation + "_single" + "_devel";
				String out1 = inputFile + "_seed_for_test";
				String out2 = inputFile + "_seed_for_train";
				generate(relation, inputFile, testPercent, out1, out2);
			}
		}
	}

	public static void generate(String type, String infileName, double percentage, String out1, String out2) throws IOException {
		ArrayList<String> firstList = new ArrayList<>();
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(out1));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(out2));
		BufferedReader br = new BufferedReader(new FileReader(infileName));
		String line = null;
		while ((line = br.readLine()) != null) {
			firstList.add(line);
		}
		br.close();
		ArrayList<String> secondList = split(percentage, firstList);
		for (String str1 : firstList) {
			bw1.write(str1 + "\n");
		}
		for (String str2 : secondList) {
			bw2.write(str2 + "\n");
		}
		bw1.close();
		bw2.close();
	}

	static ArrayList<String> split(double percentage, ArrayList<String> lines) {
		ArrayList<String> secondList = new ArrayList<>();
		Random rand = new Random();
		int size = lines.size();
		int removeTime = (int) (size * (1 - percentage));
		for (int i = 0; i < removeTime; i++) {
			int index = rand.nextInt(size - i);
			secondList.add(lines.remove(index));
		}
		return secondList;
	}
}
