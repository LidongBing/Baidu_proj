package ClassifyItemEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

public class NPPredictToItemSol {
	public static void printHelp() {

		System.out
				.println("Please give four args: predictFile, listBowFile, itemSolFile and mergedItemSolFile.");

		System.out
				.println("predictFile: each line is a prediction result, 1~4 or -1");
		System.out
				.println("listBowFile: each line is the list with its words such as 's_4_2 <TAB> naproxen usp'");
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 4) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();

		process(args[0], args[1], args[2], args[3]);
	}

	// predictFile: each line is a prediction result, 1~4 or -1
	// listBowFile: each line is the list with its words such as
	// "s_4_2 <TAB> naproxen usp"
	public static void process(String predictFile, String listBowFile,
			String outFile, String outFileMerge) throws IOException {
		BufferedReader brPre = new BufferedReader(new FileReader(predictFile));
		BufferedReader brBow = new BufferedReader(new FileReader(listBowFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		String linePre = brPre.readLine();
		String lineBow = brBow.readLine();

		HashMap<String, Double> mergedItemSolMap = new HashMap<String, Double>();

		while (linePre != null && lineBow != null) {
			if (!linePre.equals("-1")) {
				String[] toks = lineBow.split("\\t");
				if (toks.length > 1) {

					bw.write(toks[1].trim().replace(" ", "_") + "\t" + linePre);
					bw.newLine();

					double score = 0;
					if (toks.length > 2)
						score = Double.parseDouble(toks[2]);

					String combItem = toks[1].trim().replace(" ", "_") + "\t"
							+ linePre;
					if (!mergedItemSolMap.containsKey(combItem)) {
						mergedItemSolMap.put(combItem, 0.0);
					}
					mergedItemSolMap.put(combItem,
							mergedItemSolMap.get(combItem) + score);
				}

			}
			linePre = brPre.readLine();
			lineBow = brBow.readLine();

		}
		if (lineBow != null) {
			if (lineBow.length() != 0) {
				System.out
						.println("ERROR: predictFile and listBowFile do not match, haveing different number of lines");
				System.exit(0);
			}
		}

		if (linePre != null) {
			if (linePre.length() != 0) {
				System.out
						.println("ERROR: predictFile and listBowFile do not match, haveing different number of lines");
				System.exit(0);
			}
		}

		brBow.close();
		brPre.close();
		bw.close();

		BufferedWriter bwMergeItemSol = new BufferedWriter(new FileWriter(
				outFileMerge));
		ArrayList<Entry<String, Double>> entrylist = new ArrayList<Entry<String, Double>>(
				mergedItemSolMap.entrySet());

		Collections.sort(entrylist, new Comparator<Entry<String, Double>>() {
			// descending
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});

		for (Entry<String, Double> entry : entrylist) {
			bwMergeItemSol.write(entry.getKey() + "\t" + entry.getValue());
			bwMergeItemSol.newLine();
		}

		bwMergeItemSol.close();

	}

}
