package ClassifyItemEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ListPredictToListIDMap {
	public static void printHelp() {

		System.out
				.println("Please give three args: predictFile, listBowFile, and outFile.");

		System.out
				.println("predictFile: each line is a prediction result, 1~4 or -1");
		System.out
				.println("listBowFile: each line is the list with its words such as 's_4_2 <TAB> naproxen usp'");
		System.out
				.println("outFile: for output file, each line: listID <TAB> predictedlabel ");
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 3) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();

		process(args[0], args[1], args[2]);
	}

	// predictFile: each line is a prediction result, 1~4 or -1
	// listBowFile: each line is the list with its words such as
	// "s_4_2 <TAB> naproxen usp"
	public static void process(String predictFile, String listBowFile,
			String outFile) throws IOException {
		BufferedReader brPre = new BufferedReader(new FileReader(predictFile));
		BufferedReader brBow = new BufferedReader(new FileReader(listBowFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		String linePre = brPre.readLine();
		String lineBow = brBow.readLine();

		while (linePre != null && lineBow != null) {
			if (!linePre.equals("-1")) {
				String[] toks = lineBow.split("\\t");
				if (toks.length > 1) {

					bw.write(toks[0] + "\t" + linePre);
					bw.newLine();
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
	}

}
