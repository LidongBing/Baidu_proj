package ClassifyExample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * Combine a few feature files. They should have the same number or examples 
 * and the examples should be in the same order in different files
 */
public class TokFeatMerger {
	public static void printHelp() {

		System.out
				.println("Please give the args like this: inputTokFeatureFile1  inputTokFeatureFile2 ... outputfile filterDup.");
		System.out
				.println("filterDup: dofilter or nofilter, whether filter the duplication merged tok feature vectors ");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length < 4) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();

		ArrayList<String> inputFiles = new ArrayList<String>();
		for (int i = 0; i < args.length - 2; i++) {
			inputFiles.add(args[i]);
		}
		doOne(inputFiles, args[args.length - 2], args[args.length - 1]);
	}

	public static void doOne(ArrayList<String> featFiles, String outFile,
			String filter) throws IOException {
		boolean doFiltering = false;
		if (filter.equals("dofilter"))
			doFiltering = true;

		ArrayList<String> mergedVecs = new ArrayList<String>();
		loadVector(featFiles.get(0), mergedVecs);
		// System.out.println(mergedFeatIdx);

		for (int i = 1; i < featFiles.size(); i++) {
			ArrayList<String> vectors = new ArrayList<String>();
			loadVector(featFiles.get(i), vectors);
			mergeVec(vectors, mergedVecs);

			saveFeature(outFile, mergedVecs, doFiltering);

			// System.out.println("\t" + mergedFeatIdx);
		}
	}

	public static void saveFeature(String outFile, ArrayList<String> vectors,
			boolean removeDup) throws IOException {

		if (removeDup) {
			HashSet<String> uniques = new HashSet<String>();
			ArrayList<String> vectorUni = new ArrayList<String>();
			for (int i = 0; i < vectors.size(); i++) {
				if (!uniques.contains(vectors.get(i))) {
					vectorUni.add(vectors.get(i));
					uniques.add(vectors.get(i));
				}
			}
			vectors = vectorUni;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		for (String vec : vectors) {
			bw.write(vec);
			bw.newLine();
		}
		bw.close();

	}

	// vectors and mergedVecs should have same number of line items and they
	// should be in the same order
	public static void mergeVec(ArrayList<String> vectors,
			ArrayList<String> mainVecs) {
		if (vectors.size() != mainVecs.size()) {
			System.out
					.println("ERROR: fatal error, the tok featrue files to be merged have different numbers of examples");
			System.exit(0);
		}
		for (int i = 0; i < mainVecs.size(); i++) {
			String[] toks = vectors.get(i).split("\\t+");
			if (!mainVecs.get(i).startsWith(toks[0])) {
				System.out
						.println("ERROR: fatal error, the merged tok feature vectors have different IDs");
				System.exit(0);
			}
			StringBuffer sb = new StringBuffer();
			for (int j = 2; j < toks.length; j++) {
				sb.append("\t" + toks[j]);
			}
			mainVecs.set(i, mainVecs.get(i) + sb.toString());
		}
	}

	public static void loadVector(String vecFile, ArrayList<String> vectors)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(vecFile));
		String line = br.readLine();

		while (line != null && line.length() > 0) {
			vectors.add(line);

			line = br.readLine();
		}
		br.close();

	}

}
