package TrivalList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

/*
 * This class is the same as ClassifyExample.CrossValiSplitter
 * Split the exmaple file into training and testing
 */
public class CrossValiSplitter {

	public static void printHelp() {

		System.out
				.println("Please give three args: exampleFile, foldNo, and outfileNamePrefix.");

		System.out
				.println("existingExampleFile: the existing file to be splitted into sub parts ");
		System.out.println("foldNo: \'foldNo\'-fold cross validation ");
		System.out
				.println("outfileNamePrefix: the name prefix of the output files ");
		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 3) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();

		ArrayList<String> labels = new ArrayList<String>();
		HashMap<String, ArrayList<String>> exampleMap = new HashMap<String, ArrayList<String>>();

		loadLabelAndExamples(args[0], labels, exampleMap);
		splitExamplesIntoSubFiles(args[2] + "_train_part", args[2]
				+ "_test_part", labels, exampleMap, Integer.parseInt(args[1]));


	}

	public static void splitExamplesIntoSubFiles(String newTraining,
			String newTesting, ArrayList<String> labels,
			HashMap<String, ArrayList<String>> exampleMap, int foldNo)
			throws IOException {
		ArrayList<String> oneExaList = null;

		for (int i = 0; i < foldNo; i++) {

			BufferedWriter trainingBw = new BufferedWriter(new FileWriter(
					newTraining + i + ".exmps"));
			BufferedWriter testingBw = new BufferedWriter(new FileWriter(
					newTesting + i + ".exmps"));

			for (int j = 0; j < labels.size(); j++) {
				oneExaList = exampleMap.get(labels.get(j));
				for (int k = 0; k < oneExaList.size(); k++) {
					if (k % foldNo == i) {
						testingBw.write(oneExaList.get(k));
						testingBw.newLine();
					} else {
						trainingBw.write(oneExaList.get(k));
						trainingBw.newLine();
					}
				}
			}

			trainingBw.close();
			testingBw.close();
		}
	}

	public static void loadLabelAndExamples(String existingExampleFile,
			ArrayList<String> labels,
			HashMap<String, ArrayList<String>> exampleMap) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(
				existingExampleFile));

		String line = br.readLine();

		while (line != null) {
			String[] segs = line.split("\t");
			if (!exampleMap.containsKey(segs[1])) {
				exampleMap.put(segs[1], new ArrayList<String>());
			}
			exampleMap.get(segs[1]).add(line);

			line = br.readLine();
		}
		labels.addAll(exampleMap.keySet());
		Collections.sort(labels);
		br.close();

	}
}
