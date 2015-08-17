package PostprocessSSLSolution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExtractClassifyExample {

	public Solution parsedSol;
	public ArrayList<String> labels = new ArrayList<String>();
	public HashMap<String, String> labelMap = new HashMap<String, String>();
	public double thre = 0.1;
	public int topN = 0;

	public static void printHelp() {

		System.out
				.println("Please give six args, labelMapFile, solutionFile, outputFile,");
		System.out.println("      topN, thre, and singleLabel ");
		System.out
				.println("labelMapFile: contains the label ID map, in format 'symptom\t id'");
		System.out.println("solutionFile: contains the solution from SSL step");
		System.out.println("outputFile: the output file");
		System.out
				.println("topN: using the top N solutions in solutionFile, topN>=-1. if topN=-1, use all. If > 0, thre=0");
		System.out
				.println("thre: using the solutions with score >= \'thre\' in solutionFile, >= 0. If > 0, topN=0");
		System.out
				.println("singleLabel: Y if it is single label classifiation, otherwise N");

		System.exit(0);
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 6) {
			System.out.println("ERROR happened !!");
			printHelp();
		}

		if (args.length > 0 && args[0].equals("-help"))
			printHelp();

		Solution ss = new Solution(args[1]);
		ExtractClassifyExample td = new ExtractClassifyExample();
		td.loadLabel(args[0]);
		td.parsedSol = ss;
		td.topN = Integer.parseInt(args[3]);
		td.thre = Double.parseDouble(args[4]);
		td.generateTrainingFile(args[2], args[5]);

		// Solution ss = new Solution(
		// "svmData\\listSolutionsFreebaseSeedHalf.1e_8.txt");
		// ClassifyExample td = new ClassifyExample();
		// td.labelMap = td.loadLabel("svmData\\labelMap");
		// td.labels.addAll(td.labelMap.keySet());
		// td.parsedSol = ss;
		// td.topN = 5000;
		// td.thre = 0;
		// td.generateTrainingFile("svmData\\listExample" + td.topN,
		// "Y");
		// System.out.println(td.labels);

	}

	public void generateTrainingFile(String outfileName, String single)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfileName));
		ArrayList<Pair> sols = null;
		for (String label : labels) {

			if (!single.equals("N") && !single.equals("Y")) {
				System.out
						.println("ERROR: Single label 'S' or multiple label 'Y'?");
				System.exit(0);
			}

			if (single.equals("N")) {
				if (topN == 0 && thre != 0)
					sols = parsedSol.getSolsByThreWithScore(label, thre);
				else if (topN != 0 && thre == 0) {
					if (topN == -1)
						topN = Integer.MAX_VALUE;
					sols = parsedSol.getSolsByTopWithScore(label, topN);
				} else {
					System.out.println("ERROR: By TOP of by THRE?");
					System.exit(0);
				}

				for (Pair sol : sols) {

					bw.write(sol.key + "\t" + this.labelMap.get(label) + "\t"
							+ sol.value);
					bw.newLine();
				}

			} else if (single.equals("Y")) {
				if (topN == 0 && thre != 0)
					sols = parsedSol.getSolsAppearingOnceByThreWithScore(label,
							thre);
				else if (topN != 0 && thre == 0) {
					if (topN == -1)
						topN = Integer.MAX_VALUE;
					sols = parsedSol.getSolsAppearingOnceByTopWithScore(label,
							topN);
				} else {
					System.out.println("ERROR: By TOP of by THRE?");
					System.exit(0);
				}

				for (Pair sol : sols) {

					bw.write(sol.key + "\t" + this.labelMap.get(label));
					bw.newLine();
				}

			}
			// System.out.println(sols);
		}

		bw.close();
	}

	public void loadLabel(String labelFile) throws IOException {

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
			labels.add(tokens[0]);

			line = br.readLine();
		}
		br.close();

	}
}
