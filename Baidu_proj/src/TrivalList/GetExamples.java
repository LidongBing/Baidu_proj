package TrivalList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * Match the Freebase seeds against the docName@value pairs, and get the matched ones as training examples
 * 
 * Note that this program generate an example file for each class
 */
public class GetExamples {

	// public static HashMap<String, Integer> labelMap = new HashMap<String,
	// Integer>() {
	// {
	// put("disease", 1);
	// put("drug", 2);
	// put("ingredient", 3);
	// put("symptom", 4);
	// }
	// };

	public static void printHelp() {

		System.out
				.println("Please give five args: seedType, seedFile, labelMap, trivialListFile and outfile.");
		System.out
				.println("\t seedFile in format: seed <TAB> causes <TAB> kidney_stone@gout. The last column is diseaseName@value.");
		System.out
				.println("\t trivialListFile: use file of inList_aug.cfacts, in format: inList <TAB> kidney_stone@gout <TAB> s_12_8.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 5) {
			printHelp();
		}

		HashMap<String, String> labelMap = loadLabel(args[2]);
		HashSet<String> seeds = getSeeds(args[1]);
		scanTrivalList(args[3], seeds, args[0], labelMap, args[4]);
	}

	public static void scanTrivalList(String listFile, HashSet<String> seeds,
			String seedType, HashMap<String, String> labelMap, String outfile)
			throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		BufferedReader br = new BufferedReader(new FileReader(listFile));
		String line = br.readLine();
		while (line != null) {
			String[] toks = line.split("\\t");
			if (toks.length > 2) {
				String value = toks[1].trim().toLowerCase();

				if (seeds.contains(value)) {
					bw.write(line.split("\\t")[2] + "\t"
							+ labelMap.get(seedType));
					bw.newLine();
				}
			} else {
				System.out.println(line);
			}
			line = br.readLine();
		}
		br.close();
		bw.close();
	}

	// format of seed line: seed <tab> disease <tab> urolithiasis@kidney_stones 
	// the last column is diseaseName@value
	public static HashSet<String> getSeeds(String infile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(infile));
		HashSet<String> seeds = new HashSet<String>();
		String line = br.readLine();
		while (line != null) {
			String[] toks = line.split("\\t");
			seeds.add(toks[2].toLowerCase());
			line = br.readLine();
		}

		br.close();
		return seeds;
	}

	public static HashMap<String, String> loadLabel(String labelFile)
			throws IOException {
		HashMap<String, String> labelMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(labelFile));
		String line = br.readLine();
		String[] tokens = null;
		while (line != null && line.trim().length() != 0) {
			tokens = line.split("\\s+");

			if (tokens.length != 2) {
				System.out
						.println("ERROR: label map file should have two columns: labelName \t labelID");
				System.exit(0);

			}
			labelMap.put(tokens[0], tokens[1]);

			line = br.readLine();
		}
		br.close();
		return labelMap;

	}
}
