package freebaseSeedSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
				String infileName = seedPath + relation + "_single";
				String out1 = runPath + run + "/" + relation + "_single" + "_eval";
				String out2 = runPath + run + "/" + relation + "_single" + "_devel";
				generate(relation, infileName, holdPercent, out1, out2);
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
